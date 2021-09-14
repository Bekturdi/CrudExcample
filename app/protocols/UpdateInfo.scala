package protocols
import scala.io.StdIn._

object UpdateInfo {
  def main(args: Array[String]): Unit = {
    val a = readLine()
    getUpdateInfo(a)
  }
  def getUpdateInfo(info: String): List[String] = {
    val strList = info.split(s"[\\s/\\d]").map{ a =>
      println(s"a: $a")
      a.trim.replace(".", "")}.toList
        println(s"strList: $strList")
    strList
  }
}
//1. asfasfasfdf\\n 2. afdasfdasdf\\n 3. asfasfasdfd\\n 4. asfasfasdfasf
