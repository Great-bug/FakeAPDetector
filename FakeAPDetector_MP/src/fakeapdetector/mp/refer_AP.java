package fakeapdetector.mp;

public class refer_AP
{
	public static double DTWDistanceFun(double MAP[], int I, double RAP[], int J)
	{
		double dist = 0;
		// int r = Math.abs(I-J);//匹配距离
		double g1, g2, g3;
		double distance[][] = null;
		// 进行初始化
		for (int i = 0; i < I; i++)
		{
			for (int j = 0; j < J; j++)
			{
				distance[i][j] = 1200;
			}
		}
		// 动态规划求出每一个参考AP的与目标AP的距离
		distance[0][0] = (double) Math.abs(MAP[0] - RAP[0]);
		for (int i = 1; i <= I; i++)
		{
			distance[i][0] = distance[i - 1][0] + Math.abs(MAP[i] - RAP[0]);
		}
		for (int j = 1; j <= J; j++)
		{
			distance[0][j] = distance[0][j - 1] + Math.abs(MAP[0] - RAP[j]);
		}
		for (int j = 1; j < J; j++)
		{
			for (int i = 1; i < I; i++)
			{
				g1 = distance[i - 1][j] + Math.abs(MAP[i] - RAP[j]);
				g2 = distance[i - 1][j - 1] + 2 * Math.abs(MAP[i] - RAP[j]);
				g3 = distance[i][j - 1] + Math.abs(MAP[i] - RAP[j]);
				g2 = Math.min(g1, g2);
				g3 = Math.min(g2, g3);
				distance[i][j] = g3;
			}
		}
		dist = distance[I - 1][J - 1];
		return dist;
	}
}
