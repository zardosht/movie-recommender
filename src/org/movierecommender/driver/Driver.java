package org.movierecommender.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.movierecommender.driver.prediction.PredictionResult;
import org.movierecommender.driver.prediction.RankingPredictor;
import org.movierecommender.driver.similarity.SimilarityResult;
import org.movierecommender.driver.similarity.SimilarityStrategy;
import org.movierecommender.model.Item;
import org.movierecommender.model.User;
import org.movierecommender.model.UserItemMatrix;

public class Driver {

	private final UserItemMatrix matrix;
	private SimilarityStrategy similarityStrategy;

	public SimilarityStrategy getSimilarityStrategy() {
		return similarityStrategy;
	}

	public Driver setSimilarityStrategy(SimilarityStrategy similarityStrategy) {
		this.similarityStrategy = similarityStrategy;
		return this;
	}

	public RankingPredictor getRankingPredictor() {
		return rankingPredictor;
	}

	public Driver setRankingPredictor(RankingPredictor rankingPredictor) {
		this.rankingPredictor = rankingPredictor;
		return this;
	}

	private RankingPredictor rankingPredictor;
	private int K = 20;
	private int K2 = 1600;

	public Driver(UserItemMatrix matrix, SimilarityStrategy similarityStrategy,
			RankingPredictor rankingPredictor) {
		this.matrix = matrix;
		this.similarityStrategy = similarityStrategy;
		this.rankingPredictor = rankingPredictor;
	}

	public List<PredictionResult> recommendItems(User user) {
		// TODO: logging
		// TODO: impl SimStrats, RankingPredictors
		// TODO: test, refactor

		List<SimilarityResult> similarityResults = new ArrayList<SimilarityResult>();
		// calc similarity
		for (User other : matrix.getUsers()) {
			similarityResults.add(getSimilarityStrategy().calculateSimilarty(
					user, other));
		}

		// filter neighbours
		Collections.sort(similarityResults);
		List<SimilarityResult> neighbours = similarityResults.subList(0,
				(K > similarityResults.size()) ? similarityResults.size() - 1
						: K); // :)) eclipse rocks \m/ Ich mag pair programming

		// rank unranked items for this user an add those over ranking threshold
		// to list of potential favorites
		List<PredictionResult> favorites = new ArrayList<PredictionResult>();
		for (Item item : matrix.getItems()) {
			if (user.hasRanked(item)) {
				continue;
			}
			favorites.add(getRankingPredictor().predictRanking(user, item,
					neighbours));
		}

		Collections.sort(favorites);
		return favorites.subList(0,
				(K2 > similarityResults.size()) ? similarityResults.size() - 1
						: K2);

	}

}