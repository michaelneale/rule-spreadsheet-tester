package stest

import junit.framework._
import Assert._
import jxl.{Sheet, Workbook}

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
          val st = getClass getResourceAsStream "TestWorkbook.xls"
          assertNotNull(st)
          val w = Workbook.getWorkbook(st)
          assertNotNull(w)  

          val x = w.getSheets.map(_.toString)
          println(x.size)

          println("woot")
          //for (sheet: Sheet <- w.getSheets) println(sheet)
      }



}

class Data
class Expection
class Scenario(val data: List[Data], val expections: List[Expection])