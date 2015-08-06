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
	
	//�������ݿ�
	/*private static final String DATABASE_CREATE = "create table fakeapdetector("
												+"id INTEGER PRIMARY KEY,"//�Զ������������ֶ�
												+"SSID text,"//����
												+"RSSI INTEGER,"//����
												+"Var INTEGER" +
												");";
												*/


public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "fakeapdetector.db";
	public static final int DATABASE_VERSION = 1;
	//public static final String DATABASE_TABLE = "fakeapdetector";
	public DatabaseHelper(Context context) {//�����ʵ����һ������������Ҫ�����ĸ�������context�����󣬿��Կ������ݿ�Ĵ򿪺͹رգ���name�����ݿ����ƣ���factory�����������ӵĲ�ѯ�ã�һ�㲻���ã���version(�������ݿ�汾)
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//oncreate����������һ��SQLiteDatabase���ݿ����db�����������п���ʹ��db�����execSQL���������봢����DATABASE_CREATE�����е�SQL���
		
		//db.execSQL(DATABASE_CREATE);
		Log.e("DatabaseHelper", "DatabaseHelperDatabaseHelperDatabaseHelper");
		db.execSQL("CREATE TABLE IF NOT EXISTS SP_AP_TABLE(ID integer primary key autoincrement, SSID text)");//sp_RSSI<�洢RSSI>
	//	db.execSQL("CREATE TABLE IF NOT EXISTS sp_RSSI(ID integer primary key autoincrement, SSID text,RSSI integer )");//sp_RSSI<�洢RSSI>
		db.execSQL("CREATE TABLE IF NOT EXISTS MP_CONFIG_K(ID integer primary key autoincrement, SSID text,moving_min_K real,moving_max_K real,unmoving_min_K real,unmoving_max_K real,recommend_K real,K real)");//var_sequence<RSSI��ֵ>
		db.execSQL("CREATE TABLE IF NOT EXISTS SP_info_config(ID integer primary key autoincrement, SSID text, Window integer,Threshold real ,Collecting_speed integer,Safe_max_var real)");//SP_info_config<������Ϣ>
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//onupgrade�������˴�����һ��SQLiteDatabase���ݿ����db����֮�⣬Ҳ����ĿǰӦ�ó����ڻ����ϵ����ݿ�汾�ţ�oldVersion������Ŀǰ�����İ汾��ָ�������ݿ�汾�ţ�newVersion��
		//����������onUpgrade����ʱ����ʾ�����ϵ����ݿ�汾���뿪���汾�����ݿ�汾���Ѳ�ͬ
		
		//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);//������佫�ɰ�����ݱ��Ƶ���Ȼ��������
		Log.e("DatabaseHelper", "onUpgradeonUpgradeonUpgrade");
		db.execSQL("DROP TABLE IF EXISTS sp_RSSI");
		db.execSQL("DROP TABLE IF EXISTS var_sequence");
		db.execSQL("DROP TABLE IF EXISTS SP_info_config");
		onCreate(db);//�����������´������ݱ�
	}//��ô���ĺô��Ǻܿ���ܵõ�һ���°�����ݱ�ȱ������ǰ���������ݱ��е����ݣ����ڸ������ݱ�Ĺ�����һ��ɾ���ˡ�

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
public Cursor getall(){//SQL���Բ�ѯ���ݱ�Ľ����������ֱ�Ӵ����һ���б�ʹ�ã����Ƿ���һ��ָ�루cursor����������Ҫ���ݱ��е�����ʱ����ͨ��ָ��������
	return db.query(DATABASE_TABLE,
			strCols,
			null,
			null,
			null,
			null,
			null);//SQL����ȡ�����ݱ��е��������ݣ�������ʹ��GETALL��������ѯ���ݱ��е���������
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

