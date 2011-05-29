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

	public String getMRMode(){
		return getProperty("mr.mode");
	}
	
	public SimilarityStrategy getProductionSimilarityStrategy(){
		String property = getProperty("mr.production.simstrat");
		if("mse".equals(property)){
			return new MeanSquaredErrorStrategy();
		}else if("pearson".equals(property)){
			return new PearsonCorrelationStrategy();
		}
		return null;
	}

	public int getProductionKNeighbors(){
		return Integer.parseInt(getProperty("mr.production.kNeighbors"));
	}

	
	public RatingPredictor getProductionPredictionStrategy(){
		String property = getProperty("mr.production.predStrat");
		if("mean".equals(property)){
			return new MeanPredictor();
		}else if("weighted".equals(property)){
			return new WeightedPredictor();
		}
		return null;
	}
	
	public double getProductionFavoriteThreshold(){
		return Double.parseDouble(getProperty("mr.production.favThreshold"));
	}

	public List<SimilarityStrategy> getSimilarityStrategies() {
		List<SimilarityStrategy> simStats = new ArrayList<SimilarityStrategy>();
		String property = getProperty("mr.evalulation.simstrat");
		String[] split = property.split(",");
		for(int i = 0; i < split.length; i++){
			String strat = split[i].trim();
			if("mse".equals(strat)){
				simStats.add(new MeanSquaredErrorStrategy());
			}else if("pearson".equals(strat)){
				simStats.add(new PearsonCorrelationStrategy());
			}
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
		for(int i = 0; i < split.length; i++){
			String strat = split[i].trim();
			if("mean".equals(strat)){
				predStrats.add(new MeanPredictor());
			}else if("weighted".equals(strat)){
				predStrats.add(new WeightedPredictor());
			}
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

}
