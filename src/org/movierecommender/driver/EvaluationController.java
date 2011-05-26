package org.movierecommender.driver;

import java.util.List;

import org.movierecommender.driver.prediction.PredictionResult;
import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class EvaluationController extends Controller {

	public EvaluationController(UserItemMatrix matrix) {
		super(matrix);
	}

	public void evaluate() {
		User testUser = null;
		List<Item> itemsToPredict = null;
		List<SimilarityResult> neighbors = getNeighbors(getSimilarities(
				testUser, itemsToPredict));
		List<PredictionResult> ratingPredictions = getAllRatingPredictions(
				testUser, neighbors, itemsToPredict, true);

		double rmse = getRMSEError(testUser, ratingPredictions);
		double maeError = getMAEError(testUser, ratingPredictions);

		List<PredictionResult> favorites = getFavorites(ratingPredictions);

		double recall = getRecall(favorites);
		double precision = getPrecision(favorites);
		double fMeasure = getFMeasure(recall, precision);
	}

	public double getRMSEError(User testUser,
			List<PredictionResult> ratingPredictions) {
		return getError(testUser, ratingPredictions, true);
	}

	public double getMAEError(User testUser,
			List<PredictionResult> ratingPredictions) {
		return getError(testUser, ratingPredictions, false);
	}

	private double getError(User testUser,
			List<PredictionResult> ratingPredictions, boolean useRSME) {
		for (PredictionResult result : ratingPredictions) {
			double diff = result.getValue()
					- testUser.getRatings().get(result.getItem());
		}
		return 0;
	}

	public double getRecall(List<PredictionResult> favorites) {
		for (PredictionResult result : favorites) {

		}
		return 0;
	}

	public double getPrecision(List<PredictionResult> favorites) {
		for (PredictionResult result : favorites) {

		}
		return 0;
	}

	public double getFMeasure(double recall, double precision) {
		return 0;
	}

}
