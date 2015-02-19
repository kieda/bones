package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

/**
 * @author zkieda
 */
public class JacobianInverseIK extends JacobianIK{

    @Override
    public String toString() {
        return "Jacobian Inverse";
    }

    @Override
    public double[][] invertJacobian(double[][] IK) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
