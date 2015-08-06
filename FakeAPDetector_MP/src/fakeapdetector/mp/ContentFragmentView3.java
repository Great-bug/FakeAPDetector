package fakeapdetector.mp;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fakeapdetector.mp.Database.DatabaseService;
import fakeapdetector.mp.dao.SP_config_class;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;


public class ContentFragmentView3 extends Fragment 
{
	@SuppressWarnings("rawtypes")
	private DatabaseService DS;	
	//***********************************************************************************
	private TextView TX_result;//检测结果
	private TextView TX_task_state;//检测结果
	private Chronometer timer;
	private Button button_scan = null;
	private Button btn_feature_create = null;
	private Button btn_feature_clear = null;
	private EditText Edit_ssid_input = null;
	private EditText Edit_window_input = null;
	private EditText Edit_threshold_input = null;
	private EditText Edit_speed_input = null;
	private TextView TX_safemaxvar = null;
	private TextView TX_det_cur_var = null;
	private WaveView my_WaveView = null;
	//***********************************************************************************
	private boolean isScan = false;
	private boolean iscreating = false;
	//***********************************************************************************
	private Handler handler = null;
	static BufferedWriter out = null;
	static FileOutputStream fos;
	static OutputStreamWriter osw;
	//***********************************************************************************
	int workset_size = 0;
	int sum = 0;
	static List<Integer> work_set;
	boolean find_fakeAP;
	String str_find_fakeAP;
	String ssid;
	int rssi=0;
	double var=0;
	double max_var=0;
	//***********************************************************************************
	String ssid_scan,ssid_scan_default;
	int window,window_default;
	double threshold, threshold_default;
	int speed_scan,speed_scan_default,sleep_time;  //sleep_time=(int)(1000/speed_scan)
	double safe_max_var,safe_max_var_default;
	//***********************************************************************************

    private WifiManager wm;
    private String wserviceName;
	private List<ScanResult> results = new ArrayList<ScanResult>();

	
	@SuppressWarnings("rawtypes")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.content_fragment_view3, container, false);
		handler = new Handler();
		button_scan = (Button) rootView.findViewById(R.id.id_button_Scan);
		btn_feature_create = (Button) rootView.findViewById(R.id.id_button_Feature_create);
		btn_feature_clear = (Button) rootView.findViewById(R.id.id_button_Feature_clear);
		Edit_ssid_input = (EditText) rootView.findViewById(R.id.id_ssid_input);
		Edit_window_input = (EditText) rootView.findViewById(R.id.id_window_input);
		Edit_threshold_input = (EditText) rootView.findViewById(R.id.id_threshold_input);
		Edit_speed_input = (EditText) rootView.findViewById(R.id.id_speed_input);
		TX_safemaxvar = (TextView) rootView.findViewById(R.id.id_safe_max_var);
		TX_task_state = (TextView) rootView.findViewById(R.id.id_task_state);
		TX_det_cur_var = (TextView) rootView.findViewById(R.id.id_detect_var);
		my_WaveView = (WaveView) rootView.findViewById(R.id.id_waveview);
		
		timer=(Chronometer)rootView.findViewById(R.id.chronometer1);
		TX_result = (TextView) rootView.findViewById(R.id.id_result);
		TX_result.setTextColor(Color.rgb(0, 255, 0));
		
		wserviceName = Context.WIFI_SERVICE;
		wm = (WifiManager) getActivity().getSystemService(wserviceName);
		
		ssid_scan_default = "NISL";
		window_default=120;
		speed_scan_default=2;
		threshold_default=0;
		safe_max_var_default=0;
		
		work_set = new ArrayList<Integer>();
				
		DS=new DatabaseService(getActivity());
		DS.Init();
		
		button_scan.setOnClickListener(new OnClickListener()  
        {  
            @Override  
            public void onClick(View v)  
            {  
            	btn_click_detect();
            }  
        });		

		Edit_ssid_input.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{// 此处为得到焦点时的处理内容
				} else
				{					
					Edit_ssid_LostFocus();	// 此处为失去焦点时的处理内容				
				}
			}
		});
		
		btn_feature_create.setOnClickListener(new OnClickListener()  
        {  
			 @Override  
	         public void onClick(View v)  
	         {  
				 btn_click_feature_create();
	         }
        });
		
		btn_feature_clear.setOnClickListener(new OnClickListener()  
        {  
            @Override  
            public void onClick(View v)  
            {
            	ssid_scan = Edit_ssid_input.getText().toString();
            	ssid_scan = Unify_Name(ssid_scan);
            	clear_data(ssid_scan);
            }
        });	
		return rootView;
	}
	
//******************************************************************************	
	private void StartMonitor()
	{
		new Thread(new Thread_Monitor()).start();
	}
	private void Start_Feature_Create()
	{
		new Thread(new Thread_Feature_Collect()).start();
	}
//******************************************************************************
	public class Thread_Monitor implements Runnable
	{
		@Override
		public void run()
		{
			while (isScan)
			{
				try
				{
					Thread.sleep(sleep_time);//收集速率
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				MonitorRSSI(); // This lock is useless
			}
		}
	}
	//******************************************************************************
	public class Thread_Feature_Collect implements Runnable
	{
		@Override
		public void run()
		{
			while (iscreating)
			{
				try
				{
					Thread.sleep(sleep_time);//收集速率
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Feature_collect(); // This lock is useless
			}
		}
	}
//******************************************************************************
	private void MonitorRSSI()
	{
		results.clear();
		wm.startScan();
		results = wm.getScanResults();
		for (ScanResult result : results)
		{
			if(Unify_Name(result.SSID).equals(ssid_scan))
			{
				rssi = result.level;				
				DS.Inser_RSSI_DET(ssid_scan, rssi);	
				if(workset_size<window)
				{
					workset_size++;
					work_set.add(rssi);
					sum += rssi;
					
					var = sum/workset_size;
					
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
	    							TX_det_cur_var.setText(String.valueOf(var));
	    							my_WaveView.clear();
	    							my_WaveView.DrawThreshold(threshold);
	    							my_WaveView.DrawWave(var);
	    						}
	    					});
	    				}
	    			}).start();
					
				}
				else if(workset_size == window)
				{
					var  = (double)sum/(double)window;
					DS.Inser_var_DET(ssid_scan, var);
					workset_size++;
					
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
	    							TX_det_cur_var.setText(String.valueOf(var));
	    							my_WaveView.clear();
	    							my_WaveView.DrawThreshold(threshold);
	    							my_WaveView.DrawWave(var);
	    						}
	    					});
	    				}
	    			}).start();
				}
				else
				{					
					sum = sum-(work_set.get(0))+rssi;
					var  = (double)sum/(double)window;	
					work_set.add(rssi);
					work_set.remove(0);
					DS.Inser_var_DET(ssid_scan, var);
					workset_size++;
				
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
		    							TX_det_cur_var.setText(String.valueOf(var));
		    							my_WaveView.clear();
		    							my_WaveView.DrawThreshold(threshold);
		    							my_WaveView.DrawWave(var);
		    						}
		    					});
		    				}
		    			}).start();					
				}
			}			
		}
		
		if((var!=0) && (var > threshold))
		{
			find_fakeAP = true;
			str_find_fakeAP = "存在Evil-Twin AP";
		}else
		{
			find_fakeAP = false;
			str_find_fakeAP = "不存在Evil-Twin AP";
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
						TX_result.setText(str_find_fakeAP);
					}					
				});
			}
			
		}).start();		
	}
	
//******************************************************************************
	private void Feature_collect()
	{
		results.clear();
		wm.startScan();
		results = wm.getScanResults();			

		for (ScanResult result : results)
		{
			if(Unify_Name(result.SSID).equals(ssid_scan))
			{				
				rssi = result.level;				
				DS.Inser_RSSI_FEA(ssid_scan, rssi);	
				if(workset_size<window)
				{
					workset_size++;
					work_set.add(rssi);
					sum += rssi;
					var = sum/workset_size;
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
	    							my_WaveView.clear();
	    							my_WaveView.DrawWave(var);
	    						}
	    					});
	    				}
	    			}).start();
				}
				else if(workset_size == window)
				{
					var  = (double)sum/(double)window;
					max_var = var;
					threshold = max_var;
					DS.Inser_var_FEA(ssid_scan, var);
					workset_size++;
					DS.update_SP_info_config_maxvar_threshold(ssid_scan, max_var,threshold);
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
	    							TX_safemaxvar.setText(String.valueOf(max_var));
	    							my_WaveView.clear();
	    							my_WaveView.DrawThreshold(threshold);
	    							my_WaveView.DrawWave(var);
	    						}
	    					});
	    				}
	    			}).start();
				}
				else
				{					
					sum = sum-(work_set.get(0))+rssi;
					var  = (double)sum/(double)window;	
					work_set.add(rssi);
					work_set.remove(0);
					DS.Inser_var_FEA(ssid_scan, var);
					workset_size++;
					if(max_var < var)
					{
						max_var = var;
						threshold = max_var;
						DS.update_SP_info_config_maxvar_threshold(ssid_scan, max_var,threshold);
						//DS.update_SP_info_config_maxvar(ssid_scan, max_var);
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
		    							TX_safemaxvar.setText(String.valueOf(max_var));
		    						}
		    					});
		    				}
		    			}).start();
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
	    							my_WaveView.clear();
	    							my_WaveView.DrawThreshold(threshold);
	    							my_WaveView.DrawWave(var);
	    						}
	    					});
	    				}
	    			}).start();
					
					
				}
			}			
		}
	}	
//******************************************************************************

	private void clear_data(String SSID)
	{
		DS.Drop_Feature(ssid_scan);;
	}

	private void btn_click_feature_create()
	{
		if (iscreating)//当前正在建立特征库，单击该按钮后应该停止建立，并将其他连个按钮状态置为可用，将自身的text设置为“特征建立”
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
							btn_feature_create.setText("特征建立");
							btn_feature_clear.setEnabled(true);
							button_scan.setEnabled(true);
							TX_task_state.setText("无任务");
						}
					});
				}
			}).start();

			DS.update_SP_info_config_maxvar(ssid_scan, max_var);
			timer.stop();
			iscreating = false;
		} else
		{
			// *******************************************************************
			ssid_scan = Edit_ssid_input.getText().toString();
			ssid_scan = Unify_Name(ssid_scan);
			if (ssid_scan.equals(""))
				return;
			
			timer.setBase(SystemClock.elapsedRealtime());
			timer.start();
			//如果数据库中存在该AP的数据，则将其读出并赋值
			if (DS.exist_in_SP_AP_TABLE(ssid_scan))
			{
				SP_config_class cur_Config = DS.get_SP_info_config(ssid_scan);
				window = cur_Config.window;
				threshold = cur_Config.threshold;
				speed_scan = cur_Config.speed;
				safe_max_var = cur_Config.safe_max_var;
			}
			else//如果数据库中不存在该AP，则将配置信息赋予初始值，以防止因错误操作等导致后续计算异常
			{
				window = window_default;
				threshold = threshold_default;
				speed_scan = speed_scan_default;
				safe_max_var = safe_max_var_default;
			}

			try
			{
				int window_tmp = Integer.parseInt(Edit_window_input.getText().toString());
				if(window_tmp!=0)//防止用户输入0，以消除除零错误
					window = window_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}
			try
			{
				int speed_tmp = Integer.parseInt(Edit_speed_input.getText().toString());
				if(speed_tmp != 0)//防止用户输入0，以消除除零错误
					speed_scan = speed_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}
			try
			{
				double threshold_tmp = Double.parseDouble(Edit_threshold_input.getText().toString());
				if(Math.abs(threshold_tmp) - 0.0 >0.01)
					threshold = threshold_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}	
			
			sleep_time = (int) (1000 / speed_scan);//speed单位：个/秒，1秒=1000毫秒，所以扫描函数的休眠时间sleep_time应该为1000/speed_scan

			if (!DS.exist_in_SP_AP_TABLE(ssid_scan))
			{
				DS.insert_into_SP_AP_TABLE(ssid_scan);
			}

			if (!DS.exist_in_SP_info_config(ssid_scan))
			{
				DS.insert_into_SP_info_config(ssid_scan, window, speed_scan);
			} else
			{
				DS.update_SP_info_config(ssid_scan, window, threshold, speed_scan);
			}
			// ***********************************************************************************************
			//UI操作需要放在UI线程中
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
							btn_feature_clear.setEnabled(false);
							button_scan.setEnabled(false);
							btn_feature_create.setText("停止建立");
							TX_task_state.setText("特征库建立中");
							
							Edit_ssid_input.setText(ssid_scan);;
							Edit_window_input.setText(String.valueOf(window));;
							Edit_threshold_input.setText(String.valueOf(threshold));;
							Edit_speed_input.setText(String.valueOf(speed_scan));
						}
					});
				}
			}).start();

			DS.Create_Table_FEA_RSSI(ssid_scan);
			DS.Create_Table_FEA_var(ssid_scan);
			workset_size = 0;
			my_WaveView.clear_data();
			sum = 0;
			max_var  = 0;
			work_set.clear();
			iscreating = true;
			Start_Feature_Create();
		}
	}
	
	private void btn_click_detect()
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
							button_scan.setText("开始检测");//界面文字修改
							TX_task_state.setText("无任务");
							
							btn_feature_create.setEnabled(true);//其他两个按钮可用
							btn_feature_clear.setEnabled(true);
							
							Edit_ssid_input.setFocusable(true);//四个EditBoX可以修改
							Edit_window_input.setFocusable(true);
							Edit_threshold_input.setFocusable(true);
							Edit_speed_input.setFocusable(true);							
							Edit_ssid_input.setFocusableInTouchMode(true);
							Edit_window_input.setFocusableInTouchMode(true);
							Edit_threshold_input.setFocusableInTouchMode(true);
							Edit_speed_input.setFocusableInTouchMode(true);
						}
					});
				}
			}).start();
			isScan = false;
			timer.stop();
		} else
		{ 
			ssid_scan = Edit_ssid_input.getText().toString();
			ssid_scan = Unify_Name(ssid_scan);
			if(ssid_scan.equals(""))
				return;
			//*******************************************************************
			timer.setBase(SystemClock.elapsedRealtime()); 
			timer.start();
			
			if (DS.exist_in_SP_AP_TABLE(ssid_scan))
			{
				SP_config_class cur_Config = DS.get_SP_info_config(ssid_scan);
				window = cur_Config.window;
				threshold = cur_Config.threshold;
				speed_scan = cur_Config.speed;
				safe_max_var = cur_Config.safe_max_var;
			}
			else//如果数据库中不存在该AP，则将配置信息赋予初始值，以防止因错误操作等导致后续计算异常
			{
				window = window_default;
				threshold = threshold_default;
				speed_scan = speed_scan_default;
				safe_max_var = safe_max_var_default;
			}

			try
			{
				int window_tmp = Integer.parseInt(Edit_window_input.getText().toString());
				if(window_tmp!=0)//防止用户输入0，以消除除零错误
					window = window_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}
			try
			{
				int speed_tmp = Integer.parseInt(Edit_speed_input.getText().toString());
				if(speed_tmp != 0)//防止用户输入0，以消除除零错误
					speed_scan = speed_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}
			try
			{
				double threshold_tmp = Double.parseDouble(Edit_threshold_input.getText().toString());
				if(Math.abs(threshold_tmp) - 0 > 0.01)
					threshold = threshold_tmp;
			} catch (Exception e)
			{
			} finally
			{
			}	
			
			sleep_time = (int) (1000 / speed_scan);//speed单位：个/秒，1秒=1000毫秒，所以扫描函数的休眠时间sleep_time应该为1000/speed_scan

			if (!DS.exist_in_SP_AP_TABLE(ssid_scan))
			{
				DS.insert_into_SP_AP_TABLE(ssid_scan);
			}

			if (!DS.exist_in_SP_info_config(ssid_scan))
			{
				DS.insert_into_SP_info_config(ssid_scan, window, speed_scan);
			} else
			{
				DS.update_SP_info_config(ssid_scan, window, threshold, speed_scan);
			}				
			
			// ***********************************************************************************************			
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
							button_scan.setText("停止检测");//修改界面文字显示
							TX_task_state.setText("正在检测中...");
							
							btn_feature_create.setEnabled(false);//设置另外两个按钮为不可用
							btn_feature_clear.setEnabled(false);

							Edit_ssid_input.setText(ssid_scan);//修改四个EDITBOX的内容
							Edit_window_input.setText(String.valueOf(window));
							Edit_threshold_input.setText(String.valueOf(threshold));
							Edit_speed_input.setText(String.valueOf(speed_scan));
							TX_safemaxvar.setText(String.valueOf(safe_max_var));

							Edit_ssid_input.setFocusable(false);//设置四个EditBox不可用
							Edit_window_input.setFocusable(false);
							Edit_threshold_input.setFocusable(false);
							Edit_speed_input.setFocusable(false);
						}
					});
				}
			}).start();

			isScan = true;
			workset_size = 0;			
			work_set.clear();
			my_WaveView.clear_data();
			sum = 0;
			DS.Create_Table_DET_RSSI(ssid_scan);
			DS.Create_Table_DET_var(ssid_scan);
			var = 0;
			StartMonitor();
		}
	}

	private void Edit_ssid_LostFocus()
	{
		ssid_scan = Edit_ssid_input.getText().toString();
		ssid_scan = Unify_Name(ssid_scan);
		if (DS.exist_in_SP_AP_TABLE(ssid_scan))
		{
			SP_config_class cur_Config = DS.get_SP_info_config(ssid_scan);
			window = cur_Config.window;
			threshold = cur_Config.threshold;
			speed_scan = cur_Config.speed;
			safe_max_var = cur_Config.safe_max_var;
			
			my_WaveView.setWindow(window);
			my_WaveView.setBoundary(0, -100);
			my_WaveView.DrawThreshold(threshold);

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
							Edit_window_input.setText(String.valueOf(window));
							Edit_threshold_input.setText(String.valueOf(threshold));
							Edit_speed_input.setText(String.valueOf(speed_scan));
							TX_safemaxvar.setText(String.valueOf(safe_max_var));
						}
					});
				}
			}).start();
		}
	}
	
	public String Unify_Name(String str)
	{
		String rtn = str.replace("-", "_");
		return rtn;
	}
}
	




