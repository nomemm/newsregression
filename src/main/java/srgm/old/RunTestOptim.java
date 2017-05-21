package srgm.old;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

public class RunTestOptim {
	public static void main(String[] args) {
		LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer()
				.withCostRelativeTolerance(1.0e-12)
				.withParameterRelativeTolerance(1.0e-12);

		MultivariateVectorFunction aSRGM ;
		MultivariateMatrixFunction jacobian;
		
	/*	LeastSquaresProblem leastSquaresProblem = new LeastSquaresBuilder()
				.start(new double[] { 100.0, 50.0 })
				.model(aSRGM, jacobian ).target(numberOfBugs)
				.lazyEvaluation(false).maxEvaluations(1000).maxIterations(1000)
				.build();*/

//		LeastSquaresOptimizer.Optimum optimum = optimizer
//				.optimize(leastSquaresProblem);

		// LeastSquaresOptimizer.Optimum optimum = new
		// LevenbergMarquardtOptimizer().optimize(leastSquaresProblem);

//		System.out.println("RMS: " + optimum.getRMS());
//		System.out.println("evaluations: " + optimum.getEvaluations());
//		System.out.println("iterations: " + optimum.getIterations());

		System.out.println("s");
	}

}
