package org.movierecommender;

import java.io.File;
import java.io.IOException;

import org.movierecommender.data.ImportUtil;
import org.movierecommender.driver.Driver;
import org.movierecommender.model.UserItemMatrix;

public class Main {

	public static void main(String[] args) throws IOException {
		UserItemMatrix matrix = ImportUtil.importUserItemFromFile(new File("data/ml-data_0/u.data"));
		Driver driver = new Driver(matrix);
		driver.run();
	}
}
