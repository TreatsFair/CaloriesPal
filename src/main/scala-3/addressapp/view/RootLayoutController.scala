package addressapp.view

import javafx.fxml.FXML
import javafx.event.ActionEvent

@FXML
class RootLayoutController():
  // Close the application
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)
  