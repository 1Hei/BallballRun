package com.example.ballballbattle;

import com.others.ClientTask;
import com.others.DatabaseHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity implements OnClickListener,OnFocusChangeListener{
	private ImageButton btn_start,btn_setting;
	private EditText edit;
	public SQLiteOpenHelper databaseHelper = null;
	private TextView t;
	
	//玩家昵称
	public static String name = new String();
	//游戏是否开始指标
	public static boolean isGameStart = false;
	
	//音乐播放控件
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		//屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//去除标题栏
		getActionBar().hide();
		
		init();
		
		//播放游戏背景音乐
		LoginActivity.music.BGMPlay();
	}
	
	//初始化
	private void init() {
		databaseHelper = new DatabaseHelper(this, "PlayerName");
		btn_start = (ImageButton) findViewById(R.id.imageButton1);
		btn_start.setOnClickListener(this);
		btn_setting = (ImageButton) findViewById(R.id.imageButton2);
		btn_setting.setOnClickListener(this);
		edit = (EditText) findViewById(R.id.editText1);
		edit.clearFocus();
		edit.setOnFocusChangeListener(this);
		t=(TextView) findViewById(R.id.textView1);
		t.setOnClickListener(this);
		//打开、创建数据库
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user",null);
		//判断是否是第一次登录
		if(cursor.getCount()==0) {
			edit.requestFocus();
		}else {
			if(cursor.moveToFirst()) {
				edit.setText(cursor.getString(1));
				name = cursor.getString(1);
			}
			edit.setFocusable(false);
		}
		db.close();
	}
	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user",null);
		//判断是否是第一次登录
		if(cursor.getCount()==0) {
			edit.requestFocus();
		}else {
			if(cursor.moveToFirst()) {
				edit.setText(cursor.getString(1));
				name = edit.getText().toString();
			}
			edit.setFocusable(false);
		}
		db.close();
		super.onResume();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			//获取视图
			View view = getLayoutInflater().inflate(R.layout.dialog, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
			final AlertDialog dialog = builder.setView(view).show();
			setWinWidth(dialog);
			
			Button btn_ok = (Button)view.findViewById(R.id.btn_ok);
			Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			btn_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//释放资源
					LoginActivity.music.Release();
					LoginActivity.client.close();
					finish();
					System.exit(0);
				}
			});
			
			btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		return true;
	}
	
	private void setWinWidth(AlertDialog dialog) {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		
		params.width = 1000;
		
		params.height = 600;
		dialog.getWindow().setAttributes(params);
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		if(v.getId() == R.id.imageButton1) {
			//游戏开始
			//清空上次匹配玩家列表
			if(ClientTask.playerList.size()>0) {
				ClientTask.playerList.clear();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//页面跳转
			Intent intent = new Intent(MenuActivity.this, QueueActivity.class);
			startActivity(intent);
			
		}else if(v.getId() == R.id.imageButton2) {
			//打开设置
			Intent intent = new Intent(MenuActivity.this, SettingActivity.class);
			startActivity(intent);
		}else {
			edit.clearFocus();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO 自动生成的方法存根
		if(!hasFocus) {
			String name = edit.getText().toString();
			if(verify(name)) {
				//验证成功
				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				db.execSQL("insert into user(name,record) values('"+name+"','0')");
				edit.setFocusable(false);
			}else {
				Toast.makeText(this, "此昵称已存在，请重新输入.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean verify(String name) {
		// TODO 自动生成的方法存根
		if(name.length()>0)
		return true;
		return false;
	}
	
}
