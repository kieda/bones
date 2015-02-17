package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.ik.Bone;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.Group;

public class BoneGroup extends Group {
    private List<BonePeer> bones;
    private SkeletonPeer parent;
    
    public BoneGroup(SkeletonPeer parent){
        this.bones = new ArrayList<>(); 
        this.parent = parent;
    }
    
    /**
     * deselects all bones and all joints
     */
    public void deselectAllBones(){
        bones.forEach(bone -> bone.setSelected(false));
    }
    
    public void selectOneBone(JointPeer j1, JointPeer j2){
        final BonePeer bi = new BonePeer(parent, j1, j2);
        selectOneBone(bi);
    }
    
    public void selectOneBone(BonePeer bi){
        bones.forEach(
            bone -> {
                bone.setSelected(bone.equals(bi));
            });
    }
    
    public void addBone(JointPeer j1, JointPeer j2){
        BonePeer toAdd = new BonePeer(parent, j1, j2);
        for(int i = 0; i < bones.size(); i++){
            if(toAdd.equals(bones.get(i))){
                return;
            }
        }
        bones.add(toAdd);
        getChildren().add(toAdd);
        parent.getPeer().addBone(toAdd);
    }
    
    public void removeSelectedBones(){
        getChildren().removeIf(b -> {
            if(b instanceof BonePeer){
                return ((BonePeer)b).isSelected();
            } 
            return false;
        });
        
        bones.stream().filter(BonePeer::isSelected).forEach
            (parent.getPeer()::deleteBone);
        
        bones.removeIf(BonePeer::isSelected);
    }
    
    public void removeAttachedBones(final JointPeer tj){
        getChildren().removeIf(b -> {
            if(b instanceof BonePeer){
                BonePeer bone = (BonePeer)b;
                return bone.getEnd1().equals(tj) || 
                        bone.getEnd2().equals(tj);
            }
            return false;
        });
        final Predicate<BonePeer> filter = bone -> bone.getEnd1().equals(tj) || bone.getEnd2().equals(tj);
        
        bones.stream().filter(filter).forEach
            (parent.getPeer()::deleteBone);
        bones.removeIf(filter);
    }
}