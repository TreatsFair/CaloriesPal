package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField, Label}
import caloriespal.model.User
import caloriespal.util.DateUtil.*

class ProfileEditController:

  @FXML private var usernameField: TextField = _
  @FXML private var heightField: TextField = _
  @FXML private var weightField: TextField = _
  @FXML private var genderField: TextField = _
  @FXML private var dobField: TextField = _
  @FXML private var locationField: TextField = _
  @FXML private var weightGoalField: TextField = _
  @FXML private var saveButton: Button = _
  @FXML private var errorLabel: Label = _

  @FXML def initialize(): Unit =
    User.currentUser.foreach { user =>
      usernameField.setText(user.userName)
      // Populate other fields with placeholder values or actual data
      heightField.setText("")
      weightField.setText("")
      genderField.setText("")
      dobField.setText("")
      locationField.setText("")
      weightGoalField.setText("")
    }

  @FXML def handleSaveClick(): Unit =
    errorLabel.setText("") // Clear previous errors

    // Validate height, weight, and weight goal
    if !heightField.getText.matches("\\d+") then
      errorLabel.setText("Height must be a number.")
      return
    if !weightField.getText.matches("\\d+") then
      errorLabel.setText("Weight must be a number.")
      return
    if !weightGoalField.getText.matches("\\d+") then
      errorLabel.setText("Weight goal must be a number.")
      return

    // Validate date of birth
    val dob = dobField.getText
    if !dob.isValidDOB then
      errorLabel.setText("Date of Birth must be in the format dd.MM.yyyy and not in the future.")
      return

    // Save the updated details
    val newUsername = usernameField.getText
    val newHeight = heightField.getText.toInt
    val newWeight = weightField.getText.toInt
    val newGender = genderField.getText
    val newDob = dob
    val newLocation = locationField.getText
    val newWeightGoal = weightGoalField.getText.toInt

    // Update the user model (add logic to save to DB if needed)
    User.currentUser = User.currentUser.map(_.copy(userName = newUsername))
    // Close the edit window
    saveButton.getScene.getWindow.hide()