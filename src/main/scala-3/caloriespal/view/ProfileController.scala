package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.TextField
import caloriespal.model.User

class ProfileController:

  @FXML private var weightField: TextField = _
  @FXML private var goalField: TextField = _

  @FXML def initialize(): Unit =
    User.currentUser.foreach { user =>
      weightField.setText("48")  // TODO: replace with actual value from user model
      goalField.setText("1800")  // TODO: replace with actual value from user model
    }
