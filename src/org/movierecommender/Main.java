package org.movierecommender;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.movierecommender.data.ImportUtil;
import org.movierecommender.driver.Driver;
import org.movierecommender.driver.prediction.PredictionResult;
import org.movierecommender.driver.prediction.RankingPredictor;
import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.driver.similarity.SimilarityStrategy;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class Main {

	public static void main(String[] args) throws IOException {
		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File("data/ml-data_0/u.data"));
		Driver driver = new Driver(matrix, null, null);
		driver.setRankingPredictor(new RankingPredictor() {
			
			@Override
			public PredictionResult predictRanking(User main, Item item,
					List<SimilarityResult> similarity) {
				return new PredictionResult(main, item, (new Random().nextInt(10)+1));
			}
		}).setSimilarityStrategy(new SimilarityStrategy() {
			
			@Override
			public SimilarityResult calculateSimilarty(User main, User other) {
				return new SimilarityResult(main, other, new Random().nextDouble());
			}
		});
		List<PredictionResult> recItems = driver.recommendItems(matrix.getUsers().get(0));
		int i = 0;
		for(PredictionResult result : recItems) {
			System.out.println((i++)+" "+result);
		}
	}
}
