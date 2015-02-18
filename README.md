# Bones
IK and FX rigging tester with a neat UI

## Usage
Shift + Click => create a new joint, select multiple joints<br>
Delete => delete joint[s], bone[s]<br>
Ctrl + Click => create a bone from all selected joints to the target joint<br>
Click + Drag => move around joints, perform FK rigging<br>

== 
Keyframe: 
Keyframe the skeleton at different poses, and the system will solve an IK path for the end-effectors (terminal joints)

The joints with 0 lines are terminal joints, 1 line are middle joints, and 2 lines are skeleton joints.

The terminal joints are the end-effectors that are rigged using IK thru multiple keyframes. The middle joints also move as a result of the IK, and their positions are not captured explicitly in a keyframe. Skeleton joints serve as the 'root' of the skeleton, and are fixed with respect to one another.

## Screenshots
![alt text](https://raw.githubusercontent.com/kieda/bones/master/scrot1.png "")
![alt text](https://raw.githubusercontent.com/kieda/bones/master/scrot2.png "")
![alt text](https://raw.githubusercontent.com/kieda/bones/master/scrot3.png "")
