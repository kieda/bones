package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

/**
 *
 * @author zkieda
 */
public class JacobianTransposeIK implements IKType{

    @Override
    public String toString() {
        return "Jacobian Transpose";
    }

    @Override
    public IKInterpolator gen(Diff.JointMovement movement) {
        return null;
    }
    
    
    
}
