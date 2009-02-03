package stest

/**
 *
 * @author Michael Neale
 */

import java.io.InputStream
import jxl.{Sheet, Workbook, Cell}

class Runner {
    def processSheet(st: Sheet) = {
        val declarationCells = st.getColumn(0).takeWhile(_.getContents != "WHEN")
        val dataCells = st getColumn (0) dropWhile (_.getContents != "WHEN") drop (1) takeWhile (_.getContents != "EXPECT")
        val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)


        val facts = declarationCells.dropWhile(_.getContents.startsWith("Global"))
        val globals = declarationCells.takeWhile(_.getContents.startsWith("Global"))

        lazy val dataStartRow = dataCells(0).getRow
        lazy val expectStartRow = expectCells(0).getRow

        //turn it into a list - thankfully scala is nice and lazy about this
        val allcols = for (i <- 1 to (st.getColumns - 1)) yield st.getColumn(i)

        //ok here are our scenarios
        val scenarioColumns = allcols.filter((cs: Array[Cell]) => cs(dataStartRow - 1).getContents != "");






        for (col <- scenarioColumns) {
            val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
            val expectationData = col dropWhile (_.getRow < expectStartRow)
            println(dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents)))
            println(expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != ""))
        }


        ""

    }

}