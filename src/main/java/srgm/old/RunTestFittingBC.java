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

import srgm.fitters.MultiStageExponentalFitterWithBC;
import srgm.models.freeparam.HD_SRGMFunctionWithBC;
import srgm.models.freeparam.L_SRGMFunctionWithBC;
import srgm.models.freeparam.WS_SRGMFunctionWithBC;
import srgm.models.freeparam.W_SRGMFunctionWithBC;
import srgm.models.freeparam.YE_SRGMFunctionWithBC;
import srgm.models.freeparam.YR_SRGMFunctionWithBC;

public class RunTestFittingBC {

	public static void main(String[] args) {

		ParametricUnivariateFunction func[] = new ParametricUnivariateFunction[1];

		// TODO Auto-generated method stub

		File f = new File(
	//	 "/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/bugs-3-sailfish-nemo.csv");
				"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/bugs-1-tizen.csv");

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
			obs.add(Double.parseDouble(s.split(",")[2]),
					Double.parseDouble(s.split(",")[1]));
		}

		for (WeightedObservedPoint point : obs.toList()) {
			System.out.println(point.getX() + "	" + point.getY());
		}

		for (int N = 10; N < 15; N++) {
			System.out.println(N);

		//	func[0] = new W_SRGMFunctionWithBC(N);
			func[0] = new L_SRGMFunctionWithBC(N);

			for (int j = 0; j < 2; j++) {
				double start[] = generateStart(N, obs);

				for (int k = 0; k < func.length; k++) {
					ParametricUnivariateFunction srgmFunc = func[k];

					try {

						final MultiStageExponentalFitterWithBC fitterb = MultiStageExponentalFitterWithBC
								.create(N).withStartPoint(start)
								.withFunction(srgmFunc);

						// optimize
						final double[] coeffB = fitterb.fit(obs.toList());
						for (int i = 0; i < N; i++) {
							System.out.println(coeffB[i + N] + "	" + coeffB[i]
									+ "	" + coeffB[i + 2 * N] + "	"
									+ coeffB[i + 3 * N]);

						}

						// write data
						for (int m = 0; m < obs.toList().size(); m++) {
							double x = m;

							System.out.println(x + "	"
									+ srgmFunc.value(x, coeffB));
						}
						
						System.out.println("=========" + k);
					} catch (org.apache.commons.math3.exception.ConvergenceException e) {
						System.out.println(j);

					}
				}
			}
		}
	}

	private static double[] generateStart(int n, WeightedObservedPoints obs) {

		double start[] = new double[4 * n];
		for (WeightedObservedPoint point : obs.toList()) {
			point.getY();
		}

		int nrows = obs.toList().size();
		double nbugs = obs.toList().get(nrows - 1).getY();

		for (int i = 0; i < n; i++) {
			start[i] = nbugs / ( n );
			start[i + n] = i * nrows / ( n );
			start[i + 2 * n] =   Math.random(); // 2.0843586561;
			start[i + 3 * n] = Math.random(); // 25.0 ; //Math.random(); //0.0857425877; 
		}

		return start;
	}

}
