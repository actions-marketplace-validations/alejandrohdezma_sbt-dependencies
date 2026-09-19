ThisBuild / resolvers += "Test Repo" at ((ThisBuild / baseDirectory).value / "repo").toURI.toString

lazy val inherited = project

lazy val merged = project

lazy val wildcard = project

def jacksonDatabind(modules: Seq[ModuleID]): ModuleID =
  modules.find(_.name == "jackson-databind").getOrElse(sys.error("jackson-databind not found"))

@transient lazy val assertInheritedFromBom = taskKey[Unit]("Assert a BOM entry's exclusions reach the consumer")

assertInheritedFromBom := {
  val module = jacksonDatabind((inherited / libraryDependencies).value)

  assert(module.revision == "2.17.2", s"jackson-databind should resolve to 2.17.2, got: ${module.revision}")

  val rules = module.exclusions.map(rule => (rule.organization, rule.name))

  assert(rules == Vector(("com.fasterxml.jackson.core", "jackson-annotations")), s"unexpected exclusions: $rules")

  val modules = (inherited / update).value.allModules.map(_.name)

  assert(!modules.contains("jackson-annotations"), s"jackson-annotations should be excluded, got: $modules")
}

@transient lazy val assertMergedWithOwnExclusions = taskKey[Unit]("Assert the line's own exclusions are kept too")

assertMergedWithOwnExclusions := {
  val rules = jacksonDatabind((merged / libraryDependencies).value).exclusions.map(_.organization)

  assert(
    rules == Vector("com.google.guava", "com.fasterxml.jackson.core"),
    s"the line's own exclusions should be merged with the BOM's, got: $rules"
  )
}

@transient lazy val assertWildcardBecomesIntransitive = taskKey[Unit]("Assert a *:* exclusion becomes intransitive")

assertWildcardBecomesIntransitive := {
  val module = (wildcard / libraryDependencies).value
    .find(_.name == "json")
    .getOrElse(sys.error("json not found in libraryDependencies"))

  assert(!module.isTransitive, "a *:* exclusion in the BOM should make the module intransitive")

  assert(module.exclusions.isEmpty, s"no exclusion rule should be emitted, got: ${module.exclusions}")
}
