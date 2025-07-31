package caloriespal.view

import caloriespal.MainApp
import javafx.fxml.FXML

@FXML
class WelcomeController:
  @FXML
  def handleStart(): Unit =
    MainApp.showMainWindow() // This method is called when the user clicks the start button in the welcome view.