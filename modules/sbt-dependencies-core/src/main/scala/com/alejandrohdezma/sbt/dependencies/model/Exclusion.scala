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

import com.alejandrohdezma.sbt.dependencies.model.Eq._

/** A coordinate kept out of a dependency's transitive graph, as declared by the `exclude` annotation.
  *
  * Three shapes are supported, mirroring the dependency-line separators:
  *   - `org:name` — the artifact name is exact (`isCross = false`).
  *   - `org::name` — the artifact name carries the Scala binary suffix (`isCross = true`).
  *   - `org` — every artifact of that organization (`name = None`).
  */
final case class Exclusion(organization: String, name: Option[String], isCross: Boolean) {

  /** The coordinate as written in the file. Round-trips through [[Exclusion.parse]]. */
  def show: String = name.fold(organization)(n => s"$organization${if (isCross) "::" else ":"}$n")

}

object Exclusion {

  /** Groups: (1) organization, (2) separator?, (3) name? */
  private val regex = """^\s*([^\s:]+)\s*(?:(::?)\s*([^\s:]+)\s*)?$""".r

  /** Parses an exclusion coordinate. Anything that is not `org`, `org:name` or `org::name` is rejected. */
  def parse(coordinate: String): Either[String, Exclusion] = coordinate match {
    case regex(organization, null, _) =>
      Right(Exclusion(organization, None, isCross = false))

    case regex(organization, separator, name) =>
      Right(Exclusion(organization, Some(name), separator === "::"))

    case _ =>
      Left(s"'$coordinate' is not a valid exclusion; expected 'org', 'org:name' or 'org::name'")
  }

  implicit val ExclusionEq: Eq[Exclusion] = (a, b) => a.show === b.show

  implicit val ExclusionOrdering: Ordering[Exclusion] = Ordering.by(_.show)

}
