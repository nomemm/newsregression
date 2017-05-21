package srgm.visualization;

import java.io.*;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import srgm.evaluation.MetricsForSRGM;

public class LineChart {

	XYDataset dataset = new XYSeriesCollection();
	XYSeries series = new XYSeries("Model");

//	XYSeries seriesUpper = new XYSeries("Upper CI");
//	XYSeries seriesLower = new XYSeries("Lower CI");

	XYSeries seriesData = new XYSeries("Data");
	
	File lineChart;

	public void init(String name) {

		double[] coef = MetricsForSRGM.getCoeffB();
		fillSeries(coef, series);
		
		double[] coefU = MetricsForSRGM.getBootCoeffUp();
	//	fillSeries(coefU, seriesUpper);
		
		double[] coefL = MetricsForSRGM.getBootCoeffLow();
	//	fillSeries(coefL, seriesLower);
		
		fillDataSeries(seriesData, MetricsForSRGM.getObs());

		lineChart = new File("/Users/vladimirivanov/Downloads/Innopolis/lips/results/pics/LineChart_" + name + ".jpeg");
	}

	private void fillDataSeries(XYSeries s, WeightedObservedPoints obs) {
		
int ptsCnt = MetricsForSRGM.getObs().toList().size();
		
		double[] x = new double[ptsCnt];
		double[] y = new double[ptsCnt];
		int i = 0;
			 
		for (WeightedObservedPoint p : obs.toList()) {
			y[i] = p.getY();
			x[i++] = p.getX();
		}
		setData(x, y, s);				
	}

	private void fillSeries(double[] coef, XYSeries s) {
		int ptsCnt = MetricsForSRGM.getObs().toList().size();
		
		double[] x = new double[ptsCnt];
		double[] y = new double[ptsCnt];
		int i = 0;
			 
		for (WeightedObservedPoint p : MetricsForSRGM.getObs().toList()) {
			y[i] = MetricsForSRGM.getSrgmFunc().value(p.getX(), coef);
			x[i++] = p.getX();
		}
		setData(x, y, s);		
	}

	public void save() {
		JFreeChart chart = ChartFactory.createXYLineChart("Fitting", "Time", "#bugs",
				dataset, PlotOrientation.VERTICAL, true, false, false);
		
		int width = 1024; /* Width of the image */
		int height = 768; /* Height of the image */
		try {
			ChartUtilities.saveChartAsJPEG(lineChart, chart, width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setData(double[] x, double[] y, XYSeries s) {
		for (int i = 0; i < x.length; i++) {
			s.add(x[i], y[i]);
		}
		((XYSeriesCollection) dataset).addSeries(s);
	}

}