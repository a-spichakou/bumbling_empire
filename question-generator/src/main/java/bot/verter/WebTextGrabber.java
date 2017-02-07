package bot.verter;

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
            //noinspection ImplicitArrayToString
            //connection.header("Authorization", "Basic " + encodeBase64((username + ":" + password).getBytes()));
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