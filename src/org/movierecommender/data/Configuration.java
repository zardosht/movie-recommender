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
	
	public double getProductionTestSetPercentage() {
		return Double.parseDouble(getProperty("mr.production.testSetPercentage"));
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

}
