package bot.verter.service.factory;
import it.uniroma1.lcl.adw.ADW;
import bot.verter.service.ConversationEndpoint;
import bot.verter.service.SceneDecisionMaker;
import bot.verter.service.impl.ConsoleConversationEndpoint;
import bot.verter.service.impl.ContextSceneDecisionMaker;

public class ServiceFactory {
	private static ServiceFactory instance = new ServiceFactory();
	
	public static ServiceFactory getInstance(){
		return instance;
	}

	private ConversationEndpoint conversationEndpoint;
	private SceneDecisionMaker sceneDecisionMaker;
	private ADW pipeLine;
	
	public ConversationEndpoint getConversationEndpoint(){
		if(conversationEndpoint==null){
			conversationEndpoint = new ConsoleConversationEndpoint();
		}
		return conversationEndpoint;
	}

	public SceneDecisionMaker getSceneDecisionMaker() {
		if(sceneDecisionMaker==null){
			sceneDecisionMaker = new ContextSceneDecisionMaker();
		}
		return sceneDecisionMaker;
	}
	
	public ADW getADW() {
		if(pipeLine==null){
			pipeLine = new ADW();
		}
		return pipeLine;
	}
}
