package bot.verter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import bot.verter.service.ConversationEndpoint;
import bot.verter.service.factory.ServiceFactory;

public class RobotVerter {
	
	public static void main(String argv[]) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in, "utf-8"));
		
		final ServiceFactory serviceFactory = new ServiceFactory();
		final ConversationEndpoint conversationEndpoint = serviceFactory.getConversationEndpoint();
		
		String conversationSentence = input.readLine();

		while (conversationSentence != null) {
			
			final String talkBack = conversationEndpoint.talk(null, conversationSentence);
			
			System.out.println("Robot Verter: " +talkBack);
			conversationSentence = input.readLine();
		}
	}

}
