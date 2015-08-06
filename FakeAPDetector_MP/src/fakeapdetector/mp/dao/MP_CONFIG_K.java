package fakeapdetector.mp.dao;

import fakeapdetector.mp.Database.DatabaseService;
import android.content.Context;

public class MP_CONFIG_K
{
	private DatabaseService DS;
	private Context ctx;

	private String SSID;
	private float moving_min_K;
	private float moving_max_K;
	private float unmoving_min_K;
	private float unmoving_max_K;
	private float recommend_K;
	private float K;

	public MP_CONFIG_K()
	{
		SSID = new String();
		moving_min_K = 0;
		moving_max_K = 0;
		unmoving_min_K = 0;
		unmoving_max_K = 0;
		recommend_K = 0;
		K = 0;
	}
	
	public MP_CONFIG_K(Context context)
	{
		ctx = context;
		DS = new DatabaseService(ctx);
		SSID = new String();
		moving_min_K = 0;
		moving_max_K = 0;
		unmoving_min_K = 0;
		unmoving_max_K = 0;
		recommend_K = 0;
		K = 0;
	}

	public MP_CONFIG_K(Context context, String SSID)
	{
		ctx = context;
		DS = new DatabaseService(ctx);
		this.SSID = SSID;
		if (DS.exist_in_MP_CONFIG_K(SSID))
		{
			MP_CONFIG_K mp_con_k = new MP_CONFIG_K();
			mp_con_k = DS.get_MP_CONFIG_K(SSID);
			this.moving_max_K = mp_con_k.get_moving_max_K();
			this.moving_min_K = mp_con_k.get_moving_min_K();
			this.unmoving_max_K = mp_con_k.get_unmoving_max_K();
			this.unmoving_min_K = mp_con_k.get_unmoving_min_K();
			this.recommend_K = mp_con_k.get_recommend_K();
			this.K = mp_con_k.get_K();
		} else
		{
			this.moving_min_K = 0;
			this.moving_max_K = 0;
			this.unmoving_min_K = 0;
			this.unmoving_max_K = 0;
			this.recommend_K = 0;
			this.K = 0;
		}
	}

	public MP_CONFIG_K(Context context, String SSID, float moving_min_K, float moving_max_K, float unmoving_min_K, float unmoving_max_K, float recommend_K,
			float K)
	{
		this.SSID = SSID;
		this.moving_min_K = moving_min_K;
		this.moving_max_K = moving_max_K;
		this.unmoving_min_K = unmoving_min_K;
		this.unmoving_max_K = unmoving_max_K;
		this.recommend_K = recommend_K;
		this.K = K;
		this.ctx = context;
	}

	public String get_SSID()
	{
		return this.SSID;
	}
	public float get_moving_min_K()
	{
		return this.moving_min_K;
	}
	public float get_moving_max_K()
	{
		return this.moving_max_K;
	}
	public float get_unmoving_min_K()
	{
		return this.unmoving_min_K;
	}
	public float get_unmoving_max_K()
	{
		return this.unmoving_max_K;
	}
	public float get_recommend_K()
	{
		return this.recommend_K;
	}
	public float get_K()
	{
		return this.K;
	}
	

	
	public void set_SSID(String SSID)
	{
		this.SSID = SSID;
	}
	public void set_unmoving_K(float min,float max)
	{
		this.unmoving_min_K = min;
		this.unmoving_max_K = max;
	}
	public void set_moving_K(float min,float max)
	{
		this.moving_min_K = min;
		this.moving_max_K = max;
	}
	public void set_recommend_K(float recommend_K)
	{
		this.recommend_K = recommend_K;
	}
	public void set_K(float K)
	{
		this.K = K;
	}
	
	public boolean exist_in_MP_CONFIG_K()
	{
		if (DS.exist_in_MP_CONFIG_K(this.SSID))
		{
			return true;
		} else
		{
			return false;
		}
	}
	
	public void commit()
	{
		if (DS.exist_in_MP_CONFIG_K(SSID))
		{
			DS.update_MP_CONFIG_K(SSID, moving_min_K, moving_max_K, unmoving_min_K, unmoving_max_K, recommend_K, K);
		} else
		{
			DS.insert_into_MP_CONFIG_K(SSID, moving_min_K, moving_max_K, unmoving_min_K, unmoving_max_K, recommend_K, K);
		}
	}
	
	public void pull()
	{
		if (DS.exist_in_MP_CONFIG_K(SSID))
		{
			MP_CONFIG_K mp_con_k = new MP_CONFIG_K();
			mp_con_k = DS.get_MP_CONFIG_K(SSID);
			this.moving_max_K = mp_con_k.get_moving_max_K();
			this.moving_min_K = mp_con_k.get_moving_min_K();
			this.unmoving_max_K = mp_con_k.get_unmoving_max_K();
			this.unmoving_min_K = mp_con_k.get_unmoving_min_K();
			this.recommend_K = mp_con_k.get_recommend_K();
			this.K = mp_con_k.get_K();
		} 
	}
}
