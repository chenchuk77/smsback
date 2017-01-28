package net.devopskb.smsback.rules;

// builder class to build Rule object, also provides method to build a rule from JSON
public class RuleBuilder{

	// "-" operator match any, and is default
	private int priority;
	private String matchAddress = "-";
	private String matchContent = "-";
	private String replyTo;
	private String addTimestamp = "-";
	private String replaceContent = "-";

	public RuleBuilder setPriority(int priority){
		this.priority = priority;
		return this;
	}

	public RuleBuilder matchAddress(String matchAddress){
		this.matchAddress = matchAddress;
		return this;
	}
	public RuleBuilder matchContent(String matchContent){
		this.matchContent = matchContent;
		return this;
	}
	public RuleBuilder replyTo(String replyTo){
		this.replyTo = replyTo;
		return this;
	}
	public RuleBuilder addTimestamp(String addTimestamp){
			this.addTimestamp = addTimestamp;
			return this;
	}
	public RuleBuilder replaceContent(String replaceContent){
			this.addTimestamp = addTimestamp;
			return this;
	}

	// {"addTimestamp":"+","matchAddress":"4444","matchContent":"-","priority":4,"replaceContent":"-","replyTo":"4444"}
	public Rule createFromJsonString(String JsonRule){
		return new Rule(JsonRule);
	}

	// return a rule instance from this builder object
	public Rule create(){
		return new Rule(priority, matchAddress, matchContent, replyTo, addTimestamp, replaceContent);
	}
	
}