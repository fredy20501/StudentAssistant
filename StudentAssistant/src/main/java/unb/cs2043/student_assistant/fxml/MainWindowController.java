
package unb.cs2043.student_assistant.fxml;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

import fxsampler.FXSamplerConfiguration;
import fxsampler.SampleBase;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unb.cs2043.student_assistant.App;
import unb.cs2043.student_assistant.ClassTime;
import unb.cs2043.student_assistant.Course;
import unb.cs2043.student_assistant.Schedule;
import unb.cs2043.student_assistant.ScheduleArranger;
import unb.cs2043.student_assistant.Section;
import unb.cs2043.student_assistant.UNBCourseReader;
import unb.cs2043.student_assistant.FileSelect;

/**
 * Controller class for the MainWindow.fxml 
 * @author Frederic Verret
 */

public class MainWindowController implements javafx.fxml.Initializable {

	@FXML private AnchorPane container;
	
	@FXML private TreeView<Object> treeCourseList;
	@FXML private Button btnAddCourse;
	@FXML private Button btnAddSection;
	@FXML private Button btnAddClassTime;
	@FXML private Button btnGenSchedule;
	@FXML private Label msgLabel;
	
	@FXML private ContextMenu contextMenu;
	@FXML private MenuItem menuEditCourse;
	@FXML private MenuItem menuEditSection;
	@FXML private MenuItem menuEditClassTime;
	@FXML private MenuItem menuAddCourse;
	@FXML private MenuItem menuAddSection;
	@FXML private MenuItem menuAddClassTime;
	@FXML private MenuItem menuDelete;
	@FXML private MenuItem menuCollapse;
	
	private LoadUNBCoursesController LoadUNBController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		initializeContextMenu();
		setKeyBindings();
		
		//Get UNB Choices (only once) in a separate thread to use in the UNB Load Data window
		ChoiceLoader service = new ChoiceLoader();
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            //*This runs after thread is finished
            	ComboBoxChoice[][] choices = (ComboBoxChoice[][])t.getSource().getValue();
            	LoadUNBCoursesController.setChoices(choices);
            	if (LoadUNBController!=null) {
            		LoadUNBController.initializeSelects();
            	}
//            	System.out.println("LOADED!");
            }
        });
        service.start();
	}
	
	private class ChoiceLoader extends Service<ComboBoxChoice[][]> {
        protected Task<ComboBoxChoice[][]> createTask() {
            return new Task<ComboBoxChoice[][]>() {
                protected ComboBoxChoice[][] call() {
                	ComboBoxChoice[][] choices = null;
                	int i=0;
                	while (choices==null && i<3) {
                		//System.out.println("Load trial "+(++i));
                		choices = UNBCourseReader.getDropdownChoices();
                	}
                	return choices;
                }
            };
        }
    }
	
	public boolean closeWindow() {
		boolean result = App.showConfirmDialog("Do you really want to exit?\nAll unsaved data will be lost.", AlertType.WARNING);
		if (result) {
			Stage stage = (Stage)container.getScene().getWindow();
		    stage.close();
		    
		    //Closes ALL windows (this is to close the schedule result window)
		    Platform.exit();
		}
		return result;
	}
	
	//Called when clicking Quit in File menu.
	@FXML private void quit(ActionEvent event) {closeWindow();}
	
	private void initializeContextMenu() {
		contextMenu.setOnShowing(e -> {
			contextMenu.getItems().clear();
			TreeItem<Object> treeItem = treeCourseList.getSelectionModel().getSelectedItem();
			if (treeItem!=null) {
				String type = getObjectType(treeItem.getValue());
				if (type.equals("Course")) {
					contextMenu.getItems().addAll(menuAddSection, menuEditCourse, menuDelete, menuCollapse);
				}
				else if (type.equals("Section")) {
					contextMenu.getItems().addAll(menuAddClassTime, menuEditSection, menuDelete, menuCollapse);
				}
				else if (type.equals("ClassTime")) {
					contextMenu.getItems().addAll(menuEditClassTime, menuDelete, menuCollapse);
				}
			}
			else {
				contextMenu.getItems().add(menuAddCourse);
			}
		});
		contextMenu.setOnHidden(e -> {
			contextMenu.getItems().clear();
			contextMenu.getItems().add(menuAddCourse);
		});
	}
	
	private void setKeyBindings() {
		//Keybindings
		final KeyCombination ctrlShiftC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
		final KeyCombination ctrlShiftS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
		final KeyCombination ctrlShiftT = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
		final KeyCombination ctrlShiftG = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
		container.setOnKeyReleased(event -> {
			if (ctrlShiftC.match(event)) addCourse(null);
			else if (ctrlShiftS.match(event)) addSection(null);
			else if (ctrlShiftT.match(event)) addClassTime(null);
			else if (ctrlShiftG.match(event)) genSchedule();
			else if (event.getCode() == KeyCode.DELETE) deleteItem(new ActionEvent());
		});
		container.setOnKeyPressed(event -> {if (event.getCode() ==  KeyCode.ESCAPE) closeWindow();});
		treeCourseList.setOnKeyPressed(event -> {if (event.getCode() ==  KeyCode.ESCAPE) closeWindow();});
		
		App.setTooltipWithoutDelay(btnAddCourse, "Ctrl+Shift+C");
		App.setTooltipWithoutDelay(btnAddSection, "Ctrl+Shift+S");
		App.setTooltipWithoutDelay(btnAddClassTime, "Ctrl+Shift+T");
		App.setTooltipWithoutDelay(btnGenSchedule, "Ctrl+Shift+G");
	}
	
	@FXML
	//This is called when clicking on the Load UNB Data... menu item.
	private void loadUNBData() {
		//Can't use the openWindow method since I need a reference to the controller
		//to be able to call the method initializeSelects() when data is finished loading (see above).
		try {
			Parent window;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoadUNBCourses.fxml"));
			window = loader.load();
			//Controller reference
			LoadUNBController = loader.<LoadUNBCoursesController>getController();
			Stage stage = setStage(window, "Load UNB Data", 350, 260);
			stage.show();
			stage.setOnHidden(e -> {
				Schedule courseList = App.UNBCourseList;
				if (courseList!=null) {
					int numCourses = courseList.getSize();
					msgLabel.setText(numCourses+" UNB courses loaded for:\n"+courseList.getName()+".");
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			windowError();
		}
	}
	
	//These methods are called when the corresponding button is pressed.
	@FXML private void addCourse(MouseEvent event) {
		if (btnAddCourse.isDisabled()) return;
		try {
			Parent window;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEditCourse.fxml"));
			window = loader.load();
			AddEditCourseController controller = loader.<AddEditCourseController>getController();
			String title = "Add Course";
			
			//Potentially send data if editing
			TreeItem<Object> treeItem = treeCourseList.getSelectionModel().getSelectedItem();
			if (event==null && treeItem!=null && getObjectType(treeItem.getValue())=="Course") {
				//Send value of course to the window
				controller.setCourseToEdit((Course)treeItem.getValue());
				title = "Edit Course";
			}
			
			Stage stage = setStage(window, title, 425, 170);
			stage.show();
			controller.setFocus();
		} catch (IOException e) {
			e.printStackTrace();
			windowError();
		}
	}
	@FXML private void addSection(MouseEvent event) {
		if (btnAddSection.isDisabled()) return;
		try {
			Parent window;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEditSection.fxml"));
			window = loader.load();
			AddEditSectionController controller = loader.<AddEditSectionController>getController();
			String title = "Add Section";
			
			String focus = "ComboBox";
			//Send data if editing
			TreeItem<Object> treeItem = treeCourseList.getSelectionModel().getSelectedItem();
			if (event==null && treeItem!=null) {
				String objType = getObjectType(treeItem.getValue());
				if (objType=="Course") {
					//Adding section to a course
					controller.setCourseToAddTo((Course)treeItem.getValue());
					focus = "TextField";
				}
				else if (objType=="Section") {
					//Editing section
					controller.setCourseToAddTo((Course)treeItem.getParent().getValue());
					controller.setSectionToEdit((Section)treeItem.getValue());
					title = "Edit Section";
					focus = "TextField";
				}
			}
			
			Stage stage = setStage(window, title, 425, 180);
			stage.show();
			controller.setFocus(focus);
		} catch (IOException e) {
			e.printStackTrace();
			windowError();
		}
	}
	@FXML private void addClassTime(MouseEvent event) {
		if (btnAddClassTime.isDisabled()) return;
		try {
			Parent window;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEditClassTime.fxml"));
			window = loader.load();
			AddEditClassTimeController controller = loader.<AddEditClassTimeController>getController();
			String title = "Add Class Time";
			
			boolean focus = false;
			//Send data if editing
			TreeItem<Object> treeItem = treeCourseList.getSelectionModel().getSelectedItem();
			if (event==null && treeItem!=null) {
				String objType = getObjectType(treeItem.getValue());
				if (objType=="Section") {
					//Adding classtime to a section
					controller.setCourseToAddTo((Course)treeItem.getParent().getValue());
					controller.setSectionToAddTo((Section)treeItem.getValue());
				}
				else if (objType=="ClassTime") {
					//Editing a classtime
					controller.setCourseToAddTo((Course)treeItem.getParent().getParent().getValue());
					controller.setSectionToAddTo((Section)treeItem.getParent().getValue());
					controller.setClassTimeToEdit((ClassTime)treeItem.getValue());
					title = "Edit Class Time";
					focus = true;
				}
			}
			
			Stage stage = setStage(window, title, 500, 400);
			stage.show();
			if (focus) controller.setFocus();
		} catch (IOException e) {
			e.printStackTrace();
			windowError();
		}
	}
	@FXML private void genSchedule() {
		if (btnGenSchedule.isDisabled()) return;
		
		if (!isScheduleFormatCorrect(App.userSelection)) {
			return;
		}
		
		Parent window = null;
		FXMLLoader progressLoader = new FXMLLoader(getClass().getResource("/fxml/ProgressWindow.fxml"));
		try {window = progressLoader.load();}
		catch (IOException e1) {
			e1.printStackTrace();
			windowError();
			return;
		}
		
		ProgressWindowController progressController = progressLoader.<ProgressWindowController>getController();
		String title = "Computation Progress";
		Stage stage = setStage(window, title, 400, 100);
		stage.show();
		progressController.start(ScheduleArranger.MAX_TIME);
		
		final long startTime = System.nanoTime();
		
		Task<Schedule[]> task = new Task<Schedule[]>() {
			@Override
			protected Schedule[] call() throws Exception {
				return ScheduleArranger.getBestSchedules(App.userSelection);
			}
		};
		
		task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
			new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					Schedule[] schedules = (Schedule[]) t.getSource().getValue();
					
					if (progressController!=null) {
						progressController.stop();
					}
					
					genScheduleP2(schedules);
					
					if ((System.nanoTime()-startTime)/1000000000>=ScheduleArranger.MAX_TIME) {
						App.showNotification("The computation was stopped manually as it was taking too long. "
								+ "Therefore the displayed schedules are not guaranteed to be the most efficient. "
								+ "\nPlease remove some courses/sections and try again.", AlertType.WARNING);
					}
				}
			}
		);
		
		new Thread(task).start();
	}
	
	private void genScheduleP2(Schedule[] best) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/Schedule.fxml"));
		
		ScheduleController controller = new ScheduleController();
		controller.setBestSchedules(best);
		loader.setController(controller);
		
		ServiceLoader<FXSamplerConfiguration> configurationServiceLoader = ServiceLoader.load(FXSamplerConfiguration.class);
		
		Scene scene;
		try {
			scene = new Scene(loader.load(),785,920);
			Stage newStage = new Stage();
			
			scene.getStylesheets().add(SampleBase.class.getResource("fxsampler.css").toExternalForm());
	        for (FXSamplerConfiguration fxsamplerConfiguration : configurationServiceLoader) {
	        	String stylesheet = fxsamplerConfiguration.getSceneStylesheet();
	        	if (stylesheet != null) {
	            	scene.getStylesheets().add(stylesheet);
	        	}
	        }
			
			newStage.setScene(scene);
			newStage.setTitle("Schedule");
			newStage.show();
		} catch (IOException e) {
			windowError();
			e.printStackTrace();
		}
	}
	
	//This makes sure the format of the schedule makes sense before putting it through the algorithm
	private boolean isScheduleFormatCorrect(Schedule schedule) {
		boolean isCorrect = true;
		
		//Make sure schedule has at least one course
		if (schedule.getSize()<1) {
			App.showNotification("Course list must contain at least one course.", AlertType.ERROR);
			isCorrect = false;
		}
		
		for (int i=0; i<schedule.getSize() && isCorrect; i++) {
			Course course = schedule.getItem(i);
			
			//Make sure each course has a section
			if (course.getSize()<1) {
				App.showNotification("Each course must contain at least one section.", AlertType.ERROR);
				isCorrect = false;
			}
			
			for (int j=0; j<course.getSize() && isCorrect; j++) {
				Section section = course.getItem(j);
				
				//Make sure each section has a class time
				if (section.getSize()<1) {
					App.showNotification("Each section must contain at least one class time.", AlertType.ERROR);
					isCorrect = false;
				}
			}
		}
		
		return isCorrect;
	}
	
	//These methods are called when corresponding context menu item its clicked
	@FXML private void menuCourseClicked(ActionEvent event) {addCourse(null);}
	@FXML private void menuSectionClicked(ActionEvent event) {addSection(null);}
	@FXML private void menuClassTimeClicked(ActionEvent event) {addClassTime(null);}
	@FXML private void deleteItem(ActionEvent event) {
		TreeItem<Object> selectedItem = treeCourseList.getSelectionModel().getSelectedItem();
		if (selectedItem!=null) {
			Object item = selectedItem.getValue();
			String type = getObjectType(item);
			if (type.equals("Course")) {
				Course course = (Course)item;
				if (App.showConfirmDialog("Do you really want to delete "+course.getName()+"?", AlertType.WARNING)) {
					App.userSelection.remove(course);
				}
			}
			else if (type.equals("Section")) {
				Section section = (Section)item;
				List<Course> courses = App.userSelection.copyList();
				for (Course course: courses) {
					if (course.contains(section) && 
					App.showConfirmDialog("Do you really want to delete "+section.getName()+"?", AlertType.WARNING)) {
						course.remove(section);
					}
				}
			}
			else if (type.equals("ClassTime")) {
				ClassTime classTime = (ClassTime)item;
				List<Course> courses = App.userSelection.copyList();
				for (Course course: courses) {
					List<Section> sections = course.copyList();
					for (Section section: sections) {
						if (section.contains(classTime) && 
						App.showConfirmDialog("Do you really want to delete "+classTime.toString()+"?", AlertType.WARNING)) {
							section.remove((ClassTime)item);
						}
					}
				}
			}
			treeCourseList.getSelectionModel().clearSelection();
			refresh();
		}
	}
	
	@FXML private void collapseTreeView(ActionEvent event){
		for (int i = 0; i < treeCourseList.getExpandedItemCount(); i++) {
			TreeItem<?> item = treeCourseList.getTreeItem(i);
			if(item != null && !item.isLeaf())
		        item.setExpanded(false);
		}
	}
	
	private void resetButtons() {
		btnAddSection.setDisable(true);
		btnAddClassTime.setDisable(true);
	}


	private Stage setStage(Parent window, String title, int width, int height) {
		Scene scene = new Scene(window, width, height);
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setMinWidth(width+20);
		stage.setMinHeight(height+47);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setOnHiding(windowEvent -> createCourseList());
		stage.setScene(scene);
		return stage;
	}
	
	private void createCourseList() {
		resetButtons();
		
		// Root Item
		TreeItem<Object> rootItem = new TreeItem<>(new Course("List"));
		rootItem.setExpanded(true);
		btnGenSchedule.setDisable(true);
		
		for (Course course : App.userSelection.copyList()) {
			TreeItem<Object> courseCell = new TreeItem<>(course);
			courseCell.setExpanded(true);
			rootItem.getChildren().add(courseCell);
			btnAddSection.setDisable(false);
			
			for (Section section: course.copyList()) {
				TreeItem<Object> sectionCell = new TreeItem<>(section);
				sectionCell.setExpanded(true);
				courseCell.getChildren().add(sectionCell);
				btnAddClassTime.setDisable(false);
				
				for (ClassTime time: section.copyList()) {
					TreeItem<Object> timeCell = new TreeItem<>(time);
					timeCell.setExpanded(true);
					sectionCell.getChildren().add(timeCell);
					
					//Enable the "Generate" button only if have at least 1 class time.
					btnGenSchedule.setDisable(false);
				}
			}
		}
		
		//TreeView Setup
		treeCourseList.setCellFactory(e -> new TreeViewGenericCell<Object>());
		treeCourseList.setRoot(rootItem);
		treeCourseList.setShowRoot(false);
	}
	
	@SuppressWarnings("unused")
	private String getObjectType(Object item) {
		String type = null;
		try {
			Course course = (Course)item;
			type = "Course";
		}
		catch (Exception e1) {
			try {
				Section section = (Section)item;
				type = "Section";
			}
			catch (Exception e2) {
				try {
					ClassTime classtime = (ClassTime)item;
					type = "ClassTime";
				}
				catch (Exception e3) {
					//Should not come here
				}
			}
		}
		return type;
	}
	
	/**
	 * Opens a window to allow the selection of a file to be OPEN.
	 * It alters the global variable fileToLoad in App to reference the new file chosen
	 */
	@FXML
	private void selectFile(ActionEvent event) {
		FileSelect fileSelector = new FileSelect(container.getScene().getWindow(), "open");
		File fileToLoad = fileSelector.getFile();
		
		//Check if user entered something and file exists
		if (fileToLoad==null || !fileToLoad.exists()) {
			return;
		}
		
		//Check if file is valid type
		if (!FilenameUtils.getExtension(fileToLoad.getName()).equals("schedule")) {
			System.out.println(FilenameUtils.getExtension(fileToLoad.getName()));
			App.showNotification("Invalid file. Please choose a valid file \nof type '.schedule'.", AlertType.ERROR);
			return;
		}
		
		ObjectInputStream objectStream = null;
		try {
			objectStream = new ObjectInputStream(new FileInputStream(fileToLoad));
		}
		catch (IOException e) {
			System.out.println("Error finding file or Error opening stream");
			e.printStackTrace();
			return ;
		}
		//Read the course list from the file
		Schedule courseList = null;
		try {
			courseList = (Schedule) objectStream.readObject();
		}
		catch (Exception e) {
			System.out.println("Error reading data");
			e.printStackTrace();
			
			//Close the stream
			try {
				objectStream.close();
			}
			catch (IOException e2) {
				System.out.println("Error closing stream");
				e.printStackTrace();
			}
			return;
		}
		
		//Close the stream
		try {
			objectStream.close();
		}
		catch (IOException e) {
			System.out.println("Error closing stream");
			e.printStackTrace();
			return;
		}
		
		App.userSelection = courseList;
		refresh();
	}
	
	@FXML
	private void saveAs(ActionEvent event) {
		if(App.userSelection.getSize()<1) {
			App.showNotification("Course list must contain at least one course to save.", AlertType.ERROR);
			return;
		}
		// Opens the window allowing the user to set the name and path of the schedule file that is being saved.
		// It is only creating the reference. A FileInputStream is required to save the file to directory 
		FileSelect fileSelector = new FileSelect(container.getScene().getWindow(), "save");
		File saveAsFile = fileSelector.getFile();
		
		//Make sure user entered something
		if (saveAsFile==null) {return;}
		
		//Add proper extension if necessary
		if (!FilenameUtils.getExtension(saveAsFile.getName()).equals("schedule")) {
			String saveAsFileString= saveAsFile.getAbsolutePath();
			saveAsFile = new File(saveAsFileString+".schedule");
		}
		
		ObjectOutputStream objectStream = null;
		
		try {
			objectStream = new ObjectOutputStream(new FileOutputStream(saveAsFile));
		}
		catch (IOException e) {
			App.showNotification("Error creating file or Error opening Stream", AlertType.ERROR);

			System.out.println("Error creating file or Error opening stream");
			e.printStackTrace();
		}
		
		try {
			objectStream.writeObject(App.userSelection);
		}
		catch (IOException e) {
			App.showNotification("Error writing data", AlertType.ERROR);
			
			System.out.println("Error writing data");
			e.printStackTrace();
			
			//Try to delete the file:
			saveAsFile.delete();
		}
		
		//Close the stream
		try {
			objectStream.close();
		}
		catch (IOException e) {
			App.showNotification("Error closing stream", AlertType.ERROR);

			System.out.println("Error closing stream");
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void clear(ActionEvent event) {
		boolean choice = App.showConfirmDialog("This will clear your course list. All unsaved data will be lost.\n\n"
				+ "Continue?", AlertType.CONFIRMATION);
		if (choice) {
			App.userSelection.clear();
			refresh();
		}
	}
	
	private void refresh() {createCourseList();}
	
	private void windowError() {
		App.showNotification("An error occured while trying to open the window.\nPlease try again.", AlertType.ERROR);
	}
	
	@FXML
	private void openDocumentation() {
		InputStream documentationStream = getClass().getResourceAsStream("/fxml/Guide_for_Student_Scheduling_Assistant.pdf");
		Path tempOutput;
		try {
			tempOutput = Files.createTempFile("Documentation", ".pdf");
	        tempOutput.toFile().deleteOnExit();
	
	        Files.copy(documentationStream, tempOutput, StandardCopyOption.REPLACE_EXISTING);
	        File userManual = new File (tempOutput.toFile().getPath());
	        if (userManual.exists()) {
	            Desktop.getDesktop().open(userManual);
	        }
		} catch (IOException e) {e.printStackTrace();}
	}
}
