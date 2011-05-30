package org.movierecommender.controller.similarity;

import org.movierecommender.model.User;

public class SimilarityResult implements Comparable<SimilarityResult> {

	
	
	public User getOther() {
		return other;
	}

	public User getMain() {
		return main;
	}

	private final double value;
	private final User other;
	private final User main;
	private boolean sortAsc;

	public SimilarityResult(User main, User other, double value, boolean sortAsc) {
		this.sortAsc = sortAsc;
		this.main = main;
		this.other = other;
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public int compareTo(SimilarityResult o) {
		if(sortAsc){
			//less first
			return (this.value > o.value) ? 1 : -1;
		}else{
			//bigger first
			return (this.value > o.value) ? -1 : 1;
		}
	}

}
