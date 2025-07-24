package addressapp.util
import scalikejdbc.*
import scalikejdbc.*
import addressapp.model.User

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:myDB;create=true;"

  // Load the Derby JDBC driver
  Class.forName(derbyDriverClassname)

  // Create a single connection pool for the whole app
  ConnectionPool.singleton(dbURL, "admin", "admin")

  // Provide implicit session for queries
  implicit val session: AutoSession.type = AutoSession
}

// Singleton object for DB setup logic
object Database extends Database {

  def setupDB(): Unit = {
    if (!hasDBInitialized) {
      if (!tableExists("User")) {
        User.initializeTable()
      }
    }
  }

  def hasDBInitialized: Boolean = {
    tableExists("User")
  }

  private def tableExists(tableName: String): Boolean = {
    DB.getTable(tableName).isDefined
  }
}
