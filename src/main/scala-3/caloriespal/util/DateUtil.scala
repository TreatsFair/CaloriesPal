package caloriespal.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtil:

  val datePattern = "dd.MM.yyyy"
  val dateFormatter = DateTimeFormatter.ofPattern(datePattern)

  extension (date: LocalDate)
    // returns the given date as a well formatted String.
    def asString: String =
      if (date == null) then return null
      return dateFormatter.format(date)
      
    // Checks if the date is a valid date of birth (not in the future)
    def isValidDOB: Boolean =
      date != null && !date.isAfter(LocalDate.now())

  extension (dateString: String)
    def parseLocalDate: LocalDate =
      try
        LocalDate.parse(dateString, dateFormatter)
      catch
        case e: DateTimeParseException => null
    def isValid: Boolean =
      dateString.parseLocalDate != null
      
    // Checks if the string is a valid DOB (correct format and not in the future)
    def isValidDOB: Boolean =
      val date = dateString.parseLocalDate
      date != null && !date.isAfter(LocalDate.now())
