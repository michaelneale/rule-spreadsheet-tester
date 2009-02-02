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


        val allcols = for (i <- 1 to st.getColumns) yield st.getColumn(i)
        println(allcols(0)(dataStartRow - 1).getContents)
        //val cols = allcols.filter((col: Array[Cell]) => col(dataStartRow))
        //println(cols.size)

        //println(cols(0)(dataStartRow - 1).getContents)



        /*
        for (i <- 1 to st.getColumns) {
            val col = st getColumn(i)
            val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
            val expectationData = col dropWhile (_.getRow < expectStartRow)
            ""
            //TODO filter out unneeded cols
            println(dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents)))
            println(expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != ""))



        }
        */


        ""

    }

}