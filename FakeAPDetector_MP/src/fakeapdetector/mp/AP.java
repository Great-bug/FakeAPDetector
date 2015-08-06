package fakeapdetector.mp;

public class AP {
	public String SSID;
	public String BSSID;
	public int channel;
	public int RSSI;
	public int window;
	public int threshold;
	public int speed;
	public boolean live;
	public boolean log;
	
	AP(String SSID,String BSSID, int channel, int RSSI, int window,int threshold,int speed,  boolean live, boolean log)
	{
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.channel = channel;
		this.RSSI= RSSI;
		this.window=window;
		this.threshold=threshold;
		this.speed=speed;
		this.live = live;
		this.log = log;
		
		if (SSID.isEmpty())
		{
			this.SSID = "SSID_hide";
		}
	}
	
	public void setSSID(String ssid)
	{
		SSID = ssid;
	}
	
	public void setBSSID(String bssid)
	{
		BSSID = bssid;
	}
	
	public void setChannel(int ch)
	{
		channel = ch;
	}
	
	public void setRSSI(int rssi)
	{
		RSSI = rssi;
	}
	
	public void setwindow(int win)
	{
		window = win;
	}
	
	public void setthreshold(int th)
	{
		threshold = th;
	}
	
	public void setspeed(int sp)
	{
		speed = sp;
	}
	
	public void setLive(boolean Live)
	{
		live = Live;
	}
	
	public void setLog(boolean Log)
	{
		log = Log;
	}
	
	public  String getSSID()
	{
		return SSID;
	}
	
	public String getBSSID()
	{
		return BSSID;
	}
	
	public int getChannel()
	{
		return channel;
	}
	
	public int getRSSI()
	{
		return RSSI;
	}
	public int getwindow()
	{
		return window;
	}
	
	public int getthreshold()
	{
		return threshold;
	}
	
	public int getspeed()
	{
		return speed;
	}
	public boolean getLive()
	{
		return live;
	}
	
	public boolean getLog()
	{
		return log;
	}
	
}
