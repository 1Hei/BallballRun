package com.bean;

public class Bullet {
	private boolean visiable;
	private float []directions=new float[2];
	public float x,initX;
	public float y,initY;
	public int speed;
	public int size;
	public Bullet(float x,float y,int speed,int size,float hDir,float vDir) {
		// TODO 自动生成的构造函数存根
		this.x=x;
		this.y=y;
		initX=x;
		initY=y;
		this.speed=speed;
		this.size=size;
		visiable=false;
		directions[0]=hDir;
		directions[1]=vDir;
	}
	public Bullet() {
	}
	public boolean isVisiable() {
		return visiable;
	}
	public void setVisiable(boolean visiable) {
		this.visiable = visiable;
	}
	public float[] getDirections() {
		return directions;
	}
	public void setDirections(float[] directions) {
		this.directions = directions;
	}
	
	
	
}
