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


/**
 The way I was thinking of it, it would have to take the coordinates of the object - so it would
 take in an abstract object class, unless object is a class in and of itself. Then it needs to find
 or be given, but probably find, with a method) the minimum and maximum x, y, and z coordinates of the object.
 From there, it's easy to create a simple box around the object.

 This probably goes back to renderer, because that will relate heavily to size, especially if we want to
 avoid needing manual settings for the boundary box.
 */

public class BoundaryBox {

}
