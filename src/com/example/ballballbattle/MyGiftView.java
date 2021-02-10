package com.example.ballballbattle;
import com.example.ballballbattle.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class MyGiftView extends View{

	private long movieStart;
	private Movie movie;
	private int width;
	private int height;
    //�˴�������д�ù��췽��
	public MyGiftView(Context context,AttributeSet attributeSet) {
		super(context,attributeSet);
		//���ļ�����InputStream����ȡ��gifͼƬ��Դ
		movie=Movie.decodeStream(getResources().openRawResource(R.drawable.bg1));
	}
@	Override
	protected void onDraw(Canvas canvas) {
		long curTime=android.os.SystemClock.uptimeMillis();
		//��һ�β���
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime-movieStart)%duraction);
			movie.setTime(relTime);
			canvas.scale(width/movie.width(), height/movie.height());
			movie.draw(canvas, -100, -100);
		//ǿ���ػ�
		invalidate();
		}
	super.onDraw(canvas);
	}

	public void setSize(int width,int height) {
		this.width=3*width/2;
		this.height=3*height/2;
	}
}
