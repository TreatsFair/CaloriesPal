package caloriespal.model

import scalikejdbc._

case class FoodLog(
                    id: Option[Long] = None,
                    userEmail: String,
                    category: String,
                    foodName: String,
                    calories: Double,
                    protein: Double,
                    carbs: Double,
                    fat: Double,
                    logDate: java.sql.Date
                  )

object FoodLog extends SQLSyntaxSupport[FoodLog] {
  override val tableName = "FoodLog"
  def apply(rs: WrappedResultSet): FoodLog = FoodLog(
    Some(rs.long("id")),
    rs.string("userEmail"),
    rs.string("category"),
    rs.string("foodName"),
    rs.double("calories"),
    rs.double("protein"),
    rs.double("carbs"),
    rs.double("fat"),
    rs.date("logDate")
  )

  def insert(foodLog: FoodLog)(implicit session: DBSession): Long = {
    sql"""
      INSERT INTO FoodLog (userEmail, category, foodName, calories, protein, carbs, fat, logDate)
      VALUES (${foodLog.userEmail}, ${foodLog.category}, ${foodLog.foodName},
              ${foodLog.calories}, ${foodLog.protein}, ${foodLog.carbs}, ${foodLog.fat},
              ${foodLog.logDate})
    """.updateAndReturnGeneratedKey.apply()
  }

  def findByUser(email: String)(implicit session: DBSession): List[FoodLog] = {
    sql"SELECT * FROM FoodLog WHERE userEmail = $email"
      .map(FoodLog(_)).list.apply()
  }

  def findByUserAndDate(email: String, date: java.sql.Date)(implicit session: DBSession): List[FoodLog] = {
    sql"SELECT * FROM FoodLog WHERE userEmail = $email AND logDate = $date"
      .map(FoodLog(_)).list.apply()
  }

  def findByUserBetweenDates(email: String, from: java.sql.Date, to: java.sql.Date)(implicit session: DBSession): List[FoodLog] = {
    sql"SELECT * FROM FoodLog WHERE userEmail = $email AND logDate BETWEEN $from AND $to"
      .map(FoodLog(_)).list.apply()
  }

  def clearForUserAndDate(userEmail: String, date: java.sql.Date)(implicit session: DBSession): Unit = {
    sql"DELETE FROM FoodLog WHERE userEmail = $userEmail AND logDate = $date".update.apply()
  }

  def initializeTable(): Unit = {
    import scalikejdbc._
    val tableExists = DB.getTable("FoodLog").isDefined
    if (!tableExists) {
      DB autoCommit { implicit session =>
        sql"""
          CREATE TABLE FoodLog (
            id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            userEmail VARCHAR(100),
            category VARCHAR(20),
            foodName VARCHAR(100),
            calories DOUBLE,
            protein DOUBLE,
            carbs DOUBLE,
            fat DOUBLE,
            logDate DATE,
            PRIMARY KEY (id)
          )
        """.execute.apply()
      }
    }
  }
}
