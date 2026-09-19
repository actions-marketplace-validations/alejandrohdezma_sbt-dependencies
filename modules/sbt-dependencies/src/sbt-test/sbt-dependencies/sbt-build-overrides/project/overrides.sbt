libraryDependencySchemes += "org.typelevel" %% "cats-core" % "strict"

@transient lazy val assertBuildOverride = taskKey[Unit]("Assert the `sbt-build` override reaches the meta-build")

assertBuildOverride := {
  update.value

  val overrides = dependencyOverridesFromFile.value

  val catsCore = overrides
    .find(_.name.startsWith("cats-core"))
    .getOrElse(sys.error(s"cats-core override not found, got: $overrides"))

  assert(
    catsCore.name == s"cats-core_${scalaBinaryVersion.value}",
    s"the override name should be concrete, got: ${catsCore.name}"
  )

  assert(
    catsCore.crossVersion == CrossVersion.disabled,
    s"a concrete override should not be cross-versioned again, got: ${catsCore.crossVersion}"
  )
}
