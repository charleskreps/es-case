package org.ckreps.escase.util

/**
 * @author ckreps
 */
object Path {

  val DELIMITER = "/"

  def /(seg:String) = Path(seg :: Nil)

  def apply(segments:List[String]):String = new Path(segments).toString

  def apply(segments:String*):String = apply(segments.toList)
}

class Path(segments:List[String]) {

  import Path._

  def /(seg:String) = Path(seg :: segments)

  override def toString = {
    segments.addString(
      new StringBuilder(DELIMITER),
      DELIMITER).mkString
  }
}
