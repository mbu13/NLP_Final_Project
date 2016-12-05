package Helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentimentEmojis {
	
	private Map<String, List<String>> sentimentToEmojis;
	
	public SentimentEmojis() {
		sentimentToEmojis = new HashMap<>();
		try {
			sentimentToEmojis.put("happy", getEmojiFileNames("happy"));
			sentimentToEmojis.put("neutral", getEmojiFileNames("neutral"));
			sentimentToEmojis.put("sad", getEmojiFileNames("sad"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String> getEmojiFileNames(String sentiment) throws FileNotFoundException, IOException {
		List<String> result = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader("src/Data/" + sentiment))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       result.add(line);
		    }
		}
		
		return result;
	}
	
	public List<String> getEmojiNames(String sentiment) {
		return Collections.unmodifiableList(sentimentToEmojis.get(sentiment));
	}

}
