package com.suguruhamazaki.genrecur

import scala.language.higherKinds

sealed trait Nat {
  type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] <: Type
}

trait Fold[-Elem, Value] {
  type Apply[N <: Elem, Acc <: Value] <: Value
}

sealed trait _0 extends Nat {
  type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = Init
}

sealed trait Succ[N <: Nat] extends Nat {
  type FoldR[Init <: Type, Type, F <: Fold[Nat, Type]] = F#Apply[Succ[N], N#FoldR[Init, Type, F]]
}

object Foo {

  type Add[A <: Nat, B <: Nat] = A#FoldR[B, Nat, Inc]
  type Inc = Fold[Nat, Nat] {
    type Apply[N <: Nat, Acc <: Nat] = Succ[Acc]
  }
  type _1 = Succ[_0]
  type _2 = Succ[_1]
  type _3 = Succ[_2]
  type _4 = Succ[_3]

  implicitly[Add[_0, _1] =:= _1]
  /* Add[_0, _1] == _0#FoldR[_1, Nat, Inc]
   *             == _1
   */

  implicitly[Add[_1, _0] =:= _1]
  /* Add[_1, _0] == _1#FoldR[_0, Nat, Inc]
   *             == Inc#Apply[Succ[_0], _0#FoldR[_0, Nat, Inc]]
   *             == Inc#Apply[Succ[_0], _0]
   *             == Succ[_0]
   */

  implicitly[Add[_2, _1] =:= _3]
  /* Add[_2, _1] == _2#FoldR[_1, Nat, Inc]
   *             == Inc#Apply[Succ[_1], _1#FoldR[_1, Nat, Inc]]
   *             == Inc#Apply[Succ[_1], Succ[_0]#FoldR[_1, Nat, Inc]]
   *             == Inc#Apply[Succ[_1], Inc#Apply[Succ[_0], _0#FoldR[_1, Nat, Inc]]]
   *             == Inc#Apply[Succ[_1], Inc#Apply[Succ[_0], _1]]
   *             == Inc#Apply[Succ[_1], Succ[_1]]
   *             == Inc#Apply[Succ[_1], _2]
   *             == Succ[_2]
   *             == _3
   */

  type Mult[A <: Nat, B <: Nat] = A#FoldR[_0, Nat, Sum[B]]
  type Sum[By <: Nat] = Fold[Nat, Nat] {
    type Apply[N <: Nat, Acc <: Nat] = Add[By, Acc]
  }

  // implicitly[Mult[_0, _3] =:= _0]
  // implicitly[Mult[_1, _3] =:= _3]
  // implicitly[Mult[_2, _2] =:= _4]

  type Fact[A <: Nat] = A#FoldR[_1, Nat, Prod]
  type Prod = Fold[Nat, Nat] {
    type Apply[N <: Nat, Acc <: Nat] = Mult[N, Acc]
  }
  // implicitly[Fact[_3] =:= Succ[Succ[_4]]]

  type Exp[A <: Nat, B <: Nat] = B#FoldR[_1, Nat, ExpFold[A]]
  type ExpFold[By <: Nat] = Fold[Nat, Nat] {
    type Apply[N <: Nat, Acc <: Nat] = Mult[By, Acc]
  }
  // implicitly[Exp[_2, _2] =:= _4]

  type Sq[N <: Nat] = Exp[N, _2]
  // implicitly[Sq[Sq[_3]] =:= Add[_1, Mult[Exp[_2, _3], Add[_2, Exp[_2, _3]]]]]
}
