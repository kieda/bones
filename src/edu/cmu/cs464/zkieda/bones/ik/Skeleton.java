package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.gui.BonePeer;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import edu.cmu.cs464.zkieda.bones.gui.JointType;
import edu.cmu.cs464.zkieda.bones.gui.SkeletonPeer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * back-end version of the skeletonpeer.
 * 
 * the skeletonpeer controls the joints that are added and removed to
 * the skeleton, and the peer controls the basic skeleton movement. The 
 * skeleton decides the types of the joints, and has the 'final say' on the
 * actual positions of the joints. By 'final say', I mean that the peer
 * suggests movements to the skeleton, and the skeleton will then perform 
 * an appropriate movement that does not violate the constraints of the 
 * skeleton.
 */
public class Skeleton {
    private SkeletonPeer peer;
    private List<Joint> joints = new ArrayList<Joint>();

    public List<Joint> getJoints() {
        return joints;
    }
    
    //returns all children belonging to this base node
    Iterable<Joint> getChildren(final Joint base){
        return () -> new Iterator<Joint>(){
            private boolean hasNext = base != null;
            private Joint next = base;
            
            private void findNext(){
                next = next.getChild();
                hasNext = next != null;
            }
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Joint next() {
                Joint next = this.next;
                findNext();
                return next;
            }
        };
    }
    
    //for the connection algos, will need
    // 1. get all reachable points from a node (translation from a movement)
    // 2. get all joint parents that's not a skelenode (use getParent)
    //basic DFS
    //if this gets slow, change frontier to Joint. Don't add starting joints
    //for outgoing and ending joints for incoming.
    public Iterable<Joint> getReachableJoints(final Joint base){
        return () -> new Iterator<Joint>() {
            private boolean hasNext;
            private Joint next;
            private final Set<Joint> visited = new HashSet<>();
            private final Queue<Bone> frontier = new ArrayDeque<>();
            
            {
                if(base == null){
                    hasNext = false;
                } else{
                    visit(base);
                }
            }
            
            private void visit(Joint j){
                visited.add(j);
                frontier.addAll(j.getIncoming());
                frontier.addAll(j.getOutgoing());
                
                //haven't visited this node.
                next = j;
                hasNext = true;

                return;
            }
            
            private void grabNext(){
                while(!frontier.isEmpty()){
                    //get the bone off the top
                    Bone visit = frontier.remove();

                    Joint side1 = visit.getEnd();
                    if(!visited.contains(side1)){
                        visit(side1);
                        return;
                    }

                    Joint side2 = visit.getStart();
                    if(!visited.contains(side2)){
                        visit(side2);
                        return;
                    }
                }
                
                //we visited everything
                hasNext = false;
                next = null;
                return;
            }
            
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Joint next() {
                Joint next = this.next;
                grabNext();
                
                return next;
            }
        };
    }
    
    public Skeleton(SkeletonPeer peer){
        this.peer = peer;
    }
    
    
    //IMPORTANT
    //CONTRACT : we always delete the external bones before deleting this
    //joint. This will allow the system to be correct
    public void deleteJoint(JointPeer jp) {
        //todo re-evaluate node types
        joints.removeIf 
            (joint -> joint.getPeer().equals(jp));
        
    }
    
    public void deleteBone(final BonePeer bp){
        joints.stream().filter(joint -> 
            joint.getPeer() == bp.getEnd1() || joint.getPeer() == bp.getEnd2()
        ).forEach(joint -> {
            //joint has an endpoint that's part of the bonepeer.
            //delete the bone from the skeleton
            joint.getIncoming().removeIf(
                bone -> bone.getPeer() == bp
            );
            joint.getOutgoing().removeIf(
                bone -> bone.getPeer() == bp
            );
        });
        
        //todo update the node types
        //re-evaluate nodes at the endpoints.
        //remove constraints
        
        
    }
    
    public Joint addJoint(JointPeer jp){
        Joint addition = new Joint(this, jp);
        joints.add(addition);
        //should be a terminal. No extra modification necessary for the 
        //skeleton. 
        
        return addition;
    }
    
    private void skeleChain(Joint traver){
        if(traver == null) return;
        skeleChain(traver.getParent());
        traver.setJointType(JointType.SKELE);
    }
    
    public void addBone(BonePeer bp){        
        Joint A = null, B = null;
        for(Joint joint : joints){
            if(joint.getPeer().equals(bp.getEnd1())){
                //add bone from end1 to end2 
                A = joint;
                
            } else if(joint.getPeer().equals(bp.getEnd2())){
                //incoming to node 2
                B = joint;
            }
        }
       
        
        if(B.attachedToSkele()){
            skeleChain(A);
            skeleChain(B);
        } else if(A.isSingletonTerminal()){
            // A -> B
            // A is singelton, B is not attached. 
            // A becomes skele. B is terminal
            A.setJointType(JointType.SKELE);
        } else if(A.isTermJoint()){ 
            // A -> B
            // A is (non-singleton) terminal, B is not attached
            A.setJointType(JointType.MID);
        } else{
            A.setJointType(JointType.SKELE);
            // A -> B
            // A is not terminal, B is not attached
        }
        
        final Bone bone = new Bone(bp, A, B);
        A.getOutgoing().add(bone);
        B.getIncoming().add(bone);
        
        /*
         * when we draw a line from A to B
         * when B is already attached to a skele, A should also become a skele, along 
         * with the path that connects root(A) to root(B)
         * 
         * when B does not have a parent, then B must be a terminal node. 
         * If A terminal, A => JOINT 
         * Otherwise A stays the same, and B's parent 
         */
    }
}