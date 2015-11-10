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
    public float velocityx;
    public float velocityy;
    public float velocityz;
    public float weight;
    public BoundaryBox box;

    /**hypothetical class for position changing uses rigid body to get the velocity at any given time, to determine
     * the position change.**/

//    org.joml.Vector3f.getPosition();
    //where are the get/setposition functions; I can't actually read anything inside joml


    public RigidBody(float w){
        weight = w;
    }


}



//public class RigidBody extends Component
//Rigidbody will contain info - weight, velocity, position, etc. Physics engine will get this from the container
//rigidbody contains information, not functions
