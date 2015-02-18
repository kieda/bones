package edu.cmu.cs464.zkieda.bones.core;

/**
 * represents a 
 */
public class Appendage{
    public SkeletonCopy.JointCopy root, terminal;
    public SkeletonCopy.JointCopy[] joints;
    public SkeletonCopy.BoneCopy[] bones;

    Appendage(SkeletonCopy.JointCopy root, SkeletonCopy.JointCopy terminal, SkeletonCopy.JointCopy[] joints, SkeletonCopy.BoneCopy[] bones) {
        this.root = root;
        this.terminal = terminal;
        this.joints = joints;
        this.bones = bones;
    }
    
}