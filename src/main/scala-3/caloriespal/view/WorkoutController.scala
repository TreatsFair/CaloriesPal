package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label

class WorkoutController:

  @FXML private var workoutLabel: Label = _

  @FXML def initialize(): Unit =
    workoutLabel.setText("Track your workout progress here!")