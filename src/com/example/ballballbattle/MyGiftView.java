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
    //此处必须重写该构造方法
	public MyGiftView(Context context,AttributeSet attributeSet) {
		super(context,attributeSet);
		//以文件流（InputStream）读取进gif图片资源
		movie=Movie.decodeStream(getResources().openRawResource(R.drawable.bg1));
	}
@	Override
	protected void onDraw(Canvas canvas) {
		long curTime=android.os.SystemClock.uptimeMillis();
		//第一次播放
		if (movieStart == 0) {
			movieStart = curTime;
		}
		if (movie != null) {
			int duraction = movie.duration();
			int relTime = (int) ((curTime-movieStart)%duraction);
			movie.setTime(relTime);
			canvas.scale(width/movie.width(), height/movie.height());
			movie.draw(canvas, -100, -100);
		//强制重绘
		invalidate();
		}
	super.onDraw(canvas);
	}

	public void setSize(int width,int height) {
		this.width=3*width/2;
		this.height=3*height/2;
	}
}
