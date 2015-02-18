package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

/**
 * IK based on a gravitational attraction force that a joint has to itself in 
 * a frame. 
 */
public class ShitIK implements IKType{
    @Override
    public String toString() {
        return "Terrible IK";
    }

    @Override
    public IKInterpolator gen(Diff.JointMovement movement) {
        return null;
    }

}
