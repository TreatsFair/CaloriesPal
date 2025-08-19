package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import caloriespal.model.User
import caloriespal.model.FoodLog
import caloriespal.util.Database.session

import java.time.LocalDate
import java.time.Period
import caloriespal.util.DateUtil.parseLocalDate
import javafx.scene.text.Text
import java.sql.Date

class DashboardController:

  @FXML private var caloriesTargetLabel: Text = _
  @FXML private var caloriesTakenText: Text = _
  @FXML private var caloriesNeededLabel: Text = _
  @FXML private var bmiLabel: Text = _
  @FXML private var progressCircle: Circle = _
  @FXML private var breakfastCaloriesText: Text = _
  @FXML private var lunchCaloriesText: Text = _
  @FXML private var dinnerCaloriesText: Text = _
  @FXML private var snacksCaloriesText: Text = _

  private val circleCircumference = 2 * Math.PI * 100 // Circle radius is 100

  @FXML def initialize(): Unit =
    User.currentUser.foreach { user =>
      val weight = user.weight.getOrElse(0)
      val height = user.height.getOrElse(0)
      val dob = user.dob.map(_.parseLocalDate).getOrElse(LocalDate.now())
      val age = Period.between(dob, LocalDate.now()).getYears
      val goal = user.goal.getOrElse(0)
      val gender = user.gender.map(_.toLowerCase).getOrElse("")

      // Calculate BMI
      val bmi = if height > 0 then weight / Math.pow(height / 100.0, 2) else 0.0
      val bmiMessage = gender match
        case "female" =>
          if bmi < 18.5 then "Underweight"
          else if bmi < 24.9 then "Normal weight"
          else if bmi < 29.9 then "Overweight"
          else "Obese"
        case _ =>
          if bmi < 18.5 then "Underweight"
          else if bmi < 24.9 then "Normal weight"
          else if bmi < 29.9 then "Overweight"
          else "Obese"

      bmiLabel.setText(f"$bmi%.1f ($bmiMessage)")

      // Calculate BMR based on gender
      val bmr = gender match
        case "female" => 10 * weight + 6.25 * height - 5 * age - 161
        case _        => 10 * weight + 6.25 * height - 5 * age + 5

      // Calculate TDEE (calories needed to maintain weight)
      val tdee = bmr * 1.2 // Assuming little activity level
      
      val today = Date.valueOf(LocalDate.now())
      val logs = FoodLog.findByUserAndDate(user.email, today)
      val breakfastCalories = logs.filter(_.category == "Breakfast").map(_.calories).sum
      val lunchCalories = logs.filter(_.category == "Lunch").map(_.calories).sum
      val dinnerCalories = logs.filter(_.category == "Dinner").map(_.calories).sum
      val snacksCalories = logs.filter(_.category == "Snacks").map(_.calories).sum

      breakfastCaloriesText.setText(f"$breakfastCalories%.0f kcal")
      lunchCaloriesText.setText(f"$lunchCalories%.0f kcal")
      dinnerCaloriesText.setText(f"$dinnerCalories%.0f kcal")
      snacksCaloriesText.setText(f"$snacksCalories%.0f kcal")

      val caloriesTaken = breakfastCalories + lunchCalories + dinnerCalories + snacksCalories
      val caloriesNeeded = tdee - caloriesTaken
      caloriesTargetLabel.setText(f"$tdee%.0f kcal")
      caloriesNeededLabel.setText(f"$caloriesNeeded%.0f kcal")
      caloriesTakenText.setText(f"$caloriesTaken%.0f kcal")

      // Update progress circle
      val progress = Math.min(caloriesTaken / tdee, 1.0)
      progressCircle.setStrokeDashOffset(circleCircumference * (1 - progress))
    }