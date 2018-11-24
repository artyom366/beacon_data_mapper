//lazy val root = (project in file(".")).
//  settings(
//    name := "data_mapper",
//    version := "1.0",
//    scalaVersion := "2.11",
//    mainClass in Compile := Some("mapper.date.DateMapper")
//  )

name := "beacon_data_mapper"
version := "1.0"
scalaVersion := "2.11.0"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.4.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.4.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.4.0"
libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.6.10"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.2.0-SNAP10" % Test
libraryDependencies += "org.scalacheck" % "scalacheck_2.11" % "1.14.0" % Test

assemblyMergeStrategy in assembly := {
  case PathList("org","apache", xs @ _*) => MergeStrategy.last
  case PathList("org","google", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

    