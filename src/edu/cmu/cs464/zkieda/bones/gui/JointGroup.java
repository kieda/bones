package edu.cmu.cs464.zkieda.bones.gui;

import edu.cmu.cs464.zkieda.bones.ik.Joint;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;

public class JointGroup extends Group {
    private List<JointPeer> selected = new ArrayList<>();
    private List<JointPeer> unselected = new ArrayList<>();
    private final SkeletonPeer skele;

    public JointGroup(SkeletonPeer skele) {
        this.skele = skele;
    }

    public void deselectAll() {
        int len = selected.size() - 1;
        for (; len >= 0; len--) {
            selected.get(len).getIsSelected().set(false);
        }
    }

    public void selectOne(JointPeer j) {
        int len = selected.size() - 1;
        for (; len >= 0; len--) {
            JointPeer n = selected.get(len);
            if (n == j) {
                continue;
            }
            n.getIsSelected().set(false);
        }
    }

    private void removeNode(JointPeer jn) {
        if (jn.getIsSelected().get()) {
            selected.remove(jn);
        } else {
            unselected.remove(jn);
        }
        skele.getBones().removeAttachedBones(jn);
        getChildren().remove(jn);
        
        //finally, remove this node from our peer.
        skele.getPeer().deleteJoint(jn);
    }

    void addNode(JointPeer jn) {
        if (jn.getIsSelected().get()) {
            selected.add(jn);
        } else {
            unselected.add(jn);
        }
    }

    public void transfer(JointPeer jn) {
        if (jn.getIsSelected().get()) {
            selected.add(jn);
            unselected.remove(jn);
        } else {
            unselected.add(jn);
            selected.remove(jn);
        }
    }

    public void removeAllSelected() {
        for (int i = selected.size() - 1; i >= 0; i--) {
            JointPeer j = selected.get(i);
            removeNode(j);
        }
    }
    
    //adds bones selected -> tj
    void addBonesToSelected(JointPeer tj) {
        List<JointPeer> allSelected = selected;

        for (JointPeer j : allSelected) {
            if (j == tj) {
                continue;
            }

            skele.getBones().addBone(j, tj);
        }
    }
    
    List<JointPeer> getSelected(){return selected;}
    
    public List<Joint> getAllJoints(){
        return skele.getPeer().getJoints();
    }
}
