package Helpers;

public class UserMessage {
	
	private String name;
	private String message;
	private Timestamp time;

	public UserMessage(String name) {
		this.name = name;
	}
	
	public UserMessage(String name, String message) {
		this.name = name;
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public String toString() {
		return name + " " + message;
	}
}
