package srgm.utils;

public class ConverterFormCSVtoARFF {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] params = new String[]{"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/bugs-8-pmo.csv"}; 
		weka.core.converters.CSVLoader.main(params);				
	}

}
