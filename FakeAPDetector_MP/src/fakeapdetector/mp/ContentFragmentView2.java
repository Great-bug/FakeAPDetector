package fakeapdetector.mp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;










import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ContentFragmentView2 extends Fragment
{

	private TextView TX_channel_1;//信道
	private TextView TX_RSSI_1;//信号强度
	private TextView TX_SSID_1;//WIFI名字
	private TextView TX_BSSID_1;//MAC地址
	private TextView TX_channel_2;
	private TextView TX_RSSI_2;
	private TextView TX_SSID_2;
	private TextView TX_BSSID_2;
	private Activity parent_activity = null;
	private Chronometer timer;	
	private WaveView my_WaveView = null;
//	static BufferedWriter out = null;
//	static FileOutputStream fos;
//	static OutputStreamWriter osw;
	
//	SimpleDateFormat formatter ;
	//Date curDate ;// 获取当前时间
	//String date_time ;
	
	int rssi_1 = 0;
	int channel_1 = 0;
	String ssid_1;
	String bssid_1;
	int rssi_2 = 0;
	int channel_2 = 0;
	String ssid_2;
	String bssid_2;
	
	int window = 120;
	int workset_size_1=0;
	int workset_size_2=0;
	double sum_1=0;
	double sum_2=0;
	double var_1=0;
	double var_2=0;
	List<Double> RSSI_AP_1, RSSI_AP_2;
	List<Double> VAR_AP_1, VAR_AP_2;
	
	private Handler handler = null;

	private String wserviceName;
	private WifiManager wm;
	
	private List<ScanResult> results = new ArrayList<ScanResult>();

	
	private boolean isScan = false;
	private Button button_scan = null;
	private EditText Edit_ssid_input_1 = null;
	private EditText Edit_ssid_input_2 = null;
	TextView tv_connecting;
	String ssid_scan_1;
	String ssid_scan_2;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.content_fragment_view2, container, false);
		
		handler = new Handler();
		button_scan = (Button) rootView.findViewById(R.id.id_button_Scan);
		
		wserviceName = Context.WIFI_SERVICE;
		wm = (WifiManager) getActivity().getSystemService(wserviceName);
		WifiInfo info = wm.getConnectionInfo();
		parent_activity = this.getActivity();
		//wm.asyncConnect(getActivity(), new WifiServiceHandler());

		//formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
		//curDate = new Date(System.currentTimeMillis());// 获取当前时间
		//date_time = formatter.format(curDate);

		my_WaveView = (WaveView) rootView.findViewById(R.id.id_waveview);
		Edit_ssid_input_1 = (EditText) rootView.findViewById(R.id.id_ssid_input_1);
		Edit_ssid_input_2 = (EditText) rootView.findViewById(R.id.id_ssid_input_2);
		
		RSSI_AP_1 = new ArrayList<Double>();
		VAR_AP_1 = new ArrayList<Double>();
		RSSI_AP_2 = new ArrayList<Double>();
		VAR_AP_2 = new ArrayList<Double>();
		
		String mac = info.getMacAddress();
		int IpAddress = info.getIpAddress();
		String ip = (IpAddress & 0xff) + "." + (IpAddress >> 8 & 0xff) + "." + (IpAddress >> 16 & 0xff) + "." + (IpAddress >> 24 & 0xff);
		String ssid = info.getSSID();

		tv_connecting = (TextView) rootView.findViewById(R.id.id_wifi_state);
		
		String wifi_state = " ";
		if (wm.isWifiEnabled() == false)
		{
			wifi_state = "Wi-Fi未打开，请先打开Wi-Fi";
		} else if (wm.isWifiEnabled() == true && ssid == "0x")
		{
			wifi_state = "Wi-Fi已打开，未连接";
		} else if (wm.isWifiEnabled() && ssid != "0x")

		{
			wifi_state = "Wi-Fi已打开，且连接到：" + ssid + "(" + mac + ")" + "\n" + "IP地址:" + ip;
		}
		tv_connecting.setText(wifi_state);
		
		timer=(Chronometer)rootView.findViewById(R.id.chronometer1);
		
		TX_SSID_1 = (TextView) rootView.findViewById(R.id.id_ssid_1);
		TX_BSSID_1 = (TextView) rootView.findViewById(R.id.id_mac_1);
		TX_channel_1 = (TextView) rootView.findViewById(R.id.id_channel_1);
		TX_RSSI_1 = (TextView) rootView.findViewById(R.id.id_rssi_1);
		TX_SSID_2 = (TextView) rootView.findViewById(R.id.id_ssid_2);
		TX_BSSID_2 = (TextView) rootView.findViewById(R.id.id_mac_2);
		TX_channel_2 = (TextView) rootView.findViewById(R.id.id_channel_2);
		TX_RSSI_2 = (TextView) rootView.findViewById(R.id.id_rssi_2);
		
		TX_SSID_1.setTextColor(Color.rgb(0, 255, 0));
		TX_BSSID_1.setTextColor(Color.rgb(0, 255, 0));
		TX_channel_1.setTextColor(Color.rgb(0, 255, 0));
		TX_RSSI_1.setTextColor(Color.rgb(0, 255, 0));
		TX_SSID_2.setTextColor(Color.rgb(0, 255, 0));
		TX_BSSID_2.setTextColor(Color.rgb(0, 255, 0));
		TX_channel_2.setTextColor(Color.rgb(0, 255, 0));
		TX_RSSI_2.setTextColor(Color.rgb(0, 255, 0));
		
		button_scan.setOnClickListener(new OnClickListener()  
        {  
            @Override  
            public void onClick(View v)  
            {  
            	btn_scan_click();
            }
        });
		 
		return rootView;
	}
	private void StartMonitor()
	{
		new Thread(new Thread_Monitor()).start();
	}

	public class Thread_Monitor implements Runnable
	{
		@Override
		public void run()
		{
			while (isScan)
			{
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MonitorRSSI(); // This lock is useless
			}

		}

	}

	
/* new version don't save data into file
 * 	
 
	private void write_data(String ssid,int rssi, int ch, String MAC)
	{
		try
		{
			out.write(ssid+String.valueOf(rssi)+","+String.valueOf(ch)+","+MAC+"\n");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	private void MonitorRSSI()
	{
		results.clear();
		wm.startScan();
		results = wm.getScanResults();			
		rssi_1 = 0;
		rssi_2 = 0;

		for (ScanResult result : results)
		{
			if(result.SSID.equals(ssid_scan_1))
			{
				ssid_1=result.SSID;
				rssi_1 = result.level;
				channel_1 = frequencyToChannel(result.frequency);
				bssid_1 = result.BSSID;
				//**********************************************************************				
				if(workset_size_1<window)
				{
					workset_size_1++;
					sum_1 += rssi_1;					
					var_1 = sum_1/workset_size_1;
					RSSI_AP_1.add((double)rssi_1);
					VAR_AP_1.add(var_1);
				}
				else if(workset_size_1 == window)
				{
					var_1  = (double)sum_1/(double)window;
					workset_size_1++;
				}
				else
				{					
					sum_1 = sum_1-(RSSI_AP_1.get(0))+rssi_1;
					var_1  = (double)sum_1/(double)window;	
					RSSI_AP_1.add((double)rssi_1);
					RSSI_AP_1.remove(0);
					VAR_AP_1.add(var_1);
					VAR_AP_1.remove(0);
					workset_size_1++;				
				}
				//**********************************************************************
				//write_data(ssid_1,rssi_1,channel_1,bssid_1);				
			}
			if(result.SSID.equals(ssid_scan_2))
			{
				ssid_2=result.SSID;
				rssi_2 = result.level;
				channel_2 = frequencyToChannel(result.frequency);
				bssid_2 = result.BSSID;
				//**********************************************************************				
				if(workset_size_2<window)
				{
					workset_size_2++;
					sum_2 += rssi_2;					
					var_2 = sum_2/workset_size_2;
					RSSI_AP_2.add((double)rssi_2);
					VAR_AP_2.add(var_2);
				}
				else if(workset_size_1 == window)
				{
					var_2  = sum_1/window;
					workset_size_2++;
				}
				else
				{					
					sum_2 = sum_2-(RSSI_AP_2.get(0))+rssi_2;
					var_2  = (double)sum_2/(double)window;	
					RSSI_AP_2.add((double)rssi_2);
					RSSI_AP_2.remove(0);
					VAR_AP_2.add(var_2);
					VAR_AP_2.remove(0);
					workset_size_2++;				
				}
				//write_data(ssid_2,rssi_2,channel_2,bssid_2);				
			}
		}
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						TX_channel_1.setText(String.valueOf(channel_1));
						TX_RSSI_1.setText(String.valueOf(rssi_1));
						TX_BSSID_1.setText(bssid_1);
						
						TX_channel_2.setText(String.valueOf(channel_2));
						TX_RSSI_2.setText(String.valueOf(rssi_2));
						TX_BSSID_2.setText(bssid_2);
						
						//**********************************************
						my_WaveView.clear();
						my_WaveView.setSolidPaint();
						my_WaveView.setPaintColor(0, 255, 0);
						my_WaveView.DrawWave(RSSI_AP_1);
						my_WaveView.setDashPaint();
						my_WaveView.DrawWave(VAR_AP_1);
						
						my_WaveView.setSolidPaint();
						my_WaveView.setPaintColor(255, 0, 0);
						my_WaveView.DrawWave(RSSI_AP_2);
						my_WaveView.setDashPaint();
						my_WaveView.DrawWave(VAR_AP_2);
						my_WaveView.DrawAxis();
						//**********************************************
					}
				});
			}
		}).start();
		
	}
				

	private int frequencyToChannel(int frequency)
	{
		int channel = 1;
		switch (frequency)
		{
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		default:
			break;
		}

		return channel;
	}

	public void changeScan(View view)
	{
		if (isScan)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							button_scan.setText("开始扫描");
						}
					});
				}
			}).start();
			//closefile();
			isScan = false;
			timer.stop();
		} else
		{
			timer.setBase(SystemClock.elapsedRealtime()); 
			 timer.start(); 
			ssid_scan_1 = Edit_ssid_input_1.getText().toString();
			ssid_scan_2 = Edit_ssid_input_2.getText().toString();
			
			//curDate = new Date(System.currentTimeMillis());// 获取当前时间
			//date_time = formatter.format(curDate);
			
			//openLogFile(ssid_scan_1+"-"+ssid_scan_2+"-"+date_time); new version dodn't save data
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							button_scan.setText("停止扫描");
							TX_SSID_1.setText(ssid_scan_1);
							TX_SSID_2.setText(ssid_scan_2);
						}
					});
				}
			}).start();
			
			isScan = true;
			StartMonitor();
		}

	}

	public void btn_scan_click()
	{
		if (isScan)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							button_scan.setText("开始扫描");
						}
					});
				}
			}).start();
			//closefile();
			isScan = false;
			timer.stop();
		} else
		{
			timer.setBase(SystemClock.elapsedRealtime()); 
			timer.start(); 
			if (wm.isWifiEnabled() == false)
			{
				AlertDialog.Builder openwifi_dlg = new AlertDialog.Builder(parent_activity);
				openwifi_dlg.setTitle("提示");
				openwifi_dlg.setCancelable(false); // 设置不能通过“后退”按钮关闭对话框
				openwifi_dlg.setMessage("Wi-Fi未打开，是否打开Wi-Fi?");
				openwifi_dlg.setPositiveButton("确认", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialoginterface, int i)
					{
						wm.setWifiEnabled(true);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
						return;
					}
				}).show();// 显示对话框
			}
			ssid_scan_1 = Edit_ssid_input_1.getText().toString();
			ssid_scan_2 = Edit_ssid_input_2.getText().toString();
			
			//curDate = new Date(System.currentTimeMillis());// 获取当前时间
			//date_time = formatter.format(curDate);
			
			//openLogFile(ssid_scan_1+"-"+ssid_scan_2+"-"+date_time);  new version do not save data
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							button_scan.setText("停止扫描");
							TX_SSID_1.setText(ssid_scan_1);
							TX_SSID_2.setText(ssid_scan_2);
						}
					});
				}
			}).start();
			
			isScan = true;
			StartMonitor();
		}
    
	}
/*old version: save all data into the file RSSI.txt<storage>
 *  * new version has give up the method and don't save data

	public static void openLogFile(String file)
	{
		String sdDir = getSDPath();
		String filename = sdDir+"/RSSI/"+file+".txt";
		File saveFile = new File(filename); 
		try
		{
			fos = new FileOutputStream(saveFile, true);
			osw = new OutputStreamWriter(fos);
			out = new BufferedWriter(osw);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void closefile()
	{
		if(out != null)
		{
			try
			{
				out.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(osw != null)
		{
			try
			{
				osw.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(fos != null)
		{
			try
			{
				fos.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 */	
/*old version: save all data into the file RSSI.txt<storage>
 * new version has give up the method and don't save data
 * 	public static String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}
*/ 

}
