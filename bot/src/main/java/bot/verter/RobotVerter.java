package bot.verter;

import bot.verter.service.ConversationEndpoint;
import bot.verter.service.factory.ServiceFactory;
import bot.verter.storage.StorageFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class RobotVerter {

    public static void main(String argv[]) {
        try {
            System.setErr(createLoggingProxy());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    System.in, "utf-8"));

            final ServiceFactory serviceFactory = new ServiceFactory();
            final ConversationEndpoint conversationEndpoint = serviceFactory.getConversationEndpoint();
            StorageFactory storageFactory = StorageFactory.getInstance();
            storageFactory.init();

            String conversationSentence = input.readLine();

            while (conversationSentence != null) {
                storageFactory.tryReindex();
                final String talkBack = conversationEndpoint.talk(null, conversationSentence);

                System.out.println(talkBack);
                conversationSentence = input.readLine();
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static PrintStream createLoggingProxy() throws FileNotFoundException {
        return new PrintStream(new FileOutputStream("verter.log"));
    }

}
