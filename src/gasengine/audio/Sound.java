package gasengine.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import gasengine.scene.Entity;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import static  org.lwjgl.openal.AL10.*;


public class Sound
{
    FloatBuffer mVecBuff = BufferUtils.createFloatBuffer(3);

    private int mBuffer = 0;
    private int mSource = 0;

    protected int mDuration = 0;

    public Sound(String name)
    {
        mBuffer = AL10.alGenBuffers();
        mSource = AL10.alGenSources();

        WaveData waveFile;

        try
        {
            FileInputStream fin = new java.io.FileInputStream("sound/" + name);

            waveFile = WaveData.create(new BufferedInputStream(fin));

            if (waveFile == null)
                throw new RuntimeException("Failed to load file");

            fin.close();
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            ex.printStackTrace();

            return;
        }

        AL10.alBufferData(mBuffer, waveFile.format, waveFile.data, waveFile.samplerate);

        waveFile.dispose();

        AL10.alSourcei(mSource, AL10.AL_BUFFER, mBuffer);

        setPitch(1f);
        setGain(1f);
        setPosition(new Vector3f(0, 0, 0));
        setVelocity(new Vector3f(0, 0, 0));
        setLoop(false);

        if (AL10.alGetError() != AL10.AL_NO_ERROR)
            throw new RuntimeException("Failed to load audio file");
    }


    public void play() throws OpenALException
    {
        AL10.alSourcePlay(mSource);
    }

    public void pause() throws OpenALException
    {
        AL10.alSourcePause(mSource);
    }

    public void stop() throws OpenALException
    {
        AL10.alSourceStop(mSource);
    }


    public void setPitch(float pitch) throws OpenALException
    {
        AL10.alSourcef(mSource, AL10.AL_PITCH, pitch);
    }

    public void setGain(float gain) throws OpenALException
    {
        AL10.alSourcef(mSource, AL10.AL_GAIN, gain);
    }

    public void setVelocity(Vector3f vel) throws OpenALException
    {
        mVecBuff.put(vel.x);
        mVecBuff.put(vel.y);
        mVecBuff.put(vel.z);

        mVecBuff.flip();

        AL10.alSourcefv(mSource, AL10.AL_VELOCITY, mVecBuff);
    }

    public void setPosition(Vector3f pos) throws OpenALException
    {
        mVecBuff.put(pos.x);
        mVecBuff.put(pos.y);
        mVecBuff.put(pos.z);

        mVecBuff.flip();

        AL10.alSourcefv(mSource, AL10.AL_POSITION, mVecBuff);
    }

    public void setLoop(boolean loop) throws OpenALException
    {
        AL10.alSourcei(mSource, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }


    public int getChannels()
    {
        return AL10.alGetBufferi(mBuffer, AL_CHANNELS);
    }

    public int getBufferBits()
    {
        return AL10.alGetBufferi(mBuffer, AL_BITS);
    }

    public int getBufferSize()
    {
        return AL10.alGetBufferi(mBuffer, AL_SIZE);
    }

    public int getSamplingRate()
    {
        return AL10.alGetBufferi(mBuffer, AL_FREQUENCY);
    }

    public int getDuration()
    {
        if (mDuration == 0 && mBuffer != 0) {
            int bits = getBufferBits();
            int size = getBufferSize();
            int channels = getChannels();
            int frequency = getSamplingRate();

            mDuration = size / channels / bits / frequency;
        }

        return mDuration;
    }
}
