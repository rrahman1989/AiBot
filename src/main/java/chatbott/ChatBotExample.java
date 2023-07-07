package chatbott;

import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.Sraix;
import org.alicebot.ab.utils.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ChatBotExample {
    public static void main(String[] args) {
        try {
            String resourcePath = getpath();
            MagicBooleans.trace_mode = false;
            Bot bot = new Bot("super", resourcePath);
            Chat chatSession = new Chat(bot);
            String textline = "";

            while (true) {
                System.out.print("YOU: ");
                textline = IOUtils.readInputTextLine();

                if (textline == null || textline.length() < 1) {
                    textline = MagicStrings.null_input;
                } else if (textline.equals("q")) {
                    System.exit(0);
                } else if (textline.equals("wq")) {
                    bot.writeQuit();
                    System.exit(0);
                } else {
                    String request = textline;
                    String response = AIMLProcessor.respond(request, bot.name, "user", chatSession);

                    if (response.contains("<sraix")) {
                        response = performSraix(request, bot, chatSession);
                    }

                    System.out.println("BOT: " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getpath() {
        String path = System.getProperty("user.dir");
        String resourcePath = path + "/src/main/resources";
        return resourcePath;
    }

    private static String performSraix(String request, Bot bot, Chat chatSession) {
        try {
            String url = Sraix.sraixPannous(request, bot.name, chatSession);
            HttpClient httpClient = createHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream inputStream = response.getEntity().getContent();
                String responseText = extractResponseText(inputStream);
                return responseText != null ? responseText : MagicStrings.default_bot_response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return MagicStrings.default_bot_response;
    }
    
    private static String extractResponseText(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Implement your logic here to extract the response text from the XML
            // For example, you can use XPath or DOM traversal to locate the desired element
            // In this example, we assume the response text is contained in a "<result>" tag
            NodeList resultNodes = document.getElementsByTagName("result");
            if (resultNodes.getLength() > 0) {
                Node resultNode = resultNodes.item(0);
                String responseText = resultNode.getTextContent().trim();
                return responseText;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if the response text cannot be extracted
    }
    
    private static HttpClient createHttpClient() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }}, new SecureRandom());

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setSSLContext(sslContext);
        httpClientBuilder.setSSLHostnameVerifier((hostname, session) -> true); // Allow all hostnames
        return httpClientBuilder.build();
    }
}