package com.playercontroller;

import java.util.ArrayList;
import java.util.Random;

import com.bean.Bullet;
import com.bean.modelBean;
import com.constant.GameConstant;
import com.example.ballballbattle.LoginActivity;
import com.example.ballballbattle.MainActivity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.RectF;

@SuppressLint("DefaultLocale")
public class PlayerBall {
	
	private float size;		//球的半径
	public float moveSpeed;	//玩家当前的移动速度
	private float volume;	//球的体积
	private float weight;	//球的质量
	//玩家击败数量
	public int eatnum=0;
	
	
	private String name=null;
	private String location=null;
	
	//玩家集合列表
	public ArrayList<PlayerBall> selfList = new ArrayList<>();
	PlayerBall player;
	//玩家子弹
	public ArrayList<Bullet> bullets=new ArrayList<>();
	public boolean hurt=false;
	
	//是否为电脑标志
	private boolean isComputer;
	//是否死亡的标志
	private boolean isDie;
	
	//定义玩家4个方向的位移
	public float []moveDirections = new float[] {GameConstant.STAY,GameConstant.STAY};
	
	//起始点坐标
	public float px,py,nextX,nextY;
	
	//玩家绘制颜色
	private int color;
	
	//玩家选择的背景图片
	private int bg;
	
	//玩家的活动区间
	private Rect moveRegion = new Rect();
	
	//视口范围
	private int visionWidth = 800;
	
	//初始化构造函数
	public PlayerBall(float px, float py, int color,String name,Rect rect,boolean isComputer,int bg) {
		size = GameConstant.INITSIZE;
		volume = (float) (Math.PI * Math.pow(size, 3)) * 4 / 3;
		weight = volume*GameConstant.DENSITY/1000;
		moveSpeed = GameConstant.BASICSPEED * GameConstant.INITSIZE/size;
		this.name = name;
		location = MainActivity.location;
		this.color = color;
		this.px = px;
		this.py = py;
		nextX=px;
		nextY=py;
		this.moveRegion = rect;
		this.isComputer = isComputer;
		this.bg = bg;
		isDie = false;
		
		player=this;
		selfList.add(this);
	}
	
	public PlayerBall(float px, float py, int color,String name, Rect rect,float size) {
		this.size = size;
		volume = (float) (Math.PI * Math.pow(size, 3)) * 4 / 3;
		weight = volume*GameConstant.DENSITY/1000;
		moveSpeed = GameConstant.BASICSPEED * GameConstant.INITSIZE/size;
		this.name = name;
		location = MainActivity.location;
		this.color = color;
		this.px = px;
		this.py = py;
		nextX=px;
		nextY=py;
		this.moveRegion = rect;
		isDie = false;
		player=this;
		selfList.add(this);
	}
	
	public PlayerBall(float size,float moveSpeed,float weight,String name,String location,float px,float py) {
		this.size = size;
		this.moveSpeed = moveSpeed;
		this.name = name;
		this.weight = weight;
		this.location = location;
		this.px = px;
		this.py = py;
		nextX=px;
		nextY=py;
		isDie = false;
		player=this;
		selfList.add(this);
	}
	
	//玩家移动类
	public void move(float []direction) {
		moveDirections = direction;
		float dis = (float) Math.sqrt(Math.pow(direction[0], 2) + Math.pow(direction[1], 2));
			player.px+=player.moveSpeed*direction[0]/dis;
			player.py+=player.moveSpeed*direction[1]/dis;
			
			//范围检测
			if(player.px<=moveRegion.left + size)
				player.px = moveRegion.left + size;
			else if(player.px >= moveRegion.right-size)
				player.px = moveRegion.right-size;
			
			if(player.py<=moveRegion.top+size)
				player.py = moveRegion.top+size;
			else if(player.py >= moveRegion.bottom-size)
				player.py = moveRegion.bottom-size;
	}
	
	//机器移动
	public void moveSelf(PlayerBall p) {
		//下一次移动的目的点坐标
		float desX = 0,desY = 0;
		//追击还是远离
		boolean moveTo = true;
			RectF visionRect = new RectF(px-visionWidth/2, py-visionWidth/2, px+visionWidth/2, py+visionWidth/2);
			//选取小球下一步最优可移动或远离的点
		if(visionRect.contains(p.px, p.py)) {
			visionWidth=(int) (1000*Math.sqrt(size/GameConstant.INITSIZE));
			desX=p.px;
			desY=p.py;
			if(size<=p.getSize()) {
				//比最小的小
				moveTo=false;
			}else if(size>p.getSize()) {
				//比最大的大
				moveTo=true;
			}
		}else {
			visionWidth=(int) (800*Math.sqrt(size/GameConstant.INITSIZE));
			if(px <= nextX+5 && py<=nextY+5 && px > nextX-5 && py > nextY-5) {
				//更新下一步到的点
				nextX = new Random().nextInt(moveRegion.width()-200)+200;
				nextY = new Random().nextInt(moveRegion.height()-200)+200;
			}
				desX = nextX;
				desY = nextY;
		}
			
		//获取到下一步要移动的点角色移动
		float[] direction;
			if(moveTo)//靠近
				direction = new float[] {desX-px,desY-py};
			else //远离
				direction = new float[] {px-desX,py-desY};
		move(direction);
	}
	
	//玩家吐球动作
	public void spit() {
		
		if(player.size>GameConstant.INITSIZE+5) {
			Bullet bullet=new Bullet(px, py, 35, 20,moveDirections[0],moveDirections[1]);
			bullet.setVisiable(true);
			bullets.add(bullet);
			player.size-=5;
			
			//播放音效
			LoginActivity.music.SEPlay(4);
		}
		
	}
	
	public void eat(float food) {
		size+=food*GameConstant.INITSIZE/size*0.2f;
		volume = (float) (Math.PI * Math.pow(size, 3)) * 4 / 3;
		weight = volume*GameConstant.DENSITY/1000;
		
		moveSpeed = GameConstant.BASICSPEED * GameConstant.INITSIZE/size;
		//播放音效
		if(!isComputer)
		LoginActivity.music.SEPlay(1);
	}
	
	public void die() {
		isDie = true;
		if(!isComputer)
		LoginActivity.music.SEPlay(3);
		
	}
	
	//判断某点周围是否有物体
	public void judgeSurround(ArrayList<modelBean> littleBall,ArrayList<PlayerBall> Computerplayers,ArrayList<PlayerBall> otherPlayers) {
		for (modelBean m : littleBall) {
			if(!m.isDie() && getDistance(m.getX(), m.getY(), player.px, player.py)<=player.getSize()-m.getSize()) {
				//玩家碰到食物
				//食物消失
				m.setDie(true);
				player.eat(m.getSize());
			}
		}
		if(!isComputer) {
			for (PlayerBall comp : Computerplayers) {
				//玩家碰到电脑
				if(getDistance(player.px, player.py, comp.px, comp.py)<=(player.getSize()+comp.getSize())/2
						&& !player.getName().equals(comp.getName()) && !comp.isDie && !player.isDie) {
					if(player.getSize() < comp.getSize()) {
						player.die();
					}else if(player.getSize() > comp.getSize()) {
						player.eat(comp.getSize());
						player.eatnum++;
						comp.setDie(true);
					}
				}
			}
			for (PlayerBall comp : otherPlayers) {
				//玩家碰到其他玩家
				if(getDistance(player.px, player.py, comp.px, comp.py)<=(player.getSize()+comp.getSize())/2
						&& !comp.isDie && !player.isDie) {
					if(player.getSize() < comp.getSize()) {
						player.die();
					}else if(player.getSize() > comp.getSize()) {
						player.eat(comp.getSize());
						player.eatnum++;
						comp.setDie(true);
					}
				}
			}
			
			//玩家被其他玩家的子弹击中
			for (PlayerBall p : otherPlayers) {
				for (Bullet bullet : p.bullets) {
					if(getDistance(player.px, player.py, bullet.x, bullet.y)<=(player.getSize()+bullet.size)/2
							&& bullet.isVisiable() && !player.isDie) {
						bullet.setVisiable(false);
						
						//播放击中音效
						LoginActivity.music.SEPlay(2);
						//玩家减速
						player.hurt=true;
					}
				}
			}
			
			//玩家子弹击中电脑
			for (Bullet bullet : player.bullets) {
				for (PlayerBall cmp : Computerplayers) {
					if(getDistance(cmp.px, cmp.py, bullet.x, bullet.y)<=(cmp.getSize()+bullet.size)/2
							&& bullet.isVisiable() && !cmp.isDie) {
						bullet.setVisiable(false);
						
						//播放击中音效
						LoginActivity.music.SEPlay(2);
						//玩家减速
						cmp.hurt=true;
					}
				}
			} 
		}
	}
	
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size=size;
		volume = (float) (Math.PI * Math.pow(size, 3)) * 4 / 3;
		weight = volume*GameConstant.DENSITY/1000;
		moveSpeed = GameConstant.BASICSPEED * GameConstant.INITSIZE/size;
	}

	//获取玩家的总质量
	public String GetPlayerWeight() {
		float sum = 0;
		int flag = 0;
		String []danwei = new String[] {"克","千克","吨"};
		sum = weight;
		while(sum>1000) {
		sum = sum/1000;
		flag++;
		}
		return String.format("%.1f "+danwei[flag], sum);
	}
	
	//获取玩家信息
	public String getName() {
		return name;
	}
	//获取到小球绘制颜色
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getLocation() {
		return location;
	}

	public float[] getMoveDirections() {
		return moveDirections;
	}
	
	public boolean isComputer() {
		return isComputer;
	}

	public void setComputer(boolean isComputer) {
		this.isComputer = isComputer;
	}

	public boolean isDie() {
		return isDie;
	}

	public void setDie(boolean isDie) {
		this.isDie = isDie;
	}

	public int getBg() {
		return bg;
	}

	public void setBg(int bg) {
		this.bg = bg;
	}

	private float getDistance(float x1,float y1,float x2,float y2) {
		return (float) Math.sqrt(Math.pow((x1-x2), 2)+Math.pow((y1-y2), 2));
	}
}
