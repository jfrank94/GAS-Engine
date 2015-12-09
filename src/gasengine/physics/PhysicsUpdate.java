package gasengine.physics;


import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;  //float
//import org.joml.Vector3d; //double - not using
//just copying these for now; where is joml?

import java.lang.annotation.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.Pointer;
import org.lwjgl.PointerBuffer;

import gasengine.physics.BoundaryBox;


import gasengine.scene.Component;

//Do I need to build better array add/remove functions, or do they exist already?

class rbodpair {
    RigidBody first;
    RigidBody second;


    public rbodpair(RigidBody a, RigidBody b) {
        first = a;
        second = b;
    }

    public boolean rbodpairequals(rbodpair b) { //This function compares two pairs of rigidbodies and returns true if the two pairs contain the same objects in either order
        if ((this.first == b.first && this.second == b.second) || (this.first == b.second && this.second == b.first)) {
            return true;
        }
        return false;

    }

    public int whereiscollision() {
        float xdist = Math.abs(Math.min((first.box.maxx - second.box.minx), (first.box.minx - second.box.maxx)));
        float ydist = Math.abs(Math.min((first.box.maxy - second.box.miny), (first.box.miny - second.box.maxy)));
        float zdist = Math.abs(Math.min((first.box.maxz - second.box.minz), (first.box.minz - second.box.maxz)));

        if (xdist <= ydist && xdist <= zdist) {
            return 0; //if xs are closest, x collision
        }
        else if (ydist <= xdist && ydist <= zdist) {
            return 1;
        }
        return 2;
    }

}

public class PhysicsUpdate{

    public static ArrayList<RigidBody> MovingObjects = new ArrayList();//global RigidBody[] MovingObjects; //All objects which are not asleep will be in here.
    public static ArrayList<RigidBody> AllObjects = new ArrayList();//global RigidBody[] AllObjects; //This will keep track of every object. Add to this on rigidbody creation; remove on object deletion.
    //public static ArrayList CurrentCollisions = new ArrayList(); //this one should contain int[2]s; the position of an object in MovingObjects and the position of an object in AllObjects
    //or an array of RigidBody[2]s? since positions in those lists can change...
    //The above had way too much overhead on collision detection, changed to below
    public static ArrayList<rbodpair> CurrentCollisions = new ArrayList();
    public float Time;


    //global array of ordered pairs; index of item a in MovingObjects and item b in AllObjects. CurrentCollisions
    //CurrentCollisions - added to when a collision is found in collision detection, removed when a collision is successfully resolved in collision response


    public PhysicsUpdate (){ //this should only be created once, and before any components exist
    }


    public void CreatedNewObject(RigidBody b) {
        if (b.rigid == true){ //won't be added to moving objects then.
            this.AllObjects.add(b);
        }
        else {
            this.MovingObjects.add(b);
            this.AllObjects.add(b);
        }
    }


    public void Update(float time) { //This will be called every frame.

        //Order matters, think about it
        Time = time;

        this.Move();
        //perform position updates on objects in MovingObjects

        this.CollisionDetection(); //Find objects that are in overlapping positions and add them to a list

        this.CollisionResponse(); //Run through list and try to correct velocities of collided objects

        this.GoToSleep(); //put objects that have not moved in two frames to sleep

    }


    private void Move(){
        for (int a = 0; a < MovingObjects.size(); a++){
            MovingObjects.get(a).accelerate(Time);
            MovingObjects.get(a).move(Time);
            MovingObjects.get(a).getEntity().setPosition(MovingObjects.get(a).position);
        }
        //GoToSleep();
    }


    private void GoToSleep() { //This method is untested, but seems done.
        RigidBody cur;
        for(int a = 0; a < MovingObjects.size(); a++) {
            if(MovingObjects.get(a) != null) { //There's nothing in this space, moving on to the next a.
                cur = MovingObjects.get(a);
                if (cur.gettingsleepy == true) { //This item might be about to fall asleep.
                    if (cur.getEntity().getPosition(cur.position) == cur.lastposition) {
                        cur.gettingsleepy = false;
                        cur.asleep = true;
                        MovingObjects.remove(a);} //*** Better way to remove from array? Linked lists would be useful here, but not in collision detection
                    else { cur.gettingsleepy = false; } //at this point, the object has moved again, and is clearly not sleeping
                }
                else { if (cur.gettingsleepy == false) { //This item doesn't look like it's sleeping yet.
                    if (cur.getEntity().getPosition(cur.position) == cur.lastposition) {
                        cur.gettingsleepy = true;
                    } //It didn't move last frame; it might be sleeping. (It probably is, but there are some circumstances in which this is not the case.) Next frame will tell us for sure.
                }}  //Else nothing; the object is still moving and there's no need to change its state.

            }
        }
    }



    public void CollisionDetection() {

        RigidBody one;
        RigidBody two;
        Boolean check;
        int c = 0;

        // RigidBody[] testarrayforward = new RigidBody[2];
        // RigidBody[] testarraybackward = new RigidBody[2];

        for (int a = 0; a < MovingObjects.size(); a++) {
            //array list shouldn't contain any null values in the middle, and its values won't be altered during the execution of this function.
            one = MovingObjects.get(a);
            for (int b = 0; b < AllObjects.size(); b++) {
                two = AllObjects.get(b);

                //if in positive coordinates - how do we deal with negatives?
                if (one != two) { //An object can't collide with itself


                    if ( ((((one.box.minx+10000) <= (two.box.maxx+10000)) && ((one.box.maxx+10000) >= (two.box.minx+10000))) //the +10k part is just a really hasty way of dealing with negatives
                     && (((one.box.miny+10000) <= (two.box.maxy+10000)) && ((one.box.maxy+10000) >= (two.box.miny+10000)))
                     && (((one.box.minz+10000) <= (two.box.maxz+10000)) && ((one.box.maxz+10000) >= (two.box.minz+10000)))) ){
                    /**if ( ((((one.box.minx) <= (two.box.maxx)) && ((one.box.maxx) >= (two.box.minx)))
                            && (((one.box.miny) <= (two.box.maxy)) && ((one.box.maxy) >= (two.box.miny)))
                            && (((one.box.minz) <= (two.box.maxz)) && ((one.box.maxz) >= (two.box.minz)))) ) {**/
                        rbodpair testarrayforward = new rbodpair(MovingObjects.get(a), AllObjects.get(b)); //Not actually an array - it used to be, but I changed the variable type because the array had too big of an overhead. The name stuck.
                        //rbodpair testarraybackward = new rbodpair(AllObjects.get(b), MovingObjects.get(a)); //Rbodpair is a class that just holds references to two rigid bodies

                        //testarrayforward[0] = MovingObjects.get(a); //running checks here. just doing one/two didn't seem to add things properly.
                        //testarrayforward[1] = AllObjects.get(b);
                        //testarraybackward[0] = AllObjects.get(b);
                        //testarraybackward[1] = MovingObjects.get(a); //detection works properly - adding things to current collisions doesn't.
                        check = false; //setting it back to false, in case it had been true


                        if (CurrentCollisions.isEmpty() == false) { //Can't be already in there if the list is empty at the outset.
                            while (check == false && c < (CurrentCollisions.size() - 1)) {
                                for (c = 0; c < CurrentCollisions.size(); c++) {

                                    if (CurrentCollisions.get(c).rbodpairequals(testarrayforward) == true) {
                                        check = true;
                                    }//then it already exists in here, no need to add it again
                                } } } //end for, while, and if.

                        if (check == false) { //if it didn't find a match
                            CurrentCollisions.add(testarrayforward); //We have now detected a collision between two objects and added it to the list of unresolved collisons
                        }
                    }
                }
            }


        }

    }




    //Have - list of all collisions between an item a and an item b.
    //Each item has a velocity in an x, y, z direction, a weight, and a position. Also, whether or not the object is rigid or not

    public float givemeanopposite(float number){
        if (number < 0) {
            return 1;
        }
        return -1;
    }

    public void CollisionResponse() {

        RigidBody one; //One will /always/ be a moving object, from the way CollisionDetection finds collisions
        RigidBody two; //Two may or may not be a moving object - but if a collision has been detected, it should be wake up if it hasn't already done so and it's not a fixed position object
        float totalvelx;
        float totalvely;
        float totalvelz;
        int collistype;


        for (int a = 0; a < CurrentCollisions.size(); a++) {
            one = CurrentCollisions.get(a).first; //I hope this syntax works
            two = CurrentCollisions.get(a).second;

            if ( ((((one.box.minx+10000) <= (two.box.maxx+10000)) && ((one.box.maxx+10000) >= (two.box.minx+10000)))
                    && (((one.box.miny+10000) <= (two.box.maxy+10000)) && ((one.box.maxy+10000) >= (two.box.miny+10000)))
                    && (((one.box.minz+10000) <= (two.box.maxz+10000)) && ((one.box.maxz+10000) >= (two.box.minz+10000))))){
            /**if ( ((((one.box.minx) <= (two.box.maxx)) && ((one.box.maxx) >= (two.box.minx)))
                    && (((one.box.miny) <= (two.box.maxy)) && ((one.box.maxy) >= (two.box.miny)))
                    && (((one.box.minz) <= (two.box.maxz)) && ((one.box.maxz) >= (two.box.minz)))) ){**/ //just making sure this collision hasn't been resolved yet

            //First things first, if an object has been collided with and it is not rigid, it is now awake. Rigid objects are perpetually asleep.

            if (two.asleep == true && two.rigid == false) { //one does not need to be checked, for aforementioned reasons
                two.asleep = false;
                MovingObjects.add(two); //This object is now open for position updates
            }
            //Okay, we now have two awake objects that have collided. Now what?

                if (two.rigid ==true) { //rigid collisions are different from nonrigid collisions.

              //      one.velocity.y = 0;
              //      if (one.velocity.x == 0 && one.velocity.z == 0) {
              //          MovingObjects.remove(one);
              //          CurrentCollisions.remove(a);
               //         //a = a-1;
               //     }
                        if (one.hasbounced == false) { //currently tailored to floor
                        //this is an uneducated dummy response. Change it!
                        one.velocity.x = one.velocity.x*(-1)/(2*one.weight);
                        one.velocity.y = 4; //one.velocity.y*(-100)/(2*one.weight);
                        one.velocity.z = one.velocity.z*(-1)/(2*one.weight);
                        one.hasbounced = true;
                    }
                    else {
                        one.velocity.y = 0;
                        if (one.velocity.x == 0 && one.velocity.z == 0) {
                            MovingObjects.remove(one);
                            CurrentCollisions.remove(a);
                            one.hasbounced = false;
                            //a = a-1;
                        }
                    }
                }

            else { //else the other object can move, and the real fun begins. and by fun i mean pain
                //this is a dummy response; actuality deals with impulse and lots of awful things I just can't right now
                totalvelx = one.velocity.x + two.velocity.x;
                totalvely = one.velocity.y + two.velocity.y;
                totalvelz = one.velocity.z + two.velocity.z;

                collistype = CurrentCollisions.get(a).whereiscollision();

                one.velocity.x = (totalvelx*two.weight/(2*one.weight));
                if (collistype == 0) {
                    one.velocity.x = givemeanopposite(one.velocity.x); //a little bounce. seriously, though, this is like. totally not cool.
                }
                one.velocity.y = (totalvely*two.weight/(2*one.weight));
                if (collistype == 1) {
                    one.velocity.y = givemeanopposite(one.velocity.y); //a little bounce. seriously, though, this is like. totally not cool.
                }
                one.velocity.z = (totalvelz*two.weight/(2*one.weight));// + (givemeanopposite(one.velocity.z)));
                if (collistype == 2) {
                    one.velocity.z = givemeanopposite(one.velocity.z); //a little bounce. seriously, though, this is like. totally not cool.
                }
                two.velocity.x = (totalvelx*one.weight/(2*two.weight));
                two.velocity.y = (totalvely*one.weight/(2*two.weight));
                two.velocity.z = (totalvelz*one.weight/(2*two.weight));
            }

        }



    else {
            //else these objects are no longer colliding and can be removed from the list.
     CurrentCollisions.remove(a);
     a = a-1; //arraylist is now one size smaller; needs to move back one so the one at the next 'a' position is not skipped.
    }
        }
    }


}