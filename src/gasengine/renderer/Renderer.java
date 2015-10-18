package gasengine.renderer;

import gasengine.scene.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

/**
 * Created by Matthew on 10/16/2015.
 */
public class Renderer {
    //temp hack
    private static int mWidth = 1024;
    private static int mHeight = 1024;

    private Matrix4f mVTransform = new Matrix4f(); // vertex transform matrix
    private FloatBuffer mVTransformBuffer = BufferUtils.createFloatBuffer(16); // vertex transform matrix buffer

    private List<Integer> mVBos = new ArrayList<>();

    public void add_mVBo(int mVBo)
    {
        mVBos.add(mVBo);
    }

    public void render(List<Entity> entities) {
        //temp hacks
        Vector3f mCamPos = entities.get(0).getScene().getmCamPos();
        Vector3f mCamDir = entities.get(0).getScene().getmCamDir();

        Vector3f lookAt =
                new Vector3f(mCamPos)
                        .add(mCamDir);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        for(Entity entity : entities)
            for (int i = 0; i < mVBos.size(); ++i) {
                entity.getComponents(Renderable.class).forEach(cmp -> {
                    if (mVBos.contains(cmp.getData().mVBo)) {

                        GL20.glUseProgram(cmp.getData().mShaderProgram); // tell it we're using the shader program we made for the following calls

                        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, cmp.getData().mVBo); // bind the vertex buffer for the following calls
                        GL20.glEnableVertexAttribArray(cmp.getData().mVPosAttribute); // enable our shader's vertex position attribute
                        GL20.glVertexAttribPointer( // tell it how that data is packed into the vertex buffer and which input it goes to in the shader
                                cmp.getData().mVPosAttribute, // location of the vertex shader's vertex position attribute
                                3,              // size per vertex (3, since it's just the position (x, y, z))
                                GL_FLOAT,       // format
                                false,          // normalize
                                0,              // stride (this and the next one are useful if the buffer has stuff packed in other than just positions)
                                0               // offset
                        );

                        Vector3f pos = new Vector3f();
                        entity.getPosition(pos);
                        mVTransform
                                .identity() // load identity matrix
                                .perspective( // setup perspective projection
                                        (float) Math.toRadians(45f), // 45deg FOV
                                        (float) mWidth / mHeight,    // viewport aspect ratio
                                        0.01f, // near clip plane
                                        1000f   // far clip plane
                                )
                                .lookAt( // multiply by view matrix
                                        mCamPos.x, mCamPos.y, mCamPos.z, // camera position
                                        lookAt.x, lookAt.y, lookAt.z, // point camera is looking at (origin)
                                        0, 1, 0  // camera up (x is horizontal, y is vertical, z is depth)
                                )
                                .translate(pos.x, pos.y, pos.z)
                                .rotate( // rotate the object
                                        (float) Math.toRadians(
                                                ((((double) System.currentTimeMillis() * 0.5) % 1000) / 1000) * -360 // once very two seconds
                                        ),
                                        0, 1, 0 // about the vertical axis
                                )
                                .get(mVTransformBuffer); // stuff the matrix into a float buffer so we can pass it to the vertex shader

                        GL20.glUniformMatrix4fv(cmp.getData().mVTransformUniform, false, mVTransformBuffer); // pass our vertex transform matrix to the shader
                        glDrawArrays(GL_TRIANGLES, 0, cmp.getData().vertexCount); // draw the triangles
                    }
                });

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind the vertex buffer
            }
        glfwSwapBuffers(entities.get(0).getScene().getmWindow()); // swap the color buffers
        glfwPollEvents(); // check for input
    }
}
