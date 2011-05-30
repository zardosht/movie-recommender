package org.movierecommender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.movierecommender.controller.Controller;
import org.movierecommender.controller.EvaluationController;
import org.movierecommender.controller.prediction.PredictionResult;
import org.movierecommender.data.CSVWriter;
import org.movierecommender.data.Configuration;
import org.movierecommender.data.ImportUtil;
import org.movierecommender.model.UserItemMatrix;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getPackage()
			.getName());

	public static void main(String[] args) throws Exception {

		Configuration config = new Configuration();
		config.load(new FileInputStream(new File("config/config.cfg")));
		configureLogger(config);
		

		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File(
				"data/ml-data_0/u.data"));
		logger.info("UserItemMatrix intialized.");
		
		logger.info(config.toString());
		System.out.println("Configuration:");
		System.out.println("--------------");
		System.out.println(config.toString());

		String mrMode = config.getMRMode();
		if ("production".equals(mrMode)) {
			logger.info("Movie-Recommender started in production mode.");
			Controller driver = new Controller(matrix);
			String userId = args[0];
			List<PredictionResult> recommendedItems = driver.recommendItems(userId, config);
			System.out.println("Recommendations:");
			System.out.println("----------------");
			int i = 0; 
			for(PredictionResult pr : recommendedItems){
				System.out.println(++i + ".User " + userId + " would rate " + pr.getItem().toString() + " with " + pr.getValue());
			}
			//write down the params, 
			//make method, who takes the user, and returns the list of item recommended to him.
			//write down how long it took. 
			
			
		} else if ("evaluation".equals(mrMode)) {
			CSVWriter csvWriter = new CSVWriter(new File("./results/"
					+ config.getOutputFile()), Arrays.asList("userId",
					"simStrat", "kN", "predStrat", "favCount", "favThreshold",
					"testPercent", "RMSE", "MAE", "recall", "precision",
					"fMeasure", "numPredictions", "numFavorites"));
			logger.info("CSV-Writer initialized.");
			logger.info("Evaluation mode started.");
			EvaluationController evaluationController = new EvaluationController(
					matrix, config);
			evaluationController.runEvaluation(csvWriter);
			csvWriter.close();
		}

	}

	private static void configureLogger(Configuration config) {
		logger.setUseParentHandlers(false);
		try {
			FileHandler fileHandler = new FileHandler("results/log.txt");
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if("true".equals(config.getLogging())){
			logger.setLevel(Level.INFO);
		}else{
			logger.setLevel(Level.OFF);
		}
	}
}
