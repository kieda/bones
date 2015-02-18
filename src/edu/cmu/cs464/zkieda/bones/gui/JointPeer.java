package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.ik.Joint;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * represents a fx node that is a joint in our program
 * @author zkieda
 */
public class JointPeer extends Group{
    //TODO finish context menu for application. 
//    private final ContextMenu jointContextMenu = new ContextMenu();
    //changes this node to a skeleton joint, mid joint, or to a terminal joint
//    private final MenuItem toSkeleNode;
//    private final MenuItem toMidNode;
//    private final MenuItem toEndNode;
    
    //used, and should only be used by Joint.java
    private double translationX0, translationY0;
    public void setTranslation0(){
        this.translationX0 = getTranslateX();
        this.translationY0 = getTranslateY();
    }

    public double getTranslationX0() {
        return translationX0;
    }

    public double getTranslationY0() {
        return translationY0;
    }
    
    
    private JointStyle jointStyle = JointType.TERM;
    private final Circle node;
    private final BooleanProperty isSelected = new SimpleBooleanProperty();
    private final Joint peer;
    private final double radius = 10;
    
//    private double orgSceneX, orgSceneY;
//    private double orgTranslateX, orgTranslateY;
    
    public double getRadius() {
        return radius;
    }
    
    //circle. Have 'selected' effect. Have 'keyframe' effect.
    public JointPeer(final SkeletonPeer skeleton, double x, double y){
        final JointGroup jg = skeleton.getJoints();
        node = new Circle(x, y, radius);
        node.setStrokeWidth(1.0);
        node.setStroke(Color.BLACK);
        node.setFill(Color.AZURE);
        this.peer = skeleton.getPeer().addJoint(this);
        jg.addNode(this);
        
        
        getChildren().add(node);
        
        
        isSelected.addListener(
            (evt, oldval, newval) -> {
                if(newval){
                    //when selected, update to thicken 
                    node.setStrokeWidth(2.0);
                    node.setFill(Color.AZURE.saturate());
                } else{
                    node.setStrokeWidth(1.0);
                    node.setFill(Color.AZURE);
                }
                jg.transfer(this);
            });
        
        setOnMousePressed(
            t -> {
                switch(t.getButton()){
                    case PRIMARY:
                        if(t.isControlDown()) {
                            //if we're selected ignore that.
                            //otherwise add a bone from 1 to the other for each
                            jg.addBonesToSelected(this);
                        } 
                        
                        isSelected.setValue(true);

                        if(!t.isShiftDown()){
                            //deselect others
                            jg.selectOne(this);
                            skeleton.getBones().deselectAllBones();
                        }
                        
                        peer.beginTranslation(t.getSceneX(), t.getSceneY());
                        break;
                    case SECONDARY:
                        
                }
                t.consume();
            });
        
        
        setOnMouseDragged(
            t -> {
                
                double newPosX = t.getSceneX();
                double newPosY = t.getSceneY();
//                System.out.println(newPosX + " "+newPosY);
                //we attempt to move based on the translation of the new pont
                //what actually occurs depends on our skeleton.
                peer.attemptTranslation(newPosX, newPosY);
            });
        
        //menuitems for this node. WE allow the user to change node types
        //dynamically.
//        toSkeleNode = new MenuItem("To Skeleton Joint...");
//        toMidNode = new MenuItem("To Mid Joint...");
//        toEndNode = new MenuItem("To End Joint...");
//        
//        toSkeleNode.setOnAction(evt -> {
//            double xpos =  getTranslateX() + getLayoutX();
//            double ypos =  getTranslateY() + getLayoutY();
//            skeleton.getJoints().replaceNode(this, new SkeleJoint(skeleton, xpos, ypos));
//        });
//        toMidNode.setOnAction(evt -> {
//            double xpos =  getTranslateX() + getLayoutX();
//            double ypos =  getTranslateY() + getLayoutY();
//            skeleton.getJoints().replaceNode(this, new MidJoint(skeleton, xpos, ypos));
//        });
//        toEndNode.setOnAction(evt -> {
//            double xpos =  getTranslateX() + getLayoutX();
//            double ypos =  getTranslateY() + getLayoutY();
//            skeleton.getJoints().replaceNode(this, new TerminalJoint(skeleton, xpos, ypos));
//        });
        
    }
    
    //makes a horizontal line that goes thru the center of the node.
    protected Line mkLine(){
        Line xLine = new Line();
        xLine.endYProperty().bind(node.centerYProperty());
        xLine.startYProperty().bind(node.centerYProperty());
        
        xLine.startXProperty().bind(
                node.centerXProperty().subtract(getRadius()));
        xLine.endXProperty().bind(
                node.centerXProperty().add(getRadius()));
        return xLine;
    }

    public DoubleProperty xProperty(){
        return node.centerXProperty();
    }
    public DoubleProperty yProperty(){
        return node.centerYProperty();
    }
    
    public boolean isSelected(){
        return isSelected.get();
    }
    
    public BooleanProperty getIsSelected() {
        return isSelected;
    }
    
    public void applyStyle(JointStyle js){
        this.jointStyle = js;
        js.apply(this);
    }
    
    public JointStyle getJointStyle(){
        return jointStyle;
    }

    public Circle getCircle() {
        return node;
    }
    
    
    //apply a translation
    //this may seem roundabout, but this is what's necessary for javafx for 
    //some reason
    public void translate(double offsetX, double offsetY){
        double newTranslateX = translationX0 + offsetX;
        double newTranslateY = translationY0 + offsetY;
        setTranslateX(newTranslateX);
        setTranslateY(newTranslateY);
    }
    
}