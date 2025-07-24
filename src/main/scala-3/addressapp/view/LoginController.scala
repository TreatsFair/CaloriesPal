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
class LoginController():
  @FXML private var emailField: TextField = _
  @FXML private var passwordField: PasswordField = _
  
  def handleLogin(): Unit =
    val email = emailField.text.value.trim
    val password = passwordField.text.value.trim

    if email.isEmpty || password.isEmpty then
      showAlert("Login Error", "Email or password cannot be empty.")
      return

    if !isValidEmail(email) then
      showAlert("Login Error", "Invalid email format.")
      return

    if !isValidPassword(password) then
      showAlert("Login Error", "Password must be at least 8 characters.")
      return

    User.findByEmail(email) match
      case Some(user) if user.password == password =>
        // Proceed to next scene (e.g., Dashboard)
        showSuccessAlert(user.userName)
      // MainApp.showDashboard() // Assuming this method exists

      case _ =>
        showAlert("Login Failed", "Invalid email or password.")

  private def isValidEmail(email: String): Boolean =
    email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

  private def isValidPassword(password: String): Boolean =
    password.length >= 8

  private def showAlert(header: String, content: String): Unit =
    new Alert(AlertType.Error) {
      title = "Login Error"
      headerText = header
      contentText = content
    }.showAndWait()

  private def showSuccessAlert(userName: String): Unit =
    new Alert(AlertType.Information) {
      title = "Login Successful"
      headerText = s"Welcome, $userName!"
      contentText = "Login successful!"
    }.showAndWait()
