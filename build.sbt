val Http4sVersion = "0.23.30"
val CirceVersion = "0.14.14"
val MunitVersion = "1.1.1"
val LogbackVersion = "1.5.20"
val MunitCatsEffectVersion = "2.1.0"
val JansiVersion = "2.4.0"

val SlickMySQLVersion = "8.0.33"
val SlickTypesafeVersion = "3.6.1"
val H2Version = "2.4.240"

// Common settings for all subprojects
lazy val commonSettings = Seq(
  scalaVersion := "3.7.3",
  organization := "nl.sogyo",
  version := "0.1.0",
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case x => (assembly / assemblyMergeStrategy).value.apply(x)
  },
  libraryDependencies ++= Seq(
    "ch.qos.logback"        %   "logback-classic"     % LogbackVersion          % Runtime,
    "org.fusesource.jansi"  %   "jansi"               % JansiVersion            % Runtime,
    "org.scalameta"       %%  "munit"               % MunitVersion            % Test,
    "org.typelevel"       %%  "munit-cats-effect"   % MunitCatsEffectVersion  % Test,
  ),
)

// Define the core project
lazy val apiGateway = (project in file("rotom.api-gateway"))
  .dependsOn(peristence)
  .settings(
    commonSettings,
    name := "api-gateway",
    Compile / run / mainClass := Some("nl.sogyo.apigateway.Main"),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
      "io.circe"        %% "circe-core"           % CirceVersion,
      "io.circe"        %% "circe-generic"        % CirceVersion,
      "io.circe"        %% "circe-literal"        % CirceVersion,
    )
  )

lazy val peristence = (project in file("rotom.persistence"))
  .settings(
    commonSettings,
    name := "persistence",
    libraryDependencies ++= Seq(
      "com.typesafe.slick"  %%  "slick"               % SlickTypesafeVersion,
      "com.mysql"           %   "mysql-connector-j"   % SlickMySQLVersion,
      "com.h2database"      %   "h2"                  % H2Version,
    )
  )

// Define the app project, which depends on core
// lazy val AccountWriter = (project in file("account-writer"))
//     .settings(
//         commonSettings,
//         name := "account-writer"
//     )

lazy val root = (project in file("."))
  .aggregate(apiGateway, peristence)
  .dependsOn(apiGateway, peristence)
  .settings(
    name := "item-lending-library",
    commonSettings,
    publish/skip := true,
    Compile / run / fork := true
  )

Compile / run := (apiGateway / Compile / run).evaluated