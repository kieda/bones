package edu.cmu.cs464.zkieda.bones.core;

import java.util.concurrent.Semaphore;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

public class Core {
    private final DoubleProperty currentTime = 
            new SimpleDoubleProperty();
    private final BooleanProperty isPlaying =
            new SimpleBooleanProperty();
    private Timeline timelineAnimation =
            new Timeline(60);
    
    //todo structure for main animation
    //have skeleton, timeline
    {
        //as the current time of the animation moves forward, we
        //move forward the global time.
        timelineAnimation.currentTimeProperty().addListener(
            (obj, oldv, newv) -> {
                currentTime.set(newv.toSeconds());
            });
        
        timelineAnimation.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO),
            new KeyFrame(Duration.seconds(5))
        );
        
        currentTime.addListener(
            (obj, oldv, newv) -> {
                timelineAnimation.jumpTo(new Duration(newv.doubleValue()*1000));
            }
        );
        
        isPlaying.addListener(
            (v, ol, ne) -> {
                if(ne){
                    timelineAnimation.play();
                } else{
                    timelineAnimation.pause();
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
}
