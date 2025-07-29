package caloriespal.view

import javafx.fxml.FXML
import javafx.scene.control.Button

class MainWindowController:

  @FXML private var dashboardButton: Button = _
  @FXML private var foodLogButton: Button = _
  @FXML private var progressButton: Button = _
  @FXML private var workoutButton: Button = _
  @FXML private var profileButton: Button = _

  private val allNavButtons = Seq(
    () => dashboardButton,
    () => foodLogButton,
    () => progressButton,
    () => workoutButton,
    () => profileButton
  )

  private def clearActiveStyles(): Unit =
    allNavButtons.foreach(btn => btn().getStyleClass.remove("active"))

  @FXML def handleDashboardClick(): Unit =
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")
  // TODO: Load dashboard content into the main area

  @FXML def handleFoodLogClick(): Unit =
    clearActiveStyles()
    foodLogButton.getStyleClass.add("active")
// TODO: Load food log UI

  @FXML def handleProgressClick(): Unit =
    clearActiveStyles()
    progressButton.getStyleClass.add("active")

  @FXML def handleWorkoutClick(): Unit =
    clearActiveStyles()
    workoutButton.getStyleClass.add("active")

  @FXML def handleProfileClick(): Unit =
    clearActiveStyles()
    profileButton.getStyleClass.add("active")

  @FXML def handleLogout(): Unit =
    // Logic to handle logout
    clearActiveStyles()
    dashboardButton.getStyleClass.add("active")


