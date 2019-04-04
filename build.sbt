name := "SENG302 TEAM 700"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

lazy val myProject = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-core" % "2.3.0.1"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.2"

libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % Test


libraryDependencies += "io.cucumber" % "cucumber-core" % "4.2.0" % Test
libraryDependencies += "io.cucumber" % "cucumber-jvm" % "4.2.0" % Test
libraryDependencies += "io.cucumber" % "cucumber-junit" % "4.2.0" % Test
libraryDependencies += "io.cucumber" % "cucumber-java" % "4.2.0" % Test

libraryDependencies += javaJdbc
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")
testOptions in Test += Tests.Argument("-Dconfig.file=conf/test.conf")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

