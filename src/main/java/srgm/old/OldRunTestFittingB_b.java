package srgm.old;

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
import srgm.models.fixedbc.GO_SRGMFunctionWithB_b;
import srgm.models.fixedbc.L_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.W_SRGMFunctionWithBC_bc;
import srgm.models.freeparam.HD_SRGMFunctionWithBC;
import srgm.models.freeparam.L_SRGMFunctionWithBC;
import srgm.models.freeparam.WS_SRGMFunctionWithBC;
import srgm.models.freeparam.W_SRGMFunctionWithBC;
import srgm.models.freeparam.YE_SRGMFunctionWithBC;
import srgm.models.freeparam.YR_SRGMFunctionWithBC;
import weka.filters.unsupervised.instance.Resample;

public class OldRunTestFittingB_b {

	public static void main(String[] args) {

		String dataset = "cyan";
		int RELEASES_START = 9;
		
		ParametricUnivariateFunction func[] = new ParametricUnivariateFunction[1];

		File dir = new File(
				"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot");
		int boot_size = dir.listFiles().length / 4;
		int ii = 0;
		double[][] a = new double[boot_size][];
		double[] b = new double[boot_size];
	//	double[] c = new double[boot_size];
		double[][] d = new double[boot_size][];
		int not_converged = 0;
		for (File f : dir.listFiles()) {
			if (!f.toPath().toString().contains(dataset)) {
				continue;
			}

		//	System.out.println("boot sample " + ii);

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

			for (int N = RELEASES_START; N < RELEASES_START+1; N++) {

				// func[0] = new W_SRGMFunctionWithBC(N);
				func[0] = new GO_SRGMFunctionWithB_b(N);

				for (int j = 0; j < 1; j++) {
					double start[] = generateStart(N, obs);

					for (int k = 0; k < func.length; k++) {
						ParametricUnivariateFunction srgmFunc = func[k];
						try {

							final MultiStageExponentalFitterWithB_b fitterb = MultiStageExponentalFitterWithB_b
									.create(N).withStartPoint(start)
									.withFunction(srgmFunc);

							// optimize
							final double[] coeffB = fitterb.fit(obs.toList());
							for (int i = 0; i < N; i++) {
								// System.out.println(coeffB[i + N + 2] + "	" +
								// coeffB[i] + "	" + coeffB[N] + "	" + coeffB[N
								// +
								// 1]);
							}
						//	System.out.println(coeffB[N] + "	" + coeffB[N + 1]);
							a[ii] = new double[N];
							d[ii] = new double[N];
							for (int s = 0; s < N; s++) {
								a[ii][s] = coeffB[s];
								d[ii][s] = coeffB[s + N + 1];
							}
							b[ii] = coeffB[N];
							//c[ii] = coeffB[N + 1];
							// write data

							MetricsForSRGM.setCoeffB(coeffB);
							MetricsForSRGM.setObs(obs);
							MetricsForSRGM.setSrgmFunc(srgmFunc);
							MetricsForSRGM.setN(N);
							MetricsForSRGM.setType(2);

							System.out.println("GoF = " + MetricsForSRGM.GoodnessOfFit());

						} catch (org.apache.commons.math3.exception.ConvergenceException e) {
					//		System.out.println("? " + not_converged++);
							ii--;
						} catch (org.apache.commons.math3.exception.TooManyEvaluationsException e) {
					//		System.out.println("? " + not_converged++);
							ii--;
						}
					}
				}
			}
			ii++;
		}

		if ((boot_size - not_converged) / (1.0 * boot_size) <= 0.5) {
//			System.out.println("!!!!!!");
		}
		
		MetricsForSRGM.setBootSize(ii);
		MetricsForSRGM.setBootstrapValuesA(a);
		MetricsForSRGM.setBootstrapValuesB(b);
//		MetricsForSRGM.setBootstrapValuesC(c);
		MetricsForSRGM.setBootstrapValuesD(d);
		
		System.out.println("RPoF = " + MetricsForSRGM.RelativePrecisionOfFit());
		System.out.println("CovF = " + MetricsForSRGM.CoverageOfFit());		
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
