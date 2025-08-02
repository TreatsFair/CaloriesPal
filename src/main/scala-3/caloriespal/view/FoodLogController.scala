package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, ListView, TextField}
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}
import scala.jdk.CollectionConverters._
import java.io.InputStreamReader
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.{FXCollections, ListChangeListener}
import javafx.application.Platform

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
    println("Initializing FoodLogController...")
    loadFoodData()
    setupSearchField()
    setupAddButtons()
    setupAutoResizeListViews()
    println("Initialization complete!")
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
    println(s"Parsed ${allFoodItems.size} food items with nutritional values.")
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
    addBreakfastBtn.setOnAction(_ => addSelectedFoodToMeal(breakfastList))
    addLunchBtn.setOnAction(_ => addSelectedFoodToMeal(lunchList))
    addDinnerBtn.setOnAction(_ => addSelectedFoodToMeal(dinnerList))
    addSnacksBtn.setOnAction(_ => addSelectedFoodToMeal(snacksList))
  }

  private def addSelectedFoodToMeal(targetList: ListView[String]): Unit = {
    val selectedFood = foodSearchList.getSelectionModel.getSelectedItem
    if (selectedFood != null && !targetList.getItems.contains(selectedFood)) {
      targetList.getItems.add(selectedFood)
      searchField.clear()
      foodSearchList.getItems.clear()

      // Optional: print nutrition info
      allFoodItems.find(_.name == selectedFood).foreach { item =>
        println(s"Added to $targetList: ${item.name}")
        println(f"  Calories: ${item.calories}%.1f kcal")
        println(f"  Protein: ${item.protein}%.1f g")
        println(f"  Carbs: ${item.carbs}%.1f g")
        println(f"  Fat: ${item.fat}%.1f g")
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
