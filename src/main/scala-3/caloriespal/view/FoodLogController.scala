package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label

class FoodLogController:

  @FXML private var foodLogLabel: Label = _

  @FXML def initialize(): Unit =
    println("Log your food intake here!")