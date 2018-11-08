package unb.cs2043.student_assistant.fxml;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import unb.cs2043.student_assistant.App;
import unb.cs2043.student_assistant.ClassTime;
import unb.cs2043.student_assistant.Course;
import unb.cs2043.student_assistant.Section;

/**
 * Controller class for the AddEditClassTime.fxml 
 * @author Alexandre Carvalho
 */
public class AddEditClassTimeController implements javafx.fxml.Initializable {

	@FXML private ComboBox<Course> cmbCourse;
	@FXML private ComboBox<Section> cmbSection;
	@FXML private Button btnAdd;
	@FXML private Button btnCancel;
	@FXML private TextField txfUNB;
	@FXML private Button btnAddUNB;
	@FXML private Spinner<LocalTime> spinnerStart, spinnerEnd;
	@FXML private StackPane container;
	@FXML private CheckBox chkSun, chkMon, chkTue, chkWed, chkThu, chkFri, chkSat;
	@FXML private RadioButton rbtnLec, rbtnLab, rbtnTut, rbtnOth;
	@FXML private Label lblTimeError;
	final ToggleGroup group = new ToggleGroup();
	
	private ClassTime classTimeToEdit;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnCancel.setOnAction(this::closeWindow);
		btnAdd.setOnAction(this::addClassTime);
		
		//Close window when pressing Escape
		container.setOnKeyPressed(event -> {
			if (event.getCode() ==  KeyCode.ESCAPE) closeWindow(new ActionEvent());
		});
		
		rbtnLec.setUserData("Lec");
		rbtnLec.setToggleGroup(group);
		rbtnLab.setUserData("Lab");
		rbtnLab.setToggleGroup(group);
		rbtnTut.setUserData("Tutorial");
		rbtnTut.setToggleGroup(group);
		rbtnOth.setUserData("Other");
		rbtnOth.setToggleGroup(group);
		
		spinnerStart.setValueFactory(new TimeSpinnerValueFactory());
		spinnerEnd.setValueFactory(new TimeSpinnerValueFactory());
		spinnerEnd.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (spinnerStart.getValue().compareTo(spinnerEnd.getValue()) >= 0) {
				lblTimeError.setText("* End time must be greater than start time.");
			} else {
				lblTimeError.setText("");
			}
		});
		
		cmbCourse.setItems(FXCollections.observableList(App.userSelection.copyCourses()));
		cmbCourse.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent actionEvent) {
		    	Course course = cmbCourse.getSelectionModel().getSelectedItem();
		    	cmbSection.setItems(FXCollections.observableList(course.copySections()));
		    }
		});
		
		//cmbCourse.setCellFactory(e -> new ComboBoxCourseCell());
	}
	
	public void setCourseToAddTo(Course course) {
		cmbCourse.getSelectionModel().select(course);
	}
	public void setSectionToAddTo(Section section) {
		cmbSection.getSelectionModel().select(section);
	}
	public void setClassTimeToEdit(ClassTime classTimeToEdit) {
		this.classTimeToEdit = classTimeToEdit;
		btnAdd.setText("Modify");
		
		//Disable combobox when editing (without greying it out)
		cmbCourse.setMouseTransparent(true);
		cmbCourse.setFocusTraversable(true);
		cmbSection.setMouseTransparent(true);
		cmbSection.setFocusTraversable(true);
		
		//TODO: Autofill all fields using values from classTimeToEdit
	}

	
	private void addClassTime(ActionEvent event) {
		try {
				
			if (cmbCourse.getSelectionModel().getSelectedItem() == null) {
				App.showNotification("Course not selected.", AlertType.ERROR);
				return;
			}
			
			if (cmbSection.getSelectionModel().getSelectedItem() == null) {
				App.showNotification("Section not selected.", AlertType.ERROR);
				return;
			}
			
			if (!(chkSun.isSelected() || chkMon.isSelected() || chkTue.isSelected() || 
					  chkWed.isSelected() || chkThu.isSelected() || chkFri.isSelected() || chkSat.isSelected())) {
					App.showNotification("Week day not selected.", AlertType.ERROR);
					return;
			}
			
			if (group.getSelectedToggle() == null) {
				App.showNotification("Type not selected.", AlertType.ERROR);
				return;
			}
			
			if (spinnerStart.getValue() == null) {
				App.showNotification("Start time not selected", AlertType.ERROR);
				return;
			}
			
			if (spinnerEnd.getValue() == null) {
				App.showNotification("End time not selected", AlertType.ERROR);
				return;
			}
			
			if (spinnerStart.getValue().compareTo(spinnerEnd.getValue()) >= 0) {
				App.showNotification("End time must be greater than start time.", AlertType.ERROR);
				return;
			}
			
			ClassTime newClassTime = classTimeNameBuilder();
			
			//Check if adding or editing
			if (classTimeToEdit!=null) {
				classTimeToEdit = classTimeNameBuilder();
			}
			else {
				Section section = cmbSection.getSelectionModel().getSelectedItem();
				section.add(newClassTime);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		closeWindow(event);
	}
		
	// For Testing purposes
	private ClassTime classTimeNameBuilder() {
		String type = (String)group.getSelectedToggle().getUserData();
		
		ArrayList<String> days = new ArrayList<>();
//		String days = "";
		if(chkSun.isSelected())
			days.add("Su");
		if(chkMon.isSelected())
			days.add("M");
		if(chkTue.isSelected())
			days.add("T");
		if(chkWed.isSelected())
			days.add("W");
		if(chkThu.isSelected())
			days.add("Th");
		if(chkFri.isSelected())
			days.add("F");
		if(chkSat.isSelected())
			days.add("Sa");
		
		String startTime = spinnerStart.getValue().toString();
		String endTime = spinnerEnd.getValue().toString();
		
//		return (type + " " + days + " " + startTime + "-" + endTime);
		return new ClassTime(days, startTime, endTime);
	}
	
	
	private void closeWindow(ActionEvent event) {
		Stage stage = (Stage)container.getScene().getWindow();
	    stage.close();
	}
}
