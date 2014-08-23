package com.suguruhamazaki

import scala.language.higherKinds

trait Recurse {
  // abstract type member
  type Next <: Recurse

  /* 型レベルの関数を宣言するようなもの.
   * Recurse の派生型を引数に取り, 新しい型を生成する.
   */
  type X[R <: Recurse]
}

trait RecurseA extends Recurse {
  // override Next with RecurseA
  type Next = RecurseA

  // 型レベルの関数の実装. 引数で受け取った R を使い, 新しい型
  // R#X[R#Next] を生成する.
  type X[R <: Recurse] = R#X[R#Next]
}

object Recurse {

  /* RecurseA が持つ型レベルの関数 X に, RecurseA を引数として渡す.
   * 
   * 以下はコンパイルが終了せず, コンパイル時に
   * java.lang.StackOverflowError が発生する.
   */
  // type C = RecurseA#X[RecurseA]

  /*
   *  RecurseA#X[RecurseA]
   *  == RecurseA#X[RecurseA#Next]
   *  == RecurseA#X[RecurseA]
   *  == ...
   */
}
