package com.shasser.froggy;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class Sound {
    static public boolean music_on = true;
    static public boolean sound_on = true;
    static AudioManager audioManager;
    static float curVolume;
    static float maxVolume;
    static float leftVolume;
    static float rightVolume;
    static int priority;
    static int no_loop;
    static float normal_playback_rate;
    static SoundPool soundPool;
    static Context context;
    static int soundID = 1;

    public static void init(AudioManager am, Context c) {
        context = c;
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        audioManager = am;
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        leftVolume = curVolume / maxVolume;
        rightVolume = curVolume / maxVolume;
        priority = 1;
        no_loop = 0;
        normal_playback_rate = 1f;
        soundPool
                .setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool,
                                               int sampleId, int status) {
                        play();

                    }
                });
    }

    public static void load(int id) {
        if (sound_on) {
            soundID = soundPool.load(context, id, 1);
        }
    }

    public static void play() {
        soundPool.play(soundID, leftVolume, rightVolume, priority, no_loop,
                normal_playback_rate);
        soundPool.unload(soundID);
    }

    public static void pause() {
        soundPool.autoPause();
    }

    public static void resume() {
        soundPool.autoResume();
    }

}