package edu.cmu.cs464.zkieda.bones.gui;

//apply a style to a joint

import javafx.scene.shape.Line;

public interface JointStyle{
    public void apply(JointPeer tj);
}