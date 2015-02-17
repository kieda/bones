package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.MainGuiController;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * @author zkieda
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try{
            final ResourceBundle rb = new ListResourceBundle() {
                private final Object[][] resources = {
                    {"Core", new Core()}
                };
                @Override
                protected Object[][] getContents() {
                    return resources;
                }
            };
            
            BorderPane mainPane = (BorderPane)FXMLLoader.load(MainGuiController.class.getResource
                ("maingui.fxml"), rb);
            
            Scene scene = new Scene(mainPane);
        
            primaryStage.setTitle("Bones");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(IOException e){
            
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
