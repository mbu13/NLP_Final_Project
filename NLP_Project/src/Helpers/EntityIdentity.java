package Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

public class EntityIdentity {
	private static String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=";
	private static HashMap<String, String> cache = new HashMap<String, String>();
	
	public static String identifyEntity(String entity){
		if(cache.containsKey(entity))
			return cache.get(entity);
		String newEntity;
		try {
			String response = getSearchResult(entity);
			newEntity = getTopSearchResult(response);
			//System.err.println(String.format("Entity: %s => %s",entity, newEntity));
		} catch(Exception e){ 
			newEntity = entity;
		}
		cache.put(entity, newEntity);
		return newEntity;
	}
	
	private static String getTopSearchResult(String response){
		JSONObject json = new JSONObject(response);
		return json.getJSONObject("query").getJSONArray("search").getJSONObject(0).getString("title");
	}
	
	private static String getSearchResult(String entity) throws IOException {
		 URL url = new URL(baseUrl + URLEncoder.encode(entity,"UTF-8"));
		  HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException();
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
	}
}
