package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

public interface IKType {
    
    //generate a new interpolator for the following movement
    IKInterpolator gen(Diff.JointMovement movement);
}
