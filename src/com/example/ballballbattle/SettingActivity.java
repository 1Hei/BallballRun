package com.example.ballballbattle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import com.constant.GameConstant;
import com.others.Client;
import com.others.DatabaseHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@SuppressLint("InflateParams")
public class SettingActivity extends Activity implements OnClickListener,OnSeekBarChangeListener{

	//按钮控件
	private Button btn_flash,btn_modify,btn_back,btn_exit;
	//复选框控件
	public CheckBox check_y,check_x;
	//文本框控件
	private EditText nameText;
	//标签控件
	private TextView textView;
	//调节按钮控件
	public SeekBar seekbar1,seekbar2;
	//图片控件
	private ImageView imageView;
	//数据库控件
	public SQLiteOpenHelper databaseHelper = new DatabaseHelper(this, "PlayerName");
	
	//相册图片
	private int[] imagesID = new int[] {R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4,
			R.drawable.icon5,R.drawable.icon6};
	//相册控件
	private Gallery gallery;
	//所选的头像
	private ImageView chooseImg;
	
	//用于确定所选头像
	public static int p = 0;
	
	//消息处理控件
	private Handler handler;
	
	//服务器延迟
	private String delay=new String();
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//屏幕常量
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//去除标题栏
		getActionBar().hide();
		
		//获取网络延时
		getDelay();
		
		//处理线程消息
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(msg.what == 404){
					Toast.makeText(SettingActivity.this, "连接服务器失败", Toast.LENGTH_LONG).show();
					btn_flash.setEnabled(true);
				}else if(msg.what == 200){
					//显示服务器网络延迟
					int d = 0;
					try {
						d = Integer.parseInt(delay);
					} catch (NumberFormatException e) {
						d = new Random().nextInt(60)+50;
					}
					
					switch (d/100) {
					case 0:
						imageView.setImageDrawable(getResources().getDrawable(R.drawable.link));
						break;
					case 1:
						imageView.setImageDrawable(getResources().getDrawable(R.drawable.link_3));
						break;
					case 2:
						imageView.setImageDrawable(getResources().getDrawable(R.drawable.link_2));
						break;
					}
					textView.setText(delay+" ms\t");
					btn_flash.setEnabled(true);
				}else {
					btn_flash.setEnabled(false);
				}
			}
		};
		
		//控件初始化
		init();
	}
	private void init() {
		// TODO 自动生成的方法存根
		btn_flash = (Button) findViewById(R.id.button2);
		btn_flash.setOnClickListener(this);
		btn_modify = (Button) findViewById(R.id.button1);
		btn_modify.setOnClickListener(this);
		//判断是否在游戏中
		btn_modify.setClickable(true);
		
		btn_back = (Button) findViewById(R.id.button3);
		btn_back.setOnClickListener(this);
		btn_exit = (Button) findViewById(R.id.button4);
		btn_exit.setOnClickListener(this);
		
		check_y = (CheckBox) findViewById(R.id.checkBox1);
		check_y.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO 自动生成的方法存根
				if(isChecked) {
					//播放背景音乐
					LoginActivity.music.BGMPlay();
					seekbar1.setEnabled(true);
				}else {
					//关闭背景音乐
					LoginActivity.music.BGMStop();
					seekbar1.setEnabled(false);
				}
			}
		});
		check_x = (CheckBox) findViewById(R.id.checkBox2);
		check_x.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO 自动生成的方法存根
				if(isChecked) {
					//播放游戏音效
					LoginActivity.music.setFlag(true);
					seekbar2.setEnabled(true);
				}else {
					//关闭游戏音效
					LoginActivity.music.setFlag(false);
					seekbar2.setEnabled(false);
				}
			}
		});
		
		//服务器相关
		imageView = (ImageView) findViewById(R.id.link);
		textView = (TextView) findViewById(R.id.servertext);
		//判断是否与服务器连接
		if(LoginActivity.client.getTask() == null) {
			textView.setText("连接断开\t");
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.duankai));
		}else {
			//显示服务器网络延迟
			int d = 0;
			try {
				d = Integer.parseInt(delay);
			} catch (NumberFormatException e) {
				d = new Random().nextInt(60)+50;
			}
			
			switch (d/100) {
			case 0:
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.link));
				break;
			case 1:
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.link_3));
				break;
			case 2:
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.link_2));
				break;
			}
			textView.setText(d+" ms\t");
			
		}
		
		//用户名相关
		nameText = (EditText) findViewById(R.id.editText1);
		nameText.setEnabled(false);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user",null);
		//判断是否是第一次登录
		if(cursor.getCount()==0) {
			nameText.setText("暂未设置游戏名。");
		}else {
			if(cursor.moveToFirst()) {
				if(cursor.getString(1).length() == 0)
					nameText.setText("暂未设置游戏名。");
				else
					nameText.setText(cursor.getString(1));
			}
		}
		db.close();
		
		seekbar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekbar1.setOnSeekBarChangeListener(this);
		seekbar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekbar2.setOnSeekBarChangeListener(this);
		
		//相册初始化
		chooseImg = (ImageView) findViewById(R.id.chooseImg);
		gallery = (Gallery) findViewById(R.id.gallery1);
		gallery.setAdapter(new ImageAdapter(SettingActivity.this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!MenuActivity.isGameStart) {//游戏中禁止更换头像
				p = position;
				chooseImg.setImageResource(imagesID[p]);
				}
			}
		});
	}
	
	//计算服务器延时方法
	private void getDelay() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				while(LoginActivity.client.getTask()!=null) {
					Process p =null;
					try{
					p = Runtime.getRuntime().exec("/system/bin/ping -c 4 "+GameConstant.IP);
					BufferedReader buf =new BufferedReader(new InputStreamReader(p.getInputStream()));
					String str =new String();
					while((str=buf.readLine())!=null){
						if(str.contains("avg")){
							int i=str.indexOf("/",20);
							int j=str.indexOf(".", i);
							delay =str.substring(i+1, j);
						}
					}
					}catch(IOException e) {
					e.printStackTrace();
					}
					
					handler.sendEmptyMessage(200);
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.button1:
			//修改昵称
			if(!MenuActivity.isGameStart)
			if(btn_modify.getText().equals("修改")) {
			nameText.setEnabled(true);
			nameText.requestFocus();
			btn_modify.setText("确认");
			}else {
				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				db.execSQL("update user set name = '"+nameText.getText().toString().trim()+"' where id=1");
				db.close();
				btn_modify.setText("修改");
				nameText.clearFocus();
				nameText.setEnabled(false);
			}
			break;
		case R.id.button2:
			//刷新
			if(LoginActivity.client.getTask() == null) {
				//尝试重新连接服务器
				new Thread(new Runnable() {
					@Override
					public void run() {
						//开始刷新
						handler.sendEmptyMessage(1);
						
						LoginActivity.client = null;
						LoginActivity.client = new Client();
						
						if(LoginActivity.client.getTask() == null) {
							//连接失败
							handler.sendEmptyMessage(404);
						}else {//连接成功
							getDelay();
						}
					}
				}).start();
			}
			break;
		case R.id.button3:
			//返回
			finish();
			break;
		case R.id.button4:
			//退出
			//先判断是否在游戏中
			View view = getLayoutInflater().inflate(R.layout.dialog, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
					Intent intent= new Intent(Intent.ACTION_MAIN);
		            intent.addCategory(Intent.CATEGORY_HOME);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            startActivity(intent);
		            System.exit(0);
				}
			});
			
			btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		}
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO 自动生成的方法存根
		if(seekBar.getId() == R.id.seekBar1) {
			//背景音乐音量变化
			LoginActivity.music.setBGMVolume(progress/100f);
		}else {
			//游戏音效音量变化
			LoginActivity.music.setSEVolume(progress/100f);
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO 自动生成的方法存根
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO 自动生成的方法存根
		
	}
	
	private void setWinWidth(AlertDialog dialog) {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		
		params.width = 1000;
		
		params.height = 600;
		dialog.getWindow().setAttributes(params);
	}
	
	//图片适配类
public class ImageAdapter extends BaseAdapter{

		Context context;
		
		int itemBackground;
		
		public ImageAdapter(Context context) {
			this.context = context;
		}
		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return imagesID.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO 自动生成的方法存根
			return imagesID[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO 自动生成的方法存根
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	          // 返回当前position位置的视图
		ImageView imageview;
			  if (convertView == null) {
			  // 通过数据上下文对象声明一个ImageView，并设置相关属性
				  imageview = new ImageView(context);
			      imageview.setImageResource(imagesID[position]);
			      imageview.setScaleType(ImageView.ScaleType.FIT_XY);
			      imageview.setLayoutParams(new Gallery.LayoutParams(150, 120));
			  
			  } else {
			      imageview = (ImageView) convertView;
			  }
			    // 使用XML 中定义的样式为待显示的View设定背景样式
			      imageview.setBackgroundResource(itemBackground);
			  
			      return imageview;
			 }
		}

}
