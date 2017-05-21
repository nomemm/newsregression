package srgm.evaluation;

import java.util.Map;

import srgm.NewRunTestFittingB_b;
import srgm.NewRunTestFittingB_b_GoF;
import srgm.RunTestFittingBC_bc;
import srgm.RunTestFittingBC_bc_GoF;

public class RunMassiveEvaluationPRoF 
{
	public static void main(String[] args) 
	{
		String models0[] = {"YR", "WS"};
		String models1[] = {"YE"};
		String models2[] = {"GO"};
		String models3[] = {"HD", "L", "W", "WS", "YE", "YR"};
		// model - iteration
		for(String func : models2)
		{
			// n - iteration
			for(int i = 4; i <= 4; i += 1)
			{
				String[] params = new String[]{String.valueOf(i), func, "mobile"}; // mobile for mobile OS
			//		RunTestFittingBC_bc.main(params);
				NewRunTestFittingB_b.main(params);
			}
		}
	//	writeMap(RunTestFittingBC_bc.map, 2); // 1 or 2 params
		writeMap(NewRunTestFittingB_b.map, 1); // 1 or 2 params
	}
	
private static void writeMap(Map<String, double[]> map, int p) 
{	
		for(String k : map.keySet())
		{
			boolean output = true;
			double[] coefs = map.get(k);
			int N = Integer.valueOf(k.substring(k.indexOf("_")+1, k.lastIndexOf("_")));
			
			String aData = "";
			String dData = "";
			for (int i = 0; i < N; i++) 
			{
				aData += coefs[i] + "	";
				dData += coefs[i + N + p] + "	";
				if(coefs[i]< 0 || coefs[i + N + p] < 0)
				{
					output = false;
				}
			}
			if(output)
			{
				System.out.println((k + "	" + aData + dData).trim());
			}
			else
			{
				System.out.println((k + "	" + aData + dData).trim());
			}
		}
	}
}
