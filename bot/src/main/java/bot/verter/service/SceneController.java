package bot.verter.service;

import java.io.IOException;

import bot.verter.ConversationContext;

public interface SceneController {

	public boolean enter(ConversationContext conversationContext);

	public SceneController getNextController();

	public String talkBack(ConversationContext conversationContext) throws IOException;
	
	public int confidence();

}
