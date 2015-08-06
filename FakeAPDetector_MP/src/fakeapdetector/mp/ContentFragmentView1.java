package fakeapdetector.mp;

import java.util.ArrayList;
import java.util.List;

import fakeapdetector.mp.Database.DatabaseService;
import fakeapdetector.mp.dao.MP_CONFIG_K;
import fakeapdetector.mp.dao.SP_config_class;
import android.content.Context;
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

public class ContentFragmentView1 extends Fragment
{
	private WifiManager wm;
	private String wserviceName;
	private List<ScanResult> results = new ArrayList<ScanResult>();
	private phone_Move pm;
	private TextView TX_task_state = null;
	private TextView TX_moving_K = null;
	private TextView TX_unmoving_K = null;
	private TextView TX_recommend_K = null;
	private Handler handler = new Handler();
	private Button btn_data_slice = null;
	private Button btn_moving_detect = null;
	private Button btn_K_calc = null;
	private EditText Edit_target_ssid = null;
	private EditText Edit_K = null;
	private RadioGroup RDG_moving_state = null;
	private Chronometer timer;
	private MP_CONFIG_K mp_config_K = null;
	private WaveView my_WaveView = null;

	String target_ssid;
	private boolean moving = false;
	private boolean isCacing_K = false;
	int window = 120;
	int speed_scan = 2;
	int sleep_time = 0;
	int workset_size = 0;
	int rssi;
	float var;
	private List<Integer> work_set;
	private Context ctx;

	float[] moving_K, unmoving_K;
	float manual_K;
	float last_var = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.content_fragment_view1, container, false);
		pm = new phone_Move();
		btn_data_slice = (Button) rootView.findViewById(R.id.id_btn_data_slice);
		btn_moving_detect = (Button) rootView.findViewById(R.id.id_tn_moving_detect);
		btn_K_calc = (Button) rootView.findViewById(R.id.id_btn_K_detect);
		Edit_target_ssid = (EditText) rootView.findViewById(R.id.id_targetAP_ssid);
		Edit_K = (EditText) rootView.findViewById(R.id.id_K);
		RDG_moving_state = (RadioGroup) rootView.findViewById(R.id.id_Moving_RadioGroup);
		timer = (Chronometer) rootView.findViewById(R.id.cmt_task_time);
		TX_task_state = (TextView) rootView.findViewById(R.id.id_task_state);
		TX_moving_K = (TextView) rootView.findViewById(R.id.id_moving_K);
		TX_unmoving_K = (TextView) rootView.findViewById(R.id.id_unmoving_K);
		TX_recommend_K = (TextView) rootView.findViewById(R.id.id_recommend_K);
		my_WaveView = (WaveView) rootView.findViewById(R.id.id_waveview);
		ctx = getActivity();
		mp_config_K = new MP_CONFIG_K(ctx);
		
		wserviceName = Context.WIFI_SERVICE;
		wm = (WifiManager) getActivity().getSystemService(wserviceName);

		work_set = new ArrayList<Integer>();
		moving = false;
		moving_K = new float[2];// ��0��min------��1��max
		unmoving_K = new float[2];// ��0��min------��1��max
		manual_K = 0;

		Edit_target_ssid.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{// �˴�Ϊ�õ�����ʱ�Ĵ�������
				} else
				{
					Edit_target_ssid_LostFocus(); // �˴�Ϊʧȥ����ʱ�Ĵ�������
				}
			}
		});
		Edit_K.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{// �˴�Ϊ�õ�����ʱ�Ĵ�������
				} else
				{
					Edit_K_LostFocus(); // �˴�Ϊʧȥ����ʱ�Ĵ�������
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

	// ��һ��������д�С���������㷨
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

	private void Edit_K_LostFocus()
	{
		try
		{
			manual_K = Float.parseFloat(Edit_K.getText().toString());
		} catch (Exception e)
		{
		} finally
		{
		}
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

		if (isCacing_K)// ��ǰ���ڼ���Kֵ�������ð�ť��Ӧ��ֹͣ���㣬�������text����Ϊ��Kֵ��⡱
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
							btn_K_calc.setText("Kֵ���");
							TX_task_state.setText("������");
						}
					});
				}
			}).start();

			timer.stop();
			mp_config_K.commit();
			isCacing_K = false;
		} else
		{
			// *******************************************************************
			if (target_ssid.equals(""))
				return;

			timer.setBase(SystemClock.elapsedRealtime());
			timer.start();
			sleep_time = (int) (1000 / speed_scan);// speed��λ����/�룬1��=1000���룬����ɨ�躯��������ʱ��sleep_timeӦ��Ϊ1000/speed_scan

			mp_config_K.set_SSID(target_ssid);
			if (!mp_config_K.exist_in_MP_CONFIG_K())
			{
				mp_config_K.commit();
			} else
			{
				mp_config_K.pull();
				if ((mp_config_K.get_K() - this.manual_K) > 0.01 && (manual_K - 0) > 0.1)
				{
					mp_config_K.set_K(manual_K);
					mp_config_K.commit();
				}
			}
			// ***********************************************************************************************
			// UI������Ҫ����UI�߳���
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
							btn_K_calc.setText("ֹͣ����");
							TX_task_state.setText("���ڼ���Kֵ...");
							TX_moving_K.setText("[ " + String.valueOf(mp_config_K.get_moving_min_K()) + " , " + String.valueOf(mp_config_K.get_moving_max_K())
									+ " ]");
							TX_unmoving_K.setText("[ " + String.valueOf(mp_config_K.get_unmoving_min_K()) + " , "
									+ String.valueOf(mp_config_K.get_unmoving_max_K()) + " ]");
							TX_recommend_K.setText(String.valueOf(mp_config_K.get_recommend_K()));
							Edit_K.setText(String.valueOf(mp_config_K.get_K()));
						}
					});
				}
			}).start();
			workset_size = 0;
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
					Thread.sleep(sleep_time);// �ռ�����
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				RSSI_collect_calc_K();
			}
		}
	}

	private void RSSI_collect_calc_K()
	{
		results.clear();
		wm.startScan();
		results = wm.getScanResults();

		for (ScanResult result : results)
		{
			if (Unify_Name(result.SSID).equals(target_ssid))
			{
				rssi = result.level;
				if (workset_size < window)
				{
					workset_size++;
					work_set.add(rssi);
				} else if (workset_size == window)
				{
					last_var = getVariance(work_set);
					workset_size++;
				} else
				{
					work_set.add(rssi);
					work_set.remove(0);
					var = getVariance(work_set);
					float inc = var - last_var;
					last_var = var;
					if (moving)
					{
						if (moving_K[0] > Math.abs(inc))
							moving_K[0] = Math.abs(inc);
						if (moving_K[1] < Math.abs(inc))
							moving_K[1] = Math.abs(inc);
					} else
					{
						if (unmoving_K[0] > Math.abs(inc))
							unmoving_K[0] = Math.abs(inc);
						if (unmoving_K[1] < Math.abs(inc))
							unmoving_K[1] = Math.abs(inc);
					}
					mp_config_K.set_moving_K(moving_K[0], moving_K[1]);
					mp_config_K.set_unmoving_K(unmoving_K[0], unmoving_K[1]);
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
									TX_moving_K.setText("[ " + String.valueOf(mp_config_K.get_moving_min_K()) + " , " + String.valueOf(mp_config_K.get_moving_max_K())
											+ " ]");
									TX_unmoving_K.setText("[ " + String.valueOf(mp_config_K.get_unmoving_min_K()) + " , "
											+ String.valueOf(mp_config_K.get_unmoving_max_K()) + " ]");
									TX_recommend_K.setText(String.valueOf(mp_config_K.get_recommend_K()));
									Edit_K.setText(String.valueOf(mp_config_K.get_K()));
								}
							});
						}
					}).start();
				}
			}
		}
	}

	private float getVariance(List<Integer> dataset)
	{
		int sum = 0;
		float sum_1 = 0;
		float mean = 0;
		float var = 0;
		for (int i = 0; i < dataset.size(); i++)
		{
			sum += dataset.get(i);
		}
		mean = sum / dataset.size();
		for (int j = 0; j < dataset.size(); j++)
		{
			sum_1 += ((dataset.get(j) - mean) * (dataset.get(j) - mean));
		}
		var = sum_1 / dataset.size();
		return var;
	}
}
