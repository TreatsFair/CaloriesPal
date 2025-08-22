package caloriespal.model

import scalikejdbc._
import caloriespal.util.Database

case class ExerciseLog(
                        id: Option[Long] = None,
                        userEmail: String,
                        date: java.sql.Date,
                        name: String,
                        category: String,
                        durationMin: Double,
                        calories: Double,
                        notes: Option[String] = None
                      )

object ExerciseLog extends SQLSyntaxSupport[ExerciseLog] with Database {
  override val tableName = "ExerciseLog"

  def apply(rs: WrappedResultSet): ExerciseLog = ExerciseLog(
    id          = Some(rs.long("id")),
    userEmail   = rs.string("userEmail"),
    date        = rs.date("date"),
    name        = rs.string("name"),
    category    = rs.string("category"),
    durationMin = rs.double("durationMin"),
    calories    = rs.double("calories"),
    notes       = rs.stringOpt("notes")
  )

  def initializeTable(): Unit = {
    val exists = DB.getTable(tableName).isDefined
    if (!exists) {
      DB autoCommit { implicit session =>
        sql"""
          CREATE TABLE ExerciseLog (
            id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            userEmail VARCHAR(100) NOT NULL,
            date DATE NOT NULL,
            name VARCHAR(120) NOT NULL,
            category VARCHAR(40) NOT NULL,
            durationMin DOUBLE NOT NULL,
            calories DOUBLE NOT NULL,
            notes VARCHAR(400),
            PRIMARY KEY (id)
          )
        """.execute.apply()
      }
    }
  }

  // --- CRUD

  def insert(log: ExerciseLog)(implicit session: DBSession): Long =
    sql"""
      INSERT INTO ExerciseLog
        (userEmail, date, name, category, durationMin, calories, notes)
      VALUES
        (${log.userEmail}, ${log.date}, ${log.name}, ${log.category},
         ${log.durationMin}, ${log.calories}, ${log.notes})
    """.updateAndReturnGeneratedKey.apply()

  def update(log: ExerciseLog)(implicit session: DBSession): Int =
    sql"""
      UPDATE ExerciseLog SET
        userEmail   = ${log.userEmail},
        date        = ${log.date},
        name        = ${log.name},
        category    = ${log.category},
        durationMin = ${log.durationMin},
        calories    = ${log.calories},
        notes       = ${log.notes}
      WHERE id = ${log.id}
    """.update.apply()

  def delete(id: Long)(implicit session: DBSession): Int =
    sql"DELETE FROM ExerciseLog WHERE id = $id".update.apply()

  // --- Finders

  def findByUserAndDate(email: String, date: java.sql.Date)(implicit session: DBSession): List[ExerciseLog] =
    sql"SELECT * FROM ExerciseLog WHERE userEmail = $email AND date = $date ORDER BY id DESC"
      .map(ExerciseLog(_)).list.apply()

  def listByUserBetween(email: String, from: java.sql.Date, to: java.sql.Date)(implicit session: DBSession): List[ExerciseLog] =
    sql"SELECT * FROM ExerciseLog WHERE userEmail = $email AND date BETWEEN $from AND $to ORDER BY date DESC, id DESC"
      .map(ExerciseLog(_)).list.apply()

  def totalCaloriesByDate(email: String, date: java.sql.Date)(implicit session: DBSession): Double =
    sql"SELECT COALESCE(SUM(calories), 0) AS total FROM ExerciseLog WHERE userEmail = $email AND date = $date"
      .map(_.double("total")).single.apply().getOrElse(0.0)
}
