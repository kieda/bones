package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.core.Appendage;
import edu.cmu.cs464.zkieda.bones.core.Diff;
import static edu.cmu.cs464.zkieda.bones.core.SkeletonCopy.*;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

public class CyclicCoordDescentIK implements IKType{
    @Override
    public String toString() {
        return "Cyclic Coord Descent";
    }
    @Override
    public IKInterpolator gen(Diff.JointMovement movement) {
        return new IKInterpolator() {
            //consider anything in 10 pixels good enough
            private final double convergenceRadiusSquared = 300;
            private List<Double> currentTheta = new ArrayList<>();
            private List<Vector2d> currentPos = new ArrayList<>();
            private List<Vector2d> initialPos = new ArrayList<>();
            private Vector2d unitX = new Vector2d(1.0, 0.0);
            private Vector2d target;
            private int N; // number of nodes we're using

            //multile calls to converge cause the solution to get closer.
            //we don't test for real convergence, we just hope we'll have enough
            private void converge() {
                for(int i = 1; i < N; i++) {
                    //current root : at i
                    Vector2d curRoot = currentPos.get(i);
                    
                    //root to our target
                    Vector2d targetDirection = new Vector2d(target);
                    
                    //root to our end effector
                    Vector2d currentDirection = new Vector2d(currentPos.get(0));
                    targetDirection.sub(curRoot);
                    currentDirection.sub(curRoot);
                    
                    double lenTarget = targetDirection.length();
                    double lenCurrent = currentDirection.length();
                    
                    //by def of dot prod
                    double cosTheta = targetDirection.dot(currentDirection) / 
                            (lenTarget * lenCurrent);
                    
                    double sinTheta = (currentDirection.x * targetDirection.y - currentDirection.y * targetDirection.x) / 
                            (lenTarget * lenCurrent);
                    
                    double rotTheta = Math.acos(Math.max(-1, Math.min(1, cosTheta)));
                    
                    {
                        double rotTheta2 = Math.asin(Math.max(-1, Math.min(1, sinTheta)));
                        rotTheta = rotTheta2 < 0 ?  -rotTheta : rotTheta;
                    }
                    //angle we should modify for all nodes above this root. 
                    //add this angle to the currentTheta[]
                    currentTheta.set(i, rotTheta + currentTheta.get(i));
                    
                    //we have updated the rotational value.
                    //we recompute the positions of rotations for each vertex
                    
                    if(rotTheta == 0 ) continue;
                    //we move everything wrt the initial position
                    //the total movement is determined by the current theta,
                    //which is the delta theta
                    
                    //1) rotate the root bone
                    {
                        double rootTheta = currentTheta.get(N - 1); // represents the total amount we want to rotate this bone
                        BoneCopy rootBone = movement.movable.bones[N - 2];
                        
                        double cosT = Math.cos(rootTheta);
                        double sinT = Math.sin(rootTheta);
                        
                        Vector2d src  = initialPos.get(N - 2);
                        
                        //re-scale to avoid errors.
                        Vector2d dst  = currentPos.get(N - 2);
                        
                        dst.normalize();
                        dst.scale(rootBone.length);
                        
                        //rotation via matrix
                        dst.x = src.x * cosT - sinT * src.y;
                        dst.y = src.x * sinT + cosT * src.y;
                    }
//                    System.out.println(currentTheta.stream().map(Math::toDegrees).collect(Collectors.toList()));
                    //2) rotate bones that are not the root
                    {
                        for(int j = N - 2; j >= 1; j--){
                            //(j, j-1) is bone
                            // j is already found, 
                            // j-1 is what we find on each iteration,
                            // j-1 is the current bone (j, j-1)
                            // j is theta to rotate j-1
                            // prev is the previous position. 
                            
                            double theta = currentTheta.get(j);
                            BoneCopy curBone = movement.movable.bones[j-1];
                            
                            double cosT = Math.cos(theta);
                            double sinT = Math.sin(theta);

                            Vector2d src  = currentPos.get(j);
                            //re-scale to avoid errors.
                            Vector2d dst  = currentPos.get(j-1);
                            
                            Vector2d dir = new Vector2d(initialPos.get(j-1));
                            dir.sub(initialPos.get(j));
                            dir.normalize();
                            dir.scale(curBone.length);
                            
                            dst.x = dir.x * cosT - sinT * dir.y + src.x;
                            dst.y = dir.x * sinT + cosT * dir.y + src.y;
                        }
                    }
                    {
                        double dx = currentPos.get(N-1).x - target.x;
                        double dy = currentPos.get(N-1).y - target.y;
                        if(dx * dx + dy * dy < convergenceRadiusSquared) return; // converge
                    }
                }
                //nothing after this - wait for another convergence!
            }

            private void converge(int i){
                //initalization of pos, theta, and target locations.
                for(JointCopy jc :  movement.movable.joints){
                    //for each jc, we copy the value of the current 
                    Vector2d it = new Vector2d(jc.xPos - movement.movable.root.xPos, jc.yPos - movement.movable.root.yPos);
                    currentPos.add(it);
                    initialPos.add(new Vector2d(it));
                    currentTheta.add(0.0);
                }
                target = new Vector2d(movement.termDX + initialPos.get(0).x, movement.termDY + initialPos.get(0).y);
                
                N = movement.movable.joints.length;
                for(int k = 0; k < i; k++) {
                    converge();
                    
                    double dx = target.x - currentPos.get(0).x;
                    dx = Math.abs(dx);
                    double dy = target.y - currentPos.get(0).y;
                    dy = Math.abs(dy);
                    
                    if(dx*dx + dy*dy < convergenceRadiusSquared) {
                        //"close enough."
                        System.out.println("converged.");
                        break;
                    }
                }
            }
            

            //solution is the theta we rotate each node
            private List<Double> getSolution(){
                //should probably be good
                converge(7);
                return currentTheta;
            }
            
            private final List<Double> thetas;
            private final List<Vector2d> basicTranslation;
            
            {
                thetas = getSolution();
                basicTranslation = simpleTranslation();
            }
            
            private List<Vector2d> simpleTranslation(){
                List<Vector2d> ret = new ArrayList<>();
                for(int i = 0; i < currentPos.size();i++){
                    Vector2d it = new Vector2d(currentPos.get(i));
                    it.sub(initialPos.get(i));
                    ret.add(it);
                }
                return ret;
            }
            
            
            //NOTE : we could use 'thetas' to move to the target more gracefully
            //... but we're not going to do that...
            @Override
            public synchronized void increment(double h) {
                //solution of rotation for each joint (excluding terminal node)
                List<Vector2d> translation = basicTranslation; //  movement.movable.rotate(jc, thetas.get(i));
                for(int count = 0; count < translation.size(); count++){
                    Vector2d t = translation.get(count);
                    //t is the translation that should be applied to 
                    //joint_{count} in the appendage
                    JointPeer peer = movement.movable.joints[count].peer;
                    peer.setTranslateX(peer.getTranslateX() + t.x*h);
                    peer.setTranslateY(peer.getTranslateY() + t.y*h);
                }
            }
        };
    }

}
