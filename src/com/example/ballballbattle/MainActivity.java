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
	
	//���ص�������Ҫ
	View decorView;
	int uiOptions;
	//��������˳���Ϸ��Ҫ
	private long firstClick = Long.MAX_VALUE;
	
	//�����߳���Ϣ��Ҫ
	private Handler handler;
	
	//��ȡ��λ��Ϣ
	public static String location=null;
	
	//��Ϸ����ʱ��
	public static int gameStayTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameView gameView = new GameView(this);
		gameView.setBackground(getResources().getDrawable(R.drawable.timg));
//		gameView.setAlpha(0.7f);
		setContentView(gameView);
		
		//��Ļ����
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//������ʾ
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		               WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//ȥ��������
		getActionBar().hide();
		//ȥ��������ť��
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
            //������λ���ṩ�������
            Toast.makeText(this,"������λ���ṩ�������",Toast.LENGTH_SHORT).show();
            location = "�й�";
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
				// TODO �Զ����ɵķ������
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
            return LocationManager.NETWORK_PROVIDER;//���綨λ
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS��λ
        }else{
            Toast.makeText(this,"û�п��õ�λ���ṩ��",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			long secondClick = System.currentTimeMillis();
			if(firstClick<secondClick && secondClick <= firstClick+1000) {
				//���ش���
				Intent intent = new Intent(MainActivity.this, MenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				firstClick = secondClick;
				Toast.makeText(this, "�ٰ�һ�η��ش���", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return true;
	}
	
}
