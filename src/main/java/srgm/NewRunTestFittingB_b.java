package srgm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;

import srgm.evaluation.MetricsForSRGM;
import srgm.fitters.MultiStageExponentalFitterWithBC;
import srgm.fitters.MultiStageExponentalFitterWithBC_bc;
import srgm.fitters.MultiStageExponentalFitterWithB_b;
import srgm.models.fixedbc.GOS_SRGMFunctionWithB_b;
import srgm.models.fixedbc.GO_SRGMFunctionWithB_b;
import srgm.models.fixedbc.L_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.W_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.YR_SRGMFunctionWithBC_bc;
import srgm.models.freeparam.HD_SRGMFunctionWithBC;
import srgm.models.freeparam.L_SRGMFunctionWithBC;
import srgm.models.freeparam.WS_SRGMFunctionWithBC;
import srgm.models.freeparam.W_SRGMFunctionWithBC;
import srgm.models.freeparam.YE_SRGMFunctionWithBC;
import srgm.models.freeparam.YR_SRGMFunctionWithBC;
import srgm.utils.StringToModelMapper;
import srgm.visualization.LineChart;
import weka.filters.unsupervised.instance.Resample;

public class NewRunTestFittingB_b {

	public static Map<String, double[]> map = new HashMap<String, double[]>();

	public static void main(String[] args) {

		MetricsForSRGM.setType(2);
		int MAX_RELEASE = Integer.parseInt(args[0]);
		String modelName = args[1];
		String dt = args[2];

		int[] releases = new int[] { MAX_RELEASE, MAX_RELEASE, MAX_RELEASE, MAX_RELEASE };

		String[] datasets;

		if (dt.equalsIgnoreCase("mobile")) {
			datasets = new String[] { "tizen", "cyan", "nemo", "mer" };
			datasets = new String[] { "nemo" };
			//  releases = new int[] {  8, 4, 16, 13  };
		} else {
			datasets = new String[] { "tatar", "sedmsk", "minfin", "pmo" };
		//	releases = new int[] {  8, 8, 8, 8  };
		//	datasets = new String[] { "tatar" };
		}

		for (int counter = 0; counter < datasets.length; counter++) {
			String dataset = datasets[counter];
			int RELEASES_START = releases[counter];

			double[] minCoeff = new double[RELEASES_START * 2 + 1];
			double minGoF = Double.MAX_VALUE;

			ParametricUnivariateFunction model = StringToModelMapper.getModel(modelName, RELEASES_START);

			File dir;
			if (dt.equalsIgnoreCase("mobile")) {
				/* config mobile */
				dir = new File("/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot/");
			} else {
				// config - comm
				dir = new File("/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot-comm/");
			}

			int boot_size = dir.listFiles().length / datasets.length;
			int ii = 0;
			double[][] a = new double[boot_size][];
			double[] b = new double[boot_size];
			// double[] c = new double[boot_size];
			double[][] d = new double[boot_size][];
			int not_converged = 0;
			for (File f : dir.listFiles()) {
				if (!f.toPath().toString().contains(dataset)) {
					continue;
				}

				// System.out.println("boot sample " + ii);

				List<String> data = null;
				try {
					data = Files.readAllLines(f.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Collect data.
				final WeightedObservedPoints obs = new WeightedObservedPoints();

				for (String s : data) {
					if (s.startsWith("x")) {
						continue;
					}
					if (s.startsWith("@")) {
						continue;
					}
					if (s.isEmpty()) {
						continue;
					}
					obs.add(Double.parseDouble(s.split(",")[2]), Double.parseDouble(s.split(",")[1]));
				}

				for (int N = RELEASES_START; N < RELEASES_START + 1; N++) {
					//for (int j = 0; j < 1; j++) {
					int j = 0;
					boolean positive = false;
				// FOR D	while(!positive && j < 150)
					{
						j++;
						//System.out.println(j);
						double start[] = generateStart(N, obs);
						ParametricUnivariateFunction srgmFunc = model;
						
						// forced break is usefull in some experiments
						//if(ii>=30) break;
						
						try {

							final MultiStageExponentalFitterWithB_b fitterb = MultiStageExponentalFitterWithB_b
									.create(N).withStartPoint(start).withFunction(srgmFunc);
							// optimize
							boolean solution = true;
							final double[] coeffB = fitterb.fit(obs.toList());
							for (int i = 0; i < N; i++) {
								if (coeffB[i + N + 1] < 0 || coeffB[i] < 0 || coeffB[N] < 0) {
									solution = false;
								}
							}
							if (!solution) {
								// ii--;
								if (j <= 149)
								{
						// FOR D			 continue;
								}
								
							}
							else
							{
								positive = true;
							}
						//	System.out.println(coeffB[N] + " " + coeffB[N + 1]);
							a[ii] = new double[N];
							d[ii] = new double[N];
							for (int s = 0; s < N; s++) {
								a[ii][s] = coeffB[s];
								d[ii][s] = coeffB[s + N + 1];
							//	System.out.println(coeffB[s] + " " + coeffB[s + N + 1]);
							}
							b[ii] = coeffB[N];
							// c[ii] = coeffB[N + 1];
							// write data

							MetricsForSRGM.setCoeffB(coeffB);
							MetricsForSRGM.setObs(obs);
							MetricsForSRGM.setSrgmFunc(srgmFunc);
							MetricsForSRGM.setN(N);
							MetricsForSRGM.setType(2);

							double curGoF = MetricsForSRGM.GoodnessOfFit();

							///latest experiment for difference between two models
//							String bootid = modelName + "_" + RELEASES_START + "_" + dataset + "_"+ii;
							
//							double bootpred = MetricsForSRGM.PredictiveAbility();
//							double bootacc = MetricsForSRGM.AccuracyFinalPoint();
							
//							System.out.println(bootid + "	" + modelName + "	" + RELEASES_START + "	" + dataset + "	" + ii  + "	RPoF =	"
								//	+ MetricsForSRGM.RelativePrecisionOfFit() + "	CovF =	" + MetricsForSRGM.CoverageOfFit()
//									+ "	Pred =	" + bootpred + "	Acc. =	" + bootacc + "	GoF =	" + curGoF + "	b =	"
//									+ coeffB[RELEASES_START]);
							//////////////
							
							if (curGoF < minGoF) {
								minGoF = curGoF;
								minCoeff = coeffB;
							}
							// System.out.println("GoF = " +
							// MetricsForSRGM.GoodnessOfFit());

						} catch (org.apache.commons.math3.exception.ConvergenceException e) {
							// System.out.println("? " + not_converged++);
							ii--;
						} catch (org.apache.commons.math3.exception.TooManyEvaluationsException e) {
							// System.out.println("? " + not_converged++);
							ii--;
						}
					}
				}
				ii++;
			}

			if ((boot_size - not_converged) / (1.0 * boot_size) <= 0.5) {
				// System.out.println("!!!!!!");
			}
			double pred = MetricsForSRGM.PredictiveAbility();
			double acc = MetricsForSRGM.AccuracyFinalPoint();

			MetricsForSRGM.setBootSize(ii);
			MetricsForSRGM.setBootstrapValuesA(a);
			MetricsForSRGM.setBootstrapValuesB(b);
			// MetricsForSRGM.setBootstrapValuesC(c);
			MetricsForSRGM.setBootstrapValuesD(d);

			MetricsForSRGM.setCoeffB(minCoeff);
			String id = modelName + "_" + RELEASES_START + "_" + dataset;
			map.put(id, minCoeff);
			
			Double aSumm = CalculateASum(map, id);
			
			
			System.out.println(id + "	" + modelName + "	" + RELEASES_START + "	" + dataset + "	RPoF =	"
					+ MetricsForSRGM.RelativePrecisionOfFit() + "	CovF =	" + MetricsForSRGM.CoverageOfFit()
					+ "	Pred =	" + pred + "	Acc. =	" + acc + "	GoF =	" + minGoF + "	b =	"
					+ minCoeff[RELEASES_START] + "	total_a =	" + aSumm);
			

			LineChart lc = new LineChart();
			lc.init(modelName + "_" + RELEASES_START + "_" + dataset);
			lc.save();
		}
	}

	private static Double CalculateASum(Map<String, double[]> map2, String k) {
		Double aData = 0.0;
		
		
			
			double[] coefs = map.get(k);
			int N = Integer.valueOf(k.substring(k.indexOf("_")+1, k.lastIndexOf("_")));
			
			//String aData = "";
			aData = coefs[0];
			for (int i = 1; i < N; i++) 
			{
				aData += coefs[i] ;//- coefs[i-1];
			}
		
		return aData;
	}

	private static double[] generateStart(int n, WeightedObservedPoints obs) {
		double start[] = new double[2 * n + 1];
		for (WeightedObservedPoint point : obs.toList()) {
			point.getY();
		}
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
