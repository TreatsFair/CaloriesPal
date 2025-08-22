package caloriespal.model

import scalikejdbc._
import caloriespal.util.Database
import scala.util.{Try}

case class User(
                 email: String,
                 password: String,
                 userName: String,
                 height: Option[Int] = None,
                 weight: Option[Int] = None,
                 gender: Option[String] = None,
                 dob: Option[String] = None,
                 location: Option[String] = None,
                 goal: Option[Int] = None
               ) {
  // Check if user exists in DB
  def exists: Boolean = {
    DB readOnly { implicit session =>
      sql"SELECT COUNT(1) FROM Users WHERE email = $email"
        .map(_.int(1)).single.apply().getOrElse(0) > 0
    }
  }

  // Save new or edited user details into database
  def save(): Try[Int] =
    if (!exists) then
      Try(DB.autoCommit { implicit session =>
        sql"""
          INSERT INTO Users (email, password, userName, height, weight, gender, dob, location, goal)
          VALUES ($email, $password, $userName, $height, $weight, $gender, $dob, $location, $goal)
        """.update.apply()
      })
    else
      Try(DB.autoCommit { implicit session =>
        sql"""
          UPDATE Users
          SET
            password = $password,
            userName = $userName,
            height = $height,
            weight = $weight,
            gender = $gender,
            dob = $dob,
            location = $location,
            goal = $goal
          WHERE email = $email
        """.update.apply()
      })
}

object User extends Database {

  var currentUser: Option[User] = None

  // Use a non-reserved table name: "Users"
  def initializeTable(): Unit =
    if !hasTable("Users") then
      DB autoCommit { implicit session =>
        sql"""
          CREATE TABLE Users (
            email VARCHAR(100) PRIMARY KEY,
            password VARCHAR(100),
            userName VARCHAR(100) UNIQUE,
            height INTEGER,
            weight INTEGER,
            gender VARCHAR(20),
            dob VARCHAR(20),
            location VARCHAR(100),
            goal INTEGER
          )
        """.execute.apply()
      }
    else
      // Add missing columns if needed (migration for existing DB)
      addMissingColumns()

  private def hasTable(tableName: String): Boolean =
    DB.getTable(tableName).isDefined

  // Migration: Add missing columns if not present
  private def addMissingColumns(): Unit = {
    val expectedCols = Map(
      "height" -> "INTEGER",
      "weight" -> "INTEGER",
      "gender" -> "VARCHAR(20)",
      "dob" -> "VARCHAR(20)",
      "location" -> "VARCHAR(100)",
      "goal" -> "INTEGER"
    )

    val existingCols = DB readOnly { implicit session =>
      sql"SELECT COLUMNNAME FROM SYS.SYSCOLUMNS WHERE REFERENCEID = (SELECT TABLEID FROM SYS.SYSTABLES WHERE TABLENAME = 'USERS')"
        .map(_.string(1).toLowerCase).list.apply().toSet
    }

    expectedCols.foreach { case (col, colType) =>
      if (!existingCols.contains(col.toLowerCase)) {
        DB autoCommit { implicit session =>
          val alterSql = s"ALTER TABLE Users ADD COLUMN $col $colType"
          SQL(alterSql).execute.apply()
        }
      }
    }
  }

  def findByEmail(email: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM Users WHERE email = $email"
        .map(rs => User(
          rs.string("email"),
          rs.string("password"),
          rs.string("userName"),
          Option(rs.intOpt("height")).flatten,
          Option(rs.intOpt("weight")).flatten,
          Option(rs.stringOpt("gender")).flatten,
          Option(rs.stringOpt("dob")).flatten,
          Option(rs.stringOpt("location")).flatten,
          Option(rs.intOpt("goal")).flatten
        ))
        .single.apply()
    }
  }

  def findByUsername(userName: String): Option[User] = {
    DB readOnly { implicit session =>
      sql"SELECT * FROM Users WHERE userName = $userName"
        .map(rs => User(
          rs.string("email"),
          rs.string("password"),
          rs.string("userName"),
          Option(rs.intOpt("height")).flatten,
          Option(rs.intOpt("weight")).flatten,
          Option(rs.stringOpt("gender")).flatten,
          Option(rs.stringOpt("dob")).flatten,
          Option(rs.stringOpt("location")).flatten,
          Option(rs.intOpt("goal")).flatten
        ))
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