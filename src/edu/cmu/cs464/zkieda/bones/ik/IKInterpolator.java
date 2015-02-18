package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

/**
 */
public interface IKInterpolator {
    //interpolate JointMovement over a certain delta h 
    // such that sum (delta h) = 1.0 of joint movement.
    
    //increment position by this amount. 
    //automatically update the peer 
    void increment(double h);
}
