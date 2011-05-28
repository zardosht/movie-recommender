package org.movierecommender.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.movierecommender.controller.prediction.MeanPredictor;
import org.movierecommender.controller.prediction.PredictionResult;
import org.movierecommender.controller.prediction.RatingPredictor;
import org.movierecommender.controller.selection.RandomSelectionStrategy;
import org.movierecommender.controller.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.controller.similarity.SimilarityResult;
import org.movierecommender.controller.similarity.SimilarityStrategy;
import org.movierecommender.data.CSVWriter;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class EvaluationController extends Controller {

	public EvaluationController(UserItemMatrix matrix) {
		super(matrix);
	}

	public void runEvaluation(CSVWriter csvWriter) throws Exception {
		int runs = 15000;
		
		System.out.println("Eval started at: "+new Date());
		
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<HashMap<String, Object>>> futures = new ArrayList<Future<HashMap<String, Object>>>();
		
		// Options
		// simStrat: 2
		// kNeighbors: [10,200] 
		// preStrat: 2
		// threshold: [0.0,5.0]
		// [favoriteStrate: 2 (?)]
		
		for (int i = 0; i < runs; i++) {
			final int kN = i+10;
			//TODO iterate through option combinations. Use OptionFactory
			//int anzahlPermut = 345; 
			//for(int j = 0; j < anzahlPermut; j++){
				final Options options = new Options(new MeanSquaredErrorStrategy(), kN,
						new MeanPredictor(), 10, 4.0, new RandomSelectionStrategy());
				
				
				// One evaluation job
				futures.add(pool.submit(new Callable<HashMap<String, Object>>() {
					public HashMap<String, Object> call() {
						User testUser = getRandomUser();
						//TODO add optins settings to output
						return evaluate(testUser, getTestItems(testUser, 0.3), options);
					}
				}));
			//}
		}
		
		for(Future<HashMap<String, Object>> tmp : futures) {
			HashMap<String, Object> record = tmp.get();
			csvWriter.writeRecord(record);
		}
		
		System.out.println("Eval ended at: "+new Date());
		pool.shutdown();
	}

	private List<Item> getTestItems(User testUser, double percent) {
		int items = (int) Math.floor(testUser.getRatings().size() * percent);
		ArrayList<Item> result = new ArrayList<Item>(items);
		for (Item item : testUser.getRatings().keySet()) {
			if (items-- == 0) {
				break;
			}
			result.add(item);
		}
		return result;
	}

	private User getRandomUser() {
		List<User> users = userItemMatrix.getUsers();
		User testUser = users.get(new Random().nextInt(users.size()));
		return testUser;
	}

	public HashMap<String, Object> evaluate(User testUser,
			List<Item> itemsToPredict, Options options) {
		HashMap<String, Object> csvRecord = new HashMap<String, Object>();

		csvRecord.put("userId", testUser.getUserId());

		List<SimilarityResult> similarities = getSimilarities(testUser,
				itemsToPredict, options.similarityStrategy);

		List<SimilarityResult> neighbors = getNeighbors(similarities,
				options.kNeighbors);

		List<PredictionResult> ratingPredictions = getAllRatingPredictions(
				testUser, neighbors, itemsToPredict, true,
				options.ratingPredictor);

		if (ratingPredictions.size() == 0) {
			return csvRecord;
		}

		double rmse = getRMSEError(testUser, ratingPredictions);
		double maeError = getMAEError(testUser, ratingPredictions);

		csvRecord.put("RMSE", rmse);
		csvRecord.put("MAE", maeError);

		// TODO favorite selection
		List<PredictionResult> favorites = getFavorites(ratingPredictions,
				options.favCount, options.favThreshold, options.selectionStrategy);

		if(favorites.size()==0) {
			return csvRecord;
		}
		
		double recall = getRecall(favorites, options.favThreshold);
		double precision = getPrecision(favorites, options.favThreshold);
		double fMeasure = getFMeasure(recall, precision);

		csvRecord.put("recall", recall);
		csvRecord.put("precision", precision);
		csvRecord.put("fMeasure", fMeasure);

		return csvRecord;
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
		double sum = 0;
		for (PredictionResult result : ratingPredictions) {
			double diff = result.getValue()
					- testUser.getRatings().get(result.getItem());
			sum += (useRSME) ? Math.pow(diff, 2) : Math.abs(diff);
		}
		return Math.sqrt(sum / ratingPredictions.size());
	}

	public double getRecall(List<PredictionResult> favorites,
			double favThreshold) {
		// recall = tp / (tp + fn)
		int tp = 0;
		int fn = 0;
		User user = favorites.get(0).getUser(); 
		for(Item item : user.getRatings().keySet()){
			
		}
		
		/*
		 * set UI = {all items that user has rated}
		 * set UF = {items that user has rated over favThresh}
		 * set OF = {items that we have rated over favThresh}
		 * set FN = {die jenigen, die in UF sind, aber nicht in OF sind} = UF - OF
		 * 
		 * 
		 */
		
		
		
		for (PredictionResult result : favorites) {
			Integer actualRating = result.getUser().getRatings().get(result.getItem());
			if(actualRating > favThreshold){
				tp++;
			}
			if()
			
		}
		return tp / (tp + fn);
	}

	public double getPrecision(List<PredictionResult> favorites,
			double favThreshold) {
		//precision = tp / (tp + fp)
		int tp = 0;
		int fp = 0;
		for (PredictionResult result : favorites) {
			Integer actualRating = result.getUser().getRatings().get(result.getItem());
			if(actualRating < favThreshold){
				fp++;
			}else{
				tp++;
			}
		}
		
		//(tp + fp) cannot be 0!
		return tp / (tp + fp); 
	}

	public double getFMeasure(double recall, double precision) {
		return (2 * recall * precision) / (recall + precision);
	}

}
