package com.suguruhamazaki

import scala.language.higherKinds

sealed trait Bool {
  /* 引数を3つとる, 型レベルの関数の宣言 (のようなもの). */
  type If[T <: Up, F <: Up, Up] <: Up
}

sealed trait True extends Bool {
  /* 型レベル関数の実装. 1つ目の引数をそのまま返す. */
  type If[T <: Up, F <: Up, Up] = T
}

sealed trait False extends Bool {
  /* 型レベル関数の実装. 2つ目の引数をそのまま返す. */
  type If[T <: Up, F <: Up, Up] = F
}

object Bool {
  /* Bool の派生型である True or False を2つ受けとる.
   * 
   * A が True なら, True#If[B, False, Bool] は1つ目の引数 B を返すの
   * で, True か False かは B で決まる.
   *
   * A が False なら, False#If[B, False, Bool] は2つ目の引数 False を
   * 返す.
   */
  type &&[A <: Bool, B <: Bool] = A#If[B, False, Bool]
  /* Bool の派生型である True or False を2つ受けとる.
   * 
   * A が True なら, True#If[True, B, Bool] は True を返す.
   * 
   * A が False なら, False#If[True, B, Bool] は B を返すので, True か
   * False かは B で決まる.
   */
  type ||[A <: Bool, B <: Bool] = A#If[True, B, Bool]
  /* Bool の派生型である True or False を1つ受けとる.
   * 
   * A が True なら, True#If[False, True, Bool] は False を返す. A が
   * False なら, False#If[False, True, Bool] は True を返す.
   */
  type Not[A <: Bool] = A#If[False, True, Bool]

  sealed class BoolRep[B <: Bool](val value: Boolean)
  object BoolRep {
    implicit val falseRep: BoolRep[False] = new BoolRep(false)
    implicit val trueRep: BoolRep[True] = new BoolRep(true)
  }

  /* Bool型の派生型 True, False を Boolean 型の値 true, false に変換する.
   * 
   * B が True なら 上の trueRep を捕捉し, False なら falseRep を捕捉
   * する. で, b.value
   */
  def toBoolean[B <: Bool](implicit b: BoolRep[B]): Boolean = b.value
}

object TypeProgrammingPlayground {

  /* Bool の派生型である True or False を1つ受けとる.
   * 
   * A が True なら Int, False なら Long を返す.
   */
  type Rep[A <: Bool] = A#If[Int, Long, AnyVal]

  def main(args: Array[String]): Unit = {

    /* =:=[From, To] は scala.Predef で定義されている.
     * 
     *  =:=[From, To] 型の値は scala.Prefef 内で private で定義されて
     * いる singleton_ のみ.
     * 
     * その singleton_ へは, =：=[From, To] の companion object 内で
     * で定義されている implicit def tpEquals[A]: =:=[A, A] を通じての
     * みアクセスできる. =:=[A, A] なので、From と To が同じ型の場合に
     * のみ有効.
     * 
     * =:=[A, B] のような implicit val はどこにも用意されていないので,
     * そのような implicit を要求すると, コンパイルエラーとなる.
     */
    println(implicitly[Int =:= Int])
    // implicitly[Int =:= Double]

    /* =:=[From, To] と同様に scala.Predef で定義されている. From が To の
     * subtype であることを示す.
     */
    implicitly[Int <:< AnyVal]

    implicitly[Rep[True] =:= Int]
    implicitly[Rep[False] =:= Long]

    import Bool.{ &&, ||, Not, toBoolean }

    implicitly[True && True =:= True]
    implicitly[True && False =:= False]
    implicitly[False && True =:= False]
    implicitly[False && False =:= False]

    implicitly[True || True =:= True]
    implicitly[True || False =:= True]
    implicitly[False || True =:= True]
    implicitly[False || False =:= False]

    implicitly[Not[True] =:= False]
    implicitly[Not[False] =:= True]

    assert(true == toBoolean[True])
    assert(false == toBoolean[False])
  }
}
