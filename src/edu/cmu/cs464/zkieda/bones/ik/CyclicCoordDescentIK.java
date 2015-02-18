package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;
import static edu.cmu.cs464.zkieda.bones.core.SkeletonCopy.*;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;

/**
 * @author zkieda
 */
public class CyclicCoordDescentIK implements IKType{

    @Override
    public String toString() {
        return "Cyclic Coord Descent";
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
