es-case
=======

An Elasticsearch client using Scala case classes.

Features
--------

It really doesn't do much yet...currently you can:
* Create and delete an index.
* Create type mappings.
* Create, read and update "flat" case classes with Strings and primitives.


Case Class Usage
----------------

In elasticsearch, each document is a member of some "type".  A type's definition includes things like it's mapping (how the documents fields should be interpreted).  With es-case you use a case class and it's companion object to work with these concepts:
* document = case class
* type = companion object
* mapping = companion object's mapping field

In general the idea is that type-level fields will be defined in companion objects and document-level fields defined by case class instances.  The goal is for case classes to be entirely self-decribing in terms of the form they take when indexed in an Elasticsearch cluster.

Currently to work with es-case both the case class and it's companion need to extend the `Doc` trait and the `DocType` abstract class respectively.  (Like many developers I prefer it when libraries don't require me to extend some base class. But so far this is the cleanest way I've found to give the abstraction what it needs.)  Here is how a "comment" type with a mapping defining an "author" type as it's parent in Elasticsearch might look:

    object Comment extends DocType[Comment] {
      override val mapping = Some('{"comment":{"_parent":{"type":"author"}}}')
    }
    case class Comment(id:String, authorId:String, title:String, body:String) extends Doc

    object Author extends DocType[Author]
    case class Author(id:String, name:String) extends Doc


Methods that operate on types will typically take the companion object instance as a parameter.  Methods operating on document instances use case class instances as type parameters or arguments.  Also, whenever possible, methods are defined directly on case class instances, e.g., a document instance can be created by calling the `post` method on the case class instance representing it.  To illustrate here's an example using Comment and Author that creates an index, type mappings, inserts some documents, and then deletes everything on a locally running Elasticsearch instance:

    import org.ckreps.escase.Elasticsearch

    val authors = List(
      Author("3", "Walt Whitman"),
      Author("4", "Dylan Thomas"))

    val comments = List(      
      Comment("1", "3", "Song of Myself", "I celebrate myself, and sing myself,  And what I assume you shall assume,  For every atom belonging to me as good belongs to you."),
      Comment("2", "4", "Do not go gentle into that good night", "Do not go gentle into that good night, Old age should burn and rave at close of day; Rage, rage against the dying of the light."))

    // Setup new index and type mappings:
    implicit val idx = Elasticsearch("localhost", 9200).index("test")
    idx.create(Settings(5,1))
    idx.putMapping(Comment)

    // Add documents:
    authors.foreach(_.post)
    comments.foreach(_.post)

    // Cleanup:
    idx.delete    

TODO
----

* Async calls should be reworked as part of a provided (but optional) Actor or Future.  Operations should be sync by default.
* Start on a query abstraction (maybe a DSL).  Lots to do here.
* Bulk CRUD operations.
* Cluster-level operations (health checks, metadata, etc)

License
=======

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2012 Charles Kreps

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.




    




