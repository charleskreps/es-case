package org.ckreps.escase

/**
 * Examples...
 *
 * @author ckreps
 */
object Sandbox {

  import TestModel._

  val authors = List(
    Author("1", "James Joyce"),
    Author("2", "Truman Capote"),
    Author("3", "Walt Whitman"),
    Author("4", "Dylan Thomas"),
    Author("5", "Tom Wolfe"))

  val stories = List(
    Story("1", "1", "Ulysses", "Buck Mulligan came from the stairhead, bearing a bowl of lather on which a mirror and a razor lay crossed."),
    Story("2", "2", "In Cold Blood", "The village of holcomb stands on the high wheat plains of western Kansas, a lonesome area that other Kansans call \"out there.\""),
    Story("3", "2", "Breakfast at Tiffany's", "I am always drawn back to places where I have lived, the houses and their neighborhoods."),
    Story("4", "3", "Song of Myself", "I celebrate myself, and sing myself, \nAnd what I assume you shall assume,\nFor every atom belonging to me as good belongs to you."),
    Story("5", "3", "I Sing the Body Electric", "I SING the Body electric;\nThe armies of those I love engirth me, and I engirth them;\nThey will not let me off till I go with them, respond to them,\nAnd discorrupt them, and charge them full with the charge of the Soul."),
    Story("6", "4", "Do not go gentle into that good night", "Do not go gentle into that good night,\nOld age should burn and rave at close of day;\nRage, rage against the dying of the light."),
    Story("7", "5", "The Electric Kool Aid Acid Test", "That's good thinking there, Cool Breeze.  Cool Breeze is a kid with three or four days' beard sitting next to me on the stamped metal bottom of the open back part of a pickup truck."))


  def main(args:Array[String]){

    // Setup new index and type mappings:
    implicit val idx = Elasticsearch("localhost", 9200).index("test")
    idx.create(Settings(5,1))
    idx.putMapping(Story)

    // Index some documents:
    authors.foreach(_.post)
    stories.foreach(_.post)

    // Cleanup:
    idx.delete
  }
}

object TestModel{

  import org.json4s.JsonDSL._
  import org.json4s.native.JsonMethods._

  object Story extends DocType[Story]{
    override lazy val mapping =
      Some(
        compact(
          render(
            "story" ->
              ("_parent" ->
                ("type" -> "author")))))
  }
  case class Story(id:String, authorId:String, title:String, body:String) extends Doc

  object Author extends DocType[Author]
  case class Author(id:String, name:String) extends Doc
}
