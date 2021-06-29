import io.getquill.{PostgresAsyncContext, Escape}
import io.getquill.SnakeCase

package object db
{
    lazy val ctx = new PostgresAsyncContext(SnakeCase, "ctx")
}