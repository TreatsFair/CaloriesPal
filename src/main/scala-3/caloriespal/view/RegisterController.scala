package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.{TextField, PasswordField, Alert}
import javafx.scene.control.Alert.AlertType
import caloriespal.model.User
import caloriespal.MainApp
import scala.util.{Failure, Success}
import javafx.event.ActionEvent

class RegisterController:

  @FXML private var emailField: TextField = _
  @FXML private var passwordField: PasswordField = _
  @FXML private var userNameField: TextField = _

  def handleReturnToLogin(event: ActionEvent): Unit =
    MainApp.showLogin()

  def handleRegister(): Unit =
    val email = emailField.getText.trim
    val password = passwordField.getText.trim
    val userName = userNameField.getText.trim

    if email.isEmpty || password.isEmpty || userName.isEmpty then
      showAlert("Registration Error", "All fields are required.")
      return

    if !isValidEmail(email) then
      showAlert("Registration Error", "Invalid email format.")
      return

    if !isValidPassword(password) then
      showAlert("Registration Error", "Password must be at least 8 characters.")
      return

    // Check if email already exists
    if User.findByEmail(email).isDefined then
      showAlert("Registration Failed", "Email is already registered.")
      return

    // Check if username already exists
    if User.findByUsername(userName).isDefined then
      showAlert("Registration Failed", "Username is already taken.")
      return

    // Try to create new user
    User.create(email, password, userName) match
      case Success(_) =>
        showAlert("Success", "Account created! You may login now.")
        MainApp.showLogin()
      case Failure(e) =>
        showAlert("Database Error", s"Failed to register: ${e.getMessage}")

  private def isValidEmail(email: String): Boolean =
    email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

  private def isValidPassword(password: String): Boolean =
    password.length >= 8

  private def showAlert(header: String, content: String): Unit =
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Register")
    alert.setHeaderText(header)
    alert.setContentText(content)
    alert.showAndWait()
