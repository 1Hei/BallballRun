package com.example.ballballbattle;

import java.util.Random;

import com.others.Client;
import com.others.MusicPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {

	private int[] de = new int[4];
	private ImageView processBar;
	private ImageView icon;
	private MyGiftView gifView;
	private Bitmap bmp = null;
	private int length,newLength=0,flag=0;
	private Handler handler;
	private long firstClick = Long.MAX_VALUE;
	
	//媒体播放器
	public static MusicPlayer music=null;
	
	//网络连接
	public static Client client=null;
	//连接判别字
	private boolean iw = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.activity_login);
		
		//初始化场景
		init();
		
		//处理线程消息
		handler = new Handler() {
			public void handleMessage(Message msg) {
				// TODO 自动生成的方法存根
				if(msg.what == 0) {
					processBar.setImageBitmap(zoomImg(bmp, newLength));
//					processBar.setTranslationX(newLength/2);
					processBar.setVisibility(View.VISIBLE);
				}else if(msg.what == 1){
					icon.setImageDrawable(getResources().getDrawable(de[0]));
				}else if(msg.what == 2) {
					icon.setImageDrawable(getResources().getDrawable(de[(flag++)%4]));
				}else if(msg.what == 404){
					Toast.makeText(LoginActivity.this, "连接服务器失败", Toast.LENGTH_LONG).show();
					iw = true;
				}else if(msg.what == 200){
					iw = true;
				}
			}
			
		};
		
		//连接网络
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				client = new Client();
				if(client.getTask()==null) {//连接失败
					handler.sendEmptyMessage(404);
				}else {//连接成功
					handler.sendEmptyMessage(200);
				}
			}
		}).start();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			long secondClick = System.currentTimeMillis();
			if(firstClick<secondClick && secondClick <= firstClick+1000) {
				//退出游戏,释放资源
				music.Release();
				client.close();
				finish();
				System.exit(0);
			}else {
				firstClick = secondClick;
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	private void init() {
		//背景动图
		gifView=(MyGiftView) findViewById(R.id.gifBg);
		gifView.setSize(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
		//加载媒体播放器
		music = new MusicPlayer(LoginActivity.this);
		//加载进度条
		processBar = (ImageView) findViewById(R.id.imageView1);
		icon = (ImageView) findViewById(R.id.imageView2);
		de[0] = R.drawable.t1;
		de[1] = R.drawable.t2;
		de[2] = R.drawable.t3;
		de[3] = R.drawable.t4;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.t6);
		length = bmp.getWidth();
//		processBar.setX(processBar.getX()-length/2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				Random random = new Random();
				int sleeptime = random.nextInt(3)+2;
				while(newLength < length) {
					Message message = new Message();
					try {
						Thread.sleep(sleeptime*1000/length);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					newLength++;
					message.what = 0;
					handler.sendMessage(message);
					if(newLength%25==0) 
						handler.sendEmptyMessage(2);
				}
				
				//等待连接服务器线程结束
				while(iw==false) {
				try {
						Thread.sleep(150);
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				handler.sendEmptyMessage(2);
				}
				
				//加载完成页面跳转
				Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		}).start();
	}
	
	public Bitmap zoomImg(Bitmap bm, int newWidth) {
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / length;
        // 取得想要缩放的matrix参数
//        processBar.setX(processBar.getX()-newWidth/2);
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, 0.7f);
//        processBar.setX(processBar.getX()-newWidth/2);
//        processBar.setTranslationX(newWidth/2);
        // 得到新的图片   
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, length, bm.getHeight(), matrix, true);
        return newbm;
    }
}
