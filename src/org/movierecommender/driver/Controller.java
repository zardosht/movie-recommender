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

public class Controller {

	private final UserItemMatrix userItemMatrix;
	private SimilarityStrategy similarityStrategy;
	private RatingPredictor ratingPredictor;

	protected int similiarUserCount = 50;

	protected int favoriteCount = 50;

	protected int favoriteRatingThreshold = 4;
	
	public Controller(UserItemMatrix matrix) {
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
	public List<PredictionResult> getRatingPredictions(User user,
			List<Item> items) {
		// TODO: logging, see slides page 37
		// TODO: test, refactor (should'nt we think about UserItemMatrix?)

		// compute similarities for all users
		List<SimilarityResult> similarityResults = getSimilarities(user);

		// filter neighbors
		List<SimilarityResult> neighbours = getNeighbors(similarityResults);

		// rate all unrated items for this user
		List<PredictionResult> allRatingPredictions = getAllRatingPredictions(
				user, neighbours, items, true);

		return allRatingPredictions;
	}

	/**
	 * returns favorites using the rating threshold
	 * 
	 * @param allRatingPredictions
	 * @return
	 */
	public List<PredictionResult> getFavorites(
			List<PredictionResult> allRatingPredictions) {

		// TODO: change method name to recommendItems.
		// TODO: return 10 items. see slides page 36
		// TODO: what is FAVORITE_RATING_THRESHOLD? which items should be
		// considered worth recommending?
		Collections.sort(allRatingPredictions);
		int toReturn = favoriteCount;
		if (favoriteCount > allRatingPredictions.size()) {
			toReturn = allRatingPredictions.size() - 1;
		}
		return allRatingPredictions.subList(0, toReturn);
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
			List<SimilarityResult> neighbours, List<Item> items, boolean filterInvalid) {
		List<PredictionResult> allRatingPredictions = new ArrayList<PredictionResult>();
		if (items == null) {
			items = getUnratedItems(user);
		}
		for (Item item : items) {
			PredictionResult predictedRating = getPredictedRating(user,
					neighbours, item);
			if(predictedRating.isInvalid() && filterInvalid) {
				continue;
			}
			allRatingPredictions.add(predictedRating);
		}
		return allRatingPredictions;
	}

	/**
	 * returns the predicted rating that user would give for this item.
	 * 
	 * @param user
	 * @param neighbours
	 * @param item
	 * @return
	 */
	public PredictionResult getPredictedRating(User user,
			List<SimilarityResult> neighbours, Item item) {
		return getRatingPredictor().predictRating(user, item, neighbours);
	}

	/**
	 * Return a set of most similar users. This method sorts the similarity
	 * result of all users and returns (K == NUM_SIMILAR_USERS_THRESHOLD) most
	 * similar users.
	 * 
	 * @param similarityResults
	 * @return
	 */
	public List<SimilarityResult> getNeighbors(
			List<SimilarityResult> similarityResults) {
		// TODO: what does SIMILARITY_THRESHOLD mean? in page 19 you say
		// "alle benutzer mit ähnlichkeit > THRESHOLD bilden S" (S = set of
		// neighbors). But in page 36 you say return "K ähnlichste benutzer"!
		// Diese zwei haben verschiedene bedeutungen! was sollen wir nehmen?
		Collections.sort(similarityResults);
		int neighborsToReturn = similiarUserCount;
		if (similiarUserCount > similarityResults.size()) {
			neighborsToReturn = similarityResults.size() - 1;
		}
		List<SimilarityResult> neighbours = similarityResults.subList(0,
				neighborsToReturn);
		return neighbours;
	}

	/**
	 * For all users in data set, compute their similarity with the given user.
	 * 
	 * @param user
	 * @return
	 */
	public List<SimilarityResult> getSimilarities(User user) {
		return getSimilarities(user, new ArrayList<Item>());
	}
	
	public List<SimilarityResult> getSimilarities(User user, List<Item> ignoreItems) {
		List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();
		// calc similarity
		for (User other : userItemMatrix.getUsers()) {
			// compare userid so you can copy user and still filter copied user
			if (other != user && user.getUserId() != other.getUserId()) {
				similarityResults.add(getSimilarityStrategy()
						.calculateSimilarty(user, other,ignoreItems));
			}
		}
		return similarityResults;
	}

	public SimilarityStrategy getSimilarityStrategy() {
		return similarityStrategy;
	}

	public Controller setSimilarityStrategy(
			SimilarityStrategy similarityStrategy) {
		this.similarityStrategy = similarityStrategy;
		return this;
	}

	public RatingPredictor getRatingPredictor() {
		return ratingPredictor;
	}

	public Controller setRatingPredictor(RatingPredictor ratingPredictor) {
		this.ratingPredictor = ratingPredictor;
		return this;
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

}