package edu.cmu.cs464.zkieda.bones.ik;

/**
 * allows the user to create a timeline by multiple poses set up
 * @author zkieda
 */
public class KeyFrameTimeline {
    private double minTime, maxTime;
    public void addKeyFrame(Skeleton sk, double absoluteTime){
    }
    //Finding Differences between 2 skeletons. 
    // diff (s1 : Skeleton, s2 : Skeleton)
    // -> bones deleted, inserted
    // -> joints deleted, inserted
    // -> terminal node movement
    // -> translation information
    //    (these connected skeleton nodes were translated by this much. 
    //    subtract that dx, dy from the movement of connected, non-skele nodes)
    
    
    //deletions, insertions should be done with a fade (initially)
    
    
    class TimelineInfo{}
    
    //generate a timeline given the current keyframes. the timeline info 
    //returns a skeleton peer that can be changed over time. 
    public TimelineInfo makeTimeline(){return null;}
}
