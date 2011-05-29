package org.movierecommender.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CSVWriter {

	private static final int THRESHOLD = 20;
	private final File output;
	private Queue<Map<String, Object>> queue;
	private final List<String> columns;
	private BufferedWriter stream;

	public CSVWriter(File output, List<String> columns) throws IOException {
		this.output = output;
		this.columns = columns;
		this.queue = new LinkedList<Map<String, Object>>();
		writeHeader();
	}

	private void writeHeader() throws IOException {
		String line = "";
		for(String column : columns) {
			line += column+";";
		}
		getWriter().write(writeLine(line.substring(0, line.length() - 1)));
	}

	public void writeRecord(Map<String, Object> record) {
		this.queue.add(record);
		checkQueue();
	}

	private void checkQueue() {
		if (this.queue.size() < THRESHOLD) {
			return;
		}
		wirteQueue();
	}

	private void wirteQueue() {
		try {
			BufferedWriter writer = getWriter();
			while (queue.size() != 0) {
				writer.write(writeLine(lineToString(queue.poll())));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String writeLine(String lineToString) {
		return lineToString+System.getProperty("line.separator");
	}

	private String lineToString(Map<String, Object> poll) {
		String line = "";
		for (String column : columns) {
			Object object = poll.get(column);
			line += ((object == null) ? "" : object) + ";";
		}
		return line.substring(0, line.length() - 1);
	}

	private BufferedWriter getWriter() throws IOException {
		if (stream == null) {
			stream = new BufferedWriter(new FileWriter(output));
		}
		return stream;
	}

	public void close() {
		wirteQueue();
		try {
			getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
