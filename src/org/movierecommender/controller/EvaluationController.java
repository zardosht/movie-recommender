package org.movierecommender.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.movierecommender.controller.prediction.MeanPredictor;
import org.movierecommender.controller.prediction.PredictionResult;
import org.movierecommender.controller.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.controller.similarity.SimilarityResult;
import org.movierecommender.data.CSVWriter;
import org.movierecommender.data.Configuration;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class EvaluationController extends Controller {

	private static Logger logger = Logger.getLogger(EvaluationController.class
			.getPackage().getName());
	private final Configuration config;

	public EvaluationController(UserItemMatrix matrix, Configuration config) {
		super(matrix);
		this.config = config;
		logger.log(Level.INFO,
				"EvaluationController constructor call returned.");
	}

	public void runEvaluation(CSVWriter csvWriter) throws Exception {
		int runs = 100;

		System.out.println("Eval started at: " + new Date());

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		//availableProcessors = 1;
		ExecutorService pool = Executors.newFixedThreadPool(availableProcessors);
		List<Future<HashMap<String, Object>>> futures = new ArrayList<Future<HashMap<String, Object>>>();

		// Options
		// simStrat: 2
		// kNeighbors: [10,200]
		// preStrat: 2
		// threshold: [0.0,5.0]
		// TODO: what about test set and training set size?

		List<Options> allOptions = new OptionsFactory().getAllOptions(config);
		for (final Options opts : allOptions) {
			for (int i = 0; i < runs; i++) {
				// One evaluation job
				futures.add(pool
						.submit(new Callable<HashMap<String, Object>>() {
							public HashMap<String, Object> call() {
								User testUser = getRandomUser();
								// TODO add optins settings to output
								return evaluate(testUser,
										getTestItems(testUser, 0.3), opts);
							}
						}));
				// }
			}

		}

		for (Future<HashMap<String, Object>> tmp : futures) {
			HashMap<String, Object> record = tmp.get();
			csvWriter.writeRecord(record);
		}

		System.out.println("Eval ended at: " + new Date());
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

		List<PredictionResult> favorites = getAllFavorites(ratingPredictions,
				options.favThreshold);

		if (favorites.size() == 0) {
			return csvRecord;
		}

		// it can be that itemsToPredict != ratingPredictions (because of
		// invalid check)
		double recall = getRecall(ratingPredictions, favorites,
				options.favThreshold);
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

	public double getRecall(List<PredictionResult> toPredict,
			List<PredictionResult> asFavoritePredicted, double favThreshold) {

		// set FN = {die jenigen, die in UF sind, aber nicht in OF sind} = UF -
		// OF
		// recall = tp / (tp + fn)
		int tp = 0;
		int fn = 0;

		// create set of items user have rated favorite
		User user = toPredict.get(0).getUser();
		List<PredictionResult> shouldBeRecommended = new ArrayList<PredictionResult>();
		for (PredictionResult pr : toPredict) {
			Integer actualRating = user.getRatings().get(pr.getItem());
			if (actualRating >= favThreshold) {
				shouldBeRecommended.add(pr);
			}
		}

		for (PredictionResult pr : shouldBeRecommended) {
			if (asFavoritePredicted.contains(pr)) {
				tp++;
			} else {
				fn++;
			}
		}

		if (tp == 0) {
			return 0;
		}
		return (double) tp / (tp + fn);
	}

	public double getPrecision(List<PredictionResult> favorites,
			double favThreshold) {
		// precision = tp / (tp + fp)
		int tp = 0;
		int fp = 0;
		for (PredictionResult result : favorites) {
			Integer actualRating = result.getUser().getRatings()
					.get(result.getItem());
			if (actualRating < favThreshold) {
				fp++;
			} else {
				tp++;
			}
		}

		// (tp + fp) cannot be 0!
		return (double) tp / (tp + fp);
	}

	public double getFMeasure(double recall, double precision) {
		return (2 * recall * precision) / (recall + precision);
	}

}
