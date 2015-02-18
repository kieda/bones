package edu.cmu.cs464.zkieda.bones.core;

import edu.cmu.cs464.zkieda.bones.gui.JointPeer;
import static edu.cmu.cs464.zkieda.bones.core.SkeletonCopy.*;
import edu.cmu.cs464.zkieda.bones.gui.JointType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * takes diff of things in our timeline. 
 * 
 * skeleton copies -> diff list
 * 
 * todo: 
 * diff list -> timeline that moves skeleton around.
 * 
 * @author zkieda
 */
class DiffTimeline {
    //makes up the timeline
    List<Diff> diffs;
    
    public DiffTimeline(SortedMap<Double, SkeletonCopy> timeline) {
        diffs = new ArrayList<>();
        Iterator<Map.Entry<Double, SkeletonCopy>> it = timeline.entrySet().iterator();
        Map.Entry<Double, SkeletonCopy> prev = null;
        while(it.hasNext()){
            Map.Entry<Double, SkeletonCopy> en = it.next();
            if(prev == null) {
                prev = en;
                continue;
            }
            diffs.add(new Diff(prev.getKey(), prev.getValue(), en.getKey(), en.getValue()));
            prev = en;
        }
    }
}