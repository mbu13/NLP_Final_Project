package Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Helpers.NLPAnalyzer.NounToken;

public class Conversation {
	private static final double IN_CONVO = 1.0;
	private static final double NOT_IN_CONVO = 0.7;
	
	private static final double METADATA_WEIGHT = 0.1;
	private static final double COREF_WEIGHT = 0.3;
	private static final double TOPIC_WEIGHT = 0.6;
	
	private static final double NEW_CONVO_BREAKPOINT = 0.7;
	
	private Map<String,Subconversation> _subconvos;
	private List<UserMessage> _msgs;
	private NLPAnalyzer _coref;
	
	private void init(){
		_subconvos = new HashMap<String,Subconversation>();
		_msgs = new ArrayList<UserMessage>();
		
		//Init coreferencer
		_coref = new NLPAnalyzer();
		_coref.init();
	}
	public Conversation(){
		init();
	}
	
	public void addMessage(UserMessage msg){
		if (_subconvos.size() == 0){
			// If there are no sub-conversations yet, create a new conversation and add the message to it
			_msgs.add(msg);
			Subconversation newConvo = new Subconversation();
			newConvo.addMessage(msg);
			String topic = _coref.getTopics(msg.getMessage()).get(0).getTopic();
			_subconvos.put(topic,newConvo);
		}else{
			//Otherwise, calculate probabilities for each sub-conversation and pick the best fit.
			HashMap<String,Double> probs = new HashMap<String,Double>();
			for(String topic : _subconvos.keySet()){
				probs.put(topic,calculateProbability(msg,_subconvos.get(topic)));
			}
			//Pick the best conversation to add the message to
			String maxTopic = "";
			double max = 0.0;
			for(String topic : _subconvos.keySet()){
				if (max == 0.0){
					max = probs.get(topic);
					maxTopic = topic;
				}
				if (probs.get(topic) > max){
					max = probs.get(topic);
					maxTopic = topic;
				}
			}
			_msgs.add(msg);
			//If all the probabilities are low, it is probably a new conversation
			if (probs.get(maxTopic) < NEW_CONVO_BREAKPOINT){
				Subconversation newConvo;
				String topic = _coref.getTopics(msg.getMessage()).get(0).getTopic();
				if(_subconvos.get(topic) == null)
					newConvo = new Subconversation();
				else
					newConvo = _subconvos.get(topic);
				newConvo.addMessage(msg);
				
				_subconvos.put(topic,newConvo);
			}else{	
				_subconvos.get(maxTopic).addMessage(msg);
			}
		}
	}
	
	private double calculateProbability(UserMessage msg, Subconversation sc){
		// Chat metadata
		// If someone is already part of one conversation, they are likely to reply to that conversation
		double metadata = sc.getSpeakers().contains(msg.getName()) ? IN_CONVO : NOT_IN_CONVO;

		// Coreference Links
		// If there are coreference links from message into another, then there is a chance the two messages
		// are part of the same conversation
		double corefl = calculateCoreference(msg,sc);
		
		// Topics
		// Two messages that have the same topic are highly likely part of the same conversation
		double similartopics = calculateTopicSimilarity(msg,sc);
		//System.out.println(msg + " " + corefl + " " + similartopics);
		return METADATA_WEIGHT * metadata + COREF_WEIGHT * corefl + TOPIC_WEIGHT * similartopics;
	}
	
	private double calculateTopicSimilarity(UserMessage msg, Subconversation sc){
		List<NounToken> msgTopics = _coref.getTopics(msg.getMessage());
		List<UserMessage> convomsgs = sc.getMessages();
		String convo = "";
		for(UserMessage m : convomsgs){
			convo += m.getMessage() + " ";
		}
		List<NounToken> convoTopics = _coref.getTopics(convo);

		
		// Calculate the distance between two score using the distance formula
		// 		distance = sqrt(sum((x_i)^2))

		HashMap<String,Double> topicDiffs = new HashMap<String,Double>();		
		for(NounToken topic : msgTopics){
			topicDiffs.put(topic.getTopic(),topic.getScore());
		}
		for(NounToken topic : convoTopics){
			if(topicDiffs.containsKey(topic.getTopic()))
				topicDiffs.put(topic.getTopic(),topicDiffs.get(topic.getTopic()) - topic.getScore());
			else
				topicDiffs.put(topic.getTopic(), topic.getScore());	
		}
		
		double sum = 0.0;
		for(String key : topicDiffs.keySet())
			sum += topicDiffs.get(key) * topicDiffs.get(key);	
		// Take the sin of the distance
		return Math.pow(Math.sin(Math.sqrt(sum) * Math.PI /2),4);
	}
	private double calculateCoreference(UserMessage msg,Subconversation sc){
		List<UserMessage> oldmsgs = sc.getMessages();
		// Get last three messages if possible
		String convo = "";
		int start = oldmsgs.size() - 8;
		if(start < 0) {start = 0;}
		for(int i = start;i < oldmsgs.size();i++){
			convo += oldmsgs.get(i).getMessage() + " ";
		}
		return _coref.coreference(convo,msg.getMessage());
	}
	
	public void printSubconversations(){
		String ret = "";
		for(String topic : _subconvos.keySet()){
			ret += "Subconversation " + topic + "\n";
			ret += getSubconversation(topic);
		}
		System.out.println(ret);
	}
	
	public void printMessages(){
		String ret = "Messages:\n";
		for(int i = 0;i < _msgs.size();i++){
			ret += _msgs.get(i) + "\n";
		}
		System.out.println(ret);
	}
	
	public Subconversation getSubconversation(String topic){ return _subconvos.get(topic);	}
	
	public Set<String> getFilters(){ return _subconvos.keySet();	}
}
