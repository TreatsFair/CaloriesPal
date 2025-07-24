package addressapp.model

import scalikejdbc._
import addressapp.util.Database
import scala.util.{Try, Success, Failure}

case class User(email: String, password: String, userName: String)

object User extends Database {

  // Create the User table if it doesn't exist
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE User (
          email VARCHAR(100) PRIMARY KEY,
          password VARCHAR(100),
          userName VARCHAR(100)
        )
      """.execute.apply()
    }
  }

  // Find user by email
  def findByEmail(email: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM User WHERE email = $email"
        .map(rs => User(
          rs.string("email"),
          rs.string("password"),
          rs.string("userName")
        )).single.apply()
    }
  }

  // Create a new user
  def create(email: String, password: String, userName: String): Try[Int] = Try {
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO User (email, password, userName)
        VALUES ($email, $password, $userName)
      """.update.apply()
    }
  }
}
