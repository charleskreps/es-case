package org.ckreps.escase

import util.{Path, Logging}

/*
 * @author ckreps
 */
case class Elasticsearch(host:String, port:Int) extends Logging {

  val client = RestClient(host, port)

  def index(name:String) = Index(this, name)
}

case class Settings(shards:Int, replicas:Int){

  import org.json4s.JsonDSL._
  import org.json4s.native.JsonMethods._

  lazy val json =
    compact(
      render(
        "settings" ->
          ("number_of_shards" -> shards) ~
            ("number_of_replicas" -> replicas)))
}

case class Index(es:Elasticsearch, name:String) extends Logging{

  val client = es.client

  private val MAPPING = "_mapping"

  def create(settings:Settings){
    client.put(Path(name), settings.json, Callback(info(_), info(_)))
  }

  def delete{
    client.delete(Path(name), Callback(info(_), info(_)))
  }

  def exists:Boolean = {
    client.head(Path(name)).status.isSuccess
  }

  def putMapping[T <: Doc](docType:DocType[T]){
    if (docType.mapping.isDefined){
      client.put(Path(name, docType.typeName, MAPPING), docType.mapping.get, Callback(info(_), info(_)))
    }
  }

  def getMapping[T <: Doc](docType:DocType[T]){
    client.get(Path(name, docType.typeName, MAPPING), Callback(info(_), info(_)))
  }

}
