package srgm.validators;

import org.apache.commons.math3.fitting.leastsquares.ParameterValidator;
import org.apache.commons.math3.linear.RealVector;

public class ParameterValidatorB implements ParameterValidator {

	public RealVector validate(RealVector params) {

		for (int i = 0; i < params.getDimension(); i++ )
		{
			if(params.getEntry(i) < 0)
			{
			//	params.setEntry(i, 0.00001);
				//System.out.println(i + " =====================================================================================================================");
			}
		}
		
		return params;
	}

}
