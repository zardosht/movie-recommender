package org.movierecommender.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.movierecommender.driver.prediction.PredictionResult;
import org.movierecommender.driver.prediction.RatingPredictor;
import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.driver.similarity.SimilarityStrategy;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class Driver {

	private final UserItemMatrix userItemMatrix;
	private SimilarityStrategy similarityStrategy;
	private RatingPredictor ratingPredictor;
	
	private int SIMILAR_USERS_THRESHOLD = 20;
	private int FAVORITE_RATING_THRESHOLD = 50;

	public Driver(UserItemMatrix matrix, SimilarityStrategy similarityStrategy,
			RatingPredictor ratingPredictor) {
		this.userItemMatrix = matrix;
		this.similarityStrategy = similarityStrategy;
		this.ratingPredictor = ratingPredictor;
	}

	/**
	 * Recommends a set of items for a given user.
	 * 
	 * @param user
	 * @return
	 */
	public List<PredictionResult> recommendItems(User user) {
		// TODO: logging
		// TODO: test, refactor

		// compute similarities for all users
		List<SimilarityResult> similarityResults = getSimilarities(user);

		// filter neighbors
		List<SimilarityResult> neighbours = getNeighbors(similarityResults);

		// rate all unrated items for this user
		List<PredictionResult> allRatingPredictions = getAllRatingPredictions(
				user, neighbours);

		//returns favorites using the rating threshold
		return getFavorites(allRatingPredictions);

	}
	
	
	

	/**
	 * returns favorites using the rating threshold
	 * @param allRatingPredictions
	 * @return
	 */
	public List<PredictionResult> getFavorites(List<PredictionResult> allRatingPredictions) {
		Collections.sort(allRatingPredictions);
		int toReturn = FAVORITE_RATING_THRESHOLD;
		if(FAVORITE_RATING_THRESHOLD > allRatingPredictions.size()){
			toReturn = allRatingPredictions.size() - 1;
		}
		return allRatingPredictions.subList(0,toReturn);
	}

	/**
	 * Return predictions for all items not rated by this user. 
	 * @param user
	 * @param neighbours
	 * @return
	 */
	public List<PredictionResult> getAllRatingPredictions(User user,
			List<SimilarityResult> neighbours) {
		List<PredictionResult> allRatingPredictions = new ArrayList<PredictionResult>();
		for (Item item : getUnratedItems(user)) {
			PredictionResult predictedRating = getPredictedRating(user,
					neighbours, item);
			allRatingPredictions.add(predictedRating);
		}
		return allRatingPredictions;
	}

	/**
	 * returns the predicted rating that user would give for this item.
	 * @param user
	 * @param neighbours
	 * @param item
	 * @return
	 */
	public PredictionResult getPredictedRating(User user,
			List<SimilarityResult> neighbours, Item item) {
		PredictionResult predictedRating = getRatingPredictor().predictRating(user, item,
				neighbours);
		return predictedRating;
	}

	/**
	 * Return a set of most similar users. This method sorts the similarity
	 * result of all users and returns (K == NUM_SIMILAR_USERS_THRESHOLD) 
	 * most similar users.   
	 * 
	 * @param similarityResults
	 * @return
	 */
	public List<SimilarityResult> getNeighbors(
			List<SimilarityResult> similarityResults) {
		Collections.sort(similarityResults);
		int neighborsToReturn = SIMILAR_USERS_THRESHOLD;
		if (SIMILAR_USERS_THRESHOLD > similarityResults.size()) {
			neighborsToReturn = similarityResults.size() - 1;
		}
		List<SimilarityResult> neighbours = similarityResults.subList(0,
				neighborsToReturn);
		return neighbours;
	}
	
	/**
	 * Return the (K = SIMILAR_USERS_THRESHOLD) most similar users to the given user. 
	 * @param user
	 * @return
	 */
	public List<SimilarityResult> getNeighbors(User user){
		// compute similarities for all users
		List<SimilarityResult> similarityResults = getSimilarities(user);

		// filter neighbors
		List<SimilarityResult> neighbours = getNeighbors(similarityResults);
		return neighbours;
	}

	/**
	 * For all users in data set, compute their similarity with the given user.
	 * 
	 * @param user
	 * @return
	 */
	public List<SimilarityResult> getSimilarities(User user) {
		List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();
		// calc similarity
		for (User other : userItemMatrix.getUsers()) {
			similarityResults.add(getSimilarityStrategy().calculateSimilarty(
					user, other));
		}
		return similarityResults;
	}
	
	
	public SimilarityStrategy getSimilarityStrategy() {
		return similarityStrategy;
	}

	public Driver setSimilarityStrategy(SimilarityStrategy similarityStrategy) {
		this.similarityStrategy = similarityStrategy;
		return this;
	}

	public RatingPredictor getRatingPredictor() {
		return ratingPredictor;
	}

	public Driver setRatingPredictor(RatingPredictor ratingPredictor) {
		this.ratingPredictor = ratingPredictor;
		return this;
	}
	
	
	/**
	 * Get items that this user has not rated.
	 * @return
	 */
	public List<Item> getUnratedItems(User user){
		List<Item> result = new ArrayList<Item>();
		for (Item item : userItemMatrix.getItems()) {
			if (user.hasRated(item)) {
				continue;
			}
			result.add(item);
		}
		return result;
	}

}