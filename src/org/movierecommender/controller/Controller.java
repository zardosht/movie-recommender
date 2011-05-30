package org.movierecommender.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public Controller(UserItemMatrix matrix) {
		this.userItemMatrix = matrix;
	}

	// TODO RUN WITH DEFAULT OPTIONS
	// /**
	// * Recommends a set of items for a given user.
	// *
	// * @param user
	// * @return
	// */
	// public List<PredictionResult> getRatingPredictions(User user,
	// List<Item> items) {
	// // TODO: logging, see slides page 37
	// // TODO: test, refactor (should'nt we think about UserItemMatrix?)
	//
	// // compute similarities for all users
	// List<SimilarityResult> similarityResults = getSimilarities(user);
	//
	// // filter neighbors
	// List<SimilarityResult> neighbours = getNeighbors(similarityResults);
	//
	// // rate all unrated items for this user
	// List<PredictionResult> allRatingPredictions = getAllRatingPredictions(
	// user, neighbours, items, true);
	//
	// return allRatingPredictions;
	// }

	/**
	 * returns favorites using the rating threshold
	 * 
	 * @param allRatingPredictions
	 * @param favoriteCount
	 * @param favThreshold
	 * @param selectionStrategy
	 * @return
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
			result.add(allFavorites.get(i));
		}
		return result;
	}

	public List<PredictionResult> getAllFavorites(
			List<PredictionResult> allRatingPredictions, double favThreshold) {

		return getFavorites(allRatingPredictions, allRatingPredictions.size(),
				favThreshold);
	}

	/**
	 * Return predictions for all items not rated by this user.
	 * 
	 * @param user
	 * @param neighbours
	 * @param items
	 *            can be null
	 * @param filterInvalid
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
		}
		return allRatingPredictions;
	}

	/**
	 * Return a set of most similar users. This method sorts the similarity
	 * result of all users and returns (K == NUM_SIMILAR_USERS_THRESHOLD) most
	 * similar users.
	 * 
	 * @param similarityResults
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
			neighbours.add(similarityResults.get(i));
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