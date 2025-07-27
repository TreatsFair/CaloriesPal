package caloriespal.model

import scalikejdbc._
import caloriespal.util.Database
import scala.util.{Try, Success, Failure}

case class User(email: String, password: String, userName: String)

object User extends Database {

  // Use a non-reserved table name: "Users"
   def initializeTable(): Unit =
    if !hasTable("Users") then
      DB autoCommit { implicit session =>
        sql"""
          CREATE TABLE Users (
            email VARCHAR(100) PRIMARY KEY,
            password VARCHAR(100),
            userName VARCHAR(100) UNIQUE
          )
        """.execute.apply()
      }

  private def hasTable(tableName: String): Boolean =
    DB.getTable(tableName).isDefined

  def findByEmail(email: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM Users WHERE email = $email"
        .map(rs => User(rs.string("email"), rs.string("password"), rs.string("userName")))
        .single.apply()
    }
  }

  def findByUsername(userName: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM Users WHERE userName = $userName"
        .map(rs => User(rs.string("email"), rs.string("password"), rs.string("userName")))
        .single.apply()
    }
  }

  def create(email: String, password: String, userName: String): Try[Int] = Try {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO Users (email, password, userName)
        VALUES ($email, $password, $userName)
      """.update.apply()
    }
  }
}
