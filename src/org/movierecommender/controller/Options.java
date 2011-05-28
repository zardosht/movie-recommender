package org.movierecommender.controller;

import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.similarity.SimilarityStrategy;

public class Options {

	public SimilarityStrategy similarityStrategy;
	public int kNeighbors;
	public RatingPredictor ratingPredictor;
	public int favCount;
	public double favThreshold;

	public Options(SimilarityStrategy similarityStrategy, int kNeighbors,
			RatingPredictor ratingPredictor, int favCount, double favThreshold) {
				this.similarityStrategy = similarityStrategy;
				this.kNeighbors = kNeighbors;
				this.ratingPredictor = ratingPredictor;
				this.favCount = favCount;
				this.favThreshold = favThreshold;
	}
	
	public Options() {
	}
}
