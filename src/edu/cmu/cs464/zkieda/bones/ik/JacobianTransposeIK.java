package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;

public class JacobianTransposeIK extends JacobianIK{

    @Override
    public String toString() {
        return "Jacobian Transpose";
    }

    //transpose pseudo-invert of the jacobian.
    @Override
    public double[][] invertJacobian(double[][] mat) {
        double [][] transpose = new double[mat[0].length][mat.length];
        for(int i = 0; i < mat.length; i++){
            for(int j =0; j < mat[i].length; i++) {
                transpose[j][i]= mat[i][j];
            }
        }
        return transpose;
    }
}
