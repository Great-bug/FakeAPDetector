package fakeapdetector.mp.Database;

import fakeapdetector.mp.dao.MP_CONFIG_K;
import fakeapdetector.mp.dao.SP_config_class;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.util.Log;

/**
 * 数据库方法封装，创建表，删除表，数据(增删该查)...
 * @param <sp_RSSI>
 * @param <var_sequence>
 * @param <SP_info_config>
 * @param <InstallInfo>
 */
public class DatabaseService<sp_RSSI, var_sequence, SP_info_config>
{
	private static DatabaseHelper dbOpenHelper;

	public DatabaseService(Context context)
	{
		dbOpenHelper = new DatabaseHelper(context);
	}

	public void Init()
	{

		dbOpenHelper.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS SP_AP_TABLE(ID integer primary key autoincrement, SSID text)");// sp_RSSI<存储RSSI>
		// dbOpenHelper.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS sp_RSSI(ID integer primary key autoincrement, SSID text,RSSI integer )");//sp_RSSI<存储RSSI>
		// db.execSQL("CREATE TABLE IF NOT EXISTS var_sequence(ID integer primary key autoincrement, SSID text,Var integer )");//var_sequence<RSSI均值>
		dbOpenHelper
				.getWritableDatabase()
				.execSQL(
						"CREATE TABLE IF NOT EXISTS SP_info_config(ID integer primary key autoincrement, SSID text, Window integer,Threshold real ,Collecting_speed integer,Safe_max_var real)");// SP_info_config<配置信息>
		dbOpenHelper.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS MP_CONFIG_K(ID integer primary key autoincrement, SSID text,moving_min_K real,moving_max_K real,unmoving_min_K real,unmoving_max_K real,recommend_K real,K real)");
	}

	public void dropTable(String tableName)
	{
		dbOpenHelper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	public void closeDatabase(String DatabaseName)
	{
		dbOpenHelper.getWritableDatabase().close();
	}

	public void createTablesp_RSSI()
	{
		String sql = "CREATE TABLE IF NOT EXISTS sp_RSSI(ID integer primary key autoincrement, SSID text,RSSI integer )";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}

	public void createTablevar_sequence()
	{
		String sql = "CREATE TABLE IF NOT EXISTS  var_sequence(ID integer primary key autoincrement, SSID text,Var integer )";

		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}

	public void createTableSP_info_config()
	{
		String sql = "CREATE TABLE IF NOT EXISTS SP_info_config(ID integer primary key autoincrement, Window integer,Threshold integer ,Collecting_speed integer)";// SP_info_config<配置信息>

		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}

	public void savesp_RSSI(ScanResult result)
	{

		dbOpenHelper.getWritableDatabase().execSQL("insert into sp_RSSI (SSID,RSSI) values(?,?)", new Object[]
		{ result.SSID, result.level });
	}

	public void savevar_sequence(String SSID, double var)
	{
		dbOpenHelper.getWritableDatabase().execSQL("insert into var_sequence(SSID,Var) values(?,?)", new Object[]
		{ SSID, var });
	}

	public void saveSP_info_config(int window, int threshold, int speed)
	{

		dbOpenHelper.getWritableDatabase().execSQL("insert into SP_info_config(Window,Threshold,Collecting_speed) values(?,?,?)", new Object[]
		{ window, threshold, speed });
	}

	public void updatesp_RSSI(ScanResult result, int ID)
	{

		dbOpenHelper.getWritableDatabase().execSQL("update config set SSID=?,RSSI=? where ID=?", new Object[]
		{ result.SSID, result.level, ID });
	}

	public void updatevar_sequence(String SSID, int Var, int ID)
	{

		dbOpenHelper.getWritableDatabase().execSQL("update application set SSID=?,Var=? where ID=?", new Object[]
		{ SSID, Var, ID });

	}

	public void updateSP_info_config(int window, int threshold, int speed, int ID)
	{

		dbOpenHelper.getWritableDatabase().execSQL("update install set Window=?, Threshold=?,Collecting_speed=? where ID=?", new Object[]
		{ window, threshold, speed, ID });
	}

	public Cursor findsp_RSSI(Integer id)
	{

		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery("select ID,SSID,RSSI from sp_RSSI where ID=?", new String[]
		{ String.valueOf(id) });

		int ID = cursor.getInt(0);
		String SSID = cursor.getString(1);
		int RSSI = cursor.getInt(2);

		return cursor;

	}

	public boolean exist_in_SP_AP_TABLE(String SSID)
	{

		String sql = "select * from SP_AP_TABLE where SSID=\"" + SSID + "\"";
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(sql, null);

		if (cursor.getCount() != 0)
			return true;
		else
			return false;
	}
	
	public boolean exist_in_MP_CONFIG_K(String SSID)
	{

		String sql = "select * from MP_CONFIG_K where SSID=\"" + SSID + "\"";
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(sql, null);
		if (cursor.getCount() != 0)
			return true;
		else
			return false;
	}

	public boolean exist_in_SP_info_config(String SSID)
	{

		String sql = "select * from SP_info_config where SSID=\"" + SSID + "\"";
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(sql, null);

		if (cursor.getCount() != 0)
			return true;
		else
			return false;
	}

	public void insert_into_SP_AP_TABLE(String SSID)
	{
		dbOpenHelper.getWritableDatabase().execSQL("insert into SP_AP_TABLE(SSID) values(?)", new Object[]{ SSID });
	}
	public void insert_into_MP_CONFIG_K(String SSID)
	{
		dbOpenHelper.getWritableDatabase().execSQL("insert into MP_CONFIG_K(SSID) values(?)", new Object[]{ SSID });
	}
	public void insert_into_MP_CONFIG_K(String SSID,float moving_min_K,float moving_max_K,float unmoving_min_K,float unmoving_max_K,float recommend_K,float K)
	{
		dbOpenHelper.getWritableDatabase().execSQL("insert into MP_CONFIG_K(SSID,moving_min_K,moving_max_K,unmoving_min_K,unmoving_max_K,recommend_K,K) values(?,?,?,?,?,?,?)", new Object[]{ SSID,moving_min_K,moving_max_K,unmoving_min_K,unmoving_max_K,recommend_K,K });
	}

	double threshold = 0;
	double safe_max_var = 0;

	public void insert_into_SP_info_config(String SSID, int window_size, int speed)
	{
		String sql = "insert into SP_info_config(SSID,Window,Threshold,Collecting_speed,Safe_max_var) values(\"" + SSID + "\"," + String.valueOf(window_size)
				+ "," + String.valueOf(threshold) + "," + String.valueOf(speed) + "," + String.valueOf(safe_max_var) + ")";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
		// dbOpenHelper.getWritableDatabase().execSQL("insert into SP_info_config(SSID,Window,Collecting_speed) values(?,?,?)",
		// new Object[]{SSID,window_size,speed});
	}

	public Cursor findvar_sequence(Integer id)
	{

		Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery("select ID,SSID,var from config where ID=?", new String[]
		{ String.valueOf(id) });

		int ID = cursor.getInt(0);
		String SSID = cursor.getString(1);
		int var = cursor.getInt(2);

		return cursor;

	}

	public SP_config_class get_SP_info_config(String SSID)
	{
		String sql = "select Window,Threshold,Collecting_speed,Safe_max_var from SP_info_config where SSID=\"" + SSID + "\"";
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(sql, null);

		int count = cursor.getCount();

		SP_config_class rtnConfig = new SP_config_class();

		if (cursor.moveToFirst())
		{
			rtnConfig.window = cursor.getInt(0);
			rtnConfig.threshold = cursor.getFloat(1);
			rtnConfig.speed = cursor.getInt(2);
			rtnConfig.safe_max_var = cursor.getFloat(3);
			rtnConfig.SSID = SSID;
		}

		return rtnConfig;

		// return cursor;

	}
	
	public MP_CONFIG_K get_MP_CONFIG_K(String SSID)
	{
		String sql = "select moving_min_k,moving_max_k,unmoving_min_k,unmoving_max_k,recommend_K,K from MP_CONFIG_K where SSID=\"" + SSID + "\"";
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(sql, null);

		MP_CONFIG_K m_con_k = new MP_CONFIG_K();

		if (cursor.moveToFirst())
		{
			m_con_k.set_moving_K(cursor.getFloat(0), cursor.getFloat(1));
			m_con_k.set_unmoving_K(cursor.getFloat(2), cursor.getFloat(3));
			m_con_k.set_recommend_K(cursor.getFloat(4));
			m_con_k.set_K(cursor.getFloat(5));
			m_con_k.set_SSID(SSID);
		}
		return m_con_k;
	}
	
	public void update_SP_info_config(String SSID,int window,double threshold,int speed)
	{
		String sql = "update SP_info_config set Window="+String.valueOf(window)+",Threshold="+String.valueOf(threshold)+",Collecting_speed="+String.valueOf(speed)+" where SSID=\""+SSID+"\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void update_SP_info_config_maxvar(String SSID,double max_var)
	{
		String sql = "update SP_info_config set Safe_max_var="+String.valueOf(max_var)+" where SSID=\""+SSID+"\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	public void update_SP_info_config_maxvar_threshold(String SSID,double max_var,double threshold)
	{
		String sql = "update SP_info_config set Safe_max_var="+String.valueOf(max_var)+",Threshold="+String.valueOf(threshold)+" where SSID=\""+SSID+"\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void update_MP_CONFIG_K(String SSID,float moving_min_K,float moving_max_K,float unmoving_min_K,float unmoving_max_K,float recommend_K,float K)
	{
		String sql = "update MP_CONFIG_K set moving_min_K=?,moving_max_K=?,unmoving_min_K=?,unmoving_max_K=?,recommend_K=?,K=? where SSID =?";
		dbOpenHelper.getWritableDatabase().execSQL(sql,new Object[]{moving_min_K,moving_max_K,unmoving_min_K,unmoving_max_K,recommend_K,K,SSID});
	}
	
	public void update_MP_CONFIG_moving_K(String SSID,float moving_min_K,float moving_max_K)
	{
		String sql = "update MP_CONFIG_K set movin_min_K=?,moving_max_K=? where SSID =?";
		dbOpenHelper.getWritableDatabase().execSQL(sql,new Object[]{moving_min_K,moving_max_K,SSID});
	}
	
	public void update_MP_CONFIG_unmoving_K(String SSID,float unmoving_min_K,float unmoving_max_K)
	{
		String sql = "update MP_CONFIG_K set unmovin_min_K=?,unmoving_max_K=? where SSID =\"?\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql,new Object[]{unmoving_min_K,unmoving_max_K,SSID});
	}
	
	public void update_MP_CONFIG_K(String SSID,float K)
	{
		String sql = "update MP_CONFIG_K set K=? where SSID =\"?\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql,new Object[]{K,SSID});
	}
	
	public void Create_Table_DET_RSSI(String SSID)
	{
		String tableName = "SP_RSSI_DET_"+SSID;
		String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(ID integer primary key autoincrement, RSSI integer)";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Create_Table_DET_var(String SSID)
	{
		String tableName = "SP_VAR_DET_"+SSID;
		String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(ID integer primary key autoincrement, VAR integer)";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Create_Table_FEA_RSSI(String SSID)
	{
		String tableName = "SP_RSSI_FEA_"+SSID;
		String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(ID integer primary key autoincrement, RSSI integer)";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Create_Table_FEA_var(String SSID)
	{
		String tableName = "SP_VAR_FEA_"+SSID;
		String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(ID integer primary key autoincrement, VAR integer)";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Inser_var_DET(String SSID,double var)
	{
		String tableName = "SP_VAR_DET_"+SSID;
		String sql="Insert into "+tableName+"(VAR) values("+String.valueOf(var)+")";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Inser_RSSI_DET(String SSID, int RSSI)
	{
		String tableName = "SP_RSSI_DET_"+SSID;
		String sql="Insert into "+tableName+"(RSSI) values("+String.valueOf(RSSI)+")";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Inser_var_FEA(String SSID,double var)
	{
		String tableName = "SP_VAR_FEA_"+SSID;
		String sql="Insert into "+tableName+"(VAR) values("+String.valueOf(var)+")";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Inser_RSSI_FEA(String SSID, int RSSI)
	{
		String tableName = "SP_RSSI_FEA_"+SSID;
		String sql="Insert into "+tableName+"(RSSI) values("+String.valueOf(RSSI)+")";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
	}
	
	public void Drop_Feature(String SSID)
	{
		String tableName1 = "SP_RSSI_FEA_"+SSID;
		String sql="drop table if exists "+tableName1;
		dbOpenHelper.getWritableDatabase().execSQL(sql);
//**************************************************************************
		String tableName2 = "SP_RSSI_DET_"+SSID;
		sql="drop table if exists "+tableName2;
		dbOpenHelper.getWritableDatabase().execSQL(sql);
//**************************************************************************
		String tableName3 = "SP_VAR_FEA_"+SSID;
		sql="drop table if exists "+tableName3;
		dbOpenHelper.getWritableDatabase().execSQL(sql);
//**************************************************************************
		String tableName4 = "SP_VAR_DET_"+SSID;
		sql="drop table if exists "+tableName4;
		dbOpenHelper.getWritableDatabase().execSQL(sql);
//**************************************************************************
		sql = "delete from SP_info_config where SSID=\"" + SSID + "\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql);
//**************************************************************************
		sql = "delete from SP_AP_TABLE where SSID=\"" + SSID + "\"";
		dbOpenHelper.getWritableDatabase().execSQL(sql);		
	}
	
	

	public long getDataCount(String tableName)
	{
		Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery("select count(*) from " + tableName, null);
		cursor.moveToFirst();
		return cursor.getLong(0);
	}

	public void close()
	{
		dbOpenHelper.close();
	}
	
	public void branchtest()
	{
		dbOpenHelper.close();
	}
	
	

}
