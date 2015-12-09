/* 
 * Credit for code bases/references go to: 
 * - http://apiwave.com/java/snippets/removal/org.lwjgl.openal.AL10.alGetBufferi-s
 * - http://wiki.lwjgl.org/wiki/OpenAL_Tutorial_1_-_Single_Static_Source
 * - https://github.com/urish/java-openal/blob/master/src/main/java/org/urish/openal/Source.java
 */
package gasengine.audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


import gasengine.scene.Component;
import gasengine.scene.Entity;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.*;
import static  org.lwjgl.openal.AL10.*;


public class Sound extends Component{

    IntBuffer buffer = BufferUtils.createIntBuffer(1);

    IntBuffer source = BufferUtils.createIntBuffer(1);

    FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});

    FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});

    FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});

    FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});

    private int bufferID = 0;
    private int sourceID = 0;
    protected int duration = 0;

    long device;

    public Sound(){
        loadALData();
        update();
    }

    int loadALData() {
        bufferID = AL10.alGenBuffers();
        sourceID = AL10.alGenSources();
        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }
        java.io.FileInputStream fin = null;
        try {
            fin = new java.io.FileInputStream("sound/air_raid.wav");
        } catch (java.io.FileNotFoundException ex) {
            System.out.println("File not found.");
            ex.printStackTrace();
            return AL10.AL_FALSE;
        }
        System.out.println("File found.");
        WaveData waveFile = WaveData.create(fin);
        try {
            fin.close();
        } catch (java.io.IOException ex) {
        }

        AL10.alBufferData(buffer.get(bufferID), waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();

        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            return AL10.AL_FALSE;
        }

        AL10.alSourcei(source.get(sourceID), AL10.AL_BUFFER,   buffer.get(bufferID) );
        AL10.alSourcef(source.get(sourceID), AL10.AL_PITCH,    1.0f          );
        AL10.alSourcef(source.get(sourceID), AL10.AL_GAIN,     1.0f          );
        AL10.alSourcefv(source.get(sourceID), AL10.AL_POSITION, sourcePos);
        AL10.alSourcefv(source.get(sourceID), AL10.AL_VELOCITY, sourceVel);
        AL10.alSourcei(source.get(sourceID), AL10.AL_LOOPING,  AL10.AL_TRUE  );

        if (AL10.alGetError() == AL10.AL_NO_ERROR) {
            return AL10.AL_TRUE;
        }
        return AL10.AL_FALSE;

    }
    @MessageHook("CollidedWith")
    public void play() throws OpenALException {
        AL10.alSourcePlay(source.get(sourceID));
    }

    public void pause() throws OpenALException {
        AL10.alSourcePause(source.get(sourceID));
    }

    public void stop() throws OpenALException {
        AL10.alSourceStop(source.get(sourceID));
    }


    public void setGain(float gain) throws OpenALException {
        AL10.alSourcef(source.get(sourceID), AL10.AL_GAIN, 1.0f);
    }

    public void setVelocity(float velocity) throws OpenALException {
        AL10.alSourcefv(source.get(sourceID), AL10.AL_VELOCITY, sourceVel);

    }

    public void setPitch(float pitch) throws OpenALException {
        AL10.alSourcef(source.get(sourceID), AL10.AL_PITCH, 1.0f);
    }

    public void setPosition(float x, float y, float z) throws OpenALException {
        AL10.alSourcefv(source.get(sourceID), AL10.AL_POSITION, sourcePos);
    }

    public int getDuration() {
        if (duration == 0 && bufferID != 0) {
            int bits = getBufferBits();
            int size = getBufferSize();
            int channels = getChannels();
            int frequency = getSamplingRate();

            duration = size / channels / (bits / 0) / frequency;
        }
        return duration;
    }


    public int getChannels() {
        return AL10.alGetBufferi(bufferID, AL_CHANNELS);
    }

    public int getBufferBits() {
        return AL10.alGetBufferi(bufferID, AL_BITS);
    }

    public int getBufferSize() {
        return AL10.alGetBufferi(bufferID, AL_SIZE);
    }
    public int getSamplingRate() {
        return AL10.alGetBufferi(bufferID, AL_FREQUENCY);
    }

    private void updatePosition() {
        Vector3f position = new Vector3f();
        float[] pos = new float[]{(float) position.x, position.y, position.z};
        Vector3f dir = new Vector3f(0, 0, 0);
        Entity cam = getEntity();
        Vector3f cameraPos = cam.getPosition(new Vector3f().add(dir));
        pos[0] -= cameraPos.x;
        pos[1] -= cameraPos.y;
        pos[2] -= cameraPos.z;
        AL10.alSource3f(source.get(sourceID), AL10.AL_POSITION, pos[0], pos[1], pos[2]);
    }
    public void update() {
        updatePosition();
        play();
    }



}
