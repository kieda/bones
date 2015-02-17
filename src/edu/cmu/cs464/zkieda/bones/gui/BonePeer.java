package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.ik.Bone;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class BonePeer extends Group{
    private final double boneRad = 20;
    private Line bonePeer;
    private Line innerBone;
    private BooleanProperty selected;
    private JointPeer end1, end2;
//    Bone peer;
//    
//    Bone getPeer(){return peer;}
    
    public BonePeer(final SkeletonPeer parent, JointPeer end1, JointPeer end2) {
        this.end1 = end1;
        this.end2 = end2;
        this.selected = new SimpleBooleanProperty();
        bonePeer = new Line(
            end1.xProperty().get(),
            end1.yProperty().get(),
            end2.xProperty().get(),
            end2.yProperty().get()
        );
        innerBone = new Line(
            bonePeer.getStartX(),
            bonePeer.getStartX(),
            bonePeer.getEndX(),
            bonePeer.getEndY()
        );
        
        bonePeer.setStrokeWidth(boneRad);
        bonePeer.setStrokeLineCap(StrokeLineCap.ROUND);
        bonePeer.setStroke(Color.DARKSLATEGRAY.brighter().saturate());
        
        bonePeer.startXProperty().bind(end1.translateXProperty().add(end1.xProperty()));
        bonePeer.startYProperty().bind(end1.translateYProperty().add(end1.yProperty()));
        
        bonePeer.endXProperty().bind(end2.translateXProperty().add(end2.xProperty()));
        bonePeer.endYProperty().bind(end2.translateYProperty().add(end2.yProperty()));
        
        
        innerBone.setStrokeWidth(boneRad / 2);
        innerBone.setStroke(Color.web("D0E2F2"));
        
        innerBone.startYProperty().bind(bonePeer.startYProperty());
        innerBone.startXProperty().bind(bonePeer.startXProperty());
        
        innerBone.endXProperty().bind(bonePeer.endXProperty());
        innerBone.endYProperty().bind(bonePeer.endYProperty());
        
        setOnMousePressed( evt -> {
            switch(evt.getButton()){
                case PRIMARY:
                    this.selected.set(true);
                    
                    if(!evt.isShiftDown()) {
                        parent.getBones().selectOneBone(this);
                        parent.getJoints().deselectAll();
                    }
                    
                    evt.consume();
            }
        });
        
        selected.addListener((obj, oldv, newv)-> {
            if(newv){
                innerBone.setStroke(Color.web("D0E2F2").saturate().saturate().brighter());
                bonePeer.setStroke(Color.DARKSLATEGRAY.saturate());
            } else{
                bonePeer.setStroke(Color.DARKSLATEGRAY.brighter().saturate());
                innerBone.setStroke(Color.web("D0E2F2"));
            }
        });
        
        getChildren().addAll(bonePeer, innerBone);
    }
    
    public JointPeer getEnd1() {
        return end1;
    }

    public JointPeer getEnd2() {
        return end2;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public int hashCode() {
        return end1.hashCode() + end2.hashCode();
    }

    public boolean equals(Object o) {
        if(o instanceof BonePeer){
            BonePeer other = (BonePeer)o;
            return (other.end1.equals(end1) && other.end2.equals(end2)) || 
                (other.end1.equals(end2) && other.end2.equals(end1));
        } else return false;
    }
}