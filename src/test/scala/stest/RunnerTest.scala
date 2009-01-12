package stest

import junit.framework._
import Assert._
import jxl.{Sheet, Workbook, Cell}

/**
 * 
 * @author Michael Neale
 */

class RunnerTest extends TestCase {
      def testSomething() = {
          val r = new Runner             
          assertEquals(42, r.doTests)
      }

      def testWorkbookLoad() = {
          println ("hey")
          val st = getClass getResourceAsStream("TestWorkbook.xls")
          assertNotNull(st)
          val w = Workbook.getWorkbook(st)
          assertNotNull(w)  

          val x = w.getSheets.map(_.toString)
          println(x.size)

          println("woot")
          val cells = w.getSheets.map(doSheet)
          println(cells(0))
          
          //for (sheet: Sheet <- w.getSheets) println(sheet)
          println("done")
      }


      def doSheet(st: Sheet)  = {
          var count = 0;
          val lst = new Array[Array[Cell]](st.getColumns)
          while (count < st.getColumns) {
              lst(count) = st.getColumn(count)
              count = count + 1
          }
          //lst(0).map((c: Cell) => println(c.getContents))
          lst
      }


}

class Data
class Expection
class Scenario(val data: List[Data], val expections: List[Expection])