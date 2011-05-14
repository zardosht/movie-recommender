package org.movierecommender.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.movierecommender.model.UserItemMatrix;

public class ImportUtil {

	public static UserItemMatrix importUserItemFromFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		UserItemMatrix result = new UserItemMatrix();
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] split = line.split("\t");
			int userId = Integer.parseInt(split[0]);
			int itemId = Integer.parseInt(split[1]);
			int rating = Integer.parseInt(split[2]);
			result.addEntry(userId,itemId,rating);
		}
		
		return result;
	}
}
