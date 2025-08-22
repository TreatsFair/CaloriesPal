package caloriespal.view

import caloriespal.MainApp
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.Parent
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.StackPane

class MainWindowController:

  @FXML private var mainContentArea: StackPane = _
  @FXML private var dashboardButton: Button = _
  @FXML private var foodLogButton: Button = _
  @FXML private var exerciseButton: Button = _
  @FXML private var profileButton: Button = _
  @FXML private var logoutButton: Button = _

  private val allNavButtons = Seq(
    () => dashboardButton,
    () => foodLogButton,
    () => exerciseButton,
    () => profileButton
  )

  @FXML def initialize(): Unit =
    loadDashboardContent()
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

  def loadExerciseContent(): Unit =
    val loader = new FXMLLoader(getClass.getResource("/caloriespal/view/Exercise.fxml"))
    val exerciseRoot = loader.load[Parent]()
    mainContentArea.getChildren.setAll(exerciseRoot)

  @FXML def handleDashboardClick(): Unit =
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")
    loadDashboardContent()

  @FXML def handleFoodLogClick(): Unit =
    clearActiveStyles()
    foodLogButton.getStyleClass.add("active")
    loadFoodLogContent()

  @FXML def handleExerciseClick(): Unit =
    clearActiveStyles()
    exerciseButton.getStyleClass.add("active")
    loadExerciseContent()

  @FXML def handleProfileClick(): Unit =
    clearActiveStyles()
    profileButton.getStyleClass.add("active")
    loadProfileContent()

  @FXML def handleLogoutClick(): Unit =
    // Logic to handle logout
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")
    MainApp.showLogin()


