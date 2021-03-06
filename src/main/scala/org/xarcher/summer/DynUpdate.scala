package org.xarcher.summer

import slick.ast.BaseTypedType
import slick.driver.JdbcDriver.api._

/**
 * Created by djx314 on 15-6-22.
 */

sealed trait DynData[E, T] {
  type DataType = T
}

case class DynBase[E, T](colTra: E => Rep[T], value: T)(implicit val typedType: BaseTypedType[T]) extends DynData[E, T]

case class DynOpt[E, T](colTra: E => Rep[Option[T]], value: Option[T])(implicit val typedType: BaseTypedType[T]) extends DynData[E, Option[T]]

trait DynUpdate {

  private def dynUpdateAction[E, ColType <: Product, ValType <: Product, Level <: FlatShapeLevel](baseQuery: Query[E, _, Seq])(dataList: List[DynData[E, _]])(hColunms: E => ColType)(hValues: ValType)(implicit shape: Shape[Level, ColType, ValType, ColType]): DBIOAction[Int, NoStream, Effect.Write] = {

    dataList.headOption match {

      case Some(change@DynBase(currentColTran, currentValue)) =>
        import change._
        val colunmHList: E => (Rep[DataType], ColType) = (table: E) => currentColTran(table) -> hColunms(table)
        dynUpdateAction(baseQuery)(dataList.tail)(colunmHList)(currentValue -> hValues)

      case Some(change@DynOpt(currentColTran, currentValue)) =>
        import change._
        val colunmHList: E => (Rep[DataType], ColType) = (table: E) => currentColTran(table) -> hColunms(table)
        dynUpdateAction(baseQuery)(dataList.tail)(colunmHList)(currentValue -> hValues)

      case _ => baseQuery.map(s => hColunms(s)).update(hValues)

    }

  }

  def update[E](baseQuery: Query[E, _, Seq])(dataList: List[DynData[E, _]]): DBIOAction[Int, NoStream, Effect.Write] = {

    dataList.head match {

      case change@DynBase(currentColTran, currentValue) =>
        import change._
        val colunmHList: E => Tuple1[Rep[DataType]] = (table: E) => Tuple1(currentColTran(table))
        dynUpdateAction(baseQuery)(dataList.tail)(colunmHList)(Tuple1(currentValue))

      case change@DynOpt(currentColTran, currentValue) =>
        import change._
        val colunmHList: E => Tuple1[Rep[DataType]] = (table: E) => Tuple1(currentColTran(table))
        dynUpdateAction(baseQuery)(dataList.tail)(colunmHList)(Tuple1(currentValue))

    }

  }

}

object DynUpdate extends DynUpdate