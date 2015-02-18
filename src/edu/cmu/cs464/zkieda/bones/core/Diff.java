package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.JointType;
import java.util.ArrayList;
import java.util.List;

public class Diff{
    //time elapsed
    double dt;
    
    //list of movements of skeleton nodes
    List<NodeMovement> skeleMovements;
    
    //list of movements of terminal nodes
    List<JointMovement> terminalMovements;
    
    
    Diff(double t1, SkeletonCopy s1, double t2, SkeletonCopy s2){
        skeleMovements = new ArrayList<>();
        terminalMovements = new ArrayList<>();
        dt = t2 - t1;
        
        //move all skeleton nodes
        for(SkeletonCopy.JointCopy jc : s1.joints){
            if(jc.jointType == JointType.SKELE || (jc.jointType == JointType.TERM && jc.incoming.length == 0)){
                for(SkeletonCopy.JointCopy jc2 : s2.joints){
                    if(jc.peer == jc2.peer){
                        //translate jc to jc2
                        skeleMovements.add(new NodeMovement(jc, jc2.xPos - jc.xPos, jc2.yPos - jc.yPos));
                        break;
                    }
                }
            }
        }
        
        //move all appendages
        for(SkeletonCopy.JointCopy jc : s1.joints) {
            if(jc.jointType == JointType.TERM &&jc.incoming.length == 1){
                for(SkeletonCopy.JointCopy jc2 : s2.joints){
                    if(jc.peer == jc2.peer){
                        SkeletonCopy.JointCopy terminal = jc;
                        List<SkeletonCopy.JointCopy> joints = new ArrayList<>();
                        List<SkeletonCopy.BoneCopy> bones = new ArrayList<>();
                        while(jc.jointType != JointType.SKELE){
                            joints.add(jc);
                            bones.add(jc.incoming[0]);
                            jc = jc.incoming[0].start;
                        }
                        SkeletonCopy.JointCopy root = jc;
                        SkeletonCopy.JointCopy rootFinal = root;
                        
                        //get root final
                        for(SkeletonCopy.JointCopy potentialFinalRoot :  s2.joints){
                            if(potentialFinalRoot.peer == root.peer){
                                rootFinal = potentialFinalRoot;
                                break;
                            }
                        }
                        
                        
                        terminalMovements.add(new JointMovement(
                            new Appendage(root, terminal, joints.toArray(new SkeletonCopy.JointCopy[0]), bones.toArray(new SkeletonCopy.BoneCopy[0])),
                            rootFinal, terminal, jc2.xPos - jc.xPos, jc2.yPos - jc.yPos));
                        break;
                    }
                }
            }
        }
    }
    
    static class NodeMovement{
        NodeMovement(SkeletonCopy.JointCopy node, double dx, double dy) {
            this.node = node;
            this.dx = dx;
            this.dy = dy;
        }
        
        SkeletonCopy.JointCopy node;
        
        double dx, dy;
    }
    
    public static class JointMovement extends NodeMovement{
        //appendage only moves with respect to its root.
        //subtract the root's movement from the movement of 
        //the terminal
        public double termDX, termDY;
        
        //termD represents the delta movement that only the terminal is going
        //thru. = dx - (drootx), dy - (drooty) 
        // droot = rootfinal - rootinitial
        
        
        //we pass the appendage, along with termDX to make the entire 
        //translation
        
        JointMovement(Appendage movable, SkeletonCopy.JointCopy rootFinal, SkeletonCopy.JointCopy node, double dx, double dy) {
            super(node, dx, dy);
            this.movable = movable;
            double drootx = rootFinal.xPos - movable.root.xPos;
            double drooty = rootFinal.yPos - movable.root.yPos;
            termDX = dx - drootx;
            termDY = dy - drooty;
            
            //dx and dy, the amount we move the nodes regularly, should be 
            //wrt the root.
            this.dx = drootx;
            this.dy = drooty;
        }
        
        
        //move around the terminal node of the appendage
        public Appendage movable;
    }
}