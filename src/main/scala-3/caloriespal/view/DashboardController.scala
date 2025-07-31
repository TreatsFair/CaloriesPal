package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label

class DashboardController:

  @FXML private var caloriesLabel: Label = _
  @FXML private var foodSummaryLabel: Label = _
  @FXML private var exerciseLabel: Label = _

  @FXML def initialize(): Unit =
    // Example data (replace with real logic later)
    caloriesLabel.setText("1500 kcal")
    foodSummaryLabel.setText("Food: 1200 kcal")
    exerciseLabel.setText("Exercise: 300 kcal")
