package srgm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
import weka.filters.unsupervised.instance.Resample;

public class NewRunTestFittingB_b_GoF {

	public static void main(String[] args) {

		int MAX_RELEASE = Integer.parseInt(args[0]);
		String modelName = args[1];
		
		//config
		String[] datasets = new String[] { "tizen", "cyan", "nemo", "mer" };
		//int[] releases = new int[] { 7, 9, 20, 29 };
		//int[] releases = new int[] {  8, 4, 16, 13  };
		
		/*
		// config - comm 
		String[] datasets = new String[] { "tatar", "sedmsk", "minfin", "pmo" };
		*/
		int[] releases = new int[] { MAX_RELEASE, MAX_RELEASE, MAX_RELEASE, MAX_RELEASE };
		//
		
		for (int counter = 0; counter < datasets.length ; counter++) {
			String dataset = datasets[counter];
			int RELEASES_START = releases[counter];

			double[] minCoeff = new double[RELEASES_START * 2 + 2];
			double minGoF = Double.MAX_VALUE;

			ParametricUnivariateFunction model = StringToModelMapper.getModel(modelName, RELEASES_START);

			File dir = new File(
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/arff");

			int ii = 0;

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
					obs.add(Double.parseDouble(s.split(",")[2]),
							Double.parseDouble(s.split(",")[1]));
				}

				for (int N = RELEASES_START; N < RELEASES_START+1 ; N++) {

					for (int j = 0; j < 1000; j++) {
						double start[] = generateStart(N, obs);

					
							ParametricUnivariateFunction srgmFunc = model;
							try {

								final MultiStageExponentalFitterWithB_b fitterb = MultiStageExponentalFitterWithB_b
										.create(N).withStartPoint(start)
										.withFunction(srgmFunc);

								// optimize
								final double[] coeffB = fitterb.fit(obs
										.toList());
								for (int i = 0; i < N; i++) {
									// System.out.println(coeffB[i + N + 2] +
									// "	" +
									// coeffB[i] + "	" + coeffB[N] + "	" +
									// coeffB[N
									// +
									// 1]);
								}
								// System.out.println(coeffB[N] + "	" + coeffB[N
								// + 1]);

								// write data

								MetricsForSRGM.setCoeffB(coeffB);
								MetricsForSRGM.setObs(obs);
								MetricsForSRGM.setSrgmFunc(srgmFunc);
								MetricsForSRGM.setN(N);
								MetricsForSRGM.setType(2);

								double curGoF = MetricsForSRGM.GoodnessOfFit();

								if (coeffB[N] > 0 && coeffB[N + 1] > 0) {
									if (curGoF < minGoF) {
										minGoF = curGoF;
										minCoeff = coeffB;
									}
								}

								 System.out.println("GoF = " +
								 //MetricsForSRGM.GoodnessOfFit()
								 MetricsForSRGM.getCoeffB()[N]);

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

			System.out.println(modelName + "	"+RELEASES_START + "	" + dataset + "	GoF =	" + minGoF + "	"
					+ minCoeff[RELEASES_START]);

			// System.out.println(dataset + "	RPoF =	" +
			// MetricsForSRGM.RelativePrecisionOfFit() + "	CovF =	" +
			// MetricsForSRGM.CoverageOfFit());
		}
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
