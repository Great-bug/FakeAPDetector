/*
 * 		Wongxming 
 * 		m.wongxming@gmail.com
 * 		April 22,2009
 */

package fakeapdetector.mp.Database;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//public class DbAdapter{
	
	//创建数据库
	/*private static final String DATABASE_CREATE = "create table fakeapdetector("
												+"id INTEGER PRIMARY KEY,"//自动计数的整数字段
												+"SSID text,"//文字
												+"RSSI INTEGER,"//整数
												+"Var INTEGER" +
												");";
												*/


public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "fakeapdetector.db";
	public static final int DATABASE_VERSION = 1;
	//public static final String DATABASE_TABLE = "fakeapdetector";
	public DatabaseHelper(Context context) {//这个类实现了一个构造器，需要传入四个参数：context（对象，可以控制数据库的打开和关闭），name（数据库名称），factory（可以作复杂的查询用，一般不会用），version(传入数据库版本)
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//oncreate方法传入了一个SQLiteDatabase数据库类的db参数，程序中可以使用db对象的execSQL方法来输入储存于DATABASE_CREATE变量中的SQL语句
		
		//db.execSQL(DATABASE_CREATE);
		Log.e("DatabaseHelper", "DatabaseHelperDatabaseHelperDatabaseHelper");
		db.execSQL("CREATE TABLE IF NOT EXISTS SP_AP_TABLE(ID integer primary key autoincrement, SSID text)");//sp_RSSI<存储RSSI>
	//	db.execSQL("CREATE TABLE IF NOT EXISTS sp_RSSI(ID integer primary key autoincrement, SSID text,RSSI integer )");//sp_RSSI<存储RSSI>
		db.execSQL("CREATE TABLE IF NOT EXISTS MP_CONFIG_K(ID integer primary key autoincrement, SSID text,moving_min_K real,moving_max_K real,unmoving_min_K real,unmoving_max_K real,recommend_K real,K real)");//var_sequence<RSSI均值>
		db.execSQL("CREATE TABLE IF NOT EXISTS SP_info_config(ID integer primary key autoincrement, SSID text, Window integer,Threshold real ,Collecting_speed integer,Safe_max_var real)");//SP_info_config<配置信息>
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//onupgrade方法除了传入了一个SQLiteDatabase数据库类的db参数之外，也传入目前应用程序在机器上的数据库版本号（oldVersion），与目前开发的版本所指定的数据库版本号（newVersion）
		//当程序运行onUpgrade方法时，表示机器上的数据库版本号与开发版本的数据库版本号已不同
		
		//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);//首先语句将旧版的数据表移掉，然后再运行
		Log.e("DatabaseHelper", "onUpgradeonUpgradeonUpgrade");
		db.execSQL("DROP TABLE IF EXISTS sp_RSSI");
		db.execSQL("DROP TABLE IF EXISTS var_sequence");
		db.execSQL("DROP TABLE IF EXISTS SP_info_config");
		onCreate(db);//这个语句来重新创建数据表
	}//这么做的好处是很快就能得到一个新版的数据表，缺点是以前储存在数据表中的数据，都在更新数据表的过程中一并删除了。

}
/*  private Context mCtx=null;
private DatabaseHelper dbHelper;
private SQLiteDatabase db;
   /** Constructor */
/*public DbAdapter(Context contentFragmentView3) {
	this.mCtx=contentFragmentView3;
}
public DbAdapter open () throws SQLException{
	dbHelper=new DatabaseHelper(mCtx);
	db=dbHelper.getWritableDatabase();
	return this;
	}
public void close(){
	dbHelper.close();
}
private static final String KEY_ROWID="ID";
private static final String KEY_SSID="SSID";
private static final String KEY_Var="Var";
private static final String KEY_RSSI="RSSI";

String[] strCols=new String[]{
		KEY_ROWID,
		KEY_SSID,
		KEY_RSSI,
		KEY_Var
};

//get all entries
public Cursor getall(){//SQL语言查询数据表的结果，并不是直接储存成一个列表供使用，而是返回一个指针（cursor），程序需要数据表中的数据时，再通过指针来访问
	return db.query(DATABASE_TABLE,
			strCols,
			null,
			null,
			null,
			null,
			null);//SQL语言取得数据表中的所有内容，程序中使用GETALL方法来查询数据表中的所有数据
}
public SQLiteDatabase getWritableDatabase() {
	// TODO Auto-generated method stub
	return null;
}
//add an entry
public long create(String Note){
	Date now=new Date(0);
	ContentValues args=new ContentValues();
	args.put(KEY_SSID, Note);
	args.put(KEY_RSSI, now.getTime());
	return db.insert(DATABASE_TABLE, null, args);
}
}
*/

