package edu.cmu.cs464.zkieda.bones.gui;

import javafx.scene.shape.Line;

/**
 * contains joint styles that we can apply
 * 
 * we also use this enum in the backend to determine how we should be applying 
 * IK and FK rigging.
 */
public enum JointType implements JointStyle{
    TERM{
        public void apply2(JointPeer tj) {}
    },
    SKELE{
        public void apply2(JointPeer tj){
            final Line xLine, yLine;
            xLine = tj.mkLine();
            yLine = tj.mkLine();
            yLine.setRotate(90);
            tj.getChildren().addAll(xLine, yLine);
        }
    },
    MID{
        public void apply2(JointPeer tj){
            final Line xLine;
            xLine = tj.mkLine();
            xLine.setRotate(45);
            tj.getChildren().add(xLine);
        }
    };
    
    abstract void apply2(JointPeer tj);
    public final void apply(JointPeer tj){
        removeAllLines(tj);
        apply2(tj);
        
    }
    private static void removeAllLines(JointPeer tj){
        tj.getChildren().removeIf(f -> f instanceof Line);
    }
    
}
