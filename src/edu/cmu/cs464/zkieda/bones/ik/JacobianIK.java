package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Diff;
import edu.cmu.cs464.zkieda.bones.core.SkeletonCopy;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2d;

public abstract class JacobianIK implements IKType{
    
    //inverts the current matrix. 
    //different from the transpose IK to the pseudoinverse IK.
    public abstract double[][] invertJacobian(double[][] mat);
    
    
    public IKInterpolator gen(Diff.JointMovement movement) {
        return new IKInterpolator() {
            private final Vector2d unitY = new Vector2d(0.0, 1.0);
            private List<Double> currentTheta = new ArrayList<>();
            private List<Vector2d> currentPos = new ArrayList<>();
            private Vector2d target;
            private int N; // number of nodes we're using

            //multile calls to converge cause the solution to get closer.
            //we don't test for real convergence, we just hope we'll have enough
            private void converge(){
                // //temporary !
                // //works only for single joints!
                // Appendage A = movement.movable;
                // Vector2d b = new Vector2d(A.terminal.xPos - A.root.xPos,
                //     A.terminal.yPos - A.root.yPos);
                
                // Vector2d a = new Vector2d(b.x + movement.termDX, b.y + movement.termDY);
                // double movement = a.angle(b);
                // List<Double> soln = new ArrayList<>();
                
                // soln.add(0.0); // terminal should not change in theta
                // soln.add(movement); //root should move in theta

                for(int i = 1; i < N; i++){
                    //current root : i
                    Vector2d curRoot = currentPos.get(i);
                    Vector2d targetDirection = new Vector2d(target);
                    Vector2d currentDirection = new Vector2d(currentPos.get(0));
                    targetDirection.sub(curRoot);
                    currentDirection.sub(curRoot);
                    //angle we should modify for all nodes above this root. 
                    //add this angle to the currentTheta[]
                    double theta = currentDirection.angle(targetDirection);
                    currentTheta.set(i, theta + currentTheta.get(i));

                    for(int j = 0; j < i; j++){
                        //each j is a child of i, and will be modified
                        //by adding theta
                        Vector2d childOfRoot = currentPos.get(j);
                        Vector2d rootToChild = new Vector2d(childOfRoot);
                        rootToChild.sub(curRoot);
                        double childTheta = unitY.angle(rootToChild);
                        double distance = rootToChild.length();
                        childOfRoot.x = Math.cos(childTheta + theta)*distance + curRoot.x;
                        childOfRoot.y = Math.sin(childTheta + theta)*distance + curRoot.y;
                    }

                }
                //nothing after this - wait for another convergence!
            }

            private void converge(int i){
                //initalization of pos, theta, and target locations.
                for(SkeletonCopy.JointCopy jc :  movement.movable.joints){
                    //for each jc, we copy the value of the current 
                    currentPos.add(new Vector2d(jc.xPos - movement.movable.root.xPos, jc.yPos - movement.movable.root.yPos));
                    currentTheta.add(0.0);
                }
                target = new Vector2d(movement.movable.terminal.xPos,
                   movement.movable.terminal.yPos);
                N = movement.movable.joints.length;
                for(int k = 0; k < i; k++) converge();
            }
            

            //solution is the theta we rotate each node
            private List<Double> getSolution(){
                //should probably be good
                converge(7);
                // return soln;
                return currentTheta;
            }
            
            private final List<Double> thetas = getSolution();
            
            @Override
            public void increment(double h) {
                //solution of rotation for each joint (excluding terminal node)
                int i = 0;
                for(SkeletonCopy.JointCopy jc : movement.movable.joints){
                    List<Vector2d> translation =  movement.movable.rotate(jc, thetas.get(i));
                    for(int count = 0; count < translation.size(); count++){
                        Vector2d t = translation.get(count);
                        //t is the translation that should be applied to 
                        //joint_{count} in the appendage
                        JointPeer peer = movement.movable.joints[count].peer;
                        peer.setTranslateX(peer.getTranslateX() + t.x*h);
                        peer.setTranslateY(peer.getTranslateY() + t.y*h);
                    }
                    i++;
                }
            }
        };
    }
    
}
