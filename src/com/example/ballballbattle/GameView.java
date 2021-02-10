package com.example.ballballbattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.bean.Bullet;
import com.bean.ListBean;
import com.bean.modelBean;
import com.constant.GameConstant;
import com.others.ClientTask;
import com.others.DatabaseHelper;
import com.playercontroller.PlayerBall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

@SuppressLint("ClickableViewAccessibility")
public class GameView extends SurfaceView implements Callback,Runnable{

	//��������
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint wordPaint,paint;
	private boolean flag = false;
	private MainActivity activity;
	private Matrix m = new Matrix();
	//���ͼƬ
	private Bitmap[] bgs = new Bitmap[] {BitmapFactory.decodeResource(getResources(), R.drawable.icon1),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon2),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon3),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon4),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon5),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon6)};
	
	//��������ʽ
	private int[] attackBtms = new int[] {R.drawable.wrap1, R.drawable.wrap2,
			R.drawable.wrap3, R.drawable.wrap4, R.drawable.wrap5, R.drawable.wrap6};
	
	//�ֻ���Ļ�Ŀ��
	private int screenWidth;
	//�ֻ���Ļ�ĳ���
	private int screenHeight;
	//��ͬ�ֻ�����ƫ��
	private float widthOffset,heightOffset;
	
	Random random = new Random();
	
	//�����о�̬����İ뾶
	private int littleBallRadius;
	
	//����ͼƬ
	private Bitmap bg;
	
	//��ҿɻ��Χ
	private Rect rect;
	
	//��ͼ����
	private int mapWidth;
	private int mapHeight;
	//��ǰ�ֻ���Ļ���Ͻ���Ե�ͼ��λ��
	private float worldPositionX;
	private float worldPositionY;
	
	//��������б�
	private List<ListBean> gameList = QueueActivity.list;
	//���
	private PlayerBall player;
	//����������������б�
	private ArrayList<PlayerBall> Computerplayers = new ArrayList<>();
	//�������
	private ArrayList<PlayerBall> otherPlayers = new ArrayList<>();
	//ʳ�Ｏ��
	private ArrayList<modelBean> littleBall = new ArrayList<>();	
	//��Ҵ�С�����б�
	ArrayList<PlayerBall> rankList = new ArrayList<>();
	//��ҵ��ƶ����򣨴�ֱ��ˮƽ��
	private float[]directions = new float[2];
	
	
	//��Ϸ��ʼʱ��ͽ���ʱ��
	private long startTime;
	private long endTIme;

	//������������Ҫ
	private float bigSize = 0;
	private float smallSize = 0;
	private float divsion = 0;
	//��ǰ�����ĵ����һ�δ����ĵ�
	private float pX,pY,nX,nY;
	private boolean isShow;
	int alpha = 70;
	int btnAlpha = 70;
	
	private boolean isMintorPressed = false;
	
	//��ҷ���ָʾͼ��
	private Bitmap directBmp;
	
	//����ͼ����ֵ�λ��
	private float music1X,music1Y,music2X,music2Y;
	//��Ϸ��������Ч״̬
	private boolean music1Open,music2Open;
	
	//���а������ز���
	private int rankX,rankY,rankWidth,rankHeight;
	
	//����������ز���
	private int attackX,attackY,attackWidth,attackHeight;
	private Bitmap attackBtn;

	//�ж��Ƿ�����
	private boolean linkNet = false;
	
	//��ҷ����ӵ�������
	private int shootSum=0;
	
	@SuppressWarnings("deprecation")
	public GameView(Context context) {
		super(context);
		setZOrderOnTop(true);
		sfh = getHolder();
		sfh.setFormat(PixelFormat.TRANSLUCENT);
		canvas = new Canvas();
		wordPaint = new Paint();
		wordPaint.setColor(Color.BLACK);
		wordPaint.setAntiAlias(true);
		paint = new Paint();
		paint.setAntiAlias(true);
		activity = (MainActivity) context;
		sfh.addCallback(this);
		littleBallRadius = 15;
		bg = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
		
		screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
		
		widthOffset=screenWidth/1959;
		heightOffset=screenHeight/1080;
		//��������ز���
		
		bigSize = screenWidth/12;
		smallSize = bigSize/1.5f;
		divsion = screenWidth/2-bigSize;
		pX = bigSize + 10 + smallSize;
		pY = screenHeight -30 - bigSize - smallSize;
		nX = pX;
		nY = pY;
	}
	
	
	//���Ʒ���
	private void MyDraw(){
		canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawColor(Color.WHITE);
		//���Ƴ����о�̬ʳ��
		drawScreen();
		//�������
		drawPlayer();
		//����������
		drawVirtualAxis();
		//������ҷ�����ӵ�
		drawBullet();
		//������ҷ���ָʾ
		if(isShow) {
			drawDirect();
		}
		//���Ƴ����������̶�λ�õĶ���
		drawOthers();
		sfh.unlockCanvasAndPost(canvas);
	}
	
	private void drawBullet() {
		
		//����������ҷ����ӵ����
		for (PlayerBall p : otherPlayers) {
			for (Bullet bullet : p.bullets) {
				if(bullet.isVisiable()) {
					float dis = (float) Math.sqrt(Math.pow(bullet.getDirections()[0], 2) + Math.pow(bullet.getDirections()[1], 2));
					bullet.x+=bullet.speed*bullet.getDirections()[0]/dis;
					bullet.y+=bullet.speed*bullet.getDirections()[1]/dis;
					
					//�ж��ӵ��Ƿ񵽴�������
					float movedis=(float) Math.sqrt(Math.pow(bullet.initX-bullet.x, 2) + Math.pow(bullet.initY-bullet.y, 2));
					if(movedis>=GameConstant.SHOOTRANG) {
						bullet.setVisiable(false);
					}else {
						paint.setColor(p.getColor());
						canvas.drawCircle(bullet.x-worldPositionX, bullet.y-worldPositionY, bullet.size, paint);
					}
				}
			}
		}
		
		//�����������ӵ����
		for (Bullet bullet : player.bullets) {
			if(bullet.isVisiable()) {
				float dis = (float) Math.sqrt(Math.pow(bullet.getDirections()[0], 2) + Math.pow(bullet.getDirections()[1], 2));
				bullet.x+=bullet.speed*bullet.getDirections()[0]/dis;
				bullet.y+=bullet.speed*bullet.getDirections()[1]/dis;
				
				//�ж��ӵ��Ƿ񵽴�������
				float movedis=(float) Math.sqrt(Math.pow(bullet.initX-bullet.x, 2) + Math.pow(bullet.initY-bullet.y, 2));
				if(movedis>=GameConstant.SHOOTRANG) {
					bullet.setVisiable(false);
				}else {
					paint.setColor(player.getColor());
					canvas.drawCircle(bullet.x-worldPositionX, bullet.y-worldPositionY, bullet.size, paint);
				}
			}
		}
		
	}


	private void drawDirect() {
		//������ҷ���ָʾ
		float dis = (float) Math.sqrt(Math.pow(directions[0], 2) + Math.pow(directions[1], 2));
		
		float degrees;
		if(directions[0]>0) {
			degrees= (float) (Math.acos(-directions[1]/dis)*180/Math.PI);
		}else {
			degrees= (float) (Math.acos(directions[1]/dis)*180/Math.PI)+180;
		}
		wordPaint.setTextSize(50*widthOffset);
		canvas.drawText("����: ������ƫ�� "+String.format("%.1f��", degrees)+"������ʻ", 100*widthOffset, 200*heightOffset, wordPaint);
	}


	private void drawPlayer() {
		Paint ballpaint = new Paint();
		ballpaint.setAntiAlias(true);
		ballpaint.setStrokeWidth(5);
		
		//���Ƶ���
		for (PlayerBall cBall : Computerplayers) {
			if(!cBall.isDie()) {
			ballpaint.setColor(cBall.getColor());
			canvas.drawCircle(cBall.px-worldPositionX, cBall.py-worldPositionY, cBall.getSize(), ballpaint);
			wordPaint.setTextSize(cBall.getSize());
			canvas.drawText(cBall.getName(), cBall.px-worldPositionX-cBall.getSize()*cBall.getName().length()/4
					, cBall.py-worldPositionY+cBall.getSize()/2, wordPaint);
			}
		}
		
		//�����������
		for (PlayerBall cBall : otherPlayers) {
			if(!cBall.isDie()) {
				ballpaint.setColor(cBall.getColor());
				canvas.drawCircle(cBall.px-worldPositionX, cBall.py-worldPositionY, cBall.getSize(), ballpaint);
				wordPaint.setTextSize(cBall.getSize());
				canvas.drawText(cBall.getName(), cBall.px-worldPositionX-cBall.getSize()*cBall.getName().length()/2
						, cBall.py-worldPositionY+cBall.getSize()/2, wordPaint);
			}
		}
		
		//�������
		if(!player.isDie()) {
			ballpaint.setColor(player.getColor());
			canvas.drawCircle(player.px-worldPositionX, player.py-worldPositionY, player.getSize(), ballpaint);
			wordPaint.setTextSize(player.getSize());
			canvas.drawText(player.getName(), player.px-worldPositionX-player.getSize()*player.getName().length()/2
					, player.py-worldPositionY+player.getSize()/2, wordPaint);
		}
	}
	
	private void drawScreen() {
		Paint ballpaint = new Paint();
		ballpaint.setAntiAlias(true);
		ballpaint.setStrokeWidth(5);
		//���Ƴ���
		//���Ƶ�ͼ�еľ�̬ʳ��
		for (modelBean m : littleBall) {
			if(!m.isDie() && m.getX()>=worldPositionX && m.getX()<=worldPositionX+screenWidth
					&& m.getY() >= worldPositionY && m.getY()<=worldPositionY+screenHeight) {
				ballpaint.setColor(m.getDrawColor());
				if(m.getType()!=5)
					canvas.drawPath(DrawLittleBall(m.getX()-worldPositionX, m.getY()-worldPositionY, m.getType()+2,
						m.getPdegree(), m.getSize()), ballpaint);
				else
					canvas.drawCircle(m.getX()-worldPositionX, m.getY()-worldPositionY, m.getSize(), ballpaint);
			}
		}
	}
	
	private void drawVirtualAxis() {
		//������Ļ���(��͸��)
				if(isShow) {
					//���ƴ�Բ
					alpha = 70;
					paint.setColor(Color.argb(150, 0, 60, 100));
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(nX, nY, bigSize, paint);
					paint.setColor(Color.argb(120, 0, 60, 100));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, bigSize, paint);
					//����СԲ
					paint.setColor(Color.argb(120, 0, 0, 200));
					paint.setStyle(Paint.Style.FILL);
					float x = 0,y=0;
					if(pX <= nX - bigSize + 10)
						x = nX - bigSize + 10;
					else if(pX >= nX + bigSize - 10)
						x = nX + bigSize - 10;
					else {
						x = pX;
					}
					
					if(pY <= nY - bigSize + 10)
						y = nY - bigSize + 10;
					else if(pY >= nY + bigSize - 10)
						y = nY + bigSize - 10;
					else {
						y = pY;
					}
					canvas.drawCircle(x, y, smallSize, paint);
					
					//��ȡ���Ӧ���˶��ķ���
					directions[1] = y-nY;	//��ֱ����
					directions[0] = x-nX;	//ˮƽ����
					//����ƶ�
					player.move(directions);
				}else {
					paint.setColor(Color.argb(alpha, 0, 60, 255));
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(nX, nY, bigSize, paint);
					paint.setColor(Color.argb(alpha, 0, 60, 100));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, bigSize, paint);
					
					//����СԲ
					paint.setColor(Color.argb(alpha, 0, 0, 200));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, smallSize, paint);
					alpha-=5;
					if(alpha<=0)
						alpha=0;
				}
	}
	
	//�������а���
	private void drawOthers() {
		//��������ͼƬ��ָ��λ��
		//����������
		if(music1Open)
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.open), music1X, music1Y, null);
		else
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.close), music1X, music1Y, null);
		//��Ч����
		if(music2Open)
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.open1), music2X, music2Y, null);
		else
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.close1), music2X, music2Y, null);
		
		//�������а�
		paint.setColor(Color.argb(100, 255, 255, 255));
		canvas.drawRect(rankX, rankY, rankX+rankWidth, rankY+rankHeight, paint);
		wordPaint.setTextSize(80*widthOffset);
		for (int i = 0; i < 5 && i<rankList.size(); i++) {
			paint.setAlpha(100);
			canvas.drawBitmap(bgs[rankList.get(i).getBg()],rankX, rankY+i*100, paint);
			paint.setAlpha(255);
			canvas.drawText((i+1)+"��", rankX+25, rankY+90+i*100, wordPaint);
			if(rankList.get(i).getName().length()>=5) {
				paint.setTextSize((200/rankList.get(i).getName().length())*widthOffset);
			}else {
				paint.setTextSize(50*widthOffset);
			}
			paint.setColor(rankList.get(i).getColor());
			canvas.drawText(rankList.get(i).getName(), rankX+140, 
					rankY+(100-paint.getTextSize())/2+i*100+paint.getTextSize(), paint);
			paint.setTextSize(25*widthOffset);
			canvas.drawText(rankList.get(i).GetPlayerWeight(), rankX+350,
					rankY+(100-paint.getTextSize())/2+i*100+paint.getTextSize(), paint);
		}
		
		//�����������
		wordPaint.setTextSize(50*widthOffset);
		canvas.drawText("����:" + player.GetPlayerWeight(), 100*widthOffset, 100*heightOffset, wordPaint);
		
		//���ƹ�������ť
		paint.setColor(player.getColor());
		paint.setAlpha(btnAlpha);
		paint.setStrokeWidth(10);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(attackX+154, attackY+160, attackWidth/2, paint);
		paint.setAlpha(btnAlpha+30);
		canvas.drawBitmap(attackBtn, attackX, attackY, paint);
		btnAlpha = 70;
		
	}

	//��Ҫ�����߳�
	@Override
	public void run() {
		// TODO �Զ����ɵķ������
		while(!player.isDie() && rankList.size()>1) {
			//�жϳ�����λ��
			//��֤1��С���ڹ涨�ķ�Χ��
			//2��û�����޷�Χʱ��С�������Ļ����������
			worldPositionX = player.px - mapWidth/8;
			worldPositionY = player.py - mapHeight/8;
			if(worldPositionX <= 0) {
				worldPositionX = 0;
			}else if(worldPositionX >= mapWidth * 3/4) {
				worldPositionX = mapWidth * 3/4;
			}
			
			if(worldPositionY <= 0) {
				worldPositionY = 0;
			}else if(worldPositionY >= mapHeight * 3/4) {
				worldPositionY = mapHeight * 3/4;
			}
			
			//���Ƴ���
			try {
				
				for (PlayerBall c : Computerplayers) {
					if(!c.isDie()) {
					c.judgeSurround(littleBall, Computerplayers, otherPlayers);
					c.moveSelf(player);
					}
				}
				player.judgeSurround(littleBall,Computerplayers,otherPlayers);
				//�����Ʒ���
				MyDraw();
				
				//���������б�
				updateRankList();
			} catch (Exception e) {
				continue;
			}
			
			
			//���ͱ�����Ϣ��������(�����������һ��������)
			if(linkNet&&otherPlayers.size()>0) {
				String msg;
				if(shootSum<player.bullets.size()) {//�ҷ������ӵ�
					shootSum=player.bullets.size();
					msg = player.getName() + "&&" + player.getSize() + "&&" +
							player.px + "&&" + player.py + "&&" + player.getColor() + "&&" + "player" + "&&" + "s";
				}else {//�ҷ�δ�����ӵ�
					msg = player.getName() + "&&" + player.getSize() + "&&" +
							player.px + "&&" + player.py + "&&" + player.getColor() + "&&" + "player" + "&&" + "n";
				}
				LoginActivity.client.getTask().SendMessageTask(msg);
			}
		}
		//��������������
		if(linkNet && otherPlayers.size() > 0) {
			LoginActivity.client.getTask().SendMessageTask(player.getName()+"&&die");
		}
		//����ʱ��
		endTIme = new Date().getTime();
		//���������
		try {
			drawEnd();
		} catch (InterruptedException e) {
		}
	}
	
	private void drawEnd() throws InterruptedException {
		// TODO �Զ����ɵķ������
		//�����Ϸʱ��
		Thread.sleep(1000);
		float gameTime=(endTIme-startTime)/1000;
		//�ۺϵ÷�
		float score=player.getSize()*(player.eatnum+1)/10;
		if(!player.isDie()) {
			LoginActivity.music.SEPlay(5);
			//ʤ��
			canvas = sfh.lockCanvas();
			canvas.drawRGB(255, 255, 255);
			canvas.drawBitmap(bg, 0, 0, paint);
			wordPaint.setColor(Color.rgb(250, 128, 10));
			wordPaint.setTextSize(100*widthOffset);
			canvas.drawText("�������", 100*widthOffset, 100*heightOffset, wordPaint);
			
			wordPaint.setTextSize(60*widthOffset);
			wordPaint.setColor(Color.BLACK);
			canvas.drawText("���ʱ��: "+String.format("%.1f ��", gameTime), 200*widthOffset, 300*heightOffset, wordPaint);
			canvas.drawText("��������: "+player.eatnum, 200*widthOffset, 400*heightOffset, wordPaint);
			canvas.drawText("�������: "+player.GetPlayerWeight(), 200*widthOffset, 500*heightOffset, wordPaint);
			canvas.drawText("�ۺϵ÷�: "+String.format("%.1f", score), 200*widthOffset, 600*heightOffset, wordPaint);
			wordPaint.setAlpha(100);
			canvas.drawText("�������ش���", (screenWidth/2-200)*widthOffset, (screenHeight-150)*heightOffset, wordPaint);
			
			sfh.unlockCanvasAndPost(canvas);
			
			//���ݱ���������
			SQLiteOpenHelper databaseHelper = new DatabaseHelper(activity, "PlayerName");
			
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			db=databaseHelper.getWritableDatabase();
			db.execSQL("update user set record = record+"+gameTime/60+" where id=1");
			db.close();
			
		}else {
			//ʧ��
			canvas = sfh.lockCanvas();
			canvas.drawRGB(255, 255, 255);
			canvas.drawBitmap(bg, 0, 0, paint);
			wordPaint.setColor(Color.BLACK);
			wordPaint.setTextSize(100*widthOffset);
			canvas.drawText("����ʧ��", 100*widthOffset, 100*heightOffset, wordPaint);
			
			wordPaint.setTextSize(60*widthOffset);
			wordPaint.setColor(Color.BLACK);
			Thread.sleep(500);
			canvas.drawText("���ʱ��: "+String.format("%.1f ��", gameTime), 200*widthOffset, 300*heightOffset, wordPaint);
			canvas.drawText("��������: "+player.eatnum, 200*widthOffset, 400*heightOffset, wordPaint);
			canvas.drawText("�������: "+player.GetPlayerWeight(), 200*widthOffset, 500*heightOffset, wordPaint);
			canvas.drawText("�ۺϵ÷�: "+String.format("%.1f", score), 200*widthOffset, 600*heightOffset, wordPaint);
			wordPaint.setAlpha(100);
			canvas.drawText("�������ش���", (screenWidth/2-200)*widthOffset, (screenHeight-100)*heightOffset, wordPaint);

			sfh.unlockCanvasAndPost(canvas);
		}
	}


	private void updateRankList() {
		for (PlayerBall i : rankList) {
			if (i.isDie()) {
				rankList.remove(i);
			}
		}
		
		Collections.sort(rankList, new Comparator<PlayerBall>() {
			@Override
			public int compare(PlayerBall lhs, PlayerBall rhs) {
				// TODO �Զ����ɵķ������
				if(lhs.getSize()>rhs.getSize())
					return -1;
				else if(lhs.getSize()<rhs.getSize())
					return 1;
				else 
					return 0;
			}
		});
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction()&MotionEvent.ACTION_MASK;
		if(action == MotionEvent.ACTION_DOWN) {
			//��ָ����
			if(event.getX(0) > music1X && event.getX(0) <= music1X+150
					&& event.getY(0)>music1Y && event.getY(0)<=music1Y+150) {
				//������������ư�ť
				music1Open = !music1Open;
				if(music1Open)
					LoginActivity.music.BGMPlay();
				else
					LoginActivity.music.BGMStop();
			}else if(event.getX(0) > music2X && event.getX(0) <= music2X+150
					&& event.getY(0)>music2Y && event.getY(0)<=music2Y+150) {
				//���������Ч���ư�ť
				music2Open = !music2Open;
				LoginActivity.music.setFlag(music2Open);
			}else if(event.getX(0) > attackX && event.getX(0) <= attackX+attackWidth
					&& event.getY(0)>attackY && event.getY(0)<=attackY+attackHeight) {
				//���������ť
				btnAlpha = 200;
				//�����ӵ�
				if(directions[0]!=0 || directions[1]!=0) {
					player.spit();
				}
			}
		}else if(action == MotionEvent.ACTION_MOVE) {
			if(event.getX(0)<=divsion) {
				//���ݽ�ɫ�ƶ�
					pX = event.getX(0);
					pY = event.getY(0);
					isShow = true;
				}
			if(isMintorPressed) {
				if(event.getX(event.getActionIndex())<=divsion) {
						//���ݽ�ɫ�ƶ�
						pX = event.getX(event.getActionIndex());
						pY = event.getY(event.getActionIndex());
						isShow = true;
					}
			}
		}else if(action == MotionEvent.ACTION_UP) {
			isShow = false;
			nX = bigSize + 10 + smallSize;
			nY = screenHeight -30 - bigSize - smallSize;
		}else if(action == MotionEvent.ACTION_POINTER_DOWN) {
			isMintorPressed = true;
			btnAlpha = 200;
			//�����ӵ�
			if(directions[0]!=0 || directions[1]!=0) {
				player.spit();
			}
		}else if(action == MotionEvent.ACTION_POINTER_UP) {
			isMintorPressed = false;
		}
		
		if(isShow == false) {
			if(event.getX() <= bigSize + 10 + smallSize)
				nX = bigSize + 10 + smallSize;
			else 
				nX = event.getX();
		}
		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//������Ϸ��ʼʱ��
		startTime = new Date().getTime();
		//����������
		isShow = false;	
		//��ʼ����������
		init();
		//��ʼ������
		initScene();
		flag = true;
		//�������߳�����
		new Thread(this).start();
		if(LoginActivity.client.getTask()!=null) {
			linkNet=true;
		}
		//��ȡ�������ݻ������������Ϣ
		if(linkNet) {
			//������Ϣ
			LoginActivity.client.getTask().ReciveMessageTask();
		new Thread(new Runnable() {
			public void run() {
				while(flag) {
					//��ȡ���������ݣ���װ�������Ϣ
					try {
						initPlayer();
					} catch (Exception e) {
						continue;
					}
				}
				//��Ϸ����ֹͣ����
				LoginActivity.client.getTask().stopRecive();
			}

			private void initPlayer() {
				if(ClientTask.message.size() > 0) {
					//��ȡ��Ϣ
					String []str = ClientTask.message.get(ClientTask.message.size()-1).split("&&");
					if(str.length > 6 || str.length==2) {
						for (PlayerBall player : otherPlayers) {
							if(player.getName().equals(str[0].trim())) {
								if(str.length==2) {
									player.setDie(true);
								}else {
									player.px=Float.parseFloat(str[2].trim());
									player.py=Float.parseFloat(str[3].trim());
									player.setSize(Float.parseFloat(str[1].trim()));
									if(str[6].equals("s")) {
										player.spit();
									}
								}
							}
							break;
						}
					}
				}
			}
		}).start();
	}
		//ÿ��һ��ʱ��ˢ�³����еľ�̬С��
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(flag) {
					try {
						Thread.sleep(1000);
						
						for (modelBean m : littleBall) {
							if (m.isDie() & random.nextInt(50)==1) {
								m.setDie(false);
							}
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
		}).start();
		
		//��ұ����м��ٿ���,�Լ���������
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (flag) {
					
					//���Լ��ٿ���
					for (PlayerBall p : Computerplayers) {
						if(p.hurt) {
							p.moveSpeed=0;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							p.hurt=false;
						}else {
							p.moveSpeed=GameConstant.BASICSPEED * GameConstant.INITSIZE/(p.getSize());
						}
					}
					//��Ҽ��ٿ���
					for (PlayerBall p : otherPlayers) {
						if(p.hurt) {
							p.moveSpeed=0;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							p.hurt=false;
						}else {
							p.moveSpeed=GameConstant.BASICSPEED * GameConstant.INITSIZE/(p.getSize());
						}
					}
					if(player.hurt) {
						player.moveSpeed=0;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						player.hurt=false;
					}else {
						player.moveSpeed=GameConstant.BASICSPEED * GameConstant.INITSIZE/(player.getSize());
					}
					
					if(player.isDie() || rankList.size()==1) {
						//��Ϸ����
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
						}
						flag=false;
						activity.finish();
					}
					
				}
				
			}
		}).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO �Զ����ɵķ������
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO �Զ����ɵķ������
		flag = false;
	}
	
	private void init() {
		//��ʼ����ͼ����
		mapWidth = screenWidth*4;
		mapHeight = screenHeight*4;
		
		//��ʼ����ʼ���Ͻǵ�
		worldPositionX = random.nextInt(mapWidth/2);
		worldPositionY = random.nextInt(mapHeight/2);
		
		music1X = (screenWidth-150)*widthOffset;
		music1Y = 510*heightOffset;
		music2X = (music1X-150)*widthOffset;
		music2Y = 510*heightOffset;
		rankX = (int) ((screenWidth-450)*widthOffset);
		rankY = 0;
		rankWidth = (int) (450*heightOffset);
		rankHeight = (int) (500*heightOffset);
		//��������
		music1Open = LoginActivity.music.getPlayState();
		//������Ч
		music2Open = LoginActivity.music.getFlag();
		
		//��������ť��ز���
		attackX = (int) ((screenWidth-400)*widthOffset);
		attackY = (int) ((screenHeight-400)*heightOffset);
		attackWidth = (int) (300*widthOffset);
		attackHeight = (int) (300*heightOffset);
		attackBtn = BitmapFactory.decodeResource(getResources(), attackBtms[random.nextInt(6)]);
		attackBtn = Bitmap.createScaledBitmap(attackBtn, attackWidth, attackHeight, true);
		//��ҷ���ָʾͼ��
		directBmp = BitmapFactory.decodeResource(getResources(), R.drawable.up);
		directBmp = Bitmap.createBitmap(directBmp, 0, 0, directBmp.getWidth(), directBmp.getHeight(), m, true);
		//�޸ı���ͼƬ�ĳߴ�
		for(int i=0;i<bgs.length;i++) {
			bgs[i]=Bitmap.createScaledBitmap(bgs[i], rankWidth, rankHeight/5, true);
		}
		
		bg = Bitmap.createScaledBitmap(bg, (int)(screenWidth*1.1), (int)(screenHeight*1.1), true);
		//��ʼ�����λ�õ���Ϣ
		rect = new Rect(0, 0, mapWidth, mapHeight);
		player = new PlayerBall(worldPositionX + mapWidth/8 ,worldPositionY + mapHeight/8,
				Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
				gameList.get(0).getName(),rect,false, gameList.get(0).getHeadPortrait());
		rankList.add(player);
		
	}
	
	private void initScene() {
		//�����г�ʼ�ľ�̬С��
		for(int i=0;i<mapWidth;i+=littleBallRadius*2)
			for(int j=0;j<mapHeight;j+=littleBallRadius*2) {
				if(random.nextInt(80) == 1) {
					int type = random.nextInt(5)+1;
					/**
					 * type
					 * 1:������
					 * 2:������
					 * 3:�������
					 * 4:��������
					 * 5:Բ��
					 */
					littleBall.add(new modelBean(type, 
							Color.rgb(random.nextInt(255),random.nextInt(255), random.nextInt(255)),
									random.nextInt(360/(type+2)), i, j,
									random.nextInt(littleBallRadius/2)+littleBallRadius/2));
				}
			}
//		��ʼ�����
		for (int i = 1; i < gameList.size(); i++) {
			//�����ж�����Ƿ����
			if(gameList.get(i).getNews().equals("����")==false)
			if(!gameList.get(i).getNews().equals("����")) {
				//�������
				PlayerBall object = new PlayerBall(random.nextInt(mapWidth-100)+50 ,random.nextInt(mapHeight-100)+50,
								Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), 
								gameList.get(i).getName(),rect,false,0);
				otherPlayers.add(object);
				
				rankList.add(object);
			}else {
				//����
				PlayerBall playerBall = new PlayerBall(random.nextInt(mapWidth-100)+50 ,random.nextInt(mapHeight-100)+50,
						Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), 
						gameList.get(i).getName(),rect,true,gameList.get(i).getHeadPortrait());
				Computerplayers.add(playerBall);
				
				rankList.add(playerBall);
			}
		}
	}
	
	
	/**
	 * �����ຯ��
	 */
	//���ڻ����������
	private Path DrawLittleBall(float x,float y,int side,int pdegree,int size) {
		//�����������·��
		Path path = new Path();
		//С����ڽ�
		int ndegree = 360/side;
		//Χ�Ƶ�Բ������
		float rx = x;
		float ry = y;
		
		//ȷ�����Ƶ����
		x = (float) (rx + size * Math.cos(Math.PI*pdegree/180));
		y = (float) (ry - size * Math.sin(Math.PI*pdegree/180));
		path.moveTo(x, y);
		for(int i = 1;i<side;i++) {
		pdegree+=ndegree;
		x = (float) (rx + size *Math.cos(Math.PI*pdegree/180));
		y = (float) (ry - size *Math.sin(Math.PI*pdegree/180));
		path.lineTo(x, y);
		}
		path.close();
		return path;
	}
}
