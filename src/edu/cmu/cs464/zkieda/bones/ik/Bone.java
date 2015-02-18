package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.gui.BonePeer;

/**
 * @author zkieda
 */
public class Bone{
    private BonePeer peer;
    private double length;
    private Joint start, end;
    public Bone(BonePeer peer, Joint start, Joint end){
        this.peer = peer;
        this.start = start;
        this.end = end;
        double lenX = start.getxPos() - end.getxPos();
        double lenY = start.getyPos() - end.getyPos();
        
        length = Math.sqrt(lenX*lenX + lenY*lenY);
    }
    public BonePeer getPeer() {
        return peer;
    }
    public Joint getStart(){return start;}
    public Joint getEnd(){return end;}
    public double getLength(){return length;}
}