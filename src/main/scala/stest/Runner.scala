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

        for (col <- scenarioColumns) {
            //load up MVEL with data
            val factData = createObjects (facts)
            val globalData = createObjects (globals) 
            val allData = combine (factData, globalData)

            //pump in scenario data
            val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
            dataCells.map((c: Cell) => populateData(allData, c.getContents, scenarioData(c.getRow - dataStartRow).getContents))
            

            //here we fire things up in rules  TODO

            //perform checks
            val expectationData = col dropWhile (_.getRow < expectStartRow)
            println(expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != ""))

            //and we return the result - one per scenario
        }


        ""

    }



    /** Avert your eyes children. Ugly mutable code follows */
    def createObjects(ls: Seq[Array[String]]) : JavaHash[String, Object] = {
        val jh = new JavaHash[String, Object]
        for (pair <- ls)  jh.put(pair(0).trim, MVEL.eval("new " + pair(1)))
        jh
    }

    def populateData(dt: JavaHash[String, Object], expression: String, value: String) = {
        MVEL.eval(expression.replace(' ', '.') + " = '" + value + "'")
        true
    }

    def combine[K, Y](lh: JavaHash[K, Y], rh: JavaHash[K, Y]) = {
        val nh = new JavaHash[K, Y]
        nh putAll lh
        nh putAll rh
        nh
    }

    

}