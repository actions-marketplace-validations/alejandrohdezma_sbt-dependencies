lazy val myproject = project

lazy val shapes = project

@transient lazy val assertExcludedTransitiveIsGone = taskKey[Unit]("Assert the excluded transitive is not resolved")

assertExcludedTransitiveIsGone := {
  val modules = (myproject / update).value.allModules.map(_.name)

  assert(modules.contains("jackson-databind"), s"jackson-databind should be resolved, got: $modules")

  assert(!modules.contains("jackson-annotations"), s"jackson-annotations should be excluded, got: $modules")
}

@transient lazy val assertExclusionShapes = taskKey[Unit]("Assert each exclude shape becomes its sbt rule")

assertExclusionShapes := {
  val module = (shapes / libraryDependencies).value
    .find(_.name == "json")
    .getOrElse(sys.error("json not found in libraryDependencies"))

  val rules = module.exclusions.map(rule => (rule.organization, rule.name, rule.crossVersion))

  val expected = Vector(
    ("com.google.guava", "*", Disabled(): CrossVersion),
    ("org.typelevel", "cats-core", CrossVersion.binary)
  )

  assert(rules == expected, s"unexpected exclusion rules: $rules")
}
