package com.example.asg.services.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LoadInfoDB extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "asg_db"; // sql数据库的名字
	private final static int DATABASE_VERSION = 1; 			// sql数据库的版本信息
	private final static String TABLE_NAME = "loadinfo_table"; // 负载信息的表名
	public final static String FIELD_ID = "_id"; // 为了实现SimpleCursorAdaptor所比需要的
	// 后面是4个关键的负载信息内容：
	public final static String FIELD_NAME = "name";
	public final static String FIELD_aplow = "aplow";
	public final static String FIELD_apup = "apup";
	public final static String FIELD_rplow = "rplow";
	public final static String FIELD_rpup = "rpup";

	public LoadInfoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqldb) { // sqldb是一个句柄
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		sb.append(TABLE_NAME);
		sb.append(" (");
		sb.append(FIELD_ID);
		sb.append(" INTEGER primary key autoincrement, ");
		sb.append(FIELD_NAME);
		sb.append(" VARCHAR(20) NOT NULL, ");
		sb.append(FIELD_aplow);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_apup);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_rplow);
		sb.append(" DOUBLE NOT NULL, ");
		sb.append(FIELD_rpup);
		sb.append(" DOUBLE NOT NULL)");
		sqldb.execSQL(sb.toString()); // 创建表
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqldb, int arg1, int arg2) {
		// arg1为旧版本号 arg2为新版本号
		// TODO Auto-generated method stub
	}

	public Cursor select(double ap, double rp) // 查找的时候根据获得的特征信息来找
	{
		// 先得到一个读的句柄
		SQLiteDatabase db = this.getReadableDatabase();
		String whereclause = FIELD_aplow + "<? and " + FIELD_apup
				+ ">?" + " and " + FIELD_rplow + "<? and "
				+ FIELD_rpup + ">?";
		String selectionargs[] = { String.valueOf(ap), String.valueOf(ap),
				String.valueOf(rp), String.valueOf(rp) };
		String columns[] = { FIELD_NAME };
		Cursor cursor = db.query(TABLE_NAME, columns, whereclause,
				selectionargs, null, null, null);
		return cursor;
	}

	public Cursor select() // 查找的时候根据获得的特征信息来找
	{
		// 先得到一个读的句柄
		SQLiteDatabase db = this.getReadableDatabase();
		String whereclause = null;
		String selectionargs[] = null;
		String columns[] = null;
		Cursor cursor = db.query(TABLE_NAME, columns, whereclause,
				selectionargs, null, null, null);
		return cursor;
	}

	public long insert(String name, double aplow, double apup, double rplow,
			double rpup) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues(); // 存放一条记录
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_aplow, aplow);
		cv.put(FIELD_apup, apup);
		cv.put(FIELD_rplow, rplow);
		cv.put(FIELD_rpup, rpup);
		long row = db.insert(TABLE_NAME, null, cv);
		return row; // 表示到目前为止有多少行条记录
	}

	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = FIELD_ID + " =?"; // 删除的时候要根据关键字来删除
		String[] whereArgs = { String.valueOf(id) };
		db.delete(TABLE_NAME, whereClause, whereArgs);
	}

	public void update(int id, String name, double aplow, double apup,
			double rplow, double rpup) {
		// 要根据相应的关键字来找到要修改的记录
		SQLiteDatabase db = this.getWritableDatabase();
		String whereClause = FIELD_ID + " =?"; // 更新的时候要根据关键字来找
		Log.i("更新的SQL语句", whereClause);
		String[] whereArgs = { String.valueOf(id) };
		Log.i("内容", whereArgs[0]);
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_aplow, aplow);
		cv.put(FIELD_apup, apup);
		cv.put(FIELD_rplow, rplow);
		cv.put(FIELD_rpup, rpup);
		db.update(TABLE_NAME, cv, whereClause, whereArgs);

	}

}
