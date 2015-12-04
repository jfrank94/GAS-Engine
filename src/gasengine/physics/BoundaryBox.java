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


/**
 The way I was thinking of it, it would have to take the coordinates of the object - so it would
 take in an abstract object class, unless object is a class in and of itself. Then it needs to find
 or be given, but probably find, with a method) the minimum and maximum x, y, and z coordinates of the object.
 From there, it's easy to create a simple box around the object.

 This probably goes back to renderer, because that will relate heavily to size, especially if we want to
 avoid needing manual settings for the boundary box.
 */

public class BoundaryBox {
       float maxx;
       float maxy;
       float maxz;
       float minx;
       float miny;
       float minz;


      public BoundaryBox(float length, float height, float width, float scale, Vector3f center){ //center is the starting point where it was created
          minx = center.x - ((length*scale)/2);
          maxx = center.x + ((length*scale)/2);
          miny = center.y - ((height*scale)/2);
          maxy = center.y + ((height*scale)/2);
          minz = center.z - ((width*scale)/2);
          maxz = center.z + ((width*scale)/2);


 }





// public boolean DetectCollision() { //list of every object, or a specific object? entity?


  //other entity's boundary box has a minimum and maximum x, y, and z value
  //if in positive coordinates
  //1 is 'this' object, 2 is other object

  //if (((minx1 >= minx2) || ((minx1 >= maxx2)) && ((miny1 >= miny2) || ((miny1 >= maxy2)) && ((minz1 >= minz2) || ((minz1 >= maxz2)))
//  return false;




 }

