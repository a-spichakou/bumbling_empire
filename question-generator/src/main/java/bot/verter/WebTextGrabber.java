package bot.verter;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 * Utility to retrieve text content from web.
 */
public final class WebTextGrabber {

    private WebTextGrabber() {

    }

    public static String grabText(final String url, final String username, final String password) {
        assert url != null;
        Connection connection = Jsoup.connect(url);
        if (username != null && password != null) {
            String login = username + ":" + password;
            String base64login = new String(Base64.encodeBase64(login.getBytes()));
            connection.header("Authorization", "Basic " + base64login);
        }
        Document doc = get(connection);
        Element contentElement = doc.select("div[id=content]").first();
        String text = Jsoup.clean(contentElement.toString(), Whitelist.none());
        text = text.replaceAll("/", "").replaceAll("\\[(.*?)\\]", "");
        return text;
    }

    private static Document get(Connection connection) {
        try {
            return connection.get();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}