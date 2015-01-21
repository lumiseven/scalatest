/*
 * Copyright 2001-2014 Artima, Inc.
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
package org.scalactic.algebra

import org.scalacheck.Arbitrary
import org.scalactic._

import scala.language.implicitConversions

class ApplicativeSpec extends UnitSpec {

  "List" should "obey the applicative laws" in {
    // implementation for List

    class ListApplicative extends Applicative[List] {
      override def applying[A, B](ca: List[A])(cab: List[(A) => B]): List[B] = ca.flatMap(a => cab.map(ab => ab(a)))

      // a.k.a. pure, point
      override def insert[A](a: A): List[A] = List(a)
    }

    implicit val listApplicative = new ListApplicative
    new ApplicativeLaws[List]().assert()
  }

  "Option" should "obey the applicative laws" in {

    class OptionApplicative extends Applicative[Option] {
      override def applying[A, B](ca: Option[A])(cab: Option[(A) => B]): Option[B] = ca.flatMap(a => cab.map(ab => ab(a)))

      // a.k.a. pure, point
      override def insert[A](a: A): Option[A] = Option(a)
    }

    implicit val optionApplicative = new OptionApplicative

    new ApplicativeLaws[Option]().assert()
  }

  "The good nature of Or" should "obey the applicative laws" in {

    class GoodOrApplicative[Bad] extends Applicative[OrWithBad[Bad]#AndGood] {
      override def applying[A, B](ca: OrWithBad[Bad]#AndGood[A])(cab: OrWithBad[Bad]#AndGood[(A) => B]): OrWithBad[Bad]#AndGood[B] = ca.flatMap(a => cab.map(ab => ab(a)))

      // a.k.a. pure, point
      override def insert[A](a: A): OrWithBad[Bad]#AndGood[A] = Good(a)
    }

    implicit val goodOrApplicative = new GoodOrApplicative[Int]
    implicit def orArbGood[G, B](implicit arbG: Arbitrary[G]): Arbitrary[G Or B] = Arbitrary(for (g <- Arbitrary.arbitrary[G]) yield Good(g))

    new ApplicativeLaws[OrWithBad[Int]#AndGood]().assert()
  }
}
