package stest
/**
 * 
 * @author Michael Neale
 */

class SampleFact {
    var name: String = "mike"
    var age: Integer = 0

    def  setName(s: String) : Unit = {name = s}
    def getName = name


    def setAge(i: Integer) : Unit = { age = i }
    def getAge = age

}