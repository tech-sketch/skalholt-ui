package daos.common

import java.lang.reflect.Field
import daos.common.DaoUtil._
import scala.slick.driver.H2Driver.simple._

abstract class AbstractDao {

  def filter[A, B](condition: Any, q: Query[A, B, Seq]): Query[A, B, Seq] = {

    def filter[A, B](condition: Any, iterator: Iterator[Field], q: Query[A, B, Seq]): Query[A, B, Seq] = {
      if (!iterator.hasNext) q
      else {
        val field = iterator.next
        field.setAccessible(true)
        val colname = field.getName
        val value = getValue(field.get(condition))
        val filds = field.getAnnotations.filter(
          _.annotationType.getPackage.getName == "utils.annotations.searchcondition").headOption

        field.setAccessible(false)
        if (value == null || value == None || value == "" || filds == null) filter(condition, iterator, q)
        else filter(condition, iterator, qfilter(q, colname, value, filds))
      }
    }
    filter(condition, condition.getClass().getDeclaredFields().iterator, q)
  }

}
