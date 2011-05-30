package org.movierecommender.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.movierecommender.controller.prediction.PredictionResult;
import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.similarity.SimilarityResult;
import org.movierecommender.controller.similarity.SimilarityStrategy;
import org.movierecommender.data.Configuration;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class Controller {

	protected final UserItemMatrix userItemMatrix;
	private static Logger logger = Logger.getLogger(Controller.class
			.getPackage().getName());

	public Controller(UserItemMatrix matrix) {
		this.userItemMatrix = matrix;
	}

	/**
	 * Returns favorites from all predicted items. Favorites are selected using
	 * favorite threshold. Items that their predicted rating lie over favorite
	 * threshold are selected as favorite.
	 * 
	 * @param allRatingPredictions
	 *            all predictions
	 * @param favoriteCount
	 *            how many items to include in favorites (if we have many items
	 *            that are over favorite threshold). In this case the favorites
	 *            are sorted and the best items are returned.
	 * @param favThreshold
	 * @return k-best items recognized as favorite.
	 */
	public List<PredictionResult> getFavorites(
			List<PredictionResult> allRatingPredictions, int favoriteCount,
			double favThreshold) {

		List<PredictionResult> allFavorites = new ArrayList<PredictionResult>();
		for (PredictionResult pr : allRatingPredictions) {
			if (pr.getValue() >= favThreshold) {
				allFavorites.add(pr);
			}
		}

		Collections.sort(allFavorites);
		int toReturn = favoriteCount;
		if (favoriteCount > allFavorites.size()) {
			toReturn = allFavorites.size();
		}

		List<PredictionResult> result = new ArrayList<PredictionResult>();
		for (int i = 0; i < toReturn; i++) {
			PredictionResult favorite = allFavorites.get(i);
			result.add(favorite);
			logger.info(String.format("%s is favorite for %s with rating %.3f", favorite.getItem(), favorite.getUser(), favorite.getValue()));
		}
		
		return result;
	}

	/**
	 * Returns all items that their predicted rating lie over favorite
	 * threshold.
	 * 
	 * @param allRatingPredictions
	 * @param favThreshold
	 * @return
	 */
	public List<PredictionResult> getAllFavorites(
			List<PredictionResult> allRatingPredictions, double favThreshold) {

		return getFavorites(allRatingPredictions, allRatingPredictions.size(),
				favThreshold);
	}

	/**
	 * Return predictions for given items. If items is NULL, then all items not
	 * rated by user (but are also rated by at least one neighbor) are
	 * predicted.
	 * 
	 * @param user
	 * @param neighbours
	 * @param items
	 *            can be null
	 * @param filterInvalid
	 *            items whose prediction is in invalid range (not between 1.0
	 *            and 5.0) are filtered. The items can have predicted rating
	 *            over 5.0 for example with weighted prediction.
	 * @return
	 */
	public List<PredictionResult> getAllRatingPredictions(User user,
			List<SimilarityResult> neighbours, List<Item> items,
			boolean filterInvalid, RatingPredictor predictor) {
		List<PredictionResult> allRatingPredictions = new ArrayList<PredictionResult>();
		if (items == null) {
			items = getUnratedItems(user);
		}
		for (Item item : items) {
			PredictionResult predictedRating = predictor.predictRating(user,
					item, neighbours);
			if (predictedRating.isInvalid() && filterInvalid) {
				continue;
			}
			allRatingPredictions.add(predictedRating);
			logger.info(String.format("predicted rating for %s is %.2f", predictedRating.getItem(), predictedRating.getValue()));
		}
		return allRatingPredictions;
	}

	/**
	 * Return a set of most similar users. This method sorts the similarity
	 * result of all users and returns (kN) most
	 * similar users.
	 * 
	 * @param similarityResults similarity list of all users
	 * @param similiarUserCount 
	 * @return
	 */
	public List<SimilarityResult> getNeighbors(
			List<SimilarityResult> similarityResults, int similiarUserCount) {
		Collections.sort(similarityResults);
		int neighborsToReturn = similiarUserCount;
		if (similiarUserCount > similarityResults.size()) {
			neighborsToReturn = similarityResults.size();
		}
		List<SimilarityResult> neighbours = new ArrayList<SimilarityResult>();
		for (int i = 0; i < neighborsToReturn; i++) {
			SimilarityResult similarityResult = similarityResults.get(i);
			neighbours.add(similarityResult);
			logger.info(String.format("%s is a neighbor for %s with similarity %.2f.", similarityResult.getOther(), similarityResult.getMain(), similarityResult.getValue()));
		}
		return neighbours;
	}

	/**
	 * 
	 * @param user
	 * @param ignoreItems
	 * @param strategy
	 * @return
	 */
	public List<SimilarityResult> getSimilarities(User user,
			List<Item> ignoreItems, SimilarityStrategy strategy) {
		List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();
		// calc similarity
		for (User other : userItemMatrix.getUsers()) {
			// compare userid so you can copy user and still filter copied user
			if (other != user && user.getUserId() != other.getUserId()) {
				similarityResults.add(strategy.calculateSimilarty(user, other,
						ignoreItems));
			}
		}
		return similarityResults;
	}

	/**
	 * Get items that this user has not rated.
	 * 
	 * @return
	 */
	public List<Item> getUnratedItems(User user) {
		List<Item> result = new ArrayList<Item>();
		for (Item item : userItemMatrix.getItems()) {
			if (user.hasRated(item)) {
				continue;
			}
			result.add(item);
		}
		
		return result;
	}

	public List<PredictionResult> recommendItems(String userId,
			Configuration config) {

		User user = userItemMatrix.getUserByID(Integer.parseInt(userId));
		List<SimilarityResult> similarities = getSimilarities(user,
				new ArrayList<Item>(), config.getProductionSimilarityStrategy());
		List<SimilarityResult> neighbors = getNeighbors(similarities,
				config.getProductionKNeighbors());
		System.out.println("Neighbours:");
		System.out.println("-----------");
		System.out.println("We found following neighbors for user: " + userId);
		System.out
				.println("For MSE Similariy smaller values are better, for Pearson Similarity bigger values are better.");
		System.out.println();
		Collections.sort(neighbors);
		for (SimilarityResult sr : neighbors) {
			System.out.printf("User %s with similarity %.2f \n", sr.getOther()
					.getUserId(), sr.getValue());
		}
		System.out.println();
		List<PredictionResult> allRatingPredictions = getAllRatingPredictions(
				user, neighbors, null, true,
				config.getProductionPredictionStrategy());
		List<PredictionResult> favorites = getFavorites(allRatingPredictions,
				10, config.getProductionFavoriteThreshold());
		return favorites;
	}

}