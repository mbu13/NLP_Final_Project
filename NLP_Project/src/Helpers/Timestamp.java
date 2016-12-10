package Helpers;

public class Timestamp {

	private int[] format;
	
	public Timestamp(String time) {
		// 00:00:00:00:00
		
		String[] split = time.split(":");
		format = new int[split.length];
		
		for(int i = 0; i < split.length; ++i) {
			format[i] = Integer.parseInt(split[i]);
		}
	}
	
	public int getMilliseconds() {
		return format[format.length - 1];
	}
	
	public int getSeconds() {
		return format[format.length - 2];
	}
	
	public int getMinutes() {
		return format[format.length - 3];
	}
	
	public int getHours() {
		return format[format.length - 4];
	}
	
	public int getDays() {
		return format[format.length - 5];
	}
}
