package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.ik.Skeleton;
import javafx.scene.layout.Pane;

/**
 * skeleton = bones union joints
 * 'peer' mimicks what's happening on the back end
 * @author zkieda
 */
public class SkeletonPeer {
    private Skeleton peer;
    private JointGroup joints;
    private BoneGroup bones;
    private Pane head;
    
    public Skeleton getPeer(){return peer;}
    
    public SkeletonPeer(Pane head) {
        this.head = head;
        this.joints = new JointGroup(this);
        this.bones  = new BoneGroup(this);
        
        head.getChildren().addAll(bones, joints);
        this.peer = new Skeleton(this);
    }

    public BoneGroup getBones() {
        return bones;
    }
    
    public JointGroup getJoints() {
        return joints;
    }
}