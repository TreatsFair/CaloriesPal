package caloriespal.view

import caloriespal.model.User
import javafx.fxml.FXML
import javafx.scene.control.{Alert, Button, ButtonType, DatePicker, ListCell, ListView, TextField}
import javafx.scene.layout.{HBox, Priority, Region}
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}
import javafx.scene.text.Text
import javafx.geometry.Pos

import java.time.LocalDate
import java.sql.Date
import caloriespal.model.FoodLog

import scala.jdk.CollectionConverters.*
import java.io.InputStreamReader
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.application.Platform
import scalikejdbc.DB
import scalikejdbc.AutoSession
import caloriespal.util.Database.session
import javafx.scene.control.Alert.AlertType // <-- Add this import for implicit session

class FoodLogController {

  @FXML var foodSearchList: ListView[String] = _
  @FXML private var breakfastList: ListView[String] = _
  @FXML private var lunchList: ListView[String] = _
  @FXML private var dinnerList: ListView[String] = _
  @FXML private var snacksList: ListView[String] = _
  @FXML private var searchField: TextField = _
  @FXML private var addBreakfastBtn: Button = _
  @FXML private var addLunchBtn: Button = _
  @FXML private var addDinnerBtn: Button = _
  @FXML private var addSnacksBtn: Button = _
  @FXML private var saveButton: Button = _
  @FXML private var removeButton: Button = _
  @FXML private var datePicker: DatePicker = _

  case class FoodItem(
                       name: String,
                       category: String,
                       calories: Double,
                       protein: Double,
                       carbs: Double,
                       fat: Double
                     )

  private var allFoodItems: List[FoodItem] = List.empty
  private var foodNames: List[String] = List.empty

  def initialize(): Unit = {
    loadFoodData()
    setupSearchField()
    setupAddButtons()
    setupAutoResizeListViews()
    setupSaveButton()
    setupRemoveButton()
    setupListViewCellFactory(breakfastList)
    setupListViewCellFactory(lunchList)
    setupListViewCellFactory(dinnerList)
    setupListViewCellFactory(snacksList)
    setupDatePicker()
    loadUserFoodLog() // Load for default date (today)
  }

  private def setupDatePicker(): Unit = {
    if (datePicker != null) {
      datePicker.setValue(LocalDate.now())
      datePicker.valueProperty().addListener { (_, _, newDate) =>
        loadUserFoodLog()
      }
    }
  }

  @FXML def handleToday(): Unit =
    datePicker.setValue(LocalDate.now())

  private def getSelectedDate: java.sql.Date = {
    val localDate =
      if (datePicker != null && datePicker.getValue != null) datePicker.getValue
      else LocalDate.now()
    Date.valueOf(localDate)
  }

  private def loadUserFoodLog(): Unit = {
    User.currentUser match {
      case Some(user) =>
        val selectedDate = getSelectedDate
        val logs = FoodLog.findByUserAndDate(user.email, selectedDate)(scalikejdbc.AutoSession)
        breakfastList.getItems.clear()
        lunchList.getItems.clear()
        dinnerList.getItems.clear()
        snacksList.getItems.clear()
        logs.foreach { log =>
          val display = f"${log.foodName} (${log.calories}%.0f kcal)"
          log.category match {
            case "Breakfast" => breakfastList.getItems.add(display)
            case "Lunch"     => lunchList.getItems.add(display)
            case "Dinner"    => dinnerList.getItems.add(display)
            case "Snacks"    => snacksList.getItems.add(display)
            case _           => // ignore unknown category
          }
        }
      case None =>
        // No user logged in, do nothing
    }
  }

  private def setupSaveButton(): Unit = {
    saveButton.setOnAction(_ => {
      User.currentUser match {
        case Some(user) =>
          val selectedDate = getSelectedDate
          FoodLog.clearForUserAndDate(user.email, selectedDate) // Uses implicit session from Database
          saveMealToDatabase(user.email, "Breakfast", breakfastList, selectedDate)
          saveMealToDatabase(user.email, "Lunch", lunchList, selectedDate)
          saveMealToDatabase(user.email, "Dinner", dinnerList, selectedDate)
          saveMealToDatabase(user.email, "Snacks", snacksList, selectedDate)
          showAlert("Saved", "Your food log has been saved.")
        case None =>
          println("No user logged in. Cannot save food log.")
      }
    })
  }

  private def saveMealToDatabase(userEmail: String, mealType: String, listView: ListView[String], date: java.sql.Date): Unit = {
    val items = listView.getItems.asScala
    DB.localTx { implicit session =>
      items.foreach { item =>
        val foodName = item.split("\\(")(0).trim
        val calories = item.split("\\(")(1).replace("kcal)", "").trim.toDouble
        allFoodItems.find(_.name == foodName).foreach { f =>
          FoodLog.insert(FoodLog(
            userEmail = userEmail,
            category = mealType,
            foodName = f.name,
            calories = f.calories,
            protein = f.protein,
            carbs = f.carbs,
            fat = f.fat,
            logDate = date
          ))
        }
      }
    }
  }


  private def setupRemoveButton(): Unit = {
    removeButton.setOnAction(_ => {
      confirmAndRemove(breakfastList)
      confirmAndRemove(lunchList)
      confirmAndRemove(dinnerList)
      confirmAndRemove(snacksList)
    })
  }

  private def confirmAndRemove(listView: ListView[String]): Unit = {
    val selectedItem = listView.getSelectionModel.getSelectedItem
    if (selectedItem != null) {
      val alert = new Alert(AlertType.CONFIRMATION)
      alert.setTitle("Remove Item")
      alert.setHeaderText("Are you sure you want to remove this item?")
      alert.setContentText(selectedItem)
      val result = alert.showAndWait()
      if (result.isPresent && result.get() == ButtonType.OK) {
        listView.getItems.remove(selectedItem)
      }
    }
  }

  private def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(message)
    alert.showAndWait()
  }

  private def loadFoodData(): Unit = {
    val csvFilePath = "/data/daily_food_nutrition_dataset.csv"
    val inputStream = getClass.getResourceAsStream(csvFilePath)
    if (inputStream == null) {
      println(s"Failed to load CSV file at $csvFilePath")
      // Fallback dummy data
      allFoodItems = List(
        FoodItem("Apple", "Fruit", 52, 0.3, 14, 0.2),
        FoodItem("Chicken Breast", "Meat", 165, 31, 0, 3.6),
        FoodItem("Rice", "Grain", 130, 2.7, 28, 0.3),
        FoodItem("Broccoli", "Vegetable", 55, 3.7, 11.2, 0.6)
      )
    } else {
      val reader = CSVReader.open(new InputStreamReader(inputStream))
      val rawRecords = reader.allWithHeaders()
      reader.close()

      // Parse each CSV row to a FoodItem
      allFoodItems = rawRecords.flatMap { row =>
        try {
          Some(FoodItem(
            name = row("Food_Item"),
            category = row.getOrElse("Category", "Unknown"),
            calories = row("Calories (kcal)").toDoubleOption.getOrElse(0.0),
            protein = row("Protein (g)").toDoubleOption.getOrElse(0.0),
            carbs = row("Carbohydrates (g)").toDoubleOption.getOrElse(0.0),
            fat = row("Fat (g)").toDoubleOption.getOrElse(0.0)
          ))
        } catch {
          case e: Exception =>
            println(s"Skipping row due to error: ${e.getMessage}")
            None
        }
      }
    }

    foodNames = allFoodItems.map(_.name).distinct.sorted
  }

  private def setupSearchField(): Unit = {
    searchField.textProperty().addListener(new ChangeListener[String] {
      override def changed(obs: ObservableValue[_ <: String], oldText: String, newText: String): Unit = {
        if (newText.trim.nonEmpty) {
          val suggestions = fuzzySearch(newText, foodNames)
          foodSearchList.getItems.setAll(suggestions.take(10).asJava)
          foodSearchList.setVisible(true)
        } else {
          foodSearchList.getItems.clear()
          foodSearchList.setVisible(false)
        }
      }
    })

    foodSearchList.setOnMouseClicked(event => {
      if (event.getClickCount == 2) {
        val selectedItem = foodSearchList.getSelectionModel.getSelectedItem
        if (selectedItem != null) {
          searchField.setText(selectedItem)
          foodSearchList.setVisible(false)
        }
      }
    })
  }

  private def fuzzySearch(query: String, options: List[String]): List[String] = {
    val lowered = query.toLowerCase
    options.filter(_.toLowerCase.contains(lowered)).sortBy(_.toLowerCase.indexOf(lowered))
  }

  private def setupAddButtons(): Unit = {
    addBreakfastBtn.setOnAction(_ => addSelectedFoodToMealWithAlert(breakfastList))
    addLunchBtn.setOnAction(_ => addSelectedFoodToMealWithAlert(lunchList))
    addDinnerBtn.setOnAction(_ => addSelectedFoodToMealWithAlert(dinnerList))
    addSnacksBtn.setOnAction(_ => addSelectedFoodToMealWithAlert(snacksList))
  }

  private def addSelectedFoodToMealWithAlert(targetList: ListView[String]): Unit = {
    val selectedFood = foodSearchList.getSelectionModel.getSelectedItem
    if (selectedFood == null || selectedFood.trim.isEmpty) {
      showAlert("No Selection", "Please select a food item before adding.")
      return
    }
    if (!targetList.getItems.asScala.exists(_.startsWith(selectedFood))) {
      searchField.clear()
      foodSearchList.getItems.clear()
      foodSearchList.setVisible(false)

      allFoodItems.find(_.name == selectedFood).foreach { item =>
        val display = f"${item.name} (${item.calories}%.0f kcal)"
        targetList.getItems.add(display)
      }
    }
  }

  private def setupListViewCellFactory(listView: ListView[String]): Unit = {
    listView.setCellFactory(_ => new ListCell[String]() {
      override def updateItem(item: String, empty: Boolean): Unit = {
        super.updateItem(item, empty)
        if (item == null || empty) {
          setText(null)
          setGraphic(null)
        } else {
          val caloriePattern = "\\(([^)]+)\\)".r
          val name = caloriePattern.replaceAllIn(item, "").trim
          val caloriesOnly = caloriePattern.findFirstMatchIn(item).map(_.group(1)).getOrElse("")

          val nameText = new Text(name)
          val caloriesText = new Text(caloriesOnly)
          val spacer = new Region()
          HBox.setHgrow(spacer, Priority.ALWAYS)

          val row = new HBox(10, nameText, spacer, caloriesText)
          row.setAlignment(Pos.CENTER_LEFT)
          setGraphic(row)
        }
      }
    })
  }


  private def addSelectedFoodToMeal(targetList: ListView[String]): Unit = {
    val selectedFood = foodSearchList.getSelectionModel.getSelectedItem
    if (selectedFood != null && !targetList.getItems.asScala.exists(_.startsWith(selectedFood))) {
      searchField.clear()
      foodSearchList.getItems.clear()
      foodSearchList.setVisible(false)

      allFoodItems.find(_.name == selectedFood).foreach { item =>
        val display = f"${item.name} (${item.calories}%.0f kcal)"
        targetList.getItems.add(display)

        // Optional: debug print
//        println(s"Added to $targetList: ${item.name}")
//        println(f"  Calories: ${item.calories}%.1f kcal")
//        println(f"  Protein: ${item.protein}%.1f g")
//        println(f"  Carbs: ${item.carbs}%.1f g")
//        println(f"  Fat: ${item.fat}%.1f g")
      }
    }
  }


  private def setupAutoResizeListViews(): Unit = {
    setupAutoResizeListView(breakfastList)
    setupAutoResizeListView(lunchList)
    setupAutoResizeListView(dinnerList)
    setupAutoResizeListView(snacksList)
  }

  private def setupAutoResizeListView(listView: ListView[String]): Unit = {
    listView.getItems.addListener(new ListChangeListener[String] {
      override def onChanged(change: ListChangeListener.Change[_ <: String]): Unit = {
        Platform.runLater(() => {
          val itemCount = listView.getItems.size()
          val cellHeight = 24.0
          val headerHeight = 2.0
          val newHeight = Math.max(24.0, Math.min(80.0, (itemCount * cellHeight) + headerHeight))
          listView.setPrefHeight(newHeight)
        })
      }
    })
  }
}
