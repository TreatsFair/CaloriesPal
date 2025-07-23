package addressapp.view

import javafx.fxml.FXML
import javafx.event.ActionEvent
import addressapp.MainApp

@FXML
class RootLayoutController():
  // Close the application
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)
  
  @FXML
  def handleAbout(action: ActionEvent): Unit =
    MainApp.showAbout()