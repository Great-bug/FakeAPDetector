package fakeapdetector.mp;

import java.util.ArrayList;
import java.util.List;

import fakeapdetector.mp.ContentFragmentView3.Thread_Feature_Collect;
import fakeapdetector.mp.Database.DatabaseService;
import fakeapdetector.mp.dao.SP_config_class;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ContentFragmentView4 extends Fragment
{
	private WifiManager wm;
	private List<ScanResult> results = new ArrayList<ScanResult>();
	private phone_Move pm;
	private refer_AP RAP;
	private TextView refer_AP[] = new TextView[3];
	private TextView refer_DTW[] = new TextView[3];
	private TextView TX_task_state = null;
	private Handler handler = new Handler();
	private Button btn_scan = null;
	private Button btn_K_calc = null;
	private EditText Edit_target_ssid = null;
	private RadioGroup RDG_moving_state = null;
	private Chronometer timer;
	String refer_ssid[] = new String[3];
	double refer_dtw[] = new double[3];
	String target_ssid;
	private boolean moving = false;
	private boolean isCacing_K = false;
	int window = 120;
	int speed_scan = 2;
	int sleep_time = 0;
	int workset_size = 0;
	int rssi;
	int var;
	int sum = 0;
	private List<Integer> work_set;
	private DatabaseService DS;

	float[] moving_K, unmoving_K;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.content_fragment_view4, container, false);
		pm = new phone_Move();
		RAP = new refer_AP();
		btn_scan = (Button) rootView.findViewById(R.id.id_button_Scan);
		btn_K_calc = (Button) rootView.findViewById(R.id.id_K_detect);
		refer_AP[0] = (TextView) rootView.findViewById(R.id.refer_AP1);
		refer_DTW[0] = (TextView) rootView.findViewById(R.id.refer_DTW1);
		refer_AP[1] = (TextView) rootView.findViewById(R.id.refer_AP2);
		refer_DTW[1] = (TextView) rootView.findViewById(R.id.refer_DTW2);
		refer_AP[2] = (TextView) rootView.findViewById(R.id.refer_AP3);
		refer_DTW[2] = (TextView) rootView.findViewById(R.id.refer_DTW3);
		Edit_target_ssid = (EditText) rootView.findViewById(R.id.id_targetAP_ssid);
		RDG_moving_state = (RadioGroup) rootView.findViewById(R.id.id_Moving_RadioGroup);
		timer = (Chronometer) rootView.findViewById(R.id.chronometer1);
		TX_task_state = (TextView) rootView.findViewById(R.id.id_task_state);

		work_set = new ArrayList<Integer>();
		moving = false;
		moving_K = new float[2];
		unmoving_K = new float[2];

		DS = new DatabaseService(getActivity());
		DS.Init();

		for (int i = 0; i < 3; i++)
		{
			refer_AP[i].setTextColor(Color.rgb(0, 0, 255));
			refer_DTW[i].setTextColor(Color.rgb(0, 0, 255));
		}

		Edit_target_ssid.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{// 此处为得到焦点时的处理内容
				} else
				{
					Edit_target_ssid_LostFocus(); // 此处为失去焦点时的处理内容
				}
			}
		});
		btn_K_calc.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btn_click_K_calc();
			}
		});
		RDG_moving_state.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{
				RDG_moving_state_changed(arg0, arg1);
			}
		});

		return rootView;
	}

	private void MonitorAP()
	{
		// 结合之前扫描热点信息的函数
		wm.startScan();
		results = wm.getScanResults();
		int AP_number = results.size();
		String ssid_all[] = new String[AP_number];
		ScanResult result = null;
		for (int i = 0; i < ssid_all.length; i++)
		{
			ssid_all[i] = result.SSID;
		}

		// Strinvg AP_all[]=new String[10];
		// String
		// AP_all[]={"NISL","TP-LINK_1352F8","360免费WiFi-C3","TP-LINK_B21EFE","TP-LINK_726","OFFICE-620","TP-LINK_623A4E","360WiFi-0236","RR","TP-LINK-info-824"};
		pm.readTxt_number("/RSSI/123.txt", "NISL");
		double RSSI_NISL[] = pm.readTxt("/RSSI/123.txt", "NISL");
		double RSSI_NISL_number[] = pm.variance_Incre2(RSSI_NISL);
		double DTW_value[] = new double[ssid_all.length];
		for (int i = 0; i < ssid_all.length; i++)
		{
			if (ssid_all[i] != "NISL")
			{
				pm.readTxt_number("/RSSI/123.txt", ssid_all[i]);
				DTW_value[i] = (double) RAP.DTWDistanceFun(RSSI_NISL_number, RSSI_NISL_number.length,
						pm.variance_Incre2(pm.readTxt("/RSSI/123.txt", ssid_all[i])), pm.variance_Incre2(pm.readTxt("/RSSI/123.txt", ssid_all[i])).length);
			}
		}
		// 一个ssid_all[i]对应一个DTW_value[i]
		// 这块脑子被卡住了

		// 将得到的DTW值进行从小到大的排序
		bubble_Sort(DTW_value);
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
						for (int i = 1; i < 4; i++)
						{
							refer_AP[i].setText(String.valueOf(refer_ssid[i]));
							refer_DTW[i].setText(String.valueOf(refer_dtw[i]));

						}
					}
				});
			}
		}).start();
	}

	// 对一个数组进行从小到大排序算法
	private static void bubble_Sort(double[] unsort)
	{
		for (int i = 0; i < unsort.length; i++)
		{
			for (int j = i + 1; j < unsort.length; j++)
			{
				if (unsort[i] > unsort[j])
				{
					double temp = unsort[i];
					unsort[i] = unsort[j];
					unsort[j] = temp;
				}
			}
		}
	}

	private void Edit_target_ssid_LostFocus()
	{
		target_ssid = Unify_Name(Edit_target_ssid.getText().toString());
	}

	public String Unify_Name(String str)
	{
		return (str.replace("-", "_"));
	}

	public void RDG_moving_state_changed(RadioGroup arg0, int arg1)
	{
		int radioButtonId = arg0.getCheckedRadioButtonId();
		switch (radioButtonId)
		{
		case R.id.id_unmoving_radio:
			moving = false;
			break;
		case R.id.id_moving_radio:
			moving = true;
			break;
		}
	}

	public void btn_click_K_calc()
	{
		target_ssid = Unify_Name(Edit_target_ssid.getText().toString());

		if (isCacing_K)// 当前正在计算K值，单击该按钮后应该停止计算，将自身的text设置为“K值检测”
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
							btn_K_calc.setText("K值检测");
							TX_task_state.setText("无任务");
						}
					});
				}
			}).start();

			timer.stop();
			isCacing_K = false;
		} else
		{
			// *******************************************************************
			if (target_ssid.equals(""))
				return;

			timer.setBase(SystemClock.elapsedRealtime());
			timer.start();
			// 如果数据库中存在该AP的数据，则将其读出并赋值

			sleep_time = (int) (1000 / speed_scan);// speed单位：个/秒，1秒=1000毫秒，所以扫描函数的休眠时间sleep_time应该为1000/speed_scan

			if (!DS.exist_in_MP_CONFIG_K(target_ssid))
			{
				DS.insert_into_MP_CONFIG_K(target_ssid);
			}
			// ***********************************************************************************************
			// UI操作需要放在UI线程中
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
							btn_K_calc.setText("停止计算");
							TX_task_state.setText("正在计算K值...");
						}
					});
				}
			}).start();
			workset_size = 0;
			sum = 0;
			work_set.clear();
			isCacing_K = true;
			Start_calculate_K();
		}
	}
	private void Start_calculate_K()
	{
		new Thread(new Thread_Calculate_K()).start();
	}
	public class Thread_Calculate_K implements Runnable
	{
		@Override
		public void run()
		{
			while (isCacing_K)
			{
				try
				{
					Thread.sleep(sleep_time);//收集速率
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				//RSSI_collect_calc_K(); 
			}
		}
	}
	
	/*
	public void RSSI_collect_calc_K()
	{
		results.clear();
		wm.startScan();
		results = wm.getScanResults();			

		for (ScanResult result : results)
		{
			if(Unify_Name(result.SSID).equals(target_ssid))
			{				
				rssi = result.level;				
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
*/
}
