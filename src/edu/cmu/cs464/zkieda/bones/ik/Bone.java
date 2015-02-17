package edu.cmu.cs464.zkieda.bones.ik;

import edu.cmu.cs464.zkieda.bones.gui.BonePeer;

/**
 * @author zkieda
 */
public class Bone{
    private BonePeer peer;
    private double len;
    private Joint start, end;
    public Bone(BonePeer peer, Joint start, Joint end){
        this.peer = peer;
        this.start = start;
        this.end = end;
    }
    BonePeer getPeer() {
        return peer;
    }
    Joint getStart(){return start;}
    Joint getEnd(){return end;}
    double getLen(){return len;}
    
    Bone reverse(){
        return new Bone(peer, end, start);
    }
}