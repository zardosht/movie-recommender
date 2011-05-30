package org.movierecommender.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.movierecommender.controller.prediction.MeanPredictor;
import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.prediction.WeightedPredictor;
import org.movierecommender.controller.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.controller.similarity.PearsonCorrelationStrategy;
import org.movierecommender.controller.similarity.SimilarityStrategy;

public class Configuration extends Properties {

	private static final long serialVersionUID = 1L;

	public String getMRMode() {
		return getProperty("mr.mode");
	}

	public SimilarityStrategy getProductionSimilarityStrategy() {
		return getSimilarityStrategy(getProperty("mr.production.simstrat"));
	}

	private SimilarityStrategy getSimilarityStrategy(String key) {
		if ("mse".equals(key)) {
			return new MeanSquaredErrorStrategy();
		} else if ("pearson".equals(key)) {
			return new PearsonCorrelationStrategy();
		}
		return null;
	}

	public int getProductionKNeighbors() {
		return Integer.parseInt(getProperty("mr.production.kNeighbors"));
	}

	public RatingPredictor getProductionPredictionStrategy() {
		return getPredicitionStrategy(getProperty("mr.production.predStrat"));
	}

	private RatingPredictor getPredicitionStrategy(String key) {
		if ("mean".equals(key)) {
			return new MeanPredictor();
		} else if ("weighted".equals(key)) {
			return new WeightedPredictor();
		}
		return null;
	}

	public double getProductionFavoriteThreshold() {
		return Double.parseDouble(getProperty("mr.production.favThreshold"));
	}
	

	public List<SimilarityStrategy> getSimilarityStrategies() {
		List<SimilarityStrategy> simStats = new ArrayList<SimilarityStrategy>();
		String property = getProperty("mr.evalulation.simstrat");
		String[] split = property.split(",");
		for (int i = 0; i < split.length; i++) {
			SimilarityStrategy strategy = getSimilarityStrategy(split[i].trim());
			if (strategy != null)
				simStats.add(strategy);
		}
		return simStats;
	}

	public int getKNStart() {
		return Integer.parseInt(getProperty("mr.evalulation.kstart"));
	}

	public int getKNEnd() {
		return Integer.parseInt(getProperty("mr.evalulation.kend"));
	}

	public int getKNStep() {
		return Integer.parseInt(getProperty("mr.evalulation.kstep"));
	}

	public List<RatingPredictor> getPredictionStrategies() {
		List<RatingPredictor> predStrats = new ArrayList<RatingPredictor>();
		String property = getProperty("mr.evalulation.predStrat");
		String[] split = property.split(",");
		for (int i = 0; i < split.length; i++) {
			RatingPredictor strategy = getPredicitionStrategy(split[i].trim());
			if (strategy != null)
				predStrats.add(strategy);
		}
		return predStrats;
	}

	public double getFavThresholdStart() {
		return Double.parseDouble(getProperty("mr.evalulation.favStart"));
	}

	public double getFavThresholdEnd() {
		return Double.parseDouble(getProperty("mr.evalulation.favEnd"));
	}

	public double getFavThresholdStep() {
		return Double.parseDouble(getProperty("mr.evalulation.favStep"));
	}
	
	public List<Double> getTestSetPercentages() {
		List<Double> result = new ArrayList<Double>();
		String property = getProperty("mr.evaluation.testSetPercentage");
		String[] split = property.split(",");
		for (int i = 0; i < split.length; i++) {
			result.add(Double.parseDouble(split[i].trim()));
		}
		return result;
	}

	public int getNumberOfThreads() {
		int parseInt = Integer.parseInt(getProperty("mr.evaluation.threads"));
		return (parseInt==-1)?Runtime.getRuntime().availableProcessors():parseInt;
	}

	public int getRunsPerOption() {
		return Integer.parseInt(getProperty("mr.evaluation.runs"));
	}

	public String getOutputFile() {
		return getProperty("mr.evaluation.output");
	}
	
	
	public String getLogging(){
		return getProperty("mr.logging");
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer("");
		String mrMode = getMRMode();
		result.append(String.format("Mode: %s \n", mrMode));
		if("production".equals(mrMode)){
			result.append(String.format("Prediction Strategy: %s \n", getProductionPredictionStrategy().getClass().getSimpleName()));
			result.append(String.format("Similarity Strategy: %s \n", getProductionSimilarityStrategy().getClass().getSimpleName()));
			result.append(String.format("Number of Neighboours: %d \n", getProductionKNeighbors()));
			result.append(String.format("Favorite Threshold: %.1f \n", getProductionFavoriteThreshold()));
		}else{
			result.append(String.format("Prediction Strategies: %s \n", getPredictionStrategiesAsString()));
			result.append(String.format("Similarity Strategies: %s \n", getProductionSimilarityStrategiesAsString()));
			result.append(String.format("kN start: %d \n", getKNStart()));
			result.append(String.format("kN end: %d \n", getKNEnd()));
			result.append(String.format("kN step: %d \n", getKNStep()));
			result.append(String.format("Favorite Threshold start: %.1f \n", getFavThresholdStart()));
			result.append(String.format("Favorite Threshold end: %.1f \n", getFavThresholdEnd()));
			result.append(String.format("Favorite Threshold step: %.1f \n", getFavThresholdStep()));
			result.append(String.format("Test set precentages: %s \n", getTestSetPercentagesAsString()));
			result.append(String.format("Number of threads: %d \n", getNumberOfThreads()));
			result.append(String.format("Runs per options combination: %d \n", getRunsPerOption()));
		}
		return result.toString();
	}

	private String getTestSetPercentagesAsString() {
		String result = "";
		List<Double> testSetPercentages = getTestSetPercentages();
		for(Double d : testSetPercentages){
			result += String.format("%.2f;", d);
		}
		return result;
	}

	private String getProductionSimilarityStrategiesAsString() {
		String result = "";
		List<SimilarityStrategy> similarityStrategies = getSimilarityStrategies();
		for(SimilarityStrategy simStrat : similarityStrategies){
			result += simStrat.getClass().getSimpleName() + ";";
		}
		return result;
	}

	private String getPredictionStrategiesAsString() {
		String result = "";
		List<RatingPredictor> predictionStrategies = getPredictionStrategies();
		for(RatingPredictor predStrat : predictionStrategies){
			result += predStrat.getClass().getSimpleName() + ";";
		}
		return result;
	}

}
