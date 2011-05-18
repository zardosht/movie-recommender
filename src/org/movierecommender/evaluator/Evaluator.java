package org.movierecommender.evaluator;

import java.io.File;
import java.io.IOException;
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

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//TODO: on slide 37, what do you mean with "Darstellung in übersichtlicher Form der Metriken, z.B. Tabelle oder Diagramm"
		//TODO: we create training and test sets by dividing rating data based on users (80% of users for training, 20% for training). Is it ok?
		//TODO: setters for percentage of training and test sets
		
		//get user item matrix
		UserItemMatrix userItemMatrix = ImportUtil.importUserItemFromFile(new File("data/ml-data_0/u.data"));
			
		//select randomly 20% of the users from user item matrix
		Set<User> testSet = selectRandom(userItemMatrix.getUsers(), 20);
		//remove them from user item matrix
		userItemMatrix.getUsers().removeAll(testSet);
		//init driver with remaining matrix (matrix that 20% of its users have been removed)
		Driver driver = new Driver(userItemMatrix, new MeanSquaredErrorStrategy(userItemMatrix), new MeanPredictor());
		
		
		double mae = getMeanAbsoluteError(testSet, driver);
		double mse = getMeanSquaredError(testSet, driver);
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
		
		
	}

	private static double getFMeasure(double recall, double precision) {
		double fMeasure = (2 * precision * recall) / (precision + recall);
		return fMeasure;
	}

	private static double getRecall(Set<User> testSet, Driver driver) {
		// recall = tp / (tp + fn)
		//TODO: exact the same question as in precision case. 
		
		
		return 0;
	}

	private static double getPrecision(Set<User> testSet, Driver driver) {
		// precision = tp / (tp + fp)
		//TODO: how should TP and FP be calculated? using exact values? or using an interval. I mean, imaging the user u has actually rated item i1 with 7  and item i2 with 2 (r(U,i1)=7; r(U,i2)=2; (in a 10 scale)). If I predict a rating for i1 with say 8, and rating for i2 with say 1, then should my first prediction account to TP and my second account to TN (because they are both near the real rating of the user)? or my ratings should be considered false because they do not equal the actual rating of the user? I think we should consider an interval for equality of predicted rating and actual rating. For example my first rating in this example should be considered TP and my second rating TN.    
		
		int tp = 0;
		int fp = 1;
		
		return tp / (tp + fp);
	}

	private static double getMeanSquaredError(Set<User> testSet, Driver driver) {
		int sum = 0;
		int n = 0;
		for(User u : testSet){
			//select a user from test set
			//select one of items she has rated
			//unrate that item
			List<SimilarityResult> neighbors = driver.getNeighbors(u);
			for(Item item : u.getRatings().keySet()){
				Integer actualRating = u.getRatings().get(item);
				u.unrate(item);
				int predictedRating = driver.getPredictedRating(u, neighbors, item).getValue();
				int e = actualRating - predictedRating;
				sum += Math.pow(e, 2);
				n++;
				u.addRating(item, actualRating);
			}
		}
		double meanSquaredError = (double)sum / n;
		return meanSquaredError;
	}

	private static double getMeanAbsoluteError(Set<User> testSet, Driver driver) {
		//TODO: formula for MAE is wrong in slides page 28!!!!!!! oder?
		int sum = 0;
		int n = 0;
		for(User u : testSet){
			//select a user from test set
			//select one of items she has rated
			//unrate that item
			List<SimilarityResult> neighbors = driver.getNeighbors(u);
			for(Item item : u.getRatings().keySet()){
				Integer actualRating = u.getRatings().get(item);
				u.unrate(item);
				int predictedRating = driver.getPredictedRating(u, neighbors, item).getValue();
				int e = actualRating - predictedRating;
				sum += Math.abs(e);
				n++;
				u.addRating(item, actualRating);
			}
		}
		double mae = (double)sum / n;
		return mae;
	}

	private static Set<User> selectRandom(List<User> users, int percent) {
		Random random = new Random(1L);
		int size = users.size();
		int toReturn = size * percent / 100;
		Set<User> result = new HashSet<User>();
		while(result.size() < toReturn){
			result.add(users.get(random.nextInt(users.size())));
		}
		return result;
	}

}
