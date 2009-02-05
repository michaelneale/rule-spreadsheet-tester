package stest

/**
 *
 * @author Michael Neale
 */

import java.io.InputStream
import jxl.{Sheet, Workbook, Cell}
import java.util.{HashMap => JavaHash}

class Runner {
    def processSheet(st: Sheet) = {
        val declarationCells = st.getColumn(0).takeWhile(_.getContents != "WHEN")
        val dataCells = st getColumn (0) dropWhile (_.getContents != "WHEN") drop (1) takeWhile (_.getContents != "EXPECT")
        val expectCells = st.getColumn(0).dropWhile(_.getContents != "EXPECT").drop(1)


        val facts = declarationCells.filter(_.getContents startsWith("Fact")).map(_.getContents.substring(4).split(":"))
        val globals = declarationCells.filter(_.getContents startsWith("Global")).map(_.getContents.substring(6).split(":"))
        println(facts(0)(0))      //name
        println(facts(0)(1))      //value



        lazy val dataStartRow = dataCells(0).getRow
        lazy val expectStartRow = expectCells(0).getRow

        //turn it into a list - thankfully scala is nice and lazy about this
        val allcols = for (i <- 1 to (st.getColumns - 1)) yield st.getColumn(i)

        //ok here are our scenarios, filtering it down
        val scenarioColumns = allcols filter((cs: Array[Cell]) => cs(dataStartRow - 1).getContents != "");






        for (col <- scenarioColumns) {
            //load up MVEL with data
            val factData = createObjects(facts)
            val globalData = createObjects(globals)


            val scenarioData = col dropWhile (_.getRow < dataStartRow) takeWhile (_.getRow < expectStartRow - 1)
            val expectationData = col dropWhile (_.getRow < expectStartRow)

            //pump in scenario data
            println(dataCells.map((c: Cell) => (c.getContents.replace(' ', '.'), scenarioData(c.getRow - dataStartRow).getContents)))

            //perform checks
            println(expectCells.map((c: Cell) => ((c.getContents.replace(' ', '.')), expectationData(c.getRow - expectStartRow).getContents)).filter(_._1 != ""))

            //and we return the result - one per scenario
        }


        ""

    }

    /** Avert your eyes children. Ugly mutable code follows */
    def createObjects(ls: Seq[Array[String]]) : JavaHash[String, Object] = {
        import org.mvel2.MVEL
        val jh = new JavaHash[String, Object]
        for (pair <- ls)  jh.put(pair(0).trim, MVEL.eval("new " + pair(1)))
        jh
    }

    

    

}