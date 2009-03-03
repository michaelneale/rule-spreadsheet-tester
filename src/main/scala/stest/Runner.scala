package stest

/**
 * A test scenario runner that uses XLS spreadsheets to drive tests (each column is a scenario)
 * @author Michael Neale
 */

import java.io.InputStream
import jxl.{Sheet, Workbook, Cell}
import java.util.{HashMap => JavaHash}
import org.drools.KnowledgeBase
import org.mvel2.MVEL



class Runner(val knowledgeBase: KnowledgeBase) {

    def runTestsInWorkbook(input: InputStream) : Array[WorksheetReport] = {
      val w = Workbook.getWorkbook (input)
      w.getSheets.map ((s: Sheet) => WorksheetReport(s.getName, processSheet(s)))
    }


   /**
    * Process a sheet, return a List of ScenarioReports - one for each scenario column found.
    */
    def processSheet(st: Sheet) = {
        val declarationCells = st.getColumn(0).takeWhile(_.getContents != "WHEN")
        val dataCells = st getColumn (0) dropWhile (_.getContents != "WHEN") drop (1) takeWhile (_.getContents != "EXPECT")
        val expectCells = st getColumn(0) dropWhile(_.getContents != "EXPECT") drop(1)
        val facts = declarationCells filter(_.getContents startsWith("Fact")) map(_.getContents.substring(4).split(":"))
        val globals = declarationCells filter(_.getContents startsWith("Global")) map(_.getContents.substring(6).split(":"))

        lazy val dataStartRow = dataCells(0).getRow
        lazy val expectStartRow = expectCells(0).getRow

        //turn it into a list - thankfully scala is nice and lazy about this
        val allcols = for (i <- 1 to (st.getColumns - 1)) yield st.getColumn(i)

        //ok here are our scenarios, filtering it down
        val scenarioColumns = allcols filter((cs: Array[Cell]) => cs(dataStartRow - 1).getContents != "")

        //now run the tests
        val list = scenarioColumns.map((col: Array[Cell]) => processScenario( col,
                                                                  dataCells,
                                                                  expectCells,
                                                                  facts,
                                                                  globals,
                                                                  dataStartRow,
                                                                  expectStartRow) )
        list.toArray
    }

    
    def processScenario(col: Array[Cell], dataCells: Array[Cell], expectCells: Array[Cell], facts: Seq[Array[String]],  globals: Seq[Array[String]], dataStartRow: Int, expectStartRow: Int)  = {
      val factData = createObjects (facts)
      val globalData = createObjects (globals)

      //pump in scenario data
      val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
      val factStore = populateData(dataCells, combine (factData, globalData), scenarioData, dataStartRow)

      //here we fire things up in rules
      createSession(globalData, knowledgeBase).executeIterable(factData.values)

      //perform checks
      val expectationData = col dropWhile (_.getRow < expectStartRow)
      val results = expectCells.map((c: Cell) => inspectResult(factStore, c, expectationData(c.getRow - expectStartRow).getContents))
      val failures = results.filter(_.pass == false).map(_.failureDescription)
      ScenarioReport(col(dataStartRow - 1).getContents, failures, results.filter(_.failureDescription == "OK").size)
    }

    /** needed to deal with java hashes that we use later on */
    implicit def toArr[T](set: java.util.Set[T]) = {
      set.toArray(new Array[T](set.size))
    }

    def createSession(globalData: JavaHash[String, Object], kb: KnowledgeBase) = {
        val session = kb.newStatelessKnowledgeSession
        //ugh ! crazy ! this is what I have to do to live with java hashmaps
        globalData.keySet.map((key: String) => session.setGlobal(key, globalData.get(key)))
        session
    }



    /** Avert your eyes children. Ugly mutable code follows */
    def createObjects(ls: Seq[Array[String]]) : JavaHash[String, Object] = {
        val jh = new JavaHash[String, Object]
        for (pair <- ls)  jh.put(pair(0).trim, MVEL.eval("new " + pair(1)))
        jh
    }

    /** Use MVEL to populate the data for a field */
    def populateData(dataCells: Array[Cell], dt: JavaHash[String, Object], scenarioData: Array[Cell], dataStartRow: Int) = {
      for (c <- dataCells)  {
        val rawExpr = scenarioData(c.getRow - dataStartRow).getContents
        MVEL.eval(c.getContents.replace(' ', '.') + " = " + getExpression(rawExpr) , dt)
      }
      dt
    }

    def getExpression(rawExpr: String) = if (rawExpr.startsWith("{") && rawExpr.endsWith("}")) rawExpr.substring(1, rawExpr.length-1) else "'" + rawExpr + "'"

    /** Use MVEL to inspect the results */
    def inspectResult(dt: JavaHash[String, Object], cell: Cell, expected: String)  = {
        val expression = cell.getContents
        if (expression == "") {
           new PassFail(true, "NA")
        } else if (MVEL.eval(expression.replace(' ', '.') + " == " + getExpression(expected), dt).asInstanceOf[Boolean]) {
           new PassFail(true, "OK")
        } else {
           new PassFail(false, "Failure in row: " + (cell.getRow + 1) + ". Expected [" + expected + "] but was [" + MVEL.eval(expression.replace(' ', '.'), dt) + "]")
        }
    }

    /** annoyingly putAll is a mutation method in java. NAUGHTY ! */
    def combine[K, Y](lh: JavaHash[K, Y], rh: JavaHash[K, Y]) = {
        val nh = new JavaHash[K, Y]
        nh putAll lh
        nh putAll rh
        nh
    }

    case class PassFail(pass: Boolean, failureDescription: String)



}

case class ScenarioReport(name: String, failures: Array[String], totalTests: Int) {
  
  override def toString = {
    "\tScenario name: '" + name + "'. Number of tests: " + totalTests + ".\n" + (if (!failures.isEmpty) failures.reduceLeft("\t\t" + _ + "\n\t\t" + _) else "\t\tSUCCESS") 
  }
}

case class WorksheetReport(sheetName: String, scenarioReports: Array[ScenarioReport]) {


  override def toString = {
    "Sheet name: '" + sheetName + "'\n" + scenarioReports.map(_.toString).reduceLeft (_.toString + "\n" + _.toString)
  }



}

