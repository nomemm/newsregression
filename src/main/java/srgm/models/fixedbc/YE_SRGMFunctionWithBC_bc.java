package srgm.models.fixedbc;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class YE_SRGMFunctionWithBC_bc implements ParametricUnivariateFunction  {

	private int n;

	public YE_SRGMFunctionWithBC_bc(int nval) {
		n = nval;
	}

	public YE_SRGMFunctionWithBC_bc() {
		n = 1;	
	}
	
	public double[] gradient(double arg0, double... arg1) {
		double res[] = new double[2 * n + 2];

		for (int i = 1; i < n; i++) {
			res[i] = dfa(arg0 - arg1[i + n + 2], arg1[i], arg1[n], arg1[n + 1]);
		}
		res[0] = dfa(arg0, arg1[0], arg1[n], arg1[n + 1]);
				
		for(int i = 1; i < n; i++) {
			res[n] += dfb(arg0 - arg1[i + n  + 2], arg1[i], arg1[n], arg1[n + 1]);	
		}				
		res[n] += dfb(arg0, arg1[0], arg1[n], arg1[n + 1]);
		
		for(int i = 1; i < n; i++) {
			res[n + 1] += dfc(arg0 - arg1[i + n + 2], arg1[i], arg1[n], arg1[n + 1]);	
		}				
		res[n + 1] += dfc(arg0, arg1[0], arg1[n], arg1[n + 1]);
		
		for(int i = 1 ; i < n ; i++) {
			res[i + n + 2] = dfd(arg0 - arg1[i + n + 2], arg1[i], arg1[n], arg1[n + 1], arg1[i + n + 2]);
		}
		res[n + 2] = dfd(arg0, arg1[0], arg1[n], arg1[n + 1], arg1[0 + n + 2]);
		
		return res;
	}

	public double value(double arg0, double... arg1) {

		double res = 0.0;// Double.MIN_VALUE;

		res = f(arg0, arg1[0], arg1[n], arg1[n + 1]);
		
		for (int i = 1; i < n; i++) {
			res += f(arg0 - arg1[i + n + 2], arg1[i], arg1[n], arg1[n + 1]);
		}
		return res;
	}


	/////// ============== edit this part
	
	// basic value
	public double f(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x> 0) {
			res = a * ( 1 - Math.exp( -b * ( 1 - Math.exp(- c * x ) ) ) );
		}
		return res;
	}

	// partial deriv a
	public double dfa(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = ( 1 - Math.exp( -b * ( 1 - Math.exp(- c * x ) ) ) );
		}
		return res;
	}
	
	// partial deriv b
	public double dfb(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = a * (1 - Math.exp(- c * x )) * Math.exp( - b * ( 1 - Math.exp(- c * x ) ) );
		}
		return res;
	}
	
	// partial deriv c
	public double dfc(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = a * b * x * Math.exp( - b * ( 1 - Math.exp(- c * x ) ) ) * Math.exp(- c * x );
		}
		return res;
	}
	
	// partial deriv delta
	public double dfd(double x, double a, double b, double c, double d) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = - a * b * c * Math.exp(- c * x ) * Math.exp( - b * ( 1 - Math.exp(- c * x ) ) );
		}
		return res;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}	
}