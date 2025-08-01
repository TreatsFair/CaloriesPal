package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import caloriespal.model.User
import caloriespal.util.DateUtil.*
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scala.util.{Failure, Success}

class ProfileController:

  @FXML private var usernameField: TextField = _
  @FXML private var heightField: TextField = _
  @FXML private var weightField: TextField = _
  @FXML private var genderField: TextField = _
  @FXML private var dobField: TextField = _
  @FXML private var locationField: TextField = _
  @FXML private var goalField: TextField = _
  @FXML private var editButton: Button = _

  private var isEditing: Boolean = false

  @FXML def initialize(): Unit =
    User.currentUser.foreach { user =>
      usernameField.setText(user.userName)
      heightField.setText(user.height.map(_.toString).getOrElse(""))
      weightField.setText(user.weight.map(_.toString).getOrElse(""))
      genderField.setText(user.gender.getOrElse(""))
      dobField.setText(user.dob.getOrElse(""))
      locationField.setText(user.location.getOrElse(""))
      goalField.setText(user.goal.map(_.toString).getOrElse(""))
    }
    setFieldsEditable(false)

  @FXML def handleEditClick(): Unit =
    if isEditing then
      handleSaveClick()
    else
      setFieldsEditable(true)
      editButton.setText("Save")
      isEditing = true

  @FXML def handleSaveClick(): Unit =
    // Validate numeric fields
    if !heightField.getText.matches("\\d+") then
      showAlert("Validation Error", "Height must be a number.")
      return
    if !weightField.getText.matches("\\d+") then
      showAlert("Validation Error", "Weight must be a number.")
      return
    if goalField.getText.nonEmpty && !goalField.getText.matches("\\d+") then
      showAlert("Validation Error", "Weight goal must be a number.")
      return

    // Validate date of birth
    val dob = dobField.getText
    if !dob.isValidDOB then
      showAlert("Validation Error", "Date of Birth must be in the format dd.MM.yyyy and not in the future.")
      return

    // Save the updated details
    val newUsername = usernameField.getText
    val newHeight = Some(heightField.getText.toInt)
    val newWeight = Some(weightField.getText.toInt)
    val newGender = Option(genderField.getText).filter(_.nonEmpty)
    val newDob = Option(dobField.getText).filter(_.nonEmpty)
    val newLocation = Option(locationField.getText).filter(_.nonEmpty)
    val newGoal = if goalField.getText.nonEmpty then Some(goalField.getText.toInt) else None

    User.currentUser = User.currentUser.map(_.copy(
      userName = newUsername,
      height = newHeight,
      weight = newWeight,
      gender = newGender,
      dob = newDob,
      location = newLocation,
      goal = newGoal
    ))

    // Save to database
    User.currentUser.foreach { user =>
      user.save() match
        case Success(_) =>
          setFieldsEditable(false)
          editButton.setText("Edit")
          isEditing = false
        case Failure(e) =>
          showAlert("Database Error", "An error occurred while saving to the database: " + e.getMessage)
    }

  private def showAlert(header: String, content: String): Unit =
    val alert = new Alert(AlertType.Error)
    alert.setTitle("Error")
    alert.setHeaderText(header)
    alert.setContentText(content)
    alert.showAndWait()

  private def setFieldsEditable(editable: Boolean): Unit =
    val fields = Seq(usernameField, heightField, weightField, genderField, dobField, locationField, goalField)
    fields.foreach { field =>
      field.setEditable(editable)
      if editable then
        field.getStyleClass.remove("hidden-textfield")
      else if !field.getStyleClass.contains("hidden-textfield") then
        field.getStyleClass.add("hidden-textfield")
    }