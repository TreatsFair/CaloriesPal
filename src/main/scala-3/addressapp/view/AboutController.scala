package addressapp.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import scalafx.stage.Stage

@FXML
class AboutController():
  // About dialog can be used to display information about the application, such as version, author, etc.
  // MODEL PROPERTY
  // STAGE PROPERTY
  var stage: Option[Stage] = None
  // RETURN PROPERTY
  var okClicked: Boolean = false
  @FXML
  def handleClose(action: ActionEvent): Unit =
    okClicked = true
    stage.foreach(x => x.close()) // Close the dialog if the stage is defined