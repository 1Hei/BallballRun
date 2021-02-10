package com.others;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int version = 1;
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO 自动生成的构造函数存根
	}
	
	public DatabaseHelper(Context context, String name) {
		// TODO 自动生成的构造函数存根
		this(context, name, null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table user(id integer primary key,name varchar(200),record DOUBLE)";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		Toast.makeText(MenuActivity.this, "数据库已更新至版本: " + newVersion, Toast.LENGTH_SHORT).show();
	}

}
