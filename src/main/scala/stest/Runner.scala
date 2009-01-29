package stest
/**
 * 
 * @author Michael Neale
 */

import java.io.InputStream
import jxl.{Sheet, Workbook, Cell}
class Runner {



    def processSheet(st: Sheet) = {
              
          val declarationCells = st.getColumn(0).takeWhile((c: Cell) => c.getContents != "WHEN")
          val dataCells = st getColumn(0) dropWhile(_.getContents != "WHEN") drop(1) takeWhile(_.getContents != "EXPECT")
          val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)


          val facts = declarationCells.dropWhile(_.getContents.startsWith("Global"))
          val globals = declarationCells.takeWhile(_.getContents.startsWith("Global"))




          lazy val dataStartRow = dataCells(0).getRow
          lazy val expectStartRow = expectCells(0).getRow

          //now lets do column 1
          val scenarioData = st getColumn(1) dropWhile(_.getRow <  dataStartRow) takeWhile(_.getRow < expectStartRow - 1)
          val expectationData = st getColumn(1) dropWhile(_.getRow < expectStartRow)


          val scenario1Input = dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents))
          println(scenario1Input)

          val scenario1Expectations = expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != "")
          println(scenario1Expectations)
          ""
      
    }

}