package caloriespal.view

import caloriespal.model.User
import caloriespal.util.Database
import caloriespal.MainApp
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Alert, PasswordField, TextField}
import javafx.scene.control.Alert.AlertType
import javafx.util.Duration
import scalafx.animation.PauseTransition
import scalikejdbc.*

import scala.util.{Failure, Success}

class LoginController:

  @FXML private var emailField: TextField = _
  @FXML private var passwordField: PasswordField = _

  @FXML def handleRegister(): Unit =
    // Switch to Register screen
    MainApp.showRegister()

  @FXML
  def handleLogin(): Unit =
    val email = emailField.getText.trim
    val password = passwordField.getText.trim

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
        User.currentUser = Some(user) // Store the current user
        showSuccessAlert(user.userName)
        MainApp.showDashboard()
      case _ =>
        showAlert("Login Failed", "Invalid email or password.")

  private def isValidEmail(email: String): Boolean =
    email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

  private def isValidPassword(password: String): Boolean =
    password.length >= 8

  private def showAlert(header: String, content: String): Unit =
    val alert = new Alert(AlertType.ERROR)
    alert.setTitle("Login Error")
    alert.setHeaderText(header)
    alert.setContentText(content)
    alert.showAndWait()

  private def showSuccessAlert(userName: String): Unit =
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Login Successful")
    alert.setHeaderText(s"Welcome Back, $userName!")
    alert.setContentText("Login successful!")
    alert.showAndWait()
