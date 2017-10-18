package dbcw;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
public final class DatabaseSelector extends Application {
 
    private Desktop desktop = Desktop.getDesktop();
    private FileWriter writeDB;
 
    @Override
    /**
     * This handles all the application GUI components.
     */
    public void start(final Stage stage) {
        stage.setTitle("Database File Selection Dialog");
 
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Select Database File");
        TextArea information = new TextArea("Please select a database (.db) file...");
        information.setEditable(false);
        information.setWrapText(true);
        information.setPrefSize(490, 395);
        openButton.setPrefSize(490, 90);
 
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(stage);
                    information.setText("Please select a database (.db) file...");
                    if (file != null) {
                    	if (file.isDirectory())
                        openDirectory(file);
                    	else{
                    		if(isDbFile(file.getName())){
                    			writeDB = new FileWriter(file.getAbsolutePath());
                    			information.setText(information.getText() + "\nDatabase connection successful...");
                    			if (writeDB.writeToFile()){
                    				information.setText(information.getText() + "\nDatabase backup successful...");
                    				information.setText(information.getText() + "\nBackup File: " + new String(file.getAbsolutePath().substring(0 ,file.getAbsolutePath().length()-3) + "_backup.txt"));
                    				openButton.setText("Select Another Database File");
                    			}
                    		}
                    		else{
                    			information.setText("Please select a database (.db) file, no other filetype is permitted...");
                    		}
                    	}
                    }
                }
            });

        BorderPane informationPane = new BorderPane();
        informationPane.setStyle("");
        informationPane.setPadding(new Insets(5, 5, 5, 5));
        informationPane.setTop(information);
        informationPane.setBottom(openButton);
        Scene scene = new Scene(informationPane, 500, 500);
        scene.getStylesheets().add(this.getClass().getResource("controlStyle_DB_Backup.css").toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("MyFace.png")));
        stage.show();
    }
 
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    /**
     * Will check if the given file name is a database file
     * @param filename The name of the database file one wants to backup
     * @return Returns true if the file is a database file
     */
    private boolean isDbFile(String filename){
    	if (filename.substring(filename.length()-3).equals(".db"))
    		return true;
    	else
    		return false;
    }
 
    /**
     * Will open the directory in navigation to the desired file
     * @param file The directory to be opened.
     */
    private void openDirectory(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                DatabaseSelector.class.getName()).log(
                    Level.SEVERE, null, ex
                );
        }
    }
}
