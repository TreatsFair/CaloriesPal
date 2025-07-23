package addressapp

import addressapp.view.AboutController
import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import scalafx.stage.Modality.ApplicationModal
import scalafx.stage.Stage


import java.net.URL

object MainApp extends JFXApp3:
  //global rootPane variable
  var rootPane: Option[javafx.scene.layout.BorderPane] = None
  override def start(): Unit = {
    val rootLayoutResource: URL = getClass.getResource("/addressapp/view/RootLayout.fxml")
    val loader = new FXMLLoader(rootLayoutResource)
    val rootLayout = loader.load[javafx.scene.layout.BorderPane]()
    rootPane = Option(loader.getRoot[javafx.scene.layout.BorderPane]()) //initialize rootPane
    stage = new PrimaryStage():
      title = "My ScalaFX Application"
      scene = new Scene:
        root = rootLayout
    showWelcome() // show the first window as welcome view

    //      stylesheets = Seq(getClass.getResource("/styles.css").toExternalForm)
  }

  // show welcome window in the center of the root pane
  def showWelcome(): Unit =
    val welcome = getClass.getResource("/addressapp/view/Welcome.fxml")
    val loader = new FXMLLoader(welcome)
    val pane = loader.load[javafx.scene.layout.AnchorPane]()
    rootPane.foreach(_.setCenter(pane)) // Set the center of the root pane to the welcome view

  def showAbout(): Boolean =
      val about = getClass.getResource("/addressapp/view/About.fxml")
      val loader = new FXMLLoader(about)
      loader.load()
      val pane = loader.getRoot[javafx.scene.layout.AnchorPane]() // get the anchor pane from the loader
      // then create ur own window
      val mywindow = new Stage():
        initOwner(stage) // set the owner of the window to the main stage
        initModality(ApplicationModal)
        title = "About"
        scene = new Scene():
          root = pane
      val ctrl = loader.getController[AboutController]()
      ctrl.stage = Option(mywindow) // set the stage in the controller
      mywindow.showAndWait() // show the window and wait for it to close
      ctrl.okClicked




