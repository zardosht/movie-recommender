package org.movierecommender.data;

import java.util.List;
import java.util.Properties;

import org.movierecommender.controller.prediction.MeanPredictor;
import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.prediction.WeightedPredictor;
import org.movierecommender.controller.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.controller.similarity.PearsonCorrelationStrategy;
import org.movierecommender.controller.similarity.SimilarityStrategy;

public class Configuration extends Properties {
	
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


	
	public List<SimilarityStrategy> getSimilarityStrats() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getKNStart() {
		return Integer.parseInt(getProperty("mr.evalulation.kstart"));
	}

	public int getKNEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getKNStep() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<RatingPredictor> getPredictionStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getFavThresholdStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getFavThresholdEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getFavThresholdStep() {
		// TODO Auto-generated method stub
		return 0;
	}

}
