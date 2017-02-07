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
        if (args.length != 2) {
            throw new IllegalArgumentException("args: url dir");
        }
        String filesDir = args[1];
        String fileName = fileName(filesDir);
        String sourceText = WebTextGrabber.grabText(args[0], null, null);

        new QuestionPreformer().preform(sourceText,
                path(filesDir, fileName, QUESTIONS_FILE_EXT),
                path(filesDir, fileName, ANSWERS_FILE_EXT));
    }

    private static String path(String dir, String fileName, String extension) {
        return dir + "/" + fileName + extension;
    }

    private static String fileName(String filesDir) throws IOException {
        List l = new LinkedList<Path>();
        Files.createDirectories(Paths.get(filesDir));
        Files.newDirectoryStream(Paths.get(filesDir), path -> path.toString().endsWith(QUESTIONS_FILE_EXT))
                .forEach(l::add);
        return String.valueOf(l.size() + 1);
    }

}
