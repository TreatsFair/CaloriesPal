package caloriespal

import caloriespal.util.Database
import caloriespal.view.AboutController
import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import scalafx.scene.image.Image
import scalafx.stage.Modality.ApplicationModal
import scalafx.stage.Stage
import scalafx.scene.text.Font
import java.net.URL

object MainApp extends JFXApp3:
  var rootPane: Option[javafx.scene.layout.BorderPane] = None
  override def start(): Unit =
    Database.setupDB()
    val rootLayoutResource: URL = getClass.getResource("/caloriespal/view/RootLayout.fxml")
    val loader = new FXMLLoader(rootLayoutResource)
    val rootLayout = loader.load[javafx.scene.layout.BorderPane]()
    rootPane = Option(loader.getRoot[javafx.scene.layout.BorderPane]())
    Font.loadFont(getClass.getResource("/fonts/black-mango-regular.ttf").toExternalForm, 14)
    Font.loadFont(getClass.getResource("/fonts/Cygre-Regular.ttf").toExternalForm, 14)
    stage = new PrimaryStage():
      title = "CaloriesPal"
      icons += new Image(getClass.getResource("/images/logo.png").toExternalForm)
      scene = new Scene:
        root = rootLayout
    showLogin()


  def showLogin(): Unit =
    val loginResource = getClass.getResource("/caloriespal/view/Login.fxml")
    val loginLoader = new FXMLLoader(loginResource)
    val pane = loginLoader.load[javafx.scene.layout.AnchorPane]()
    rootPane.foreach(_.setCenter(pane))


  def showRegister(): Unit =
    val registerResource = getClass.getResource("/caloriespal/view/Register.fxml")
    val registerLoader = new FXMLLoader(registerResource)
    val pane = registerLoader.load[javafx.scene.layout.AnchorPane]()
    rootPane.foreach(_.setCenter(pane))


  def showMainWindow(): Unit =
    val mainResource = getClass.getResource("/caloriespal/view/MainWindow.fxml")
    val mainLoader = new FXMLLoader(mainResource)
    val pane = mainLoader.load[javafx.scene.layout.BorderPane]()
    rootPane.foreach(_.setCenter(pane))


  def showAbout(): Boolean =
      val about = getClass.getResource("/caloriespal/view/About.fxml")
      val loader = new FXMLLoader(about)
      loader.load()
      val pane = loader.getRoot[javafx.scene.layout.AnchorPane]() // get the anchor pane from the loader
      // then create ur own window
      val mywindow = new Stage():
        initOwner(stage) // set the owner of the window to the main stage
        initModality(ApplicationModal)
        title = "About"
        icons += new Image(getClass.getResource("/images/logo.png").toExternalForm)
        scene = new Scene():
          root = pane
      val ctrl = loader.getController[AboutController]()
      ctrl.stage = Option(mywindow) // set the stage in the controller
      mywindow.showAndWait() // show the window and wait for it to close
      ctrl.okClicked




