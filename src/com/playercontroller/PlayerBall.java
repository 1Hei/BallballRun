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
	
	private float size;		//��İ뾶
	public float moveSpeed;	//��ҵ�ǰ���ƶ��ٶ�
	private float volume;	//������
	private float weight;	//�������
	//��һ�������
	public int eatnum=0;
	
	
	private String name=null;
	private String location=null;
	
	//��Ҽ����б�
	public ArrayList<PlayerBall> selfList = new ArrayList<>();
	PlayerBall player;
	//����ӵ�
	public ArrayList<Bullet> bullets=new ArrayList<>();
	public boolean hurt=false;
	
	//�Ƿ�Ϊ���Ա�־
	private boolean isComputer;
	//�Ƿ������ı�־
	private boolean isDie;
	
	//�������4�������λ��
	public float []moveDirections = new float[] {GameConstant.STAY,GameConstant.STAY};
	
	//��ʼ������
	public float px,py,nextX,nextY;
	
	//��һ�����ɫ
	private int color;
	
	//���ѡ��ı���ͼƬ
	private int bg;
	
	//��ҵĻ����
	private Rect moveRegion = new Rect();
	
	//�ӿڷ�Χ
	private int visionWidth = 800;
	
	//��ʼ�����캯��
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
	
	//����ƶ���
	public void move(float []direction) {
		moveDirections = direction;
		float dis = (float) Math.sqrt(Math.pow(direction[0], 2) + Math.pow(direction[1], 2));
			player.px+=player.moveSpeed*direction[0]/dis;
			player.py+=player.moveSpeed*direction[1]/dis;
			
			//��Χ���
			if(player.px<=moveRegion.left + size)
				player.px = moveRegion.left + size;
			else if(player.px >= moveRegion.right-size)
				player.px = moveRegion.right-size;
			
			if(player.py<=moveRegion.top+size)
				player.py = moveRegion.top+size;
			else if(player.py >= moveRegion.bottom-size)
				player.py = moveRegion.bottom-size;
	}
	
	//�����ƶ�
	public void moveSelf(PlayerBall p) {
		//��һ���ƶ���Ŀ�ĵ�����
		float desX = 0,desY = 0;
		//׷������Զ��
		boolean moveTo = true;
			RectF visionRect = new RectF(px-visionWidth/2, py-visionWidth/2, px+visionWidth/2, py+visionWidth/2);
			//ѡȡС����һ�����ſ��ƶ���Զ��ĵ�
		if(visionRect.contains(p.px, p.py)) {
			visionWidth=(int) (1000*Math.sqrt(size/GameConstant.INITSIZE));
			desX=p.px;
			desY=p.py;
			if(size<=p.getSize()) {
				//����С��С
				moveTo=false;
			}else if(size>p.getSize()) {
				//�����Ĵ�
				moveTo=true;
			}
		}else {
			visionWidth=(int) (800*Math.sqrt(size/GameConstant.INITSIZE));
			if(px <= nextX+5 && py<=nextY+5 && px > nextX-5 && py > nextY-5) {
				//������һ�����ĵ�
				nextX = new Random().nextInt(moveRegion.width()-200)+200;
				nextY = new Random().nextInt(moveRegion.height()-200)+200;
			}
				desX = nextX;
				desY = nextY;
		}
			
		//��ȡ����һ��Ҫ�ƶ��ĵ��ɫ�ƶ�
		float[] direction;
			if(moveTo)//����
				direction = new float[] {desX-px,desY-py};
			else //Զ��
				direction = new float[] {px-desX,py-desY};
		move(direction);
	}
	
	//���������
	public void spit() {
		
		if(player.size>GameConstant.INITSIZE+5) {
			Bullet bullet=new Bullet(px, py, 35, 20,moveDirections[0],moveDirections[1]);
			bullet.setVisiable(true);
			bullets.add(bullet);
			player.size-=5;
			
			//������Ч
			LoginActivity.music.SEPlay(4);
		}
		
	}
	
	public void eat(float food) {
		size+=food*GameConstant.INITSIZE/size*0.2f;
		volume = (float) (Math.PI * Math.pow(size, 3)) * 4 / 3;
		weight = volume*GameConstant.DENSITY/1000;
		
		moveSpeed = GameConstant.BASICSPEED * GameConstant.INITSIZE/size;
		//������Ч
		if(!isComputer)
		LoginActivity.music.SEPlay(1);
	}
	
	public void die() {
		isDie = true;
		if(!isComputer)
		LoginActivity.music.SEPlay(3);
		
	}
	
	//�ж�ĳ����Χ�Ƿ�������
	public void judgeSurround(ArrayList<modelBean> littleBall,ArrayList<PlayerBall> Computerplayers,ArrayList<PlayerBall> otherPlayers) {
		for (modelBean m : littleBall) {
			if(!m.isDie() && getDistance(m.getX(), m.getY(), player.px, player.py)<=player.getSize()-m.getSize()) {
				//�������ʳ��
				//ʳ����ʧ
				m.setDie(true);
				player.eat(m.getSize());
			}
		}
		if(!isComputer) {
			for (PlayerBall comp : Computerplayers) {
				//�����������
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
				//��������������
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
			
			//��ұ�������ҵ��ӵ�����
			for (PlayerBall p : otherPlayers) {
				for (Bullet bullet : p.bullets) {
					if(getDistance(player.px, player.py, bullet.x, bullet.y)<=(player.getSize()+bullet.size)/2
							&& bullet.isVisiable() && !player.isDie) {
						bullet.setVisiable(false);
						
						//���Ż�����Ч
						LoginActivity.music.SEPlay(2);
						//��Ҽ���
						player.hurt=true;
					}
				}
			}
			
			//����ӵ����е���
			for (Bullet bullet : player.bullets) {
				for (PlayerBall cmp : Computerplayers) {
					if(getDistance(cmp.px, cmp.py, bullet.x, bullet.y)<=(cmp.getSize()+bullet.size)/2
							&& bullet.isVisiable() && !cmp.isDie) {
						bullet.setVisiable(false);
						
						//���Ż�����Ч
						LoginActivity.music.SEPlay(2);
						//��Ҽ���
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

	//��ȡ��ҵ�������
	public String GetPlayerWeight() {
		float sum = 0;
		int flag = 0;
		String []danwei = new String[] {"��","ǧ��","��"};
		sum = weight;
		while(sum>1000) {
		sum = sum/1000;
		flag++;
		}
		return String.format("%.1f "+danwei[flag], sum);
	}
	
	//��ȡ�����Ϣ
	public String getName() {
		return name;
	}
	//��ȡ��С�������ɫ
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
