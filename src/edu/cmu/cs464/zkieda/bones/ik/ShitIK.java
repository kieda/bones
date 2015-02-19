package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;

/**
 * IK that finds a basic solution for the final position.
 * Does not actually follow what bones are supposed to do.
 */
public class ShitIK implements IKType{
    @Override
    public String toString() {
        return "Terrible IK";
    }

    @Override
    public IKInterpolator gen(Diff.JointMovement movement) {
        return new IKInterpolator() {
            private boolean done = false;
            private double tot = 0.0;

            @Override
            public void increment(double h) {
                tot+=h;
                if(tot > .9999999 && (!done)){
                    JointPeer jc = movement.movable.terminal.peer;
                    jc.setTranslateX(jc.getTranslateX() + movement.termDX);
                    jc.setTranslateY(jc.getTranslateY() + movement.termDY);

                    done = true;
                }
            }
        };
    }

}
