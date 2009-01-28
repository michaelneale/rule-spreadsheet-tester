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

          doSheet(w.getSheets()(0))
      }


      def doSheet(st: Sheet)  = {
          val declarationCells = st.getColumn(0).takeWhile((c: Cell) => c.getContents != "WHEN")
          val dataCells = st getColumn(0) dropWhile(_.getContents != "WHEN") drop(1) takeWhile(_.getContents != "EXPECT")
          val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)


          lazy val dataStartRow = dataCells(0).getRow
          lazy val expectStartRow = expectCells(0).getRow

          //now lets do column 1
          val scenarioData = st getColumn(1) dropWhile(_.getRow <  dataStartRow) takeWhile(_.getRow < expectStartRow - 1)
          val expectationData = st getColumn(1) dropWhile(_.getRow < expectStartRow)

          //do the data
          println("Scenario data: " + scenarioData.map((c: Cell) => c.getContents + ":" + dataCells(c.getRow - dataStartRow).getContents))
          //println(expectationData.map((c: Cell) => c.getContents))
          //println(expectCells.map((c: Cell) => c.getContents))

          val scenario1Input = dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents))
          println(scenario1Input)

          //do the expectations
          //println("Expectation data: " + expectationData.map((c: Cell) => c.getContents + ":" + expectCells(c.getRow - expectStartRow).getContents))
          ""
      }





}

class Data
class Expection
class Scenario(val data: List[Data], val expections: List[Expection])