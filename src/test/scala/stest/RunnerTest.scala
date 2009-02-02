package stest

import junit.framework._
import Assert._
import org.mvel2.MVEL
import java.util.{HashMap => JavaHash}
import jxl.{Sheet, Workbook, Cell}
import scala.xml.XML

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
        val hm = new JavaHash[String, Any]
        hm.put("f",  f)
        assertNotNull(MVEL.eval("f.name='mic'", hm))
        assertNotNull(MVEL.eval("f.age='42'", hm))
        assertEquals("mic", f.name)
        assertEquals(42, f.age)

        println("done")

    }


    def testAtom : Unit = {
        val feed = <feed xmlns="http://www.w3.org/2005/Atom">
                 <title>Example Feed</title>
                 <subtitle>A subtitle.</subtitle>
                 <link href="http://example.org/feed/" rel="self"/>
                 <link href="http://example.org/"/>
                 <updated>2003-12-13T18:30:02Z</updated>
                 <author>
                   <name>John Doe</name>
                   <email>johndoe@example.com</email>
                 </author>
                        { entryItems  }
                 <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>
                </feed>

        
        println(feed)

    }

    def entryItems = {
            <entry>
              <title>Atom-Powered Robots Run Amok</title>
              <link href="http://example.org/2003/12/13/atom03"/>
              <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
              <updated>2003-12-13T18:30:02Z</updated>
              <summary>Some text.</summary>
            </entry>

        
    }





}
