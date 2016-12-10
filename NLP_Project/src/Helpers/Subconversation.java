package Helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subconversation {
	private Set<String> _speakers;
	private List<UserMessage> _msgs;
	
	private void init(){
		_speakers = new HashSet<String>();
		_msgs = new ArrayList<UserMessage>();
	}
	
	public Subconversation(){
		init();
	}
	
	public void addMessage(UserMessage msg){
		_msgs.add(msg);
		_speakers.add(msg.getName());
	}
	
	public List<UserMessage> getMessages(){ return new ArrayList<UserMessage>(_msgs); }
	public Set<String> getSpeakers() { return new HashSet<String>(_speakers); }
	
	public String toString(){
		String ret = "";
		for(int i = 0;i < _msgs.size();i++){
			ret += "    " + _msgs.get(i) + "\n";
		}
		return ret;
	}
}
