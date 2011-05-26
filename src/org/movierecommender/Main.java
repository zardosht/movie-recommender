package org.movierecommender;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.movierecommender.data.ImportUtil;
import org.movierecommender.driver.Controller;
import org.movierecommender.driver.prediction.MeanPredictor;
import org.movierecommender.driver.prediction.PredictionResult;
import org.movierecommender.driver.similarity.MeanSquaredErrorStrategy;
import org.movierecommender.model.UserItemMatrix;

public class Main {
	// test //test //test //test
	public static void main(String[] args) throws IOException {
			
		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File(
				"data/ml-data_0/u.data"));
		Controller driver = new Controller(matrix);
		driver.setRatingPredictor(new MeanPredictor()).setSimilarityStrategy(
				new MeanSquaredErrorStrategy());
	}
}
