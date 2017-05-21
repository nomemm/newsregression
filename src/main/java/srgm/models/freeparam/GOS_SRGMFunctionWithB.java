package srgm.models.freeparam;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class GOS_SRGMFunctionWithB implements ParametricUnivariateFunction  {

	private int n;

	public GOS_SRGMFunctionWithB(int nval) {
		n = nval;
	}

	public GOS_SRGMFunctionWithB() {
		n = 1;
	
	}
	
	public double[] gradient(double arg0, double... arg1) {
		double res[] = new double[3 * n ];

		for (int i = 0; i < n; i++) {
			res[i] = dfa(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n ]);
		}
		
		
		for(int i = 0; i < n; i++) {
			res[i + 2*n]= dfb(arg0 - arg1[i + n ], arg1[i], arg1[i + 2*n]);	
		}				
		
		for(int i = 0 ; i < n ; i++) {
			res[i + n] = dfd(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n ], arg1[i + n]);
		}
		
		return res;
	}

	public double value(double arg0, double... arg1) {

		double res = 0.0;

		for (int i = 0; i < n; i++) {
			res += f(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n]);
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
