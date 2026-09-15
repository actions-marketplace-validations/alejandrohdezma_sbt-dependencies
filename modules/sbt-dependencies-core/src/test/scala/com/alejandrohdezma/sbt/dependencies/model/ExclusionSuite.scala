/*
 * Copyright 2025-2026 Alejandro Hernández <https://github.com/alejandrohdezma>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alejandrohdezma.sbt.dependencies.model

class ExclusionSuite extends munit.FunSuite {

  test("parse reads an exact artifact coordinate") {
    val result = Exclusion.parse("com.google.protobuf:protobuf-java")

    assertEquals(result, Right(Exclusion("com.google.protobuf", Some("protobuf-java"), isCross = false)))
  }

  test("parse reads a cross-compiled artifact coordinate") {
    val result = Exclusion.parse("org.typelevel::cats-core")

    assertEquals(result, Right(Exclusion("org.typelevel", Some("cats-core"), isCross = true)))
  }

  test("parse reads an organization-only coordinate") {
    val result = Exclusion.parse("com.google.protobuf")

    assertEquals(result, Right(Exclusion("com.google.protobuf", None, isCross = false)))
  }

  test("parse trims surrounding whitespace") {
    val result = Exclusion.parse("  com.google.protobuf : protobuf-java  ")

    assertEquals(result, Right(Exclusion("com.google.protobuf", Some("protobuf-java"), isCross = false)))
  }

  test("parse rejects a coordinate with a version") {
    val result = Exclusion.parse("com.google.protobuf:protobuf-java:4.0.0")

    assertEquals(
      result,
      Left(
        "'com.google.protobuf:protobuf-java:4.0.0' is not a valid exclusion; expected 'org', 'org:name' or 'org::name'"
      )
    )
  }

  test("parse rejects an empty coordinate") {
    assert(Exclusion.parse("").isLeft)
  }

  test("show round-trips every supported shape") {
    val coordinates = List("com.google.protobuf", "com.google.protobuf:protobuf-java", "org.typelevel::cats-core")

    coordinates.foreach { coordinate =>
      assertEquals(Exclusion.parse(coordinate).map(_.show), Right(coordinate))
    }
  }

}
