package gasengine.physics;

/**
 * Created by Kate on 10/12/2015.
 */

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


//create new project in gasengine package, got it;

public class RigidBody extends Component {

    //velocity is a float and a direction pair; perhaps float and float, with the latter being 0-360 degrees?
    //public  =
    public Vector3f velocity;
    public Vector3f acceleration;
    public float weight;
    public BoundaryBox box;
    public boolean rigid; //If true, the object is fixed in place; this is meant for things such as walls.
    public boolean asleep; //If true, the object has not changed position in several frames. It will not be subject to physics updates until it wakes.
    public boolean gettingsleepy; //Object appears to be falling asleep
    public Vector3f lastposition; //The position the object was in before it last moved. Movement function should update this.
    public Vector3f position;
    public float terminal;
    public int whatami;

    /**
     * hypothetical class for position changing uses rigid body to get the velocity at any given time, to determine
     * the position change.
     **/

//    org.joml.Vector3f.getPosition();
    //where are the get/setposition functions; I can't actually read anything inside joml
    public RigidBody(float w, boolean isrigid, Vector3f start, float maxval, float scale, int type){
        weight = w;
        asleep = false; //The item will not start out asleep, at least for now.
        gettingsleepy = false;
        rigid = isrigid;
        position = start;
        lastposition = position;
        box = new BoundaryBox(maxval, scale, start, type);
        velocity.x = 0;
        velocity.y = 0;
        velocity.z = 0;
        acceleration.x = 0;
        acceleration.y = -9.8f; //WILL PROBABLY NEED TO CONVERT THIS AND OTHER UNITS ONCE WE UNDERSTAND THE SCALE OF THE GRID!!!
        acceleration.z = 0;
        terminal = (float)Math.sqrt((2*weight)/(1.05f*0.343f*maxval*maxval));
        //.343 is the density of air, most likely to be wrong. 1.05 is cube drag coefficient.

                PhysicsUpdate.AllObjects.add(this); //add this rigid body to the list of all existing rigid bodies
    }

    public void changeacceleration(char d, float accel){
        switch (d){
            case 'x':
                acceleration.x = accel;
                break;
            case 'y':
                acceleration.y = accel;
                break;
            case 'z':
                acceleration.z = accel;
                break;
        }

    }

    public void accelerate(float time){
        velocity.x = velocity.x + acceleration.x*time;
        if(velocity.x > 100 || velocity.x < -100){    //The 100 here is an arbitrary cap value
            if(velocity.x > 100){
                velocity.x = 100;
            }
            else if(velocity.x < -100){
                velocity.x = -100;
            }
        }

        velocity.y = velocity.y + acceleration.y*time;
        if(velocity.y > 100 || velocity.y < terminal){
            if(velocity.y > 100){
                velocity.y = 100;
             }
            else if(velocity.y < terminal){
                velocity.y = terminal;
            }
        }

        velocity.z = velocity.z + acceleration.z*time;
        if(velocity.z > 100 || velocity.z < -100){
            if(velocity.z > 100){
                velocity.z = 100;
            }
            else if(velocity.z < -100){
                velocity.z = -100;
            }
        }
    }

    public void move(float time){
        // if(!rigid) {
            //accelerate(time);

            float movementx = time * velocity.x;
            float movementy = time * velocity.y;
            float movementz = time * velocity.z;

            position.x += movementx; //Moving center position point
            position.y += movementy;
            position.z += movementz;

            box.minx = movementx; //Moving Boundary box
            box.maxx = movementx;

            box.miny = movementy;
            box.maxy = movementy;

            box.minz = movementz;
            box.maxz = movementz;
            /**
             * EACH BOUNDARY BOX POINT NEEDS TO MOVE BY MOVEMENTX, Y, Z...
             *
             */

            //if( movementx+movementy+movementz != 0 ) {
              //  asleep = false;
            //}
            //This won't work! If there are = positive, negative movement
            //there will be a glitch. I doubt a complete 0 will ever be an
            //issue SO check if != 0?
       // }
    }
}

/**
 *  CHECKING IF GETTING SLEEPY:
 *  -If both velocity and acceleration are = 0
 *  if((velocity.x+velocity.y+velocity.z == 0) && (accelerationx+accelerationy+accelerationz == 0)){
 *      gettingsleepy = true;
 *  }
 */



//public class RigidBody extends Component
//Rigidbody will contain info - weight, velocity, position, etc. Physics engine will get this from the container
//rigidbody contains information, not functions
