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
