package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import caloriespal.model.User
import caloriespal.model.FoodLog
import caloriespal.model.ExerciseLog
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
  @FXML private var weightLabel: Text = _
  @FXML private var progressCircle: Circle = _
  @FXML private var breakfastCaloriesText: Text = _
  @FXML private var lunchCaloriesText: Text = _
  @FXML private var dinnerCaloriesText: Text = _
  @FXML private var snacksCaloriesText: Text = _
  @FXML private var weightTargetLabel: Text = _
  @FXML private var bmiTargetLabel: Text = _
  @FXML private var caloriesBurntText: Text = _

  @FXML private var proteinCircle: Circle = _
  @FXML private var carbsCircle: Circle = _
  @FXML private var fatCircle: Circle = _

  @FXML private var proteinPercentText: Text = _
  @FXML private var carbsPercentText: Text = _
  @FXML private var fatPercentText: Text = _


  private val circleCircumference = 2 * Math.PI * 100 // Circle radius is 100

  @FXML def initialize(): Unit =
    User.currentUser.foreach { user =>
      val weight = user.weight.getOrElse(0)
      val height = user.height.getOrElse(0)
      val dob = user.dob.map(_.parseLocalDate).getOrElse(LocalDate.now())
      val age = Period.between(dob, LocalDate.now()).getYears
      val gender = user.gender.map(_.toLowerCase).getOrElse("")
      val goalOpt = user.goal

      // If profile is incomplete, set all calorie values to 0
      val profileComplete =
        weight > 0 && height > 0 && age > 0 && (gender == "male" || gender == "female")

      // Set current weight and BMI labels
      val (bmi, bmiMessage) = calculateBMI(weight, height, gender)
      bmiLabel.setText(f"$bmi%.1f ($bmiMessage)")
      weightLabel.setText(f"$weight KG")

      // Set target weight and BMI labels only if goal is specified
      user.goal match
        case Some(targetWeight) =>
          weightTargetLabel.setText(f"$targetWeight KG")
          val (bmiTarget, bmiTargetMessage) = calculateBMI(targetWeight, height, gender)
          bmiTargetLabel.setText(f"$bmiTarget%.1f ($bmiTargetMessage)")
        case None =>
          weightTargetLabel.setText("0")
          bmiTargetLabel.setText("0.0")

      // Calculate BMR and TDEE for current weight
      val bmr =
        if profileComplete then
          gender match
            case "female" => 10 * weight + 6.25 * height - 5 * age - 161
            case "male"   => 10 * weight + 6.25 * height - 5 * age + 5
            case _        => 0.0
        else 0.0
      val tdee = if profileComplete then bmr * 1.2 else 0.0 // Assuming little activity level

      // If goal is specified, calculate calories needed for target weight
      val (caloriesTarget, caloriesNeeded) = goalOpt match
        case Some(targetWeight) if profileComplete =>
          val bmrTarget = gender match
            case "female" => 10 * targetWeight + 6.25 * height - 5 * age - 161
            case "male"   => 10 * targetWeight + 6.25 * height - 5 * age + 5
            case _        => 0.0
          val tdeeTarget = bmrTarget * 1.2
          val needed = if targetWeight < weight then tdeeTarget - 500
          else if targetWeight > weight then tdeeTarget + 500
          else tdeeTarget
          (tdeeTarget, needed)
        case _ =>
          (tdee, tdee)

      // Today’s food logs
      val today = Date.valueOf(LocalDate.now())
      val logs = FoodLog.findByUserAndDate(user.email, today)
      val exerciseLogs = ExerciseLog.findByUserAndDate(user.email, today)

      val breakfastCalories = logs.filter(_.category == "Breakfast").map(_.calories).sum
      val lunchCalories = logs.filter(_.category == "Lunch").map(_.calories).sum
      val dinnerCalories = logs.filter(_.category == "Dinner").map(_.calories).sum
      val snacksCalories = logs.filter(_.category == "Snacks").map(_.calories).sum

      breakfastCaloriesText.setText(f"$breakfastCalories%.0f kcal")
      lunchCaloriesText.setText(f"$lunchCalories%.0f kcal")
      dinnerCaloriesText.setText(f"$dinnerCalories%.0f kcal")
      snacksCaloriesText.setText(f"$snacksCalories%.0f kcal")

      val caloriesTaken = breakfastCalories + lunchCalories + dinnerCalories + snacksCalories
      val caloriesBurnt = exerciseLogs.map(_.calories).sum

      caloriesTakenText.setText(f"$caloriesTaken%.0f kcal")
      caloriesBurntText.setText(f"$caloriesBurnt%.0f kcal")

      val caloriesRemaining = caloriesTarget - caloriesTaken + caloriesBurnt

      caloriesTargetLabel.setText(f"$caloriesTarget%.0f kcal")
      caloriesNeededLabel.setText(f"$caloriesRemaining%.0f kcal")

    { // calories progress circle
      val r = progressCircle.getRadius
      val circumference = 2 * Math.PI * r

      progressCircle.getStrokeDashArray.setAll(java.lang.Double.valueOf(circumference))
      val safeTarget = if (caloriesTarget > 0) caloriesTarget else 1.0
      val progress = math.max(0.0, math.min((caloriesTaken - caloriesBurnt) / safeTarget, 1.0))
      progressCircle.setStrokeDashOffset(circumference * (1 - progress))
      progressCircle.setRotate(-90)
    }

      // ===== MACRO DONUT (StackPane, 3 stacked circles) =====
      // Compute macro fractions
      val proteinTakenG = logs.map(_.protein).sum
      val carbsTakenG = logs.map(_.carbs).sum
      val fatTakenG = logs.map(_.fat).sum
      val totalG = proteinTakenG + carbsTakenG + fatTakenG

      val macros = Seq(
        ("protein", proteinTakenG),
        ("carbs", carbsTakenG),
        ("fat", fatTakenG)
      )
      val macroFracs = if (totalG > 0) macros.map { case (name, g) => (name, g / totalG, g) } else macros.map { case (name, _) => (name, 0.0, 0.0) }
      val sortedMacros = macroFracs.sortBy(-_._2) // Descending by fraction

      // Assign circles by sorted order: largest on bottom, smallest on top
      val circleMap = Map(
        "fat" -> fatCircle,
        "protein" -> proteinCircle,
        "carbs" -> carbsCircle
      )

      var startFrac = 0.0
      sortedMacros.foreach { case (name, frac, grams) =>
        val circle = circleMap(name)
        setSegment(circle, frac, startFrac, gap = 0.02)
        circle.setOpacity(if frac > 0 then 1.0 else 0.0)
        startFrac += frac
      }

      // Set macro percent texts (order does not matter)
      def pct(x: Double) = f"${x * 100}%.0f%%"
      carbsPercentText.setText(s"Carbs: ${pct(macroFracs(1)._2)} — ${carbsTakenG.formatted("%.0f")}g")
      proteinPercentText.setText(s"Protein: ${pct(macroFracs(0)._2)} — ${proteinTakenG.formatted("%.0f")}g")
      fatPercentText.setText(s"Fat: ${pct(macroFracs(2)._2)} — ${fatTakenG.formatted("%.0f")}g")
    }

  private def setSegment(circle: Circle, segFrac: Double, startFrac: Double, gap: Double = 0.01): Unit =
    if circle != null then
      val r = circle.getRadius
      val C = 2 * Math.PI * r
      val segLen = C * math.max(0.0, math.min(segFrac, 1.0))
      val gapLen = C - segLen
      circle.getStrokeDashArray.setAll(
        java.lang.Double.valueOf(segLen),
        java.lang.Double.valueOf(gapLen)
      )
      circle.setRotate(-90)
      circle.setStrokeDashOffset(C * (1 - startFrac))

  private def calculateBMI(weight: Double, height: Int, gender: String): (Double, String) =
    val bmi = if height > 0 then weight / Math.pow(height / 100.0, 2) else 0.0
    val bmiMessage = gender match
      case "female" =>
        if bmi < 18.5 then "Underweight"
        else if bmi < 24.9 then "Normal"
        else if bmi < 29.9 then "Overweight"
        else "Obese"
      case _ =>
        if bmi < 18.5 then "Underweight"
        else if bmi < 24.9 then "Normal"
        else if bmi < 29.9 then "Overweight"
        else "Obese"
    (bmi, bmiMessage)