package edu.cmu.cs464.zkieda.bones.ik;

/**
 * IK based on a gravitational attraction force that a joint has to itself in 
 * a frame. 
 */
public class ShitIK implements IKType{
    @Override
    public String toString() {
        return "Newton's IK";
    }
}
