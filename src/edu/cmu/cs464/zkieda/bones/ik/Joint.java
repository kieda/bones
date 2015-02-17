package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import edu.cmu.cs464.zkieda.bones.gui.JointType;
import java.util.ArrayList;
import java.util.List;

public class Joint{
    private JointPeer peer;
    private JointType thisJointType;
    private List<Bone> incoming, outgoing;
    private Skeleton skeleton;
    
    public JointType getJointType(){
        return thisJointType;
    }
    
    List<Bone> getIncoming() {
        return incoming;
    }

    List<Bone> getOutgoing() {
        return outgoing;
    }
    
    Joint(Skeleton skele, JointPeer peer){
        this.peer = peer;
        this.skeleton = skele;
        
        thisJointType = JointType.TERM;
        
        incoming = new ArrayList<>();
        outgoing = new ArrayList<>();
    }
    
    JointPeer getPeer(){return peer;}
    
    void setJointType(JointType newJointType){
        thisJointType = newJointType;
        peer.applyStyle(newJointType);
    }
    
    //origin of where we initially clicked
    private double originX, originY;
    
    //begins translation. The origin represents the initial positioon that
    //we used when the mouse was pressed.
    //invariant : this is called first
    public void beginTranslation(double originX, double originY){
        this.originX = originX;
        this.originY = originY;
        
        //start the initial translation - these are the nodes that will be 
        //affected
        switch(thisJointType){
            case SKELE:
                for(Joint j : skeleton.getReachableJoints(this)){
                    j.peer.setTranslation0();
                }
                break;
            case MID:
                for(Joint j : skeleton.getChildren(this)){
                    j.peer.setTranslation0();
                }
                break;
            case TERM:
                this.peer.setTranslation0();
                break;
        }
    }
    
    //attempts to translate this joint as close as possible to the target
    //position. only modifies this joint, and the children of this joint.
    //in the case that this joint is a skeleton node, we translate our entire
    //skeleton in the requested direction.
    
    //the x and y position represent the position with repsect to the scene
    //difference should be orixinX - ptx, originY - pty
    public void attemptTranslation(double ptx, double pty){
        if(ptx < peer.getRadius() || pty < peer.getRadius()) return;
        if(ptx + peer.getRadius() > peer.getScene().getWidth()) return;
        if(pty + peer.getRadius() > peer.getScene().getHeight()) return;
        
        double dx = ptx - originX;
        double dy = pty - originY;
       
        switch(thisJointType){
            case SKELE:{
                //translate all connected components
                for(Joint j : skeleton.getReachableJoints(this)){
                    j.peer.translate(dx, dy);
                }
                break;
            }case MID:
                //translate children as well
                for(Joint j : skeleton.getChildren(this)){
                    j.peer.translate(dx, dy);
                }
                break;
            case TERM:
                //just translate this
                peer.translate(dx, dy); 
                break;
        }
    }
    
    
    
    public boolean isSkeleJoint(){
        return thisJointType == JointType.SKELE;
    }
    
    public boolean isMidJoint(){
        return thisJointType == JointType.MID;
    }
    
    public boolean isTermJoint(){
        return thisJointType == JointType.TERM;
    }
    public boolean isSingletonTerminal(){
        return isTermJoint() && incoming.isEmpty();
    }
    public Joint getParent(){
        switch(thisJointType){
            case SKELE:
                return null;
            case MID:
                assert incoming.size() == 1;
                //return parent.getRoot()
                //assert we have 1 parent
                return incoming.get(0).getStart();
            case TERM:
                assert incoming.size() <= 1;
                //either our direct parent, or NONE if we are headless
                return incoming.isEmpty() ? null : incoming.get(0).getStart();
        }
        
        //should never reach here
        throw new AssertionError();
    }
    
    public Joint getRoot(){
        Joint parent = getParent();
        return parent==null? this : parent.getRoot();
    }
    
    public boolean attachedToSkele(){
        return getRoot().isSkeleJoint();
    }
    
    public Joint getChild(){
        switch(thisJointType){
            case SKELE: return null;
            case MID: 
                assert outgoing.size() == 1;
                return outgoing.get(0).getEnd();
            case TERM: 
                assert outgoing.isEmpty();
                return null;
        }
        throw new AssertionError();
    }
}
