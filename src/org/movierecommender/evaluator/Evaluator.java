package org.movierecommender.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.movierecommender.data.ImportUtil;
import org.movierecommender.driver.Driver;
import org.movierecommender.driver.prediction.MeanPredictor;
import org.movierecommender.driver.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class Evaluator {

	public enum ErrorType {
		MAE, MSE
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO: on slide 37, what do you mean with
		// "Darstellung in übersichtlicher Form der Metriken, z.B. Tabelle oder Diagramm"
		// TODO: we create training and test sets by dividing rating data based
		// on users (80% of users for training, 20% for training). Is it ok?
		// TODO: setters for percentage of training and test sets

		System.out.println(new Date());
		// get user item matrix
		UserItemMatrix userItemMatrix = ImportUtil
				.importUserItemFromFile(new File("data/ml-data_0/10K-u.data"));

		// select randomly 20% of the users from user item matrix
		Set<User> testSet = selectRandom(userItemMatrix.getUsers(), 20);
		// remove them from user item matrix
		userItemMatrix.getUsers().removeAll(testSet);
		// init driver with remaining matrix (matrix that 20% of its users have
		// been removed)
		Driver driver = new Driver(userItemMatrix,
				new MeanSquaredErrorStrategy(userItemMatrix),
				new MeanPredictor());

		double mae = getError(testSet, driver, ErrorType.MAE);
		double mse = getError(testSet, driver, ErrorType.MSE);
		double rootMSE = Math.sqrt(mse);
		System.out.println("MAE: " + mae);
		System.out.println("MSE: " + mse);
		System.out.println("RMSE: " + rootMSE);

		double precision = getPrecision(testSet, driver);
		double recall = getRecall(testSet, driver);
		double fMeasure = getFMeasure(recall, precision);
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
		System.out.println("f-measure: " + fMeasure);

		System.out.println(new Date());

	}

	private static double getFMeasure(double recall, double precision) {
		double fMeasure = (2 * precision * recall) / (precision + recall);
		return fMeasure;
	}

	private static double getRecall(Set<User> testSet, Driver driver) {
		// recall = tp / (tp + fn)
		// TODO: exact the same question as in precision case.

		return 0;
	}

	private static double getPrecision(Set<User> testSet, Driver driver) {
		// precision = tp / (tp + fp)
		// TODO: how should TP and FP be calculated? using exact values? or
		// using an interval. I mean, imaging the user u has actually rated item
		// i1 with 7 and item i2 with 2 (r(U,i1)=7; r(U,i2)=2; (in a 10 scale)).
		// If I predict a rating for i1 with say 8, and rating for i2 with say
		// 1, then should my first prediction account to TP and my second
		// account to TN (because they are both near the real rating of the
		// user)? or my ratings should be considered false because they do not
		// equal the actual rating of the user? I think we should consider an
		// interval for equality of predicted rating and actual rating. For
		// example my first rating in this example should be considered TP and
		// my second rating TN.
		// I think we need to define the favorite Threshold, so that we can say
		// if our prediction
		// was correct (TP) or false (FP)

		int tp = 0;
		int fp = 1;

		return tp / (tp + fp);
	}

	private static double getError(Set<User> testSet, Driver driver,
			ErrorType errorType) {
		// TODO: formula for MAE is wrong in slides page 28!!!!!!! oder?
		System.out.println("========================================");
		if (errorType.equals(ErrorType.MAE)) {
			System.out.println("We started MAE");
		} else {
			System.out.println("We started RMSE");
		}
		System.out.println();

		int sum = 0;
		int n = 0;
		int numPredictions = 0;
		for (User u : testSet) {
			// select a user from test set
			// select one of items she has rated
			// unrate that item
			// get neighbors for the user, while the item of interest is unrated
			// predict the rating and compute error
			// set back the rating for that item again, so that you can continue
			// with other items.
			for (Item item : u.getRatings().keySet()) {
				Integer actualRating = u.getRatings().get(item);
				u.unrate(item);
				List<SimilarityResult> neighbors = driver.getNeighbors(u);
				double predictedRating = driver.getPredictedRating(u,
						neighbors, item).getValue();
				if (predictedRating == -1) {
					// No other neighbor had rated this item. We could not
					// predict rating based on what neighbors had rated this
					// item.
					u.addRating(item, actualRating);
					continue; 
				}
				System.out
						.printf("%d. We predicted for %s rating %f. Actual rating was %d . \n",
								numPredictions++, item.toString(),
								predictedRating, actualRating);
				double e = actualRating - predictedRating;
				if (errorType.equals(ErrorType.MAE)) {
					sum += Math.abs(e);
				} else {
					sum += Math.pow(e, 2);
				}
				n++;
				u.addRating(item, actualRating);
			}
		}
		double error = (double) sum / n;
		return error;
	}

	private static Set<User> selectRandom(List<User> users, int percent) {
		Random random = new Random(1L);
		int size = users.size();
		int toReturn = size * percent / 100;
		Set<User> result = new HashSet<User>();
		while (result.size() < toReturn) {
			result.add(users.get(random.nextInt(users.size())));
		}
		return result;
	}

}
