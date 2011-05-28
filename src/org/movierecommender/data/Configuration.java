package org.movierecommender.data;

import java.util.List;
import java.util.Properties;

import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.similarity.SimilarityStrategy;

public class Configuration extends Properties {
	
	List<SimilarityStrategy> simStats;
	int kNStart;
	int kNEnd;
	int kNStep;
	List<RatingPredictor> predStats;
	double favThresholdStart;
	double favThresholdEnd;
	double favThresholdStep;
	

	public List<SimilarityStrategy> getSimilarityStrats() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getKNStart() {
		// TODO Auto-generated method stub
		return 0;
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
