package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Label

class ProgressController:

  @FXML private var progressLabel: Label = _

  @FXML def initialize(): Unit =
    progressLabel.setText("View your progress overview here!")