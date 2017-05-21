package srgm.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import srgm.fitters.MultiStageExponentalFitterWithBC_bc;
import srgm.fitters.MultiStageExponentalFitterWithB_b;

public class MetricsForSRGM {

	static WeightedObservedPoints obs = new WeightedObservedPoints();
	static double[] coeffB;
	static int N;
	static int type = 3;

	public static int getType() {
		return type;
	}

	public static void setType(int type) {
		MetricsForSRGM.type = type;
	}

	static int bootSize;
	static ParametricUnivariateFunction srgmFunc;

	public static int getN() {
		return N;
	}

	public static int getBootSize() {
		return bootSize;
	}

	public static void setBootSize(int bootSize) {
		MetricsForSRGM.bootSize = bootSize;
	}

	public static void setN(int n) {
		N = n;
	}

	static double[][] bootstrapValuesA;
	static double[] bootstrapValuesB;
	static double[] bootstrapValuesC;
	static double[][] bootstrapValuesD;

	public static double[][] getBootstrapValuesD() {
		return bootstrapValuesD;
	}

	public static void setBootstrapValuesD(double[][] bootstrapValuesD) {
		MetricsForSRGM.bootstrapValuesD = new double[bootSize][];
		for (int j = 0; j < bootSize; j++) {
			MetricsForSRGM.bootstrapValuesD[j] = new double[bootstrapValuesD[j].length];
			for (int i = 0; i < bootstrapValuesD[j].length; i++) {
				MetricsForSRGM.bootstrapValuesD[j][i] = bootstrapValuesD[j][i];
			}
		}
	}

	public static double[][] getBootstrapValuesA() {
		return bootstrapValuesA;
	}

	public static void setBootstrapValuesA(double[][] bootstrapValuesA) {
		MetricsForSRGM.bootstrapValuesA = new double[bootSize][];
		for (int j = 0; j < bootSize; j++) {
			MetricsForSRGM.bootstrapValuesA[j] = new double[bootstrapValuesA[j].length];
			for (int i = 0; i < bootstrapValuesA[j].length; i++) {
				MetricsForSRGM.bootstrapValuesA[j][i] = bootstrapValuesA[j][i];
			}
		}
	}

	public static double[] getBootstrapValuesB() {
		return bootstrapValuesB;
	}

	public static void setBootstrapValuesB(double[] bootstrapValuesB) {
		MetricsForSRGM.bootstrapValuesB = new double[bootSize];
		for (int j = 0; j < bootSize; j++) {
			MetricsForSRGM.bootstrapValuesB[j] = bootstrapValuesB[j];
		}
	}

	public static double[] getBootstrapValuesC() {
		return bootstrapValuesC;
	}

	public static void setBootstrapValuesC(double[] bootstrapValuesC) {
		MetricsForSRGM.bootstrapValuesC = new double[bootSize];
		for (int j = 0; j < bootSize; j++) {
			MetricsForSRGM.bootstrapValuesC[j] = bootstrapValuesC[j];
		}
	}

	public static WeightedObservedPoints getObs() {
		return obs;
	}

	public static void setObs(WeightedObservedPoints obs) {
		MetricsForSRGM.obs = obs;
	}

	public static double[] getCoeffB() {
		return coeffB;
	}

	public static double getFinalA() {
		List<WeightedObservedPoint> val = obs.toList();
		int n = obs.toList().size();
		double res = srgmFunc.value(val.get(n-1).getX(), coeffB);
		return res;
	}
	
	public static double getTotalA() {
		List<WeightedObservedPoint> val = obs.toList();
		int n = obs.toList().size();
		double res = 0;
		
		
		
		return res;
	}
	
	public static void setCoeffB(double[] coeffB) {
		MetricsForSRGM.coeffB = coeffB;
	}

	public static ParametricUnivariateFunction getSrgmFunc() {
		return srgmFunc;
	}

	public static void setSrgmFunc(ParametricUnivariateFunction srgmFunc) {
		MetricsForSRGM.srgmFunc = srgmFunc;
	}

	// Metric 1 - GoF
	public static double GoodnessOfFit() {
		List<WeightedObservedPoint> val = obs.toList();
		double res = 0.0;
		
		// count on observations 
		int n = obs.toList().size();
		
		// count of parameters
		int p = coeffB.length;
		for (int m = 0; m < obs.toList().size(); m++) {
			double x = val.get(m).getX();

			// System.out.println(x + "	" + srgmFunc.value(x, coeffB) + "	");
			res += (val.get(m).getY() - srgmFunc.value(x, coeffB))
					* (val.get(m).getY() - srgmFunc.value(x, coeffB));
		}
		return Math.sqrt(res / (n - p));
	}

	// Metric 2 - RPoF
	public static double RelativePrecisionOfFit() {
		double res = 0.0;
		List<WeightedObservedPoint> val = obs.toList();
		
		int n = obs.toList().size();
		int p = coeffB.length;

		double coeff_low[] = getLower(p);
		double coeff_up[] = getUpper(p);

		for (int m = 0; m < obs.toList().size(); m++) {
			double x = val.get(m).getX();
			res += Math.abs(srgmFunc.value(x, coeff_low)
					- srgmFunc.value(x, coeff_up));
		}
		// System.out.println("max = " + n);

		return res / n;
	}

	// Metric 3 - CoF
	public static double CoverageOfFit() {
		double res = 0.0;
		List<WeightedObservedPoint> val = obs.toList();
		int n = obs.toList().size();
		int p = coeffB.length;

		double coeff_low[] = getLower(p);
		double coeff_up[] = getUpper(p);

		for (int m = 0; m < obs.toList().size(); m++) {
			double x = val.get(m).getX();
			// System.out.println(x + "	" + srgmFunc.value(x, coeff_low) + "	" +
			// srgmFunc.value(x, coeff_up));

			if ((srgmFunc.value(x, coeffB) < srgmFunc.value(x, coeff_low) && srgmFunc
					.value(x, coeffB) >= srgmFunc.value(x, coeff_up))
					|| (srgmFunc.value(x, coeffB) > srgmFunc
							.value(x, coeff_low) && srgmFunc.value(x, coeffB) <= srgmFunc
							.value(x, coeff_up)))
				res++;
		}
		return 1.0 * res / n;
	}

	// Metric 4 - Accuracy, AccFP
	public static double AccuracyFinalPoint() {
		List<WeightedObservedPoint> val = obs.toList();
		int n = obs.toList().size();
		double res = Math.abs(val.get(n-1).getY() - srgmFunc.value(val.get(n-1).getX(), coeffB)) / val.get(n-1).getY();
		return res;
	}

	// Metric 5 - Predictive Ability
	public static double PredictiveAbility() {		
		List<WeightedObservedPoint> val = obs.toList();
		
		int n = obs.toList().size();		
		int i = n - 1;		

		double res = Math.abs(val.get(i).getY() - srgmFunc.value(val.get(i).getX(), coeffB))
				/ val.get(i).getY();

		if (res > 0.1) {
			return Double.NaN;
		}

		while (res <= 0.1) {
			// reduce dataset
			List<WeightedObservedPoint> aNewDataset = new ArrayList<WeightedObservedPoint>();
			for (int k = 0; k < i; k++) {
				aNewDataset.add(k, val.get(k));
			}
			double[] aNewCoeffB = null;			
			ParametricUnivariateFunction aNewSrgmFunc = srgmFunc;
			// build new model on a reduced dataset
			
			// 3-param model
			if (type == 3) {

				for(int j = 0; j < 100; j++)
				{
				double start[] = generateStart3(N, aNewDataset);
				
				try {
					final MultiStageExponentalFitterWithBC_bc fitterb = MultiStageExponentalFitterWithBC_bc
							.create(N).withStartPoint(start)
							.withFunction(aNewSrgmFunc);
					// optimize
					aNewCoeffB = fitterb.fit(aNewDataset);
					if (aNewCoeffB != null)
					{
						break;
					}
				} catch (org.apache.commons.math3.exception.ConvergenceException e) {
					// System.out.println("? " + not_converged++);
				} catch (org.apache.commons.math3.exception.TooManyEvaluationsException e) {
					// System.out.println("? " + not_converged++);
				}
				}

			// 2-param model
			} else {
				for(int j = 0; j < 100; j++)
				{
				double start[] = generateStart2(N, aNewDataset);
				
				try {
					final MultiStageExponentalFitterWithB_b fitterb = MultiStageExponentalFitterWithB_b
							.create(N).withStartPoint(start)
							.withFunction(aNewSrgmFunc);
					// optimize
					aNewCoeffB = fitterb.fit(aNewDataset);
					if (aNewCoeffB != null)
					{
						break;
					}
				} catch (org.apache.commons.math3.exception.ConvergenceException e) {
					// System.out.println("? " + not_converged++);
				} catch (org.apache.commons.math3.exception.TooManyEvaluationsException e) {
					// System.out.println("? " + not_converged++);
				}
				
				}
			}

			// evaluate result
			res = Math.abs(val.get(n-1).getY()
					- aNewSrgmFunc.value(val.get(n-1).getX(), aNewCoeffB))
					/ val.get(n-1).getY();

			i--;
		}

		return 1.0 * i / n;
	}

	public static double[] ConfidenceIntervals(double[] bootstrapValues) {
		Percentile perc = new Percentile();
		perc.setData(bootstrapValues);
		double low = perc.evaluate(2.5);
		double up = perc.evaluate(97.5);
		double[] res = new double[2];
		res[0] = low;
		res[1] = up;
		return res;
	}

	// lower B + lower C
	private static double[] getLower(int p) {
		double coeff_low[] = new double[p];
		coeff_low[N] = ConfidenceIntervals(bootstrapValuesB)[0];
		if (type == 3) {
			coeff_low[N + 1] = ConfidenceIntervals(bootstrapValuesC)[0];
		}

		double aVals[] = new double[bootSize];
		double dVals[] = new double[bootSize];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < bootSize; j++) {
				aVals[j] = bootstrapValuesA[j][i]; // coeffB[i]; //
				if (type == 3) {
					dVals[j] = bootstrapValuesD[j][i]; // coeffB[i + N + 2];
				} else {
					dVals[j] = bootstrapValuesD[j][i]; // coeffB[i + N + 1]; //
														// ;
				}
			}

			coeff_low[i] = ConfidenceIntervals(aVals)[0];
			if (type == 3) {
				coeff_low[i + N + 2] = ConfidenceIntervals(dVals)[0];
			} else {
				coeff_low[i + N + 1] = ConfidenceIntervals(dVals)[0];
			}
		}
		return coeff_low;
	}

	private static double[] getUpper(int p) {
		double coeff_up[] = new double[p];
		coeff_up[N] = ConfidenceIntervals(bootstrapValuesB)[1];
		if (type == 3) {
			coeff_up[N + 1] = ConfidenceIntervals(bootstrapValuesC)[1];
		}

		double aVals[] = new double[bootSize];
		double dVals[] = new double[bootSize];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < bootSize; j++) {
				aVals[j] = bootstrapValuesA[j][i]; // coeffB[i]; //
				if (type == 3) {
					dVals[j] = bootstrapValuesD[j][i]; // coeffB[i + N + 2];
				} else {
					dVals[j] = bootstrapValuesD[j][i]; // coeffB[i + N + 1]; //
														// ;
				}
			}

			coeff_up[i] = ConfidenceIntervals(aVals)[1];
			if (type == 3) {
				coeff_up[i + N + 2] = ConfidenceIntervals(dVals)[1];
			} else {
				coeff_up[i + N + 1] = ConfidenceIntervals(dVals)[1];
			}
		}

		return coeff_up;
	}

	public static double[] getBootCoeffUp() {
		int p = coeffB.length;
		return getUpper(p);
	}

	public static double[] getBootCoeffLow() {
		int p = coeffB.length;
		return getLower(p);
	}

	private static double[] generateStart3(int n,
			List<WeightedObservedPoint> aNewDataset) {
		double start[] = new double[2 * n + 2];

		int nrows = aNewDataset.size();
		double nbugs = aNewDataset.get(nrows - 1).getY();
		for (int i = 0; i < n; i++) {
			start[i] = nbugs / (n);
			start[i + n + 2] = i * nrows / (n);
			start[n] = Math.random(); // 2.0843586561;
			start[n + 1] = Math.random(); // 25.0 ; //Math.random();
											// //0.0857425877;
		}
		return start;
	}

	private static double[] generateStart2(int n,
			List<WeightedObservedPoint> aNewDataset) {
		double start[] = new double[2 * n + 1];

		int nrows = obs.toList().size();
		double nbugs = obs.toList().get(nrows - 1).getY();
		for (int i = 0; i < n; i++) {
			start[i] = nbugs / (n);
			start[i + n + 1] = i * nrows / (n);
			start[n] = Math.random(); // 2.0843586561;
		}
		return start;
	}

}
