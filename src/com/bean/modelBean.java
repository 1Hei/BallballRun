package com.bean;

public class modelBean {
	//��������
	private int type;
	//������ɫ
	private int drawColor;
	//ģ��ƫת��
	private int pdegree;
	//ģ�͵�λ������
	private int x;
	private int y;
	//ģ�͵ĳߴ�
	private int size;
	
	//ģ���Ƿ���ʾ
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
