package com.others;

import java.util.HashMap;

import com.example.ballballbattle.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class MusicPlayer{
    private MediaPlayer mp = null;
    private SoundPool sp = null;
    private HashMap<Integer, Integer> soundmap = new HashMap<>();
    private boolean isPlay = true;
    
    //播放音效
    boolean flag = true;
    
    public MusicPlayer(Context context) {
		// TODO 自动生成的构造函数存根
    	mp = MediaPlayer.create(context, R.raw.bgm);
    	mp.setVolume(0.7f, 0.7f);
    	mp.setLooping(true);
    	BGMLoad();
    	sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    	soundmap.put(2, sp.load(context, R.raw.boom, 1));
    	soundmap.put(1, sp.load(context, R.raw.eat, 1));
    	soundmap.put(3, sp.load(context, R.raw.gameover, 1));
    	soundmap.put(4, sp.load(context, R.raw.shoot, 1));
    	soundmap.put(5, sp.load(context, R.raw.victory, 1));
	}
    private void BGMLoad() {
    	try {
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void BGMPlay() {
    	if(!mp.isPlaying()) {
    	mp.start();
    	isPlay = true;
    	}
    }
    public void BGMStop() {
    	if(mp.isPlaying()) {
    		mp.seekTo(0);
    		mp.pause();
    		isPlay = false;
    	}
    }
    
    public void setBGMVolume(float v) {
    	mp.setVolume(v, v);
    }
    
    public void setSEVolume(float v) {
		sp.setVolume(soundmap.get(1), v, v);
		sp.setVolume(soundmap.get(2), v, v);
		sp.setVolume(soundmap.get(3), v, v);
		sp.setVolume(soundmap.get(4), v, v);
		sp.setVolume(soundmap.get(5), v, v);
    }
    
    public void SEPlay(int id) {
    	if(flag)
    	sp.play(soundmap.get(id), 1, 1, 0, 0, 1);
    }
    
    public void setFlag(boolean f) {
    	flag = f;
    }
    public boolean getFlag(){
    	return this.flag;
    }
    public boolean getPlayState() {
    	return isPlay;
    }
    
    public void Release() {
    	if(mp.isPlaying()) {
    		mp.stop();
    	}
    	mp.release();
    	sp.release();
    }
}