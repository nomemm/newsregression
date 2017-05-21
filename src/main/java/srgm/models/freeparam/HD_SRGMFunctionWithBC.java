package srgm.models.freeparam;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.JacobianFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class HD_SRGMFunctionWithBC implements ParametricUnivariateFunction  {

	private int n;

	public HD_SRGMFunctionWithBC(int nval) {
		n = nval;
	}

	public HD_SRGMFunctionWithBC() {
		n = 1;
	
	}
	
	public double[] gradient(double arg0, double... arg1) {
		double res[] = new double[4 * n ];

		for (int i = 0; i < n; i++) {
			res[i] = dfa(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n ], arg1[i + 3*n ]);
		}
		
		
		for(int i = 0; i < n; i++) {
			res[i + 2*n] = dfb(arg0 - arg1[i + n ], arg1[i], arg1[i + 2*n], arg1[i + 3*n ]);	
		}				
		
		for(int i = 0; i < n; i++) {
			res[i + 3*n] = dfc(arg0 - arg1[i + n ], arg1[i], arg1[i + 2*n], arg1[i + 3*n ]);	
		}				
		
		
		for(int i = 0 ; i < n ; i++) {
			res[i + n] = dfd(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n ], arg1[i + 3*n ], arg1[i + n]);
		}
		
		return res;
	}

	public double value(double arg0, double... arg1) {

		double res = 0.0;// Double.MIN_VALUE;

		for (int i = 0; i < n; i++) {
			res += f(arg0 - arg1[i + n], arg1[i], arg1[i + 2*n], arg1[i + 3*n]);
		}
		return res;
	}

	
	/////// ============== edit this part
	
	// basic value
	public double f(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x> 0) {
			res = a * (1 - Math.exp( - b * x)) / (1 + c * Math.exp( - b * x)) ;
		}
		return res;
	}

	// partial deriv a
	public double dfa(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = (1 - Math.exp( - b * x)) / (1 + c * Math.exp( - b * x)) ;
		}
		return res;
	}
	
	// partial deriv b
	public double dfb(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res =  a * x * Math.exp( - b * x) * (1 + c) / ( (1 + c * Math.exp( - b * x)) * (1 + c * Math.exp( - b * x)) ) ;
		}
		return res;
	}
	
	// partial deriv c
	public double dfc(double x, double a, double b, double c) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = - a * Math.exp( - b * x) * (1 -  Math.exp( - b * x)) /  ( (1 + c * Math.exp( - b * x)) * (1 + c * Math.exp( - b * x)) ) ;
		}
		return res;
	}
	
	// partial deriv delta
	public double dfd(double x, double a, double b, double c, double d) {
		double res = 0.0;// Double.MIN_VALUE;
		if (x > 0) {
			res = - a * b * Math.exp( - b * x) * 
					( 					1 					/ (1 + c * Math.exp( - b * x)) + 
							c * (1 -  Math.exp( - b * x)) 	/ ( (1 + c * Math.exp( - b * x)) * (1 + c * Math.exp( - b * x)) ) 
							); 
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
