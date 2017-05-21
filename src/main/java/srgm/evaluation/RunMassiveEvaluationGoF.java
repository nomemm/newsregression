package srgm.evaluation;

import srgm.MayRunTestFittingB_b_GoF;
import srgm.NewRunTestFittingB_b;
import srgm.NewRunTestFittingB_b_GoF;
import srgm.RunTestFittingBC_bc;
import srgm.RunTestFittingBC_bc_GoF;


public class RunMassiveEvaluationGoF {
	// @deprecated
	public static void main(String[] args) {
		
		String models1[] = {"WS"};
		
		String models2[] = {"GO"};
		
		String models3[] = {"HD", "L", "W", "WS", "YE", "YR"};
		
		// model - iteration
		for(String func : models2)
		{
		// n - iteration
		for(int i=1; i < 30; i++)
		{
		String[] params = new String[]{String.valueOf(i), func};
	//	RunTestFittingBC_bc_GoF.main(params);
			MayRunTestFittingB_b_GoF.main(params);
		}
		}
	}

}
