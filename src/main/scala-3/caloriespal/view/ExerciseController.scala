package caloriespal.view

import caloriespal.model.{ExerciseLog, User}
import caloriespal.util.Database.session
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.scene.text.Text
import javafx.collections.{FXCollections, ObservableList}
import java.time.LocalDate
import java.sql.Date

class ExerciseController:

  @FXML private var exerciseDatePicker: DatePicker = _
  @FXML private var todayBtn: Button = _
  @FXML private var totalBurnedText: Text = _
  @FXML private var nameField: TextField = _
  @FXML private var categoryCombo: ComboBox[String] = _
  @FXML private var durationField: TextField = _
  @FXML private var caloriesField: TextField = _
  @FXML private var notesArea: TextArea = _
  @FXML private var addBtn: Button = _
  @FXML private var updateBtn: Button = _
  @FXML private var deleteBtn: Button = _
  @FXML private var clearBtn: Button = _
  @FXML private var errorText: Text = _
  @FXML private var exerciseTable: TableView[ExerciseLog] = _
  @FXML private var colName: TableColumn[ExerciseLog, String] = _
  @FXML private var colCategory: TableColumn[ExerciseLog, String] = _
  @FXML private var colDuration: TableColumn[ExerciseLog, java.lang.Double] = _
  @FXML private var colCalories: TableColumn[ExerciseLog, java.lang.Double] = _
  @FXML private var colDate: TableColumn[ExerciseLog, Date] = _
  @FXML private var prevDayBtn: Button = _
  @FXML private var nextDayBtn: Button = _

  private var selectedLog: Option[ExerciseLog] = None
  private var isEditing: Boolean = false

  private def setFieldsEditable(editable: Boolean): Unit = {
    nameField.setEditable(editable)
    categoryCombo.setDisable(!editable)
    durationField.setEditable(editable)
    caloriesField.setEditable(editable)
    notesArea.setEditable(editable)
  }

  @FXML def initialize(): Unit =
    exerciseDatePicker.setValue(LocalDate.now())
    setupTable()
    loadTable()
    updateTotalBurned()
    clearForm()
    setFieldsEditable(true)
    updateBtn.setDisable(true)
    deleteBtn.setDisable(true)
    deleteBtn.setText("Delete")
    addBtn.setDisable(false)
    exerciseTable.getSelectionModel.selectedItemProperty().addListener { (_, _, log) =>
      if log != null then
        selectedLog = Some(log)
        fillForm(log)
        setFieldsEditable(false)
        updateBtn.setDisable(false)
        deleteBtn.setDisable(false)
        deleteBtn.setText("Delete")
        isEditing = false
        addBtn.setDisable(true)
      else
        selectedLog = None
        updateBtn.setDisable(true)
        deleteBtn.setDisable(true)
        deleteBtn.setText("Delete")
        setFieldsEditable(true)
        isEditing = false
        addBtn.setDisable(false)
    }
    exerciseDatePicker.valueProperty().addListener { (_, _, _) =>
      loadTable()
      updateTotalBurned()
      clearForm()
      setFieldsEditable(true)
      updateBtn.setDisable(true)
      deleteBtn.setDisable(true)
      deleteBtn.setText("Delete")
      isEditing = false
      addBtn.setDisable(false) // Enable add on date change
    }

  private def getSelectedDate: Date =
    val localDate = Option(exerciseDatePicker.getValue).getOrElse(LocalDate.now())
    Date.valueOf(localDate)

  private def setupTable(): Unit =
    colName.setCellValueFactory(cellData => new javafx.beans.property.SimpleStringProperty(cellData.getValue.name))
    colCategory.setCellValueFactory(cellData => new javafx.beans.property.SimpleStringProperty(cellData.getValue.category))
    colDuration.setCellValueFactory(cellData => new javafx.beans.property.SimpleDoubleProperty(cellData.getValue.durationMin).asObject())
    colCalories.setCellValueFactory(cellData => new javafx.beans.property.SimpleDoubleProperty(cellData.getValue.calories).asObject())

  private def loadTable(): Unit =
    User.currentUser.foreach { user =>
      val logs = ExerciseLog.findByUserAndDate(user.email, getSelectedDate)
      val items: ObservableList[ExerciseLog] = FXCollections.observableArrayList(logs*)
      exerciseTable.setItems(items)
    }

  private def updateTotalBurned(): Unit =
    User.currentUser.foreach { user =>
      val total = ExerciseLog.totalCaloriesByDate(user.email, getSelectedDate)
      totalBurnedText.setText(f"$total%.0f kcal")
    }

  @FXML def handleToday(): Unit =
    exerciseDatePicker.setValue(LocalDate.now())

  @FXML def handlePrevDay(): Unit =
    val current = Option(exerciseDatePicker.getValue).getOrElse(LocalDate.now())
    exerciseDatePicker.setValue(current.minusDays(1))

  @FXML def handleNextDay(): Unit =
    val current = Option(exerciseDatePicker.getValue).getOrElse(LocalDate.now())
    exerciseDatePicker.setValue(current.plusDays(1))

  @FXML def handleAdd(): Unit =
    errorText.setText("")
    User.currentUser match
      case Some(user) =>
        val name = nameField.getText.trim
        val category = Option(categoryCombo.getValue).getOrElse("")
        val duration = durationField.getText.trim.toDoubleOption.getOrElse(0.0)
        val calories = caloriesField.getText.trim.toDoubleOption.getOrElse(0.0)
        val notes = Option(notesArea.getText).filter(_.nonEmpty)
        if name.isEmpty || category.isEmpty || duration <= 0 || calories <= 0 then
          showAlert("Error", "Please fill all fields with valid values.")
        else
          val log = ExerciseLog(
            userEmail = user.email,
            date = getSelectedDate,
            name = name,
            category = category,
            durationMin = duration,
            calories = calories,
            notes = notes
          )
          ExerciseLog.insert(log)
          loadTable()
          updateTotalBurned()
          clearForm()
      case None =>
        showAlert("Error", "No user logged in.") // Use pop-up for no user

  @FXML def handleUpdate(): Unit =
    if selectedLog.isDefined && !isEditing then
      setFieldsEditable(true)
      isEditing = true
      deleteBtn.setText("Save")
      deleteBtn.setStyle("-fx-background-color: #66bb6a; -fx-text-fill: white;")
      updateBtn.setDisable(true)
    else
      showAlert("Error", "Select an exercise to update.")

  @FXML def handleDelete(): Unit =
    if isEditing && selectedLog.isDefined then
      // Save logic
      val name = nameField.getText.trim
      val category = Option(categoryCombo.getValue).getOrElse("")
      val duration = durationField.getText.trim.toDoubleOption.getOrElse(0.0)
      val calories = caloriesField.getText.trim.toDoubleOption.getOrElse(0.0)
      val notes = Option(notesArea.getText).filter(_.nonEmpty)
      if name.isEmpty || category.isEmpty || duration <= 0 || calories <= 0 then
        showAlert("Error", "Please fill all fields with valid values.")
      else
        val updated = selectedLog.get.copy(
          name = name,
          category = category,
          durationMin = duration,
          calories = calories,
          notes = notes
        )
        ExerciseLog.update(updated)
        showAlert("Saved", "Exercise updated successfully.")
        loadTable()
        updateTotalBurned()
        clearForm()
        setFieldsEditable(false)
        isEditing = false
        deleteBtn.setText("Delete")
        deleteBtn.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;")
        updateBtn.setDisable(false)
    else if selectedLog.isDefined then
      val log = selectedLog.get
      val alert = new Alert(Alert.AlertType.CONFIRMATION)
      alert.setTitle("Delete Exercise")
      alert.setHeaderText("Are you sure you want to delete this exercise?")
      alert.setContentText(s"Name: ${log.name}\nCategory: ${log.category}\nDuration: ${log.durationMin} min")
      val result = alert.showAndWait()
      if result.isPresent && result.get() == ButtonType.OK then
        ExerciseLog.delete(log.id.get)
        showAlert("Deleted", "Exercise deleted.")
        loadTable()
        updateTotalBurned()
        clearForm()
        setFieldsEditable(true)
        updateBtn.setDisable(true)
        deleteBtn.setDisable(true)
        deleteBtn.setText("Delete")
        deleteBtn.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;")
        isEditing = false
    else
      showAlert("Error", "No exercise selected.") 

  @FXML def handleClear(): Unit =
    clearForm()

  private def clearForm(): Unit =
    nameField.setText("")
    categoryCombo.getSelectionModel.clearSelection()
    durationField.setText("")
    caloriesField.setText("")
    notesArea.setText("")
    selectedLog = None
    updateBtn.setDisable(true)
    deleteBtn.setDisable(true)
    deleteBtn.setText("Delete")
    deleteBtn.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;")
    setFieldsEditable(true)
    isEditing = false
    errorText.setText("")
    addBtn.setDisable(false) 

  private def fillForm(log: ExerciseLog): Unit =
    nameField.setText(log.name)
    categoryCombo.setValue(log.category)
    durationField.setText(log.durationMin.toString)
    caloriesField.setText(log.calories.toString)
    notesArea.setText(log.notes.getOrElse(""))
    updateBtn.setDisable(false)
    deleteBtn.setDisable(false)
    deleteBtn.setText("Delete")
    setFieldsEditable(false)
    isEditing = false

  private def showAlert(title: String, message: String): Unit = {
    val alert = new Alert(Alert.AlertType.INFORMATION)
    alert.setTitle(title)
    alert.setHeaderText(null)
    alert.setContentText(message)
    alert.showAndWait()
  }
