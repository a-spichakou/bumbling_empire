package bot.verter;

public class ConversationContext {
	
	private String pierName;
	
	private String lastSubject;
	private String lastObject;
	private String lastRelation;
	
	private Boolean lastIsQuestion = false;
	
	private Boolean lastIsNegative = false;
	
	private String lastSentiment;
	
	private String sentence;
	
	public void resetAfterResponce(){
		lastIsQuestion = null;
		lastIsNegative = null;
		lastSubject = null;
	}

	@Override
	public String toString() {
		return "ConversationContext [pierName=" + pierName + ", lastSubject="
				+ lastSubject + ", lastObject=" + lastObject
				+ ", lastRelation=" + lastRelation + ", lastIsQuestion="
				+ lastIsQuestion + ", lastIsNegative=" + lastIsNegative
				+ ", lastSentiment=" + lastSentiment + ", sentence=" + sentence
				+ "]";
	}



	public String getLastSubject() {
		return lastSubject;
	}

	public void setLastSubject(String lastSabject) {
		this.lastSubject = lastSabject;
	}

	public boolean isLastIsQuestion() {
		return lastIsQuestion;
	}

	public void setLastIsQuestion(boolean lastIsQuestion) {
		this.lastIsQuestion = lastIsQuestion;
	}

	public boolean isLastIsNegative() {
		return lastIsNegative;
	}

	public void setLastIsNegative(boolean lastIsNegative) {
		this.lastIsNegative = lastIsNegative;
	}

	public String getPierName() {
		return pierName;
	}

	public void setPierName(String pierName) {
		this.pierName = pierName;
	}

	public String getSentiment() {
		return lastSentiment;
	}

	public void setSentiment(String sentiment) {
		this.lastSentiment = sentiment;
	}


	public String getSentence() {
		return sentence;
	}


	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getLastObject() {
		return lastObject;
	}


	public void setLastObject(String lastObject) {
		this.lastObject = lastObject;
	}


	public String getLastRelation() {
		return lastRelation;
	}


	public void setLastRelation(String lastRelation) {
		this.lastRelation = lastRelation;
	}


	public Boolean getLastIsQuestion() {
		return lastIsQuestion;
	}


	public void setLastIsQuestion(Boolean lastIsQuestion) {
		this.lastIsQuestion = lastIsQuestion;
	}


	public Boolean getLastIsNegative() {
		return lastIsNegative;
	}


	public void setLastIsNegative(Boolean lastIsNegative) {
		this.lastIsNegative = lastIsNegative;
	}


	public String getLastSentiment() {
		return lastSentiment;
	}


	public void setLastSentiment(String lastSentiment) {
		this.lastSentiment = lastSentiment;
	}
	
	
	
}
