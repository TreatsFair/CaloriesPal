package addressapp
import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*

import java.net.URL

object MainApp extends JFXApp3:
  override def start(): Unit = {
    val rootLayoutResource: URL = getClass.getResource("/addressapp/view/RootLayout.fxml")
    val loader = new FXMLLoader(rootLayoutResource)
    val rootLayout = loader.load[javafx.scene.layout.BorderPane]()
    stage = new PrimaryStage():
      title = "My ScalaFX Application"
      scene = new Scene:
        root = rootLayout
  }
  //      stylesheets = Seq(getClass.getResource("/styles.css").toExternalForm)

end MainApp
