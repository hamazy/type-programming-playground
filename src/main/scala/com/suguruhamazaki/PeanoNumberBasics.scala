package com.suguruhamazaki

import scala.language.higherKinds

/* http://apocalisp.wordpress.com/2010/06/16/type-level-programming-in-scala-part-4a-peano-number-basics/ */

/* 非負の整数を表わす. */
sealed trait Nat {
  /* 型関数宣言. 1つ目の引数は Nat を型引数にとる型 */
  type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] <: Up

  type Compare[N <: Nat] <: Comparison
}

/* 0 を表わす. */
sealed trait _0 extends Nat {
  /* 型関数の実装. 2つ目の引数をそのまま返す */
  type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] = IfZero

  /* 引数 N が 0 以外であれば ConstLT を返す. 0 であれば EQ を返す. */
  type Compare[N <: Nat] = N#Match[ConstLT, EQ, Comparison]
  type ConstLT[N] = LT
}

/* 0 以外の数を表わす. */
sealed trait Succ[N <: Nat] extends Nat {
  /* 型関数の実装. 1つ目の引数 NonZero に N を適用して返す */
  type Match[NonZero[N <: Nat] <: Up, IfZero <: Up, Up] = NonZero[N]

  /* 引数 O が 0 であれば GT を返す. それ以外であれば N#Comare を返す */
  type Compare[O <: Nat] = O#Match[N#Compare, GT, Comparison]
}

sealed trait _1 extends Succ[_0]
sealed trait _2 extends Succ[_1]
sealed trait _3 extends Succ[_2]
sealed trait _4 extends Succ[_3]

object Foo {
  /* A が _0 であれば, True を返す. A が Succ[N] であれば, ConstFalse
   * に N を適用した ConstFalse[N], すなわち False を返す.
   */
  type Is0[A <: Nat] = A#Match[ConstFalse, True, Bool]
  type ConstFalse[A] = False

  implicitly[Is0[_0] =:= True]
  /* Is0[_0] == _0#Match[ConstFalse, True, Bool]
   *         == True
   */

  implicitly[Is0[Succ[_0]] =:= False]
  /* Is0[Succ[_0]] == Succ[_0]#Match[ConstFalse, True, Bool]
   *               == ConstFalse[_0]
   *               == False
   */

  implicitly[_0#Compare[_0]#eq =:= True]
  /* _0#Compare[_0]#eq == _0#Match[ConstLT, EQ, Comparison]#eq
   *                   == EQ#eq
   *                   == EQ#Match[False, True, False, Bool]
   *                   == True
   */

  implicitly[_0#Compare[_0]#lt =:= False]
  /* _0#Compare[_0]#eq == _0#Match[ConstLT, EQ, Comparison]#lt
   *                   == EQ#lt
   *                   == EQ#Match[True, False, False, Bool]
   *                   == False
   */

  implicitly[_3#Compare[_4]#le =:= True]
  /* _3#Compare[_4]#le == Succ[_2]#Compare[_4]#le
   *                   == _4#Match[_2#Compare, GT, Comparison]#le
   *                   == Succ[_3]#Match[_2#Compare, GT, Comparison]#le
   *                   == _2#Compare[_3]#le
   *                   == Succ[_1]#Compare[_3]#le
   *                   == _3#Match[_1#Compare, GT, Comparison]#le
   *                   == Succ[_2]#Match[_1#Compare, GT, Comparison]#le
   *                   == _1#Compare[_2]#le
   *                   == Succ[_0]#Compare[_2]#le
   *                   == _2#Match[_0#Compare, GT, Comparison]#le
   *                   == _0#Compare[_2]#le
   *                   == _2#Match[ConstLT, EQ, Comparison]#le
   *                   == Succ[_1]#Match[ConstLT, EQ, Comparison]#le
   *                   == ConstLT[_1]#le
   *                   == LT#le
   *                   == LT#Match[True, True, False, Bool]
   *                   == True
   */
  implicitly[_3#Compare[_3]#le =:= True]
  implicitly[_3#Compare[_3]#lt =:= False]
}

/* 比較結果を表わす. */
sealed trait Comparison {
  /* 比較結果が何なのかを取り出す型関数 */
  type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] <: Up

  type gt = Match[False, False, True, Bool]
  type ge = Match[False, True, True, Bool]
  type eq = Match[False, True, False, Bool]
  type le = Match[True, True, False, Bool]
  type lt = Match[True, False, False, Bool]
}

sealed trait GT extends Comparison {
  type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfGT
}

sealed trait EQ extends Comparison {
  type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfEQ
}

sealed trait LT extends Comparison {
  type Match[IfLT <: Up, IfEQ <: Up, IfGT <: Up, Up] = IfLT
}
