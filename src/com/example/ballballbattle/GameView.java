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

	//基础变量
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint wordPaint,paint;
	private boolean flag = false;
	private MainActivity activity;
	private Matrix m = new Matrix();
	//相册图片
	private Bitmap[] bgs = new Bitmap[] {BitmapFactory.decodeResource(getResources(), R.drawable.icon1),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon2),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon3),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon4),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon5),
			BitmapFactory.decodeResource(getResources(), R.drawable.icon6)};
	
	//攻击键样式
	private int[] attackBtms = new int[] {R.drawable.wrap1, R.drawable.wrap2,
			R.drawable.wrap3, R.drawable.wrap4, R.drawable.wrap5, R.drawable.wrap6};
	
	//手机屏幕的宽度
	private int screenWidth;
	//手机屏幕的长度
	private int screenHeight;
	//不同手机长宽偏移
	private float widthOffset,heightOffset;
	
	Random random = new Random();
	
	//场景中静态物体的半径
	private int littleBallRadius;
	
	//背景图片
	private Bitmap bg;
	
	//玩家可活动范围
	private Rect rect;
	
	//地图参数
	private int mapWidth;
	private int mapHeight;
	//当前手机屏幕左上角相对地图的位置
	private float worldPositionX;
	private float worldPositionY;
	
	//本轮玩家列表
	private List<ListBean> gameList = QueueActivity.list;
	//玩家
	private PlayerBall player;
	//本局其他电脑玩家列表
	private ArrayList<PlayerBall> Computerplayers = new ArrayList<>();
	//联网玩家
	private ArrayList<PlayerBall> otherPlayers = new ArrayList<>();
	//食物集合
	private ArrayList<modelBean> littleBall = new ArrayList<>();	
	//玩家大小排序列表
	ArrayList<PlayerBall> rankList = new ArrayList<>();
	//玩家的移动方向（垂直和水平）
	private float[]directions = new float[2];
	
	
	//游戏开始时间和结束时间
	private long startTime;
	private long endTIme;

	//绘制虚拟轴需要
	private float bigSize = 0;
	private float smallSize = 0;
	private float divsion = 0;
	//当前触碰的点和上一次触碰的点
	private float pX,pY,nX,nY;
	private boolean isShow;
	int alpha = 70;
	int btnAlpha = 70;
	
	private boolean isMintorPressed = false;
	
	//玩家方向指示图标
	private Bitmap directBmp;
	
	//设置图标出现的位置
	private float music1X,music1Y,music2X,music2Y;
	//游戏音乐与音效状态
	private boolean music1Open,music2Open;
	
	//排行榜绘制相关参数
	private int rankX,rankY,rankWidth,rankHeight;
	
	//攻击按键相关参数
	private int attackX,attackY,attackWidth,attackHeight;
	private Bitmap attackBtn;

	//判断是否联网
	private boolean linkNet = false;
	
	//玩家发射子弹的数量
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
		//虚拟轴相关参数
		
		bigSize = screenWidth/12;
		smallSize = bigSize/1.5f;
		divsion = screenWidth/2-bigSize;
		pX = bigSize + 10 + smallSize;
		pY = screenHeight -30 - bigSize - smallSize;
		nX = pX;
		nY = pY;
	}
	
	
	//绘制方法
	private void MyDraw(){
		canvas = sfh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawColor(Color.WHITE);
		//绘制场景中静态食物
		drawScreen();
		//绘制玩家
		drawPlayer();
		//绘制虚拟轴
		drawVirtualAxis();
		//绘制玩家发射的子弹
		drawBullet();
		//绘制玩家方向指示
		if(isShow) {
			drawDirect();
		}
		//绘制场景中其他固定位置的东西
		drawOthers();
		sfh.unlockCanvasAndPost(canvas);
	}
	
	private void drawBullet() {
		
		//绘制联网玩家发射子弹情况
		for (PlayerBall p : otherPlayers) {
			for (Bullet bullet : p.bullets) {
				if(bullet.isVisiable()) {
					float dis = (float) Math.sqrt(Math.pow(bullet.getDirections()[0], 2) + Math.pow(bullet.getDirections()[1], 2));
					bullet.x+=bullet.speed*bullet.getDirections()[0]/dis;
					bullet.y+=bullet.speed*bullet.getDirections()[1]/dis;
					
					//判断子弹是否到达最大射程
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
		
		//绘制自身发射子弹情况
		for (Bullet bullet : player.bullets) {
			if(bullet.isVisiable()) {
				float dis = (float) Math.sqrt(Math.pow(bullet.getDirections()[0], 2) + Math.pow(bullet.getDirections()[1], 2));
				bullet.x+=bullet.speed*bullet.getDirections()[0]/dis;
				bullet.y+=bullet.speed*bullet.getDirections()[1]/dis;
				
				//判断子弹是否到达最大射程
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
		//绘制玩家方向指示
		float dis = (float) Math.sqrt(Math.pow(directions[0], 2) + Math.pow(directions[1], 2));
		
		float degrees;
		if(directions[0]>0) {
			degrees= (float) (Math.acos(-directions[1]/dis)*180/Math.PI);
		}else {
			degrees= (float) (Math.acos(directions[1]/dis)*180/Math.PI)+180;
		}
		wordPaint.setTextSize(50*widthOffset);
		canvas.drawText("导航: 正在向偏北 "+String.format("%.1f°", degrees)+"方向行驶", 100*widthOffset, 200*heightOffset, wordPaint);
	}


	private void drawPlayer() {
		Paint ballpaint = new Paint();
		ballpaint.setAntiAlias(true);
		ballpaint.setStrokeWidth(5);
		
		//绘制电脑
		for (PlayerBall cBall : Computerplayers) {
			if(!cBall.isDie()) {
			ballpaint.setColor(cBall.getColor());
			canvas.drawCircle(cBall.px-worldPositionX, cBall.py-worldPositionY, cBall.getSize(), ballpaint);
			wordPaint.setTextSize(cBall.getSize());
			canvas.drawText(cBall.getName(), cBall.px-worldPositionX-cBall.getSize()*cBall.getName().length()/4
					, cBall.py-worldPositionY+cBall.getSize()/2, wordPaint);
			}
		}
		
		//绘制其他玩家
		for (PlayerBall cBall : otherPlayers) {
			if(!cBall.isDie()) {
				ballpaint.setColor(cBall.getColor());
				canvas.drawCircle(cBall.px-worldPositionX, cBall.py-worldPositionY, cBall.getSize(), ballpaint);
				wordPaint.setTextSize(cBall.getSize());
				canvas.drawText(cBall.getName(), cBall.px-worldPositionX-cBall.getSize()*cBall.getName().length()/2
						, cBall.py-worldPositionY+cBall.getSize()/2, wordPaint);
			}
		}
		
		//绘制玩家
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
		//绘制场景
		//绘制地图中的静态食物
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
		//虚拟轴的绘制(半透明)
				if(isShow) {
					//绘制大圆
					alpha = 70;
					paint.setColor(Color.argb(150, 0, 60, 100));
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(nX, nY, bigSize, paint);
					paint.setColor(Color.argb(120, 0, 60, 100));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, bigSize, paint);
					//绘制小圆
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
					
					//获取玩家应该运动的方向
					directions[1] = y-nY;	//垂直方向
					directions[0] = x-nX;	//水平方向
					//玩家移动
					player.move(directions);
				}else {
					paint.setColor(Color.argb(alpha, 0, 60, 255));
					paint.setStrokeWidth(20);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(nX, nY, bigSize, paint);
					paint.setColor(Color.argb(alpha, 0, 60, 100));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, bigSize, paint);
					
					//绘制小圆
					paint.setColor(Color.argb(alpha, 0, 0, 200));
					paint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(nX, nY, smallSize, paint);
					alpha-=5;
					if(alpha<=0)
						alpha=0;
				}
	}
	
	//绘制排行榜函数
	private void drawOthers() {
		//绘制设置图片在指定位置
		//背景音控制
		if(music1Open)
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.open), music1X, music1Y, null);
		else
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.close), music1X, music1Y, null);
		//音效控制
		if(music2Open)
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.open1), music2X, music2Y, null);
		else
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.close1), music2X, music2Y, null);
		
		//绘制排行榜
		paint.setColor(Color.argb(100, 255, 255, 255));
		canvas.drawRect(rankX, rankY, rankX+rankWidth, rankY+rankHeight, paint);
		wordPaint.setTextSize(80*widthOffset);
		for (int i = 0; i < 5 && i<rankList.size(); i++) {
			paint.setAlpha(100);
			canvas.drawBitmap(bgs[rankList.get(i).getBg()],rankX, rankY+i*100, paint);
			paint.setAlpha(255);
			canvas.drawText((i+1)+"、", rankX+25, rankY+90+i*100, wordPaint);
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
		
		//绘制玩家质量
		wordPaint.setTextSize(50*widthOffset);
		canvas.drawText("重量:" + player.GetPlayerWeight(), 100*widthOffset, 100*heightOffset, wordPaint);
		
		//绘制攻击键按钮
		paint.setColor(player.getColor());
		paint.setAlpha(btnAlpha);
		paint.setStrokeWidth(10);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(attackX+154, attackY+160, attackWidth/2, paint);
		paint.setAlpha(btnAlpha+30);
		canvas.drawBitmap(attackBtn, attackX, attackY, paint);
		btnAlpha = 70;
		
	}

	//主要绘制线程
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		while(!player.isDie() && rankList.size()>1) {
			//判断场景的位置
			//保证1、小球在规定的范围内
			//2、没到极限范围时，小球相对屏幕总在正中心
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
			
			//绘制场景
			try {
				
				for (PlayerBall c : Computerplayers) {
					if(!c.isDie()) {
					c.judgeSurround(littleBall, Computerplayers, otherPlayers);
					c.moveSelf(player);
					}
				}
				player.judgeSurround(littleBall,Computerplayers,otherPlayers);
				//主绘制方法
				MyDraw();
				
				//更新排名列表
				updateRankList();
			} catch (Exception e) {
				continue;
			}
			
			
			//发送本地信息给服务器(在有其他玩家一起的情况下)
			if(linkNet&&otherPlayers.size()>0) {
				String msg;
				if(shootSum<player.bullets.size()) {//我方发射子弹
					shootSum=player.bullets.size();
					msg = player.getName() + "&&" + player.getSize() + "&&" +
							player.px + "&&" + player.py + "&&" + player.getColor() + "&&" + "player" + "&&" + "s";
				}else {//我方未发射子弹
					msg = player.getName() + "&&" + player.getSize() + "&&" +
							player.px + "&&" + player.py + "&&" + player.getColor() + "&&" + "player" + "&&" + "n";
				}
				LoginActivity.client.getTask().SendMessageTask(msg);
			}
		}
		//告诉其他已死亡
		if(linkNet && otherPlayers.size() > 0) {
			LoginActivity.client.getTask().SendMessageTask(player.getName()+"&&die");
		}
		//结束时间
		endTIme = new Date().getTime();
		//绘制最后结果
		try {
			drawEnd();
		} catch (InterruptedException e) {
		}
	}
	
	private void drawEnd() throws InterruptedException {
		// TODO 自动生成的方法存根
		//玩家游戏时长
		Thread.sleep(1000);
		float gameTime=(endTIme-startTime)/1000;
		//综合得分
		float score=player.getSize()*(player.eatnum+1)/10;
		if(!player.isDie()) {
			LoginActivity.music.SEPlay(5);
			//胜利
			canvas = sfh.lockCanvas();
			canvas.drawRGB(255, 255, 255);
			canvas.drawBitmap(bg, 0, 0, paint);
			wordPaint.setColor(Color.rgb(250, 128, 10));
			wordPaint.setTextSize(100*widthOffset);
			canvas.drawText("任务完成", 100*widthOffset, 100*heightOffset, wordPaint);
			
			wordPaint.setTextSize(60*widthOffset);
			wordPaint.setColor(Color.BLACK);
			canvas.drawText("存活时长: "+String.format("%.1f 秒", gameTime), 200*widthOffset, 300*heightOffset, wordPaint);
			canvas.drawText("吞噬数量: "+player.eatnum, 200*widthOffset, 400*heightOffset, wordPaint);
			canvas.drawText("玩家质量: "+player.GetPlayerWeight(), 200*widthOffset, 500*heightOffset, wordPaint);
			canvas.drawText("综合得分: "+String.format("%.1f", score), 200*widthOffset, 600*heightOffset, wordPaint);
			wordPaint.setAlpha(100);
			canvas.drawText("即将返回大厅", (screenWidth/2-200)*widthOffset, (screenHeight-150)*heightOffset, wordPaint);
			
			sfh.unlockCanvasAndPost(canvas);
			
			//数据保存至本地
			SQLiteOpenHelper databaseHelper = new DatabaseHelper(activity, "PlayerName");
			
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			db=databaseHelper.getWritableDatabase();
			db.execSQL("update user set record = record+"+gameTime/60+" where id=1");
			db.close();
			
		}else {
			//失败
			canvas = sfh.lockCanvas();
			canvas.drawRGB(255, 255, 255);
			canvas.drawBitmap(bg, 0, 0, paint);
			wordPaint.setColor(Color.BLACK);
			wordPaint.setTextSize(100*widthOffset);
			canvas.drawText("任务失败", 100*widthOffset, 100*heightOffset, wordPaint);
			
			wordPaint.setTextSize(60*widthOffset);
			wordPaint.setColor(Color.BLACK);
			Thread.sleep(500);
			canvas.drawText("存活时长: "+String.format("%.1f 秒", gameTime), 200*widthOffset, 300*heightOffset, wordPaint);
			canvas.drawText("吞噬数量: "+player.eatnum, 200*widthOffset, 400*heightOffset, wordPaint);
			canvas.drawText("玩家质量: "+player.GetPlayerWeight(), 200*widthOffset, 500*heightOffset, wordPaint);
			canvas.drawText("综合得分: "+String.format("%.1f", score), 200*widthOffset, 600*heightOffset, wordPaint);
			wordPaint.setAlpha(100);
			canvas.drawText("即将返回大厅", (screenWidth/2-200)*widthOffset, (screenHeight-100)*heightOffset, wordPaint);

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
				// TODO 自动生成的方法存根
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
			//手指按下
			if(event.getX(0) > music1X && event.getX(0) <= music1X+150
					&& event.getY(0)>music1Y && event.getY(0)<=music1Y+150) {
				//点击背景音控制按钮
				music1Open = !music1Open;
				if(music1Open)
					LoginActivity.music.BGMPlay();
				else
					LoginActivity.music.BGMStop();
			}else if(event.getX(0) > music2X && event.getX(0) <= music2X+150
					&& event.getY(0)>music2Y && event.getY(0)<=music2Y+150) {
				//点击背景音效控制按钮
				music2Open = !music2Open;
				LoginActivity.music.setFlag(music2Open);
			}else if(event.getX(0) > attackX && event.getX(0) <= attackX+attackWidth
					&& event.getY(0)>attackY && event.getY(0)<=attackY+attackHeight) {
				//点击攻击按钮
				btnAlpha = 200;
				//发射子弹
				if(directions[0]!=0 || directions[1]!=0) {
					player.spit();
				}
			}
		}else if(action == MotionEvent.ACTION_MOVE) {
			if(event.getX(0)<=divsion) {
				//操纵角色移动
					pX = event.getX(0);
					pY = event.getY(0);
					isShow = true;
				}
			if(isMintorPressed) {
				if(event.getX(event.getActionIndex())<=divsion) {
						//操纵角色移动
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
			//发射子弹
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
		//计算游戏开始时间
		startTime = new Date().getTime();
		//虚拟轴隐藏
		isShow = false;	
		//初始化场景参数
		init();
		//初始化场景
		initScene();
		flag = true;
		//主绘制线程启动
		new Thread(this).start();
		if(LoginActivity.client.getTask()!=null) {
			linkNet=true;
		}
		//读取网络数据绘制网络玩家信息
		if(linkNet) {
			//接收信息
			LoginActivity.client.getTask().ReciveMessageTask();
		new Thread(new Runnable() {
			public void run() {
				while(flag) {
					//获取服务器数据，封装成玩家信息
					try {
						initPlayer();
					} catch (Exception e) {
						continue;
					}
				}
				//游戏结束停止接收
				LoginActivity.client.getTask().stopRecive();
			}

			private void initPlayer() {
				if(ClientTask.message.size() > 0) {
					//读取消息
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
		//每隔一段时间刷新场景中的静态小球
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
		
		//玩家被击中减速控制,以及结束操作
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (flag) {
					
					//电脑减速控制
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
					//玩家减速控制
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
						//游戏结束
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
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自动生成的方法存根
		flag = false;
	}
	
	private void init() {
		//初始化地图参数
		mapWidth = screenWidth*4;
		mapHeight = screenHeight*4;
		
		//初始化起始左上角点
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
		//背景音乐
		music1Open = LoginActivity.music.getPlayState();
		//背景音效
		music2Open = LoginActivity.music.getFlag();
		
		//攻击键按钮相关参数
		attackX = (int) ((screenWidth-400)*widthOffset);
		attackY = (int) ((screenHeight-400)*heightOffset);
		attackWidth = (int) (300*widthOffset);
		attackHeight = (int) (300*heightOffset);
		attackBtn = BitmapFactory.decodeResource(getResources(), attackBtms[random.nextInt(6)]);
		attackBtn = Bitmap.createScaledBitmap(attackBtn, attackWidth, attackHeight, true);
		//玩家方向指示图标
		directBmp = BitmapFactory.decodeResource(getResources(), R.drawable.up);
		directBmp = Bitmap.createBitmap(directBmp, 0, 0, directBmp.getWidth(), directBmp.getHeight(), m, true);
		//修改背景图片的尺寸
		for(int i=0;i<bgs.length;i++) {
			bgs[i]=Bitmap.createScaledBitmap(bgs[i], rankWidth, rankHeight/5, true);
		}
		
		bg = Bitmap.createScaledBitmap(bg, (int)(screenWidth*1.1), (int)(screenHeight*1.1), true);
		//初始化玩家位置等信息
		rect = new Rect(0, 0, mapWidth, mapHeight);
		player = new PlayerBall(worldPositionX + mapWidth/8 ,worldPositionY + mapHeight/8,
				Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)),
				gameList.get(0).getName(),rect,false, gameList.get(0).getHeadPortrait());
		rankList.add(player);
		
	}
	
	private void initScene() {
		//场景中初始的静态小球
		for(int i=0;i<mapWidth;i+=littleBallRadius*2)
			for(int j=0;j<mapHeight;j+=littleBallRadius*2) {
				if(random.nextInt(80) == 1) {
					int type = random.nextInt(5)+1;
					/**
					 * type
					 * 1:三角形
					 * 2:正方形
					 * 3:正五边形
					 * 4:正六边形
					 * 5:圆形
					 */
					littleBall.add(new modelBean(type, 
							Color.rgb(random.nextInt(255),random.nextInt(255), random.nextInt(255)),
									random.nextInt(360/(type+2)), i, j,
									random.nextInt(littleBallRadius/2)+littleBallRadius/2));
				}
			}
//		初始化玩家
		for (int i = 1; i < gameList.size(); i++) {
			//首先判断玩家是否掉线
			if(gameList.get(i).getNews().equals("掉线")==false)
			if(!gameList.get(i).getNews().equals("电脑")) {
				//其他玩家
				PlayerBall object = new PlayerBall(random.nextInt(mapWidth-100)+50 ,random.nextInt(mapHeight-100)+50,
								Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), 
								gameList.get(i).getName(),rect,false,0);
				otherPlayers.add(object);
				
				rankList.add(object);
			}else {
				//电脑
				PlayerBall playerBall = new PlayerBall(random.nextInt(mapWidth-100)+50 ,random.nextInt(mapHeight-100)+50,
						Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)), 
						gameList.get(i).getName(),rect,true,gameList.get(i).getHeadPortrait());
				Computerplayers.add(playerBall);
				
				rankList.add(playerBall);
			}
		}
	}
	
	
	/**
	 * 辅助类函数
	 */
	//用于绘制正多边形
	private Path DrawLittleBall(float x,float y,int side,int pdegree,int size) {
		//绘制正多边形路径
		Path path = new Path();
		//小球的内角
		int ndegree = 360/side;
		//围绕的圆心坐标
		float rx = x;
		float ry = y;
		
		//确定绘制的起点
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
