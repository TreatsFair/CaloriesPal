package caloriespal.view

import javafx.fxml.FXML
import javafx.event.ActionEvent
import caloriespal.MainApp

@FXML
class RootLayoutController():
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)
  
  @FXML
  def handleAbout(action: ActionEvent): Unit =
    MainApp.showAbout()