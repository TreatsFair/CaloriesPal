package addressapp.view

import addressapp.model.User
import addressapp.util.Database
import scalafx.scene.control.{Alert, PasswordField, TextField}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import javafx.fxml.FXML
import scalafx.Includes._
import scalikejdbc._

import scala.util.{Failure, Success}

@FXML
class RegisterController():
  @FXML private var emailField: TextField = _
  @FXML private var passwordField: PasswordField = _
  @FXML private var userNameField: TextField = _
  
  def handleRegister(): Unit =
    val email = emailField.text.value.trim
    val password = passwordField.text.value.trim
    val userName = userNameField.text.value.trim

    if email.isEmpty || password.isEmpty || userName.isEmpty then
      showAlert("Registration Error", "All fields are required.")
      return

    if !isValidEmail(email) then
      showAlert("Registration Error", "Invalid email format.")
      return

    if !isValidPassword(password) then
      showAlert("Registration Error", "Password must be at least 8 characters.")
      return

    User.findByEmail(email) match
      case Some(_) =>
        showAlert("Registration Failed", "Email already registered.")
      case None =>
        User.create(email, password, userName) match
          case Success(_) =>
            showAlert("Success", "Account created! You may login now.")
          // Optional: redirect to login scene
          case Failure(e) =>
            showAlert("Database Error", s"Failed to register: ${e.getMessage}")

  private def isValidEmail(email: String): Boolean =
    email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

  private def isValidPassword(password: String): Boolean =
    password.length >= 8

  private def showAlert(header: String, content: String): Unit =
    new Alert(AlertType.Information) {
      title = header
      headerText = None
      contentText = content
    }.showAndWait()
