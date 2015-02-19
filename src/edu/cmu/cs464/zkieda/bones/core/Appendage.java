package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javafx.util.Pair;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 * represents a 
 */
public class Appendage{
    public SkeletonCopy.JointCopy root, terminal;
    public SkeletonCopy.JointCopy[] joints;
    public SkeletonCopy.BoneCopy[] bones;

    Appendage(SkeletonCopy.JointCopy root, SkeletonCopy.JointCopy terminal, SkeletonCopy.JointCopy[] joints, SkeletonCopy.BoneCopy[] bones) {
        this.root = root;
        this.terminal = terminal;
        this.joints = joints;
        this.bones = bones;
    }
    
    //we rotate all of the children of root in appendage by theta
    //the stream represents the applicable translation for each joint.
    //the translation IS padded, such that when we rotate around the root, 
    //we padd a sequence of (0,0) from the root of the appendage till the 
    //specified rotational root.
    public List<Vector2d> rotate(SkeletonCopy.JointCopy root, double theta){
        int max = -1;
        for(int i = joints.length - 1; i >= 0; i--){
            //from root to len-1 is the 
            if(joints[i].peer == root.peer) {
                max = i; 
                break;
            }
        }
        if(max < 0) throw new IllegalArgumentException("Root " + root + " is not in appendage!");
        
        List<Vector2d> re = new ArrayList<>();
        
        for(int i = 0; i < max; i++) {
            //rotate joint i  around root
            double diffX = joints[i].xPos - root.xPos;
            double diffY = joints[i].yPos - root.yPos;
            
            double distance = Math.sqrt(diffX * diffX + diffY * diffY);
            double thetaInitial = Math.atan2(diffY, diffX);
            double thetaFinal = thetaInitial + theta;
            Vector2d delta = new Vector2d(
                distance * Math.cos(thetaFinal) + root.xPos - joints[i].xPos,
                distance * Math.sin(thetaFinal) + root.yPos - joints[i].yPos);
            re.add(delta);
        }
        
        Vector2d NULL = new Vector2d(0, 0);
        for(int i = max; i < joints.length; i++){ // continue till we reach the root
            //root is at min.
            re.add(NULL);
        }
        return re;
    }
    
}