package bot.verter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static final String QUESTIONS_FILE_EXT = ".questions";

    private static final String ANSWERS_FILE_EXT = ".answers";

    //args:
    //uri https://en.wikipedia.org/wiki/Uberrima_fides
    //dir dir1/dir2
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 4) {
            throw new IllegalArgumentException("args: dir text/url [username] [password]");
        }

        String filesDir = args[0];
        Files.createDirectories(Paths.get(filesDir));

        String url = args[1];
        String sourceText;
        String fileName;
        if (url.startsWith("http")) {
            String username = null;
            if (args.length > 2) {
                username = args[2];
            }
            String password = null;
            if (args.length > 3) {
                password = args[3];
            }
            sourceText = WebTextGrabber.grabText(url, username, password);
            fileName = url.substring(url.lastIndexOf("/"), url.length()) + "_" + System.currentTimeMillis();
        } else {
            sourceText = url;
            fileName = "source" + "_" + System.currentTimeMillis();
        }


        new QuestionPreformer().preform(sourceText,
                path(filesDir, fileName, QUESTIONS_FILE_EXT),
                path(filesDir, fileName, ANSWERS_FILE_EXT));
    }

    private static String path(String dir, String fileName, String extension) {
        return dir + "/" + fileName + extension;
    }

}
