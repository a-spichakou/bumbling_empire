package bot.verter.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bot.verter.model.ConversationContext;
import bot.verter.service.SceneController;
import bot.verter.service.SceneDecisionMaker;

public class ContextSceneDecisionMaker implements SceneDecisionMaker{

	private List<SceneController> sceneControllers = new ArrayList<SceneController>();
	
	public ContextSceneDecisionMaker(){
		sceneControllers.add(new ThemeQuestionAnswerController());
	}
	
	@Override
	public String talkBack(ConversationContext conversationContex) {
		final ArrayList<SceneController> replayCandidates = new ArrayList<SceneController>();
		
		for(SceneController controller: sceneControllers){
			walk(conversationContex, replayCandidates, controller);
		}
		
		Collections.sort(replayCandidates, new Comparator<SceneController>(){
			@Override
			public int compare(SceneController o1, SceneController o2) {
				return o1.confidence()-o2.confidence();
			}
		});
		
		final StringBuilder builder = new StringBuilder();
		if(replayCandidates.size()>0){
			String talkBack = "";
			try {
				talkBack = replayCandidates.get(0).talkBack(conversationContex);
			} catch (IOException e) {
				e.printStackTrace();
			}
			builder.append(talkBack);
		}
		return builder.toString();
	}

	private void walk(ConversationContext conversationContex,
			ArrayList<SceneController> replayCandidates, SceneController controller) {
		boolean enter = controller.enter(conversationContex);
		if(enter){
			replayCandidates.add(controller);
			
			final SceneController nextController = controller.getNextController();
			if(nextController!=null){
				walk(conversationContex, replayCandidates, nextController);
			}
		}
	}

}
