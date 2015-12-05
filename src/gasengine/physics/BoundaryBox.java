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
        int type;


      public BoundaryBox(float maxval, float scale, Vector3f center, int type){ //center is the starting point where it was created
          //I was hoping we could get l/w/h, not just 'furthest point in point cloud'... oh well.
          if (type == 1){ //floor
              minx = center.x - ((maxval * scale) / 2);
              maxx = center.x + ((maxval * scale) / 2);
              miny = center.y - 1;
              maxy = center.y + 1;
              minz = center.z - ((maxval * scale) / 2);
              maxz = center.z + ((maxval * scale) / 2);
          }
          else if (type == 2) { //wall in x direction
              minx = center.x - ((maxval * scale) / 2);
              maxx = center.x + ((maxval * scale) / 2);
              miny = center.y - ((maxval * scale) / 2);
              maxy = center.y + ((maxval * scale) / 2);
              minz = center.z - 1;
              maxz = center.z + 1;

          }
          else if (type == 3) { //wall in z direction
              minx = center.x - 1;
              maxx = center.x + 1;
              miny = center.y - ((maxval * scale) / 2);
              maxy = center.y + ((maxval * scale) / 2);
              minz = center.z - ((maxval * scale) / 2);
              maxz = center.z + ((maxval * scale) / 2);
          }

          else { //else it's just a regular object
              minx = center.x - ((maxval * scale) / 2);
              maxx = center.x + ((maxval * scale) / 2);
              miny = center.y - ((maxval * scale) / 2);
              maxy = center.y + ((maxval * scale) / 2);
              minz = center.z - ((maxval * scale) / 2);
              maxz = center.z + ((maxval * scale) / 2);
          }

 }



// public boolean DetectCollision() { //list of every object, or a specific object? entity?


  //other entity's boundary box has a minimum and maximum x, y, and z value
  //if in positive coordinates
  //1 is 'this' object, 2 is other object

  //if (((minx1 >= minx2) || ((minx1 >= maxx2)) && ((miny1 >= miny2) || ((miny1 >= maxy2)) && ((minz1 >= minz2) || ((minz1 >= maxz2)))
//  return false;




 }

