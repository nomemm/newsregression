package srgm.models.fixedbc;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class GOS_SRGMFunctionWithB_b implements ParametricUnivariateFunction  {

	private int n;

	public GOS_SRGMFunctionWithB_b(int nval) {
		n = nval;
	}

	public GOS_SRGMFunctionWithB_b() {
		n = 1;	
	}
	
	public double[] gradient(double arg0, double... arg1) {
		double res[] = new double[2 * n + 1];

		for (int i = 1; i < n; i++) {
			res[i] = dfa(arg0 - arg1[i + n + 1], arg1[i], arg1[n]);
		}
		res[0] = dfa(arg0, arg1[0], arg1[n]);
				
		for(int i = 1; i < n; i++) {
			res[n] += dfb(arg0 - arg1[i + n  + 1], arg1[i], arg1[n]);	
		}				
		res[n] += dfb(arg0, arg1[0], arg1[n]);
		
		
		for(int i = 1 ; i < n ; i++) {
			res[i + n + 1] = dfd(arg0 - arg1[i + n + 1], arg1[i], arg1[n], arg1[i + n + 1]);
		}
		res[n + 1] = dfd(arg0, arg1[0], arg1[n], 0);
		
		return res;
	}

	public double value(double arg0, double... arg1) {

		double res = 0.0;// Double.MIN_VALUE;

		res = f(arg0, arg1[0], arg1[n]);
		
		for (int i = 1; i < n; i++) {
			res += f(arg0 - arg1[i + n + 1], arg1[i], arg1[n]);
		}
		return res;
	}

	public double f(double x, double a, double b) {
		double res = 0.0;
		if (x> 0) {
			res = a * ( 1 - (1 + b * x) * Math.exp(- b * x) );
		}
		return res;
	}

	public double dfa(double x, double a, double b) {
		double res = 0.0;
		if (x > 0) {
			res = ( 1 - (1 + b * x) * Math.exp(- b * x) );
		}
		return res;
	}
	
	public double dfb(double x, double a, double b) {
		double res = 0.0;
		if (x > 0) {
			res = a * b * x * x *  Math.exp(- b * x);
		}
		return res;
	}
	
	public double dfd(double x, double a, double b, double d) {
		double res = 0.0;
		if (x > 0) {
			res =  - a * b * b * x *  Math.exp(- b * x);
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
