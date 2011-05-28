package org.movierecommender;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.movierecommender.controller.EvaluationController;
import org.movierecommender.data.CSVWriter;
import org.movierecommender.data.ImportUtil;
import org.movierecommender.model.UserItemMatrix;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getPackage().getName());
	
	public static void main(String[] args) throws Exception {
		
		configureLogger();

		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File(
				"data/ml-data_0/u.data"));
		// Controller driver = new Controller(matrix);
		logger.log(Level.INFO, "UserItemMatrix intialized.");
		
		CSVWriter csvWriter = new CSVWriter(new File(
				"./results/eval.csv"), Arrays.asList("userId","RMSE","MAE","recall","precision","fMeasure"));
		EvaluationController evaluationController = new EvaluationController(
				matrix);
		evaluationController.runEvaluation(csvWriter);
		csvWriter.close();

	}

	private static void configureLogger() {
		logger.setLevel(Level.OFF);
	}
}
