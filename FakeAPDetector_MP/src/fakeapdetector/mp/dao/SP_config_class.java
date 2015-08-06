package fakeapdetector.mp.dao;

public class SP_config_class
{
	public String SSID;
	public double threshold;
	public double safe_max_var;
	public int window;
	public int speed;
	
	public SP_config_class()
	{
		SSID=new String();
		threshold = 0;
		safe_max_var=0;
		window=120;
		speed=2;
	}
}
