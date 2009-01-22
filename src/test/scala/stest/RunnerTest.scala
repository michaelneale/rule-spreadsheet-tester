package stest

import junit.framework._
import Assert._
import jxl.{Sheet, Workbook, Cell}

/**
 * Remember for all test runners to work, must return Unit !
 * Some of them won't work otherwise (eg surefire !).
 * @author Michael Neale
 */
class RunnerTest extends TestCase {



      def testWorkbookLoad() :Unit = {
          println ("hey")
          val st = getClass getResourceAsStream("TestWorkbook.xls")
          assertNotNull(st)
          val w = Workbook.getWorkbook (st)
          assertNotNull(w)  
          w.getSheets.map(doSheet)
      }


      def doSheet(st: Sheet)  = {
          val declarationCells = st.getColumn(0).takeWhile((c: Cell) => c.getContents != "WHEN")
          val dataCells = st getColumn(0) dropWhile(_.getContents != "WHEN") drop(1) takeWhile(_.getContents != "EXPECT")
          val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)


          lazy val dataStartRow = dataCells(0).getRow
          lazy val expectStartRow = expectCells(0).getRow

          //now lets to column 1
          val scenarioData = st getColumn(1) dropWhile(_.getRow <  dataStartRow) takeWhile(_.getRow < expectStartRow - 1)
          val expectationData = st getColumn(1) dropWhile(_.getRow < expectStartRow)

          //do the data
          println(scenarioData.map((c: Cell) => c.getContents + ":" + dataCells(c.getRow - dataStartRow).getContents))

          //do the expectations
          println(expectationData.map((c: Cell) => c.getContents + ":" + dataCells(c.getRow - dataStartRow).getContents))          
          ""
      }





}

class Data
class Expection
class Scenario(val data: List[Data], val expections: List[Expection])