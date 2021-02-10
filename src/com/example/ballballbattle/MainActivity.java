package com.example.ballballbattle;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity{
	
	//隐藏导航栏需要
	View decorView;
	int uiOptions;
	//点击两次退出游戏需要
	private long firstClick = Long.MAX_VALUE;
	
	//处理线程消息需要
	private Handler handler;
	
	//获取方位信息
	public static String location=null;
	
	//游戏持续时间
	public static int gameStayTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameView gameView = new GameView(this);
		gameView.setBackground(getResources().getDrawable(R.drawable.timg));
//		gameView.setAlpha(0.7f);
		setContentView(gameView);
		
		//屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		               WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//去除标题栏
		getActionBar().hide();
		//去除导航按钮栏
		decorView = getWindow().getDecorView();
		uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
		                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
		                | View.SYSTEM_UI_FLAG_IMMERSIVE;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = judgeProvider(lm);
		if (provider != null) {
           
           location = lm.getLastKnownLocation(provider).getProvider();
        }else{
            //不存在位置提供器的情况
            Toast.makeText(this,"不存在位置提供器的情况",Toast.LENGTH_SHORT).show();
            location = "中国";
        }
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 1) {
					decorView.setSystemUiVisibility(uiOptions);
				}
			}
		};
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				while (true) {
					handler.sendEmptyMessage(1);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}
	
	private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;//网络定位
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        }else{
            Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			long secondClick = System.currentTimeMillis();
			if(firstClick<secondClick && secondClick <= firstClick+1000) {
				//返回大厅
				Intent intent = new Intent(MainActivity.this, MenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				firstClick = secondClick;
				Toast.makeText(this, "再按一次返回大厅", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return true;
	}
	
}
