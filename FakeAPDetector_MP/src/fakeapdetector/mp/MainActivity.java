package fakeapdetector.mp;

import fakeapdetector.mp.Database.DatabaseService;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends ActionBarActivity// 榛樿缁ф壙ActionBarActivity鑰屼笉鏄疉ctivity鏄増鏈洿鏂扮殑闂�?��(鍘熷洜鎵惧埌浜�
													// 鏄洜涓烘垜鏇存柊浜�?Android SDK
													// Tools 22.6.2
													// 瀵艰嚧浜嗚繖涓棶棰�?涓巗dk鐗堟湰娌℃湁鍏崇�?鎴戝垹鎺�?2.6.2
													// 鐗堟�?瑁呭�?2.3 灏卞ソ浜�?
{
	private DatabaseService DS;

	RadioGroup rdg = null;
	RadioButton rd_view1 = null;
	RadioButton rd_view2 = null;
	RadioButton rd_view3 = null;
	RadioButton rd_view4 = null;

	private ContentFragmentView1 mView1;
	private ContentFragmentView2 mView2;
	private ContentFragmentView3 mView3;
	private ContentFragmentView4 mView4;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * if (savedInstanceState == null)
		 * {
		 * getSupportFragmentManager().beginTransaction().add(R.id.container,
		 * new ContentFragmentView1()).commit();
		 * }
		 */
		WindowManager wm = this.getWindowManager();

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		DS = new DatabaseService(this);
		DS.Init();

		rdg = (RadioGroup) findViewById(R.id.id_ViewRadioGroup);
		rd_view1 = (RadioButton) findViewById(R.id.id_first_view);
		rd_view2 = (RadioButton) findViewById(R.id.id_second_view);
		rd_view3 = (RadioButton) findViewById(R.id.id_third_view);
		rd_view4 = (RadioButton) findViewById(R.id.id_fourth_view);

		rd_view1.setWidth(width / 4);
		rd_view2.setWidth(width / 4);
		rd_view3.setWidth(width / 4);
		rd_view4.setWidth(width / 4);

		mView1 = new ContentFragmentView1();
		mView2 = new ContentFragmentView2();
		mView3 = new ContentFragmentView3();
		mView4 = new ContentFragmentView4();

		setDefaultView();

		rdg.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1)
			{

				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();

				int radioButtonId = arg0.getCheckedRadioButtonId();

				switch (radioButtonId)
				{
				case R.id.id_first_view:
					transaction.replace(R.id.id_content, mView1);
					transaction.commit();
					break;
				case R.id.id_second_view:
					transaction.replace(R.id.id_content, mView2);
					transaction.commit();
					break;
				case R.id.id_third_view:
					transaction.replace(R.id.id_content, mView3);
					transaction.commit();
					break;
				case R.id.id_fourth_view:
					transaction.replace(R.id.id_content, mView4);
					transaction.commit();
					break;
				}
			}
		});

	}

	public void setDefaultView()
	{
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.replace(R.id.id_content, mView1);
		transaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
