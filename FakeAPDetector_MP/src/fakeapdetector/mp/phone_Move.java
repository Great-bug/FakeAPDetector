package fakeapdetector.mp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.Environment;

public class phone_Move
{
	// �����ṩ��ͬ�ļ���ַ�Լ���ͬ�Ĺؼ��ʽ����ļ��Ķ�ȡ
	// ����count��Ҫ���ж�����ȡ�ļ�RSSI�ĸ���
	static int count = 0;

	public static int readTxt_number(String address1, String ssid1)
	{
		String filePath = null;
		try
		{
			String filepath1 = getSDPath();// ��ȡSD����Ŀ¼
			String filepath = filepath1 + address1;
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists())
			{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null)
				{
					int begin = lineTxt.indexOf(ssid1);
					String s = lineTxt.substring(begin + 1);
					if (lineTxt != s)
					{
						count++;
					}
				}
				read.close();
			} else
			{
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e)
		{
			System.out.println("��ȡ�ļ�����");
			e.printStackTrace();
		}

		return count;
	}

	public double[] readTxt(String address, String ssid)
	{
		// int count1 = readTxt_number(address, ssid );
		String filepath = null;
		String Rssi_data[] = null;
		Rssi_data = new String[count];
		double Rssi_data_double[] = new double[Rssi_data.length];
		try
		{
			String filepath1 = getSDPath();// ��ȡSD����Ŀ¼
			filepath = filepath1 + address;
			String encoding = "UTF-8";
			File file = new File(filepath);
			if (file.isFile() && file.exists())
			{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int count1 = 0;
				while ((lineTxt = bufferedReader.readLine()) != null)
				{
					int begin = lineTxt.indexOf(ssid);
					String s = lineTxt.substring(begin + 1);
					if (lineTxt != s)
					{
						String[] a = lineTxt.split(",");

						Rssi_data[count1] = a[1];

						// System.out.println(a[1]);
						// System.out.println(lineTxt);
						// ���ַ�������ת��Ϊ��������

						Rssi_data_double[count1] = Double.parseDouble(Rssi_data[count1]);
						count1++;
					}
				}
				read.close();
			} else
			{
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e)
		{
			System.out.println("��ȡ�ļ�����");
			e.printStackTrace();
		}
		return Rssi_data_double;
	}

	// ��ȡSD��·��
	public static String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
		}
		return sdDir.toString();
	}

	// ���Ӧ��AP����������
	public double[] variance_Incre2(double Numble2[])
	{
		// int count2=readTxt_number(String address,String ssid )
		double sum = 0;
		double sum_li1[] = null;
		sum_li1 = new double[count - 119];
		double sum_li2[] = null;
		sum_li2 = new double[count - 119];
		double sum_li3[] = null;
		sum_li3 = new double[count - 120];
		// ���ȵõ���ǰ�滬������Ϊ120�����ݵĺ�
		for (int i = 0; i < 120; i++)
		{
			sum = +Numble2[i];
		}
		sum_li1[0] = sum;

		for (int j = 0; j < (count - 120); j++)
		{
			sum_li1[j + 1] = sum_li1[j] + Numble2[120 + j] - Numble2[j];
			for (int k = 0; k < 120; k++)
			{
				sum_li2[j] = ((Numble2[k] - (sum_li1[j] / 120)) * (Numble2[k] - (sum_li1[j] / 120))) / 120;
			}
		}
		// ��������
		for (int m = 0; m < sum_li2.length - 1; m++)
		{
			sum_li3[m] = sum_li2[m + 1] - sum_li2[m];
		}
		// �����鷽�����������ֵ
		// for(int n=1;n<sum_li3.length;n++){
		// //value= value+ sum_li3[n];
		//
		// max = sum_li3[0];
		// if(sum_li3[n]<max)
		// max=sum_li3[n];
		// }
		return sum_li3;

	}
}
