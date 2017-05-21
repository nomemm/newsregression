package srgm.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

import srgm.models.fixedbc.GOS_SRGMFunctionWithB_b;
import srgm.models.fixedbc.GO_SRGMFunctionWithB_b;
import srgm.models.fixedbc.HD_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.L_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.WS_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.W_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.YE_SRGMFunctionWithBC_bc;
import srgm.models.fixedbc.YR_SRGMFunctionWithBC_bc;

public class StringToModelMapper {

	public static ParametricUnivariateFunction getModel(String name, int n) {

		Map<String,ParametricUnivariateFunction> func = new HashMap<String,ParametricUnivariateFunction>();

		func.put("GO", new GO_SRGMFunctionWithB_b(n));
		func.put("GOS", new GOS_SRGMFunctionWithB_b(n));
		func.put("HD", new HD_SRGMFunctionWithBC_bc(n));
		func.put("L", new L_SRGMFunctionWithBC_bc(n));
		func.put("W", new W_SRGMFunctionWithBC_bc(n));
		func.put("WS", new WS_SRGMFunctionWithBC_bc(n));
		func.put("YE", new YE_SRGMFunctionWithBC_bc(n));
		func.put("YR", new YR_SRGMFunctionWithBC_bc(n));

		
		return func.get(name);

	}

}
