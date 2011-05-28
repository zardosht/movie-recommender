package org.movierecommender;

import java.io.File;
import java.util.Arrays;

import org.movierecommender.data.CSVWriter;
import org.movierecommender.data.ImportUtil;
import org.movierecommender.driver.EvaluationController;
import org.movierecommender.model.UserItemMatrix;

public class Main {
	// test //test //test //test
	public static void main(String[] args) throws Exception {

		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File(
				"data/ml-data_0/u.data"));
		// Controller driver = new Controller(matrix);

		CSVWriter csvWriter = new CSVWriter(new File(
				"./results/eval.csv"), Arrays.asList("userId","RMSE","MAE","recall","precision","fMeasure"));
		EvaluationController evaluationController = new EvaluationController(
				matrix);
		evaluationController.runEvaluation(csvWriter);
		csvWriter.close();

	}
}
