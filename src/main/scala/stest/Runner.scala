package stest

/**
 * A test scenario runner that uses XLS spreadsheets to drive tests (each column is a scenario)
 * @author Michael Neale
 */

import java.io.InputStream
import jxl.{Sheet, Workbook, Cell}
import java.util.{HashMap => JavaHash}
import org.mvel2.MVEL

class Runner {

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
        val scenarioColumns = allcols filter((cs: Array[Cell]) => cs(dataStartRow - 1).getContents != "");


        val res = scenarioColumns.map((col: Array[Cell]) => processScenario(col, dataCells, expectCells, facts, globals, dataStartRow, expectStartRow))

        res(0).map(println(_))
        res(1).map(println(_))

        

    }

    
    def processScenario(col: Array[Cell], dataCells: Array[Cell], expectCells: Array[Cell], facts: Seq[Array[String]],  globals: Seq[Array[String]], dataStartRow: Int, expectStartRow: Int) : Array[String] = {
      val factData = createObjects (facts)
      val globalData = createObjects (globals)


      //pump in scenario data
      val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
      val factStore = populateData(dataCells, combine (factData, globalData), scenarioData, dataStartRow)


      //here we fire things up in rules  TODO

      //perform checks
      val expectationData = col dropWhile (_.getRow < expectStartRow)
      expectCells.map((c: Cell) => inspectResult(factStore, c, expectationData(c.getRow - expectStartRow).getContents))
      
    }



    /** Avert your eyes children. Ugly mutable code follows */
    def createObjects(ls: Seq[Array[String]]) : JavaHash[String, Object] = {
        val jh = new JavaHash[String, Object]
        for (pair <- ls)  jh.put(pair(0).trim, MVEL.eval("new " + pair(1)))
        jh
    }

    /** Use MVEL to populate the data for a field */
    def populateData(dataCells: Array[Cell], dt: JavaHash[String, Object], scenarioData: Array[Cell], dataStartRow: Int) = {
      for (c <- dataCells) {
        MVEL.eval(c.getContents.replace(' ', '.') + " = '" + scenarioData(c.getRow - dataStartRow).getContents + "'", dt)
      }
      dt
    }

    /** Use MVEL to inspect the results - return a string report */
    def inspectResult(dt: JavaHash[String, Object], cell: Cell, expected: String)  = {
        val expression = cell.getContents
        if (expression == "" || MVEL.eval(expression.replace(' ', '.') + " == '" + expected + "'", dt).asInstanceOf[Boolean]) {
            "OK"
        } else {
            "Failure"
        }
    }

    /** annoyingly putAll is a mutation method in java. NAUGHTY ! */
    def combine[K, Y](lh: JavaHash[K, Y], rh: JavaHash[K, Y]) = {
        val nh = new JavaHash[K, Y]
        nh putAll lh
        nh putAll rh
        nh
    }




}


