package caloriespal.util

import scalikejdbc._
import caloriespal.model.User
import caloriespal.model.FoodLog

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true"

  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "admin", "admin")
  implicit val session: AutoSession.type = AutoSession
}

object Database extends Database {
  def setupDB(): Unit = {
    if (!hasDBInitialized) {
      User.initializeTable()
      FoodLog.initializeTable()
    }
  }

  def hasDBInitialized: Boolean = {
    tableExists("User")
  }

  private def tableExists(tableName: String): Boolean = {
    DB.getTable(tableName).isDefined
  }
}
