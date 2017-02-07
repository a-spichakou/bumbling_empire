package bot.verter.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import bot.verter.model.ConversationContext;
import bot.verter.service.ConversationEndpoint;
import bot.verter.service.factory.ServiceFactory;

public class ConsoleConversationEndpoint implements ConversationEndpoint{
	
	private Map<String, ConversationContext> conversations = new HashMap<String, ConversationContext>();
	private ServiceFactory serviceFactory = ServiceFactory.getInstance();
	
	public ConsoleConversationEndpoint() {

    }

	public String talk(String session, String sentence) {
		ConversationContext conversationContext = conversations.get(session);
		if(conversationContext==null){
			conversationContext = new ConversationContext();
			conversations.put(session, conversationContext);
		}

		if(sentence==null || sentence.isEmpty()){
			return StringUtils.EMPTY;
		}
		
		conversationContext.setSentence(sentence);
		String talkBack = serviceFactory.getSceneDecisionMaker().talkBack(conversationContext);
		
		return talkBack;
	}

}
