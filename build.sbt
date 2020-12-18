name := "SharingNotes"
scalaVersion := "2.12.12"

crossScalaVersions := List("2.12.12", "2.11.12")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test

libraryDependencies += "org.apache.tika" % "tika-core" % "1.24"
libraryDependencies += "org.apache.tika" % "tika-parsers" % "1.24"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0"
libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0"

resolvers += Resolver.bintrayRepo("hmrc", "releases")
libraryDependencies += "uk.gov.hmrc" %% "emailaddress" % "3.5.0"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "8.7.0"
libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "8.7.0"
libraryDependencies += "org.apache.lucene" % "lucene-facet" % "8.7.0"
