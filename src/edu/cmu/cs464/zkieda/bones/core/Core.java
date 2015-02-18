package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.JointGroup;
import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import edu.cmu.cs464.zkieda.bones.gui.JointType;
import edu.cmu.cs464.zkieda.bones.gui.SkeletonPeer;
import edu.cmu.cs464.zkieda.bones.ik.Bone;
import edu.cmu.cs464.zkieda.bones.ik.IKInterpolator;
import edu.cmu.cs464.zkieda.bones.ik.IKType;
import edu.cmu.cs464.zkieda.bones.ik.Joint;
import edu.cmu.cs464.zkieda.bones.ik.Skeleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Core {
    private SkeletonPeer skeleton; 
    private JointGroup joints;
    private Pane pane;
    public void init(Pane pane){
        this.pane = pane;
        this.skeleton = new SkeletonPeer(pane);
        this.joints = skeleton.getJoints();
    }

    public SkeletonPeer getSkeleton() {
        return skeleton;
    }

    public JointGroup getJoints() {
        return joints;
    }
    
    
    private final DoubleProperty currentTime = 
            new SimpleDoubleProperty();
    private final BooleanProperty isPlaying =
            new SimpleBooleanProperty();
    
    private Timeline animation = new Timeline(60);
    private SortedMap<Double, SkeletonCopy> timeline = new TreeMap<>();
    
    //todo structure for main animation
    //have skeleton, timeline
    {
        //as the current time of the animation moves forward, we
        //move forward the global time.
        animation.currentTimeProperty().addListener(
            (obj, oldv, newv) -> {
                currentTime.set(newv.toSeconds());
            });
        
        animation.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO),
            new KeyFrame(Duration.seconds(5))
        );
        
        currentTime.addListener(
            (obj, oldv, newv) -> {
                animation.jumpTo(new Duration(newv.doubleValue()*1000));
            }
        );
        
        isPlaying.addListener(
            (v, ol, ne) -> {
                if(ne){
                    animation.playFromStart();
//                    animation.play();
                } else{
                    animation.pause();
                }
            }
        );
    }
    
    public BooleanProperty isPlaying(){
        return isPlaying;
    }
    
    public DoubleProperty currentTime(){
        return currentTime;
    }
    
    public void addCurrentFrame(){
        addFrame(currentTime().get(), skeleton.getPeer());
    }
    
    private void addFrame(double time, Skeleton val){
        timeline.put(time, new SkeletonCopy(val));
        dirty = true;
    }
    
    //frame every 60 millis
    private final static long resolution = 100;
    private IKType ik;
    private boolean dirty = true;
    private DiffTimeline dt;
    
    public void setIK(IKType ik){
        this.ik = ik;
        dirty = true;
    }
    
    private double curtime = 0;
    private double prevtime;
    public void animate(){
        if(!dirty) return;
        if(timeline.size() == 0){
            //just stay in the same position.s
            animation.getKeyFrames().clear();
            animation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO),
                new KeyFrame(Duration.seconds(5))
            );
            return;
        }
        
        //otherwise generate a timeline that will update the sketon, starting
        //from the beginning
        curtime=0;
        prevtime=0;
        dt = new DiffTimeline(timeline);
        
        //for every XX millis, add in a new keyframe that has 
        //performs an action that positions the current skeleton
        animation.getKeyFrames().clear();
        
        //todo get for arb index
        KeyFrame initial = new KeyFrame(Duration.ZERO,
            (evt -> {
                //apply diff to skeleton0
                //skeleton0 is the skeleton reconstructed from 
                //the first keyframe.
                
                //1. skeleton0 := firstkeyframe.skeletoncopy.reconstruct(skeleton)
                //2. for diffi in diffs
                //    2a. apply translation to skeleton0
                //      skeleton(i-1) -> skeletoni
                //      (this is an event that occurs)
                
                double time0 = timeline.firstKey();
                SkeletonCopy skelecopy0 = timeline.get(time0);
                
                skelecopy0.reconstruct(skeleton);
            }));
        
        animation.getKeyFrames().add(initial);
        
        for(Diff d : dt.diffs){
            addKeyFrame(d);
        }
        
        double timeremaining = 5.0 - curtime;
        if(timeremaining > 0){
            //add a keyframe that does nothing
            animation.getKeyFrames().add(new KeyFrame(Duration.seconds(5.0)));
        }
        
        dirty = false;
    }
    
    /*
     * adds a keyframe to the timeline animation thru a percentage of the 
     * diff. 
     */
    private void addFrame(final Diff d, double dh, List<IKInterpolator> ap){
        //time for frame
        double time = curtime + dh;
        //assert 0 <= dh < d.dt
        
        //amount we have stepped thru the frame since last time
        double amount = (time - prevtime)/d.dt;
  
        
        //add event : modify the current skeleton.
        //currently : keyframe just modifies the translation as appropriate
        //we do not uase ik (yet) to interpolate the joints.
        animation.getKeyFrames().add(new KeyFrame(
            Duration.seconds(time), 
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //modify the current skeleton appropriately to fit the diff
                    for (Diff.NodeMovement nm : d.skeleMovements) {
                        JointPeer np = nm.node.peer;
                        np.setTranslateX(np.getTranslateX() + amount*nm.dx);
                        np.setTranslateY(np.getTranslateY()+ amount*nm.dy);
                    }

                   int i = 0;
                   for(Diff.JointMovement jm : d.terminalMovements){
                       //the interpolator for this joint movement
                       IKInterpolator interp = ap.get(i);
                       interp.increment(amount); 
                       //increment the movement by the amount wanted
                       //this should move the appendage fwd as part of this 
                       //event.
                       
                       for(SkeletonCopy.JointCopy appendageJoint : jm.movable.joints){
                           if(appendageJoint.jointType != JointType.SKELE) {
                                JointPeer np = appendageJoint.peer;
                                np.setTranslateX(np.getTranslateX() + amount*jm.dx);
                                np.setTranslateY(np.getTranslateY() + amount*jm.dy);
                           }
                       }
                       i++;
                   }
                }
        }));
        prevtime = time;
       
    }
    
    private final double stepsize = .025;//~40 frames per second
    
    private void addKeyFrame(final Diff d){
        double elapsed = 0;
        
        
        //make an interpolator for each movement
        List<IKInterpolator> ap = d.terminalMovements.stream().map(ik::gen).collect(Collectors.toList());
        
        while(elapsed < d.dt){
            addFrame(d, elapsed, ap);
            elapsed += stepsize;
        }
    
        
        //add in final frame
        addFrame(d, d.dt, ap);
        
        curtime+=d.dt;
    }
}