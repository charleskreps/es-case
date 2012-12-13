package org.ckreps.escase

import org.eclipse.jetty
import jetty.client.{ContentExchange, HttpClient}
import jetty.io.Buffer
import jetty.http.HttpMethods._
import java.io.ByteArrayInputStream

/**
 * @author ckreps
 */
case class Status(code:Int){
  val isSuccess = code >= 200 && code < 400
  val isFail = !isSuccess
}
case class Content(entity:String)
case class Callback(onStatus:Status => Any,
                    onContent:Content => Any = _ => {})
case class Result(status:Status, content:Content)

case class RestClient(host:String, port:Int){

  private val client = new HttpClient
  client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL)
  client.start

  private val BASE_URI = "http://%s:%d".format(host, port)

  def post(path:String, entity:String):Result = {
    SyncExchange(path, POST, Some(entity)).exec
  }

  def post(path:String, entity:String, callback:Callback){
    AsyncExchange(path, POST, callback, Some(entity)).exec
  }

  def put(path:String, entity:String):Result = {
    SyncExchange(path, PUT, Some(entity)).exec
  }

  def put(path:String, entity:String, callback:Callback){
    AsyncExchange(path, PUT, callback, Some(entity)).exec
  }

  def get(path:String):Result = {
    SyncExchange(path, GET).exec
  }

  def get(path:String, callback:Callback){
    AsyncExchange(path, GET, callback).exec
  }

  def delete(path:String):Result = {
    SyncExchange(path, DELETE).exec
  }

  def delete(path:String, callback:Callback){
    AsyncExchange(path, DELETE, callback).exec
  }

  def head(path:String):Result = {
    SyncExchange(path, HEAD).exec
  }

  def head(path:String, callback:Callback){
    AsyncExchange(path, HEAD, callback).exec
  }

  private abstract class BaseExchange(path:String,
                                      method:String,
                                      entity:Option[String] = None) extends ContentExchange{
    setURL(BASE_URI + path)
    setMethod(method)
    if (entity.isDefined){
      setRequestContentSource(new ByteArrayInputStream(entity.get.getBytes("UTF-8")))
    }
  }

  private case class SyncExchange(path:String,
                                  method:String,
                                  entity:Option[String] = None) extends BaseExchange(path, method, entity){


    def exec:Result = {
      client.send(this)
      waitForDone
      Result(
        Status(getResponseStatus),
        Content(getResponseContent))
    }
  }

  private case class AsyncExchange(path:String,
                                   method:String,
                                   callback:Callback,
                                   entity:Option[String] = None) extends BaseExchange(path, method, entity){

    override def onResponseStatus(version:Buffer,
                                  status:Int,
                                  reason:Buffer){
      callback.onStatus(Status(status))
    }

    override def onResponseContent(content:Buffer){
      callback.onContent(Content(content.toString))
    }

    def exec{
      client.send(this)
    }
  }

}
