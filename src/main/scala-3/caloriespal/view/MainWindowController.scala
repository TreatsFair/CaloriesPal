package caloriespal.view

import caloriespal.MainApp
import caloriespal.model.User
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.Parent
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.StackPane

class MainWindowController:

  @FXML private var mainContentArea: StackPane = _
  @FXML private var dashboardButton: Button = _
  @FXML private var foodLogButton: Button = _
//  @FXML private var progressButton: Button = _
  @FXML private var workoutButton: Button = _
  @FXML private var profileButton: Button = _
  @FXML private var logoutButton: Button = _
//  @FXML private var welcomeLabel: Label = _

  private val allNavButtons = Seq(
    () => dashboardButton,
    () => foodLogButton,
//    () => progressButton,
    () => workoutButton,
    () => profileButton
  )

  @FXML def initialize(): Unit =
    // Show the user’s name (optional)
//    User.currentUser.foreach(user =>
//      welcomeLabel.setText(s"Hello, ${user.userName}!")
//    )

    // load dashboard as initial content
    loadDashboardContent()

    // Set Dashboard button as active
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")

  private def clearActiveStyles(): Unit =
    allNavButtons.foreach(btn => btn().getStyleClass.remove("active"))

  def loadDashboardContent(): Unit =
    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/Dashboard.fxml"))
    val dashboardRoot = loader.load[Parent]()
    mainContentArea.getChildren.setAll(dashboardRoot)

  def loadProfileContent(): Unit =
    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/Profile.fxml"))
    val profileRoot = loader.load[Parent]()
    mainContentArea.getChildren.setAll(profileRoot)

  def loadFoodLogContent(): Unit =
    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/FoodLog.fxml"))
    val foodLogRoot = loader.load[Parent]()
    mainContentArea.getChildren.setAll(foodLogRoot)

//  def loadProgressContent(): Unit =
//    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/Progress.fxml"))
//    val progressRoot = loader.load[Parent]()
//    mainContentArea.getChildren.setAll(progressRoot)

  def loadWorkoutContent(): Unit =
    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/Workout.fxml"))
    val workoutRoot = loader.load[Parent]()
    mainContentArea.getChildren.setAll(workoutRoot)

  @FXML def handleDashboardClick(): Unit =
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")
    loadDashboardContent()

  @FXML def handleFoodLogClick(): Unit =
    clearActiveStyles()
    foodLogButton.getStyleClass.add("active")
    loadFoodLogContent()

//  @FXML def handleProgressClick(): Unit =
//    clearActiveStyles()
//    progressButton.getStyleClass.add("active")
//    loadProgressContent()

  @FXML def handleWorkoutClick(): Unit =
    clearActiveStyles()
    workoutButton.getStyleClass.add("active")
    loadWorkoutContent()

  @FXML def handleProfileClick(): Unit =
    clearActiveStyles()
    profileButton.getStyleClass.add("active")
    loadProfileContent()

  @FXML def handleLogoutClick(): Unit =
    // Logic to handle logout
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")
    MainApp.showLogin()


