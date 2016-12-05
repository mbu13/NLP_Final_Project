package Interface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Helpers.SentimentAnalyzer;
import Helpers.UserMessage;

public class Driver {

	public static void main(String[] args) {
		UserInterface UI;
		// Populate with initial data
		try {
			UI = new UserInterface(parse("src/Data/dataset1"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private static List<UserMessage> parse(String filename) throws FileNotFoundException, IOException {
		List<UserMessage> messages = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	// process the line
		    	String[] content = line.split("###");
		    	messages.add(new UserMessage(content[0], content[1]));
		    }
		}
		
		return messages;
	}

}
