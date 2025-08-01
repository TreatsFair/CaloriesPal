package caloriespal.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtil:

  val DATE_PATTERN = "dd.MM.yyyy"
  val DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN)

  extension (date: LocalDate)
    // returns the given date as a well formatted String.
    def asString: String =
      if (date == null) then return null
      return DATE_FORMATTER.format(date)
      
    // Checks if the date is a valid date of birth (not in the future)
    def isValidDOB: Boolean =
      date != null && !date.isAfter(LocalDate.now())

  extension (dateString: String)
    // converts a String in the format of the defined
    // DATE_PATTERN to a LocalDate object.
    // returns null if the String could not be converted.
    def parseLocalDate: LocalDate =
      try
        LocalDate.parse(dateString, DATE_FORMATTER)
      catch
        case e: DateTimeParseException => null
    def isValid: Boolean =
      dateString.parseLocalDate != null
      
    // Checks if the string is a valid DOB (correct format and not in the future)
    def isValidDOB: Boolean =
      val date = dateString.parseLocalDate
      date != null && !date.isAfter(LocalDate.now())
