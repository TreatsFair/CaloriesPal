package caloriespal.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import scalafx.stage.Stage

@FXML
class AboutController():
  var stage: Option[Stage] = None
  var okClicked: Boolean = false
  @FXML
  def handleClose(action: ActionEvent): Unit =
    okClicked = true
    stage.foreach(x => x.close()) 