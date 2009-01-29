package stest

import junit.framework._
import Assert._
import org.mvel2.MVEL
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

          val rt = new Runner
          rt.processSheet(w.getSheets()(0))

          
      }


    

    def testMVEL : Unit = {

        val f = new SampleFact
        val hm = new java.util.HashMap[String, Any]
        hm.put("f",  f)
        assertNotNull(MVEL.eval("f.name='mic'", hm))
        assertNotNull(MVEL.eval("f.age='42'", hm))
        assertEquals("mic", f.name)
        assertEquals(42, f.age)
        println("done")
    }





}
