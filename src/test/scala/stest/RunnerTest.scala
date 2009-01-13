package stest

import junit.framework._
import Assert._
import jxl.{Sheet, Workbook, Cell}

/**
 * 
 * @author Michael Neale
 */

class RunnerTest extends TestCase {


      def testWorkbookLoad() = {
          println ("hey")
          val st = getClass getResourceAsStream("TestWorkbook.xls")
          assertNotNull(st)
          val w = Workbook.getWorkbook(st)
          assertNotNull(w)  
          w.getSheets.map(doSheet)
      }


      def doSheet(st: Sheet)  = {
          val declarationCells = st.getColumn(0).takeWhile((c: Cell) => c.getContents != "WHEN")
          val dataCells = st getColumn(0) dropWhile(_.getContents != "WHEN") drop(1) takeWhile(_.getContents != "EXPECT")
          val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)

          println(declarationCells.size)
          println(dataCells.size)
          println(expectCells.size)
          ""
      }





}

class Data
class Expection
class Scenario(val data: List[Data], val expections: List[Expection])