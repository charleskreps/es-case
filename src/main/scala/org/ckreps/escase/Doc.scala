package org.ckreps.escase

import org.json4s._
import org.json4s.native.Serialization._
import util.{Path, Logging}

/**
 * @author ckreps
 */
object Doc{
  def typeName[T <: Doc : Manifest] = manifest.erasure.getSimpleName.toLowerCase
}
trait Doc {
  self: Product =>

  def typeName = Doc.typeName(Manifest.classType(getClass))

  def json = write(this)(DefaultFormats)

  def post(implicit index:Index){
    val result = index.client.post(
      Path(index.name, typeName),
      json)
    println(result)
  }

  def put(id:String)(implicit index:Index){
    val result = index.client.put(
      Path(index.name, typeName, id),
      json)
    println(result)
  }
}

abstract class DocType[T <: Doc: Manifest] extends Logging{

  def typeName = Doc.typeName(manifest)

  def mapping:Option[String] = None

  def get(id:String)(implicit index:Index){
    index.client.get(
      Path(index.name, typeName, id),
      Callback(info(_), info(_)))
  }

}
