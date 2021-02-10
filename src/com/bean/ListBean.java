package com.bean;

public class ListBean {
	    private int headPortrait;
	    private String name;
	    private String news;
	    private String time;
	    
	    public ListBean(int headPortrait, String name, String news, String time){
	        this.headPortrait = headPortrait;
	        this.name = name;
	        this.news = news;
	        this.time = time;
	    }
	 
	    public int getHeadPortrait() {
	        return headPortrait;
	    }
	 
	    public String getName() {
	        return name;
	    }
	 
	    public String getNews() {
	        return news;
	    }
	 
	    public String getTime() {
	        return time;
	    }
	    
	    public void SetPortrait(int i) {
	    	this.headPortrait = i;
	    }
}
