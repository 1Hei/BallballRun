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

	//��ť�ؼ�
	private Button btn_flash,btn_modify,btn_back,btn_exit;
	//��ѡ��ؼ�
	public CheckBox check_y,check_x;
	//�ı���ؼ�
	private EditText nameText;
	//��ǩ�ؼ�
	private TextView textView;
	//���ڰ�ť�ؼ�
	public SeekBar seekbar1,seekbar2;
	//ͼƬ�ؼ�
	private ImageView imageView;
	//���ݿ�ؼ�
	public SQLiteOpenHelper databaseHelper = new DatabaseHelper(this, "PlayerName");
	
	//���ͼƬ
	private int[] imagesID = new int[] {R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4,
			R.drawable.icon5,R.drawable.icon6};
	//���ؼ�
	private Gallery gallery;
	//��ѡ��ͷ��
	private ImageView chooseImg;
	
	//����ȷ����ѡͷ��
	public static int p = 0;
	
	//��Ϣ����ؼ�
	private Handler handler;
	
	//�������ӳ�
	private String delay=new String();
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//��Ļ����
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//ȥ��������
		getActionBar().hide();
		
		//��ȡ������ʱ
		getDelay();
		
		//�����߳���Ϣ
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(msg.what == 404){
					Toast.makeText(SettingActivity.this, "���ӷ�����ʧ��", Toast.LENGTH_LONG).show();
					btn_flash.setEnabled(true);
				}else if(msg.what == 200){
					//��ʾ�����������ӳ�
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
		
		//�ؼ���ʼ��
		init();
	}
	private void init() {
		// TODO �Զ����ɵķ������
		btn_flash = (Button) findViewById(R.id.button2);
		btn_flash.setOnClickListener(this);
		btn_modify = (Button) findViewById(R.id.button1);
		btn_modify.setOnClickListener(this);
		//�ж��Ƿ�����Ϸ��
		btn_modify.setClickable(true);
		
		btn_back = (Button) findViewById(R.id.button3);
		btn_back.setOnClickListener(this);
		btn_exit = (Button) findViewById(R.id.button4);
		btn_exit.setOnClickListener(this);
		
		check_y = (CheckBox) findViewById(R.id.checkBox1);
		check_y.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO �Զ����ɵķ������
				if(isChecked) {
					//���ű�������
					LoginActivity.music.BGMPlay();
					seekbar1.setEnabled(true);
				}else {
					//�رձ�������
					LoginActivity.music.BGMStop();
					seekbar1.setEnabled(false);
				}
			}
		});
		check_x = (CheckBox) findViewById(R.id.checkBox2);
		check_x.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO �Զ����ɵķ������
				if(isChecked) {
					//������Ϸ��Ч
					LoginActivity.music.setFlag(true);
					seekbar2.setEnabled(true);
				}else {
					//�ر���Ϸ��Ч
					LoginActivity.music.setFlag(false);
					seekbar2.setEnabled(false);
				}
			}
		});
		
		//���������
		imageView = (ImageView) findViewById(R.id.link);
		textView = (TextView) findViewById(R.id.servertext);
		//�ж��Ƿ������������
		if(LoginActivity.client.getTask() == null) {
			textView.setText("���ӶϿ�\t");
			imageView.setImageDrawable(getResources().getDrawable(R.drawable.duankai));
		}else {
			//��ʾ�����������ӳ�
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
		
		//�û������
		nameText = (EditText) findViewById(R.id.editText1);
		nameText.setEnabled(false);
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user",null);
		//�ж��Ƿ��ǵ�һ�ε�¼
		if(cursor.getCount()==0) {
			nameText.setText("��δ������Ϸ����");
		}else {
			if(cursor.moveToFirst()) {
				if(cursor.getString(1).length() == 0)
					nameText.setText("��δ������Ϸ����");
				else
					nameText.setText(cursor.getString(1));
			}
		}
		db.close();
		
		seekbar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekbar1.setOnSeekBarChangeListener(this);
		seekbar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekbar2.setOnSeekBarChangeListener(this);
		
		//����ʼ��
		chooseImg = (ImageView) findViewById(R.id.chooseImg);
		gallery = (Gallery) findViewById(R.id.gallery1);
		gallery.setAdapter(new ImageAdapter(SettingActivity.this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!MenuActivity.isGameStart) {//��Ϸ�н�ֹ����ͷ��
				p = position;
				chooseImg.setImageResource(imagesID[p]);
				}
			}
		});
	}
	
	//�����������ʱ����
	private void getDelay() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO �Զ����ɵķ������
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
		// TODO �Զ����ɵķ������
		switch (v.getId()) {
		case R.id.button1:
			//�޸��ǳ�
			if(!MenuActivity.isGameStart)
			if(btn_modify.getText().equals("�޸�")) {
			nameText.setEnabled(true);
			nameText.requestFocus();
			btn_modify.setText("ȷ��");
			}else {
				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				db.execSQL("update user set name = '"+nameText.getText().toString().trim()+"' where id=1");
				db.close();
				btn_modify.setText("�޸�");
				nameText.clearFocus();
				nameText.setEnabled(false);
			}
			break;
		case R.id.button2:
			//ˢ��
			if(LoginActivity.client.getTask() == null) {
				//�����������ӷ�����
				new Thread(new Runnable() {
					@Override
					public void run() {
						//��ʼˢ��
						handler.sendEmptyMessage(1);
						
						LoginActivity.client = null;
						LoginActivity.client = new Client();
						
						if(LoginActivity.client.getTask() == null) {
							//����ʧ��
							handler.sendEmptyMessage(404);
						}else {//���ӳɹ�
							getDelay();
						}
					}
				}).start();
			}
			break;
		case R.id.button3:
			//����
			finish();
			break;
		case R.id.button4:
			//�˳�
			//���ж��Ƿ�����Ϸ��
			View view = getLayoutInflater().inflate(R.layout.dialog, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final AlertDialog dialog = builder.setView(view).show();
			setWinWidth(dialog);
			
			Button btn_ok = (Button)view.findViewById(R.id.btn_ok);
			Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			btn_ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//�ͷ���Դ
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
		// TODO �Զ����ɵķ������
		if(seekBar.getId() == R.id.seekBar1) {
			//�������������仯
			LoginActivity.music.setBGMVolume(progress/100f);
		}else {
			//��Ϸ��Ч�����仯
			LoginActivity.music.setSEVolume(progress/100f);
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO �Զ����ɵķ������
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO �Զ����ɵķ������
		
	}
	
	private void setWinWidth(AlertDialog dialog) {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		
		params.width = 1000;
		
		params.height = 600;
		dialog.getWindow().setAttributes(params);
	}
	
	//ͼƬ������
public class ImageAdapter extends BaseAdapter{

		Context context;
		
		int itemBackground;
		
		public ImageAdapter(Context context) {
			this.context = context;
		}
		@Override
		public int getCount() {
			// TODO �Զ����ɵķ������
			return imagesID.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO �Զ����ɵķ������
			return imagesID[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO �Զ����ɵķ������
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	          // ���ص�ǰpositionλ�õ���ͼ
		ImageView imageview;
			  if (convertView == null) {
			  // ͨ�����������Ķ�������һ��ImageView���������������
				  imageview = new ImageView(context);
			      imageview.setImageResource(imagesID[position]);
			      imageview.setScaleType(ImageView.ScaleType.FIT_XY);
			      imageview.setLayoutParams(new Gallery.LayoutParams(150, 120));
			  
			  } else {
			      imageview = (ImageView) convertView;
			  }
			    // ʹ��XML �ж������ʽΪ����ʾ��View�趨������ʽ
			      imageview.setBackgroundResource(itemBackground);
			  
			      return imageview;
			 }
		}

}
