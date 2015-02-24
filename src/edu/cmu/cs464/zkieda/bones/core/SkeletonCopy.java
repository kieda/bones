package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import edu.cmu.cs464.zkieda.bones.gui.JointType;
import edu.cmu.cs464.zkieda.bones.gui.SkeletonPeer;
import edu.cmu.cs464.zkieda.bones.ik.Bone;
import edu.cmu.cs464.zkieda.bones.ik.Joint;
import edu.cmu.cs464.zkieda.bones.ik.Skeleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.layout.Pane;

public class SkeletonCopy{
    JointCopy[] joints;
    private Map<JointPeer, JointCopy> map = new HashMap<>();
    
    public static class BoneCopy{
        public double length;
        public JointCopy start, end;
        BoneCopy(Bone b, Map<JointPeer, JointCopy> map){
            length = b.getLength();
            start = map.get(b.getStart().getPeer());
            end = map.get(b.getEnd().getPeer());
        }
    }
    
    public static class JointCopy{
        public JointPeer peer;
        public BoneCopy[] incoming, outgoing;
        public JointType jointType;
        public double xPos, yPos;
        
        void init(Joint j, Map<JointPeer, JointCopy> map){
            xPos = j.getxPos();
            yPos = j.getyPos();
            
            peer = j.getPeer();
            
            incoming = new BoneCopy[j.getIncoming().size()];
            outgoing = new BoneCopy[j.getOutgoing().size()];
            for(int i = 0; i < j.getIncoming().size(); i++){
                incoming[i] = new BoneCopy(j.getIncoming().get(i), map);
            }
            for(int i = 0; i < j.getOutgoing().size(); i++){
                outgoing[i] = new BoneCopy(j.getOutgoing().get(i), map);
            }
            jointType = j.getJointType();
        }
    }
    
    public SkeletonCopy(Skeleton original){
        List<Joint> joints = original.getJoints();
        this.joints = new JointCopy[joints.size()];
        for(int i = 0; i < joints.size(); i++){
            this.joints[i] = new JointCopy();
            map.put(joints.get(i).getPeer(), this.joints[i]);
        }
        for(int i = 0; i < joints.size(); i++){
            this.joints[i].init(joints.get(i), map);
        }
    }
    
    public void reconstruct(SkeletonPeer gui){
        for(Joint jp : gui.getJoints().getAllJoints()){
            //for each joint in gui
            //find position of this joint
            JointCopy jc =  map.get(jp.getPeer());
            jp.getPeer().setTranslateX(jc.xPos - jc.peer.getCircle().getCenterX());
            jp.getPeer().setTranslateY(jc.yPos - jc.peer.getCircle().getCenterY());
        }
    }
}