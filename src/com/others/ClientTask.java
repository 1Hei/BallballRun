package com.others;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bean.ListBean;

public class ClientTask{
	@SuppressWarnings("unused")
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	
	//接收任务
	private ReciveThread recive;
	
	//用户列表容器
	public static CopyOnWriteArrayList<ListBean> playerList = new CopyOnWriteArrayList<>();
	//消息容器
	public static CopyOnWriteArrayList<String> message = new CopyOnWriteArrayList<>();
	
	public ClientTask(Socket socket) throws UnsupportedEncodingException, IOException {
		// TODO 自动生成的构造函数存根
		this.socket = socket;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
		recive = new ReciveThread(br);
	}
	
	public void SendMessageTask(String msg) {
		pw.println(msg);
		pw.flush();
	}
	
	public void ReciveMessageTask() {
		new Thread(recive).start();
	}
	
	public void stopRecive() {
		recive.stop();
	}
}

class ReciveThread implements Runnable{

	private BufferedReader br;
	private boolean stop;
	
	public ReciveThread(BufferedReader br) {
		this.br = br;
	}
	@Override
	public void run() {
		stop = false;
		String result = new String();
		while(!stop) {
			result = null;
			try {
				result = br.readLine();
				if(result != null) {
					//字符处理
					messageHandler(result);
				}
			} catch (IOException e) {
				//服务器异常处理
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		stop = true;
	}
	
	private void messageHandler(String msg) {
		
		String[] strs = msg.split("&&");
		if(strs.length>1) {
			if(strs.length == 5 && strs[4].equals("list")) {
				//用户列表信息
				ListBean lBean = new ListBean(Integer.parseInt(strs[0]), strs[1], strs[2], strs[3]);
				ClientTask.playerList.add(lBean);
			}else if(strs.length == 2 && strs[1].equals("leave")) {
				//玩家匹配时离开
				ListBean lBean = new ListBean(0, strs[1], "掉线", "掉线");
				ClientTask.playerList.add(lBean);
			}
			else if((strs.length >= 6 && strs[5].equals("player"))||strs[1].equals("die")){
				ClientTask.message.add(msg);
			}
		}
	}
	
}

