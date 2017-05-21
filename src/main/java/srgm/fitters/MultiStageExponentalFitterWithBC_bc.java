package srgm.fitters;

import java.util.Collection;

import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.linear.DiagonalMatrix;

import srgm.validators.ParameterValidatorB;

public class MultiStageExponentalFitterWithBC_bc extends AbstractCurveFitter {

	/** Parametric function to be fitted. */
	private final ParametricUnivariateFunction FUNCTION;
	/** Initial guess. */
	private final double[] initialGuess;
	/** Maximum number of iterations of the optimization algorithm. */
	private final int maxIter;
	ParameterValidatorB pvZero = new ParameterValidatorB();

	private MultiStageExponentalFitterWithBC_bc(double[] initialGuess,
			int maxIter, ParametricUnivariateFunction f) {
		this.FUNCTION = f;
		this.initialGuess = initialGuess;
		this.maxIter = maxIter;
	}

	public static MultiStageExponentalFitterWithBC_bc create(int n) {
		return new MultiStageExponentalFitterWithBC_bc(new double[2 * n + 2], 1000,
				null);
	}

	public MultiStageExponentalFitterWithBC_bc withFunction(
			ParametricUnivariateFunction f) {

		return new MultiStageExponentalFitterWithBC_bc(initialGuess.clone(),
				maxIter, f);
	}

	public MultiStageExponentalFitterWithBC_bc withStartPoint(double[] newStart) {
		return new MultiStageExponentalFitterWithBC_bc(newStart.clone(), maxIter,
				FUNCTION);
	}

	protected LeastSquaresProblem getProblem(
			Collection<WeightedObservedPoint> observations) {
		// Prepare least-squares problem.
		final int len = observations.size();
		final double[] target = new double[len];
		final double[] weights = new double[len];

		int i = 0;
		for (WeightedObservedPoint obs : observations) {
			target[i] = obs.getY();
			weights[i] = obs.getWeight();
			++i;
		}

		final AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(
				FUNCTION, observations);

		if (initialGuess == null) {
			throw new MathInternalError();
		}

		// Return a new least squares problem set up to fit a polynomial curve
		// to the
		// observed points.

		return new LeastSquaresBuilder()
				.maxEvaluations(1000)
				.maxIterations(maxIter)
				.start(initialGuess)
				.target(target)
				.weight(new DiagonalMatrix(weights))
				.model(model.getModelFunction(),
						model.getModelFunctionJacobian())
				.parameterValidator(pvZero).build();
	}

}
