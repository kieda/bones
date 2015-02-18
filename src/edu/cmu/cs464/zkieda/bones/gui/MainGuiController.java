package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.core.Core;
import edu.cmu.cs464.zkieda.bones.ik.CyclicCoordDescentIK;
import edu.cmu.cs464.zkieda.bones.ik.IKType;
import edu.cmu.cs464.zkieda.bones.ik.JacobianInverseIK;
import edu.cmu.cs464.zkieda.bones.ik.JacobianTransposeIK;
import edu.cmu.cs464.zkieda.bones.ik.ShitIK;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * represents pretty much all front end activity.
 * Activity in the front-end is influenced by what's happening in 
 * 'core'. 
 */
public class MainGuiController implements Initializable {
    @FXML private BorderPane fullpane;
    @FXML private ChoiceBox<IKType> iktypes;
    @FXML private Slider bottomSlider;
    @FXML private ToggleButton playPauseButton;
    @FXML private Pane bonepane;
    @FXML private Button keyframeButton;
    
    private final List<IKType> allIKTypes;
    private final IKType defaultIK;
    
    private Node playGraphic, pauseGraphic;
    private Core core;
            
    private static Node getGraphic(String resource, 
            String altName) {
        try{
            File f = new File(resource);
            Image image = new Image(f.toURI().toString());
            ImageView iv = new ImageView(image);
            iv.setFitHeight(24);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            
            return iv;
        } catch(Exception e){
            return new Text(altName);
        }
    }
    
    public MainGuiController(){
        IKType ccd = new CyclicCoordDescentIK();
        
        allIKTypes = new ArrayList<>();
        allIKTypes.add(ccd);
        
        allIKTypes.add(new JacobianTransposeIK());
        allIKTypes.add(new JacobianInverseIK());
        allIKTypes.add(new ShitIK());
        
        defaultIK = ccd;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        core = (Core)rb.getObject("Core");
        
        {
            //listener for changes on the IK type
            //change it when necessary
            iktypes.valueProperty().addListener(
                (val, oldval, newval) -> {
                    // set the inverse kinematic type somewhere
                    
                    //when we change the ik, set the state back to 
                    //the beginning.
                    core.currentTime().set(0);
                    core.setIK(newval);
                }
            );
            
            iktypes.setValue(defaultIK);
            iktypes.getItems().addAll(allIKTypes);
        }
        
        {
            playGraphic = getGraphic("./resources/play.png", "Play");
            pauseGraphic = getGraphic("./resources/pause.png", "Pause");
        
            playPauseButton.setGraphic(playGraphic);
            playPauseButton.selectedProperty().addListener(
                (val, oldval, newval) -> {
                    if(newval){
                        core.animate();
                        playPauseButton.setGraphic(pauseGraphic);
                    } else{
                        playPauseButton.setGraphic(playGraphic);
                    }
                }
            );
            
            //isPlaying iff playPauseButton is on. 
            playPauseButton.selectedProperty().bindBidirectional(core.isPlaying());
        }
        
        {
            //when the bottom slider is at the max position, stop playing
            bottomSlider.valueProperty().addListener(
                (val, oldval, newval) -> {
                    if(newval.doubleValue()>=bottomSlider.getMax()){
                        playPauseButton.setSelected(false);
                    }
                });
            
            //bound to the current time.
            bottomSlider.valueProperty().bindBidirectional(core.currentTime());
        }
        
        {
            
            core.init(bonepane);
            
            //add new joint
            bonepane.addEventHandler(MouseEvent.MOUSE_PRESSED, 
                evt -> {
                    switch(evt.getButton()){
                        case PRIMARY:
                            if(evt.isShiftDown()){
                                JointPeer jp = new JointPeer(core.getSkeleton(), evt.getSceneX(), evt.getSceneY());
                                jp.setTranslateX(evt.getX() - evt.getSceneX());
                                jp.setTranslateY(evt.getY() - evt.getSceneY());
                                core.getJoints().getChildren().add(jp);
                            }
                            core.getJoints().deselectAll();
                            core.getSkeleton().getBones().deselectAllBones();
                            break;
                        case SECONDARY:
                            break;
                    }
                });
            
            fullpane.addEventHandler(KeyEvent.KEY_PRESSED,
                t -> {
                    switch(t.getCode()){
                        case BACK_SPACE:
                        case DELETE:
                            core.getSkeleton().getBones().removeSelectedBones();
                            core.getJoints().removeAllSelected();
                            break;
                    }
                });

        }
        
        {
            keyframeButton.setOnAction(
                t -> {
                    core.addCurrentFrame();
                }
            );
        }
    }
}
