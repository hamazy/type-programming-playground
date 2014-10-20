package com.suguruhamazaki

sealed trait HList

final case class HCons[H, T <: HList](head: H, tail: T) extends HList {
  def ::[T](v: T) = HCons(v, this)
}

sealed class HNil extends HList {
  def ::[T](v: T) = HCons(v, this)
}

object HNil extends HNil

// aliases for building HList types and for pattern matching
object HList {
  type ::[H, T <: HList] = HCons[H, T]
  val :: = HCons
}

object Foo {
  import HList.::
  val f: (String :: Boolean :: Double :: HNil) => String = {
    case "s" :: false :: _ => "test"
    case h :: true :: 1.0 :: HNil => h
    // compilation error because of individual type mismatches and length mismatch
    // case 3 :: "i" :: HNil => "invalid"
    case _ => error("unknown")
  }
}
