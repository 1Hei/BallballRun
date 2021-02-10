package com.others;

import java.util.List;

import com.bean.ListBean;
import com.example.ballballbattle.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
@SuppressWarnings("rawtypes")
public class MyAdapter extends ArrayAdapter{
 
 
    @SuppressWarnings("unchecked")
	public MyAdapter(Context context, int resource, List<ListBean> objects) {
        super(context, resource, objects);
    }
 
    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
    	ListBean link = (ListBean)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list, null);
 
        ImageView headPortrait = (ImageView)view.findViewById(R.id.headPortrait);
        TextView name = (TextView)view.findViewById(R.id.name);
        TextView news = (TextView)view.findViewById(R.id.news);
        TextView time = (TextView)view.findViewById(R.id.time);
 
        headPortrait.setImageResource(GetImg(link.getHeadPortrait()));
        name.setText(link.getName());
        news.setText(link.getNews());
        time.setText(link.getTime());
 
 
        return view;
    }
    
  //获取头像资源
  	private int GetImg(int p) {
  		switch (p) {
  		case 0:
  			return R.drawable.icon1;
  		case 1:
  			return R.drawable.icon2;
  		case 2:
  			return R.drawable.icon3;
  		case 3:
  			return R.drawable.icon4;
  		case 4:
  			return R.drawable.icon5;
  		case 5:
  			return R.drawable.icon6;
  		default:
  			return R.drawable.ic_launcher;
  		}
  	}
}

