// Common settings for all subprojects
val MunitVersion = "1.1.1"
val MunitCatsEffectVersion = "2.1.0"
val LogbackVersion = "1.5.20"
val JansiVersion = "2.4.0"
val ScalaLoggingVersion = "3.9.5"

lazy val commonSettings = Seq(
  scalaVersion := "3.7.3",
  organization := "nl.sogyo",
  version := "0.1.0",
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case x => (assembly / assemblyMergeStrategy).value.apply(x)
  },
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion % Runtime,
    "org.fusesource.jansi" % "jansi" % JansiVersion % Runtime,
    "org.scalameta" %% "munit" % MunitVersion % Test,
    "org.typelevel" %% "munit-cats-effect" % MunitCatsEffectVersion % Test,
  ),
)

// Other dependency packages
val Http4sVersion = "0.23.30"

lazy val Http4sDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-ember-server" % Http4sVersion,
    "org.http4s" %% "http4s-ember-client" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion,
  )
)

val CirceVersion = "0.14.14"

lazy val CirceDependencies = Seq(
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core" % CirceVersion,
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-literal" % CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion,
  )
)

val SlickMySQLVersion = "8.0.33"
val SlickTypesafeVersion = "3.6.1"
val H2Version = "2.4.240"

lazy val SlickDependencies = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % SlickTypesafeVersion,
    "com.mysql" % "mysql-connector-j" % SlickMySQLVersion,
    "com.h2database" % "h2" % H2Version,
  )
)

val KafkaClientsVersion = "4.1.1"

lazy val KafkaDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.apache.kafka" % "kafka-clients" % KafkaClientsVersion,
  )
)

// Define root project and subprojects
lazy val persistence = (project in file("rotom.persistence"))
  .settings(
    commonSettings,
    name := "persistence",
    SlickDependencies,
    CirceDependencies,
    Http4sDependencies,
  )

lazy val kafka = (project in file("rotom.kafka"))
  .dependsOn(persistence)
  .settings(
    commonSettings,
    name := "kafka",
    KafkaDependencies,
    CirceDependencies
  )

lazy val eventProjector = (project in file("rotom.event-projector"))
  .dependsOn(persistence, kafka)
  .settings(
    commonSettings,
    name := "event-projector",
    Compile / run / mainClass := Some("nl.sogyo.projector.app"),
    Http4sDependencies,
    KafkaDependencies,
    CirceDependencies
  )

lazy val apiGateway = (project in file("rotom.api-gateway"))
  .dependsOn(persistence, kafka)
  .settings(
    commonSettings,
    name := "api-gateway",
    Compile / run / mainClass := Some("nl.sogyo.apigateway.Main"),
    Http4sDependencies,
    CirceDependencies,
  )

lazy val root = (project in file("."))
  .aggregate(apiGateway, persistence, kafka)
  .dependsOn(apiGateway, persistence, kafka)
  .settings(
    name := "item-lending-library",
    commonSettings,
    publish/skip := true,
    Compile / run / fork := true
  )

Compile / run := (apiGateway / Compile / run).evaluated