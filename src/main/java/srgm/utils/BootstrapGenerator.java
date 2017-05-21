package srgm.utils;

import java.io.File;

import weka.filters.unsupervised.instance.Resample;

public class BootstrapGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String basePath = "/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/";
		
		/* config
		String arffSrc = "arff/";
		String bootOutput = "boot/";
*/
	
		// config - comm
		String arffSrc = "arff/";
		String bootOutput = "boot-comm/";
		//
		
		int bootSize = 1000;
		File dir = new File(basePath + arffSrc);
		
		for(File f : dir.listFiles())
		{
		
		
		for (int i = 0; i < bootSize; i++) {

			String[] params = new String[] {
					"-i",
					f.toPath().toString(),
					"-o",
					basePath + bootOutput + f.getName() + i + ".arff",
					"-no-replacement","-Z", "80" , "-S", ""+i };

			Resample.main(params);
		}
		}
	/*
		for (int i = 0; i < 1000; i++) {

			String[] params = new String[] {
					"-i",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/arff/bugs-1-tizen.arff",
					"-o",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot80/bugs-1-tizen-resample_" + i + ".arff",
					"-no-replacement", "-Z", "80" , "-S", ""+i};

			Resample.main(params);
		}

		for (int i = 0; i < 1000; i++) {

			String[] params = new String[] {
					"-i",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/arff/bugs-2-cyanogen.arff",
					"-o",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot80/bugs-2-cyanogen-resample_" + i + ".arff",
					"-no-replacement", "-Z", "80" , "-S", ""+i};

			Resample.main(params);
		}

		for (int i = 0; i < 1000; i++) {

			String[] params = new String[] {
					"-i",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/arff/bugs-4-sailfish-mer.arff",
					"-o",
					"/Users/vladimirivanov/Downloads/Innopolis/lips/data-analysis/srgm/data/boot80/bugs-4-sailfish-mer-resample_" + i + ".arff",
					"-no-replacement", "-Z", "80" , "-S", ""+i};

			Resample.main(params);
		}
*/
		
		
	}

}
