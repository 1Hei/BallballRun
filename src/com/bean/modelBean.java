package com.bean;

public class modelBean {
	//绘制类型
	private int type;
	//绘制颜色
	private int drawColor;
	//模型偏转角
	private int pdegree;
	//模型的位置坐标
	private int x;
	private int y;
	//模型的尺寸
	private int size;
	
	//模型是否显示
	private boolean isDie;
	
	public modelBean(int type,int drawColor,int pdegree,int x,int y,int size) {
		this.type = type;
		this.drawColor = drawColor;
		this.pdegree = pdegree;
		this.x = x;
		this.y = y;
		this.size=size;
		isDie=false;
	}

	public int getType() {
		return type;
	}

	public int getDrawColor() {
		return drawColor;
	}

	public int getPdegree() {
		return pdegree;
	}

	public boolean isDie() {
		return isDie;
	}

	public void setDie(boolean isDie) {
		this.isDie = isDie;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
