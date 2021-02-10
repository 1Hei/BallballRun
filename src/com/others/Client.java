package com.others;

import java.io.IOException;
import java.net.Socket;

import com.constant.GameConstant;

public class Client {
	private ClientTask task;
	private Socket socket;
	private boolean iw;
	public String info = new String();
	public Client() {
		try {
			info = null;
			iw = true;
			socket = new Socket(GameConstant.IP, 50008);
			task = new ClientTask(socket);
		} catch (Exception e) {
			iw = false;
			info = e.toString();
		} 
	}
	
	public ClientTask getTask() {
		return iw?task:null;
	}
	
	public void close() {
		task = null;
		try {
			if(null!=socket) {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
