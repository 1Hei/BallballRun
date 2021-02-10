package com.example.ballballbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.bean.ListBean;
import com.others.ClientTask;
import com.others.DatabaseHelper;
import com.others.MyAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class QueueActivity extends Activity {

	private ListView listView;
	private TextView textView;
	private ProgressBar progressBar;
	public static List<ListBean> list = new ArrayList<>();
	private MyAdapter adapter;
	private Random random = new Random();
	private long firstClick = Long.MAX_VALUE;
	
	//�����߳���Ϣ
	private Handler handler;
	//���ݿ����
	private DatabaseHelper databasehelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//��Ļ����
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		             WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//ȥ��������
		getActionBar().hide();
		        
		setContentView(R.layout.activity_queue);
		list.clear();
		//��ʼ��
		init();
		//�����߳���Ϣ
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 200) {
					adapter = new MyAdapter(QueueActivity.this, R.layout.list, list);
					listView.setAdapter(adapter);
					
				}else if(msg.what == 0){
					progressBar.setVisibility(View.GONE);
					textView.setText("׼����ϣ�����������Ϸ.");
				}
			}
		};
		
		//��ȡƥ����г�Ա
		waiting();
	}
	private void waiting() {
		if(LoginActivity.client.getTask() == null) {
			//δ����
			textView.setText("��⵽��δ���ӵ�����������ת�����˶��С�");
			progressBar.setVisibility(View.GONE);
			new Thread(new Runnable() {
				public void run() {
					for (int i = 0; i < 9; i++) {
						list.add(new ListBean(random.nextInt(6), "R"+(i+1), "����", random.nextInt(50)+" m"));
					}
					handler.sendEmptyMessage(200);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					handler.sendEmptyMessage(0);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//������Ϸ
					Intent intent = new Intent(QueueActivity.this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
					
				}
			}).start();
		}else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					//���������̣߳�Ѱ�����
					int flag = 0,tmp=0;
					
					String msg = list.get(0).getHeadPortrait()+"&&"+list.get(0).getName()+"&&"
							+list.get(0).getNews()+"&&"+list.get(0).getTime()+"&&"+"list";
					LoginActivity.client.getTask().SendMessageTask(msg);
					
					LoginActivity.client.getTask().ReciveMessageTask();
					while (list.size()<10) {
						flag++;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						//����Ŷ����
						if(ClientTask.playerList.size() > tmp) {
							//�ж��Ƿ����˵���
							if(ClientTask.playerList.get(tmp).getTime().equals("����"))
							for (ListBean bean : list) {
								if(bean.getName().equals(ClientTask.playerList.get(tmp).getName()))
									list.remove(bean);
							}
							
							list.add(new ListBean(0, ClientTask.playerList.get(tmp).getName()
									, ClientTask.playerList.get(tmp).getNews(), ClientTask.playerList.get(tmp).getTime()+" m"));
							tmp++;
						}
						
						//��ӵ������
						if(flag > 10 || list.size()>2)
						list.add(new ListBean(random.nextInt(6), "R"+flag, "����", random.nextInt(50)+" m"));
						
						handler.sendEmptyMessage(200);
					}
					//��������ֹͣ����
					LoginActivity.client.getTask().stopRecive();
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//׼��������Ϸ
					handler.sendEmptyMessage(0);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//������Ϸ
					Intent intent = new Intent(QueueActivity.this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			}).start();
			
		}
	}
	private void init() {
		listView = (ListView) findViewById(R.id.listView1);
		listView.setAlpha(0.5f);
		textView = (TextView) findViewById(R.id.find);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		databasehelper = new DatabaseHelper(this, "PlayerName");
		SQLiteDatabase db = databasehelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from user",null);
		cursor.moveToFirst();
		list.add(new ListBean(SettingActivity.p, cursor.getString(1), "����û�", cursor.getDouble(2)+" m"));
	}
	
	//����˳�
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
			long secondClick = System.currentTimeMillis();
			if(firstClick<secondClick && secondClick <= firstClick+1000) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						//���߷�����������
						LoginActivity.client.getTask().SendMessageTask(list.get(0).getName()+"&&"+"leave");
					}
				}).start();
				finish();
			}else {
				firstClick = secondClick;
				Toast.makeText(this, "�ٰ�һ�δ���", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return true;
	}
	
	
}
