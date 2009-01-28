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


          val scenario1Input = dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents))
          println(scenario1Input)

          val scenario1Expectations = expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != "")
          println(scenario1Expectations)
          ""
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
