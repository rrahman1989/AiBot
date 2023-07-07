package chatbott;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatGoogle {
	


    public static void main(String[] args) {
        MagicBooleans.trace_mode = false;
        Bot bot = new Bot("super", "config");
        Chat chat = new Chat(bot);

        String input = "what is the weather";

        String response = chat.multisentenceRespond(input);
        System.out.println("Bot: " + response);

        System.out.println("We are at top");
        
        if (response.startsWith("<oob>")) {
            OOBProcessor oobProcessor = new OOBProcessor(bot, chat);
            String oobResponse = oobProcessor.respond(response);
            System.out.println("Bot (OOB): " + oobResponse);
        }else {
        	System.out.println("Top of else");
        	OOBProcessor oobProcessor = new OOBProcessor(bot, chat);
        	System.out.println("before of else");
            String oobResponse = oobProcessor.respond(response);
            System.out.println("Else Bot (OOB): " + oobResponse);
        }
    }

    public static class OOBProcessor {
        private Bot bot;
        private Chat chat;

        public OOBProcessor(Bot bot, Chat chat) {
            this.bot = bot;
            this.chat = chat;
        }

        public String respond(String response) {
        	System.out.println("Inside Respond");
            if (response.startsWith("<oob>")) {
                response = response.replace("<oob>", "").replace("</oob>", "");
                if (response.toLowerCase().startsWith("search google")) {
                	System.out.println("Inside Respond");
                    String searchQuery = response.substring(14); // Extract the search query
                    String searchResults = performGoogleSearch(searchQuery);
                    return searchResults;
                }
            }else {
            	System.out.println("Else responds");
            	response = response.replace("<oob>", "").replace("</oob>", "");

                	System.out.println("Inside Respond");
                    String searchQuery = response.substring(14); // Extract the search query
                    System.out.println("Query: " + searchQuery);
                    String searchResults = performGoogleSearch("weather");
                    System.out.println("search result: " + searchResults);
                    return searchResults;
            
        }
            return "";
        }

        private String performGoogleSearch(String searchQuery) {
            String googleSearchUrl = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + CSE_ID + "&q=" + searchQuery;

            System.out.println("URL: " + googleSearchUrl);

            try {
                // Open a connection to the Google search URL
                URL url = new URL(googleSearchUrl);
                URLConnection connection = url.openConnection();

                // Read the content of the URL response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                // Process the response data as needed
                String responseData = responseBuilder.toString();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(responseData);
                JSONArray itemsArray = jsonResponse.getJSONArray("items");

                StringBuilder resultBuilder = new StringBuilder();

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemObject = itemsArray.getJSONObject(i);
                    String title = itemObject.getString("title");
                    String snippet = itemObject.getString("snippet");
                    
                    
                    	// Append the extracted data to the result string
                        resultBuilder.append("Title: ").append(title).append("\n");
                        resultBuilder.append("Snippet: ").append(snippet).append("\n\n");
                        
                   

                    
                }

                // Return the formatted result string
                return resultBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Return an empty string if an error occurred
            return "";
        }
    }

}
