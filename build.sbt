lazy val root = (project in file(".")).
  settings(
    name := "data_mapper",
    version := "1.0",
    scalaVersion := "2.12.7",
    mainClass in Compile := Some("mapper.date.DateMapper")
  )

name := "beacon_data_mapper"
version := "1.0"
scalaVersion := "2.12.7"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.4.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.4.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.4.0"
libraryDependencies += "org.scala-lang" % "scala-parser-combinators" % "2.11.0-M4"

assemblyMergeStrategy in assembly := {
  case PathList("org","apache", xs @ _*) => MergeStrategy.last
  case PathList("org","google", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

    