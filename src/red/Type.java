package red;

import java.util.regex.Pattern;

public enum Type {
	UNKNOWN(null, null, null, null),
	
	//match: scr_1_BOTTOM.
	NINJAHAX(
		Pattern.compile("scr_(\\d+)_(BOTTOM|TOP_LEFT|TOP_RIGHT)\\."), 
		Pattern.compile("TOP_RIGHT"),
		Pattern.compile("BOTTOM"),
		new int[] {1}
	),
	
	//match: bot_0001.
	NTR(
		Pattern.compile("(bot|top|sup|inf)_(\\d+)\\."),
		null,
		Pattern.compile("(bot|inf)"),
		new int[] {2}
	), 
	
	//match: 20161206_0800_LO_00000.
	TESTMENU(
		Pattern.compile("(\\d+)_(\\d+)_(UL|UR|LO)_(\\d+)\\."),
		Pattern.compile("UR"),
		Pattern.compile("LO"),
		new int[] {1, 2, 4}
	),
	
	//match: 2018-10-12_21-17-00.723_top.
	LUMA(
		Pattern.compile("(\\d+)-(\\d+)-(\\d+)_(\\d+)-(\\d+)-(\\d+)\\.(\\d+)_(top|bot)\\."),
		null,
		Pattern.compile("bot"),
		//I don't know if the last digits are unique, so I'll assume the whole thing is the ID
		//If someone wants to change this in the future, please create a PR.
		new int[] {1, 2, 3, 4, 5, 6, 7} 
	);
	
	//The pattern that matches the file
	public final Pattern IS;
	
	//Should return true if the image is a right image
	public final Pattern RIGHT;
	
	//Should return true if the image is a bottom image
	public final Pattern BOT;
	
	//A list of groups that represents the ID
	public final int[] GROUP;
	
	Type(Pattern pattern, Pattern right, Pattern bot,int[] group) {
		this.IS = pattern;
		this.RIGHT = right;
		this.BOT = bot;
		this.GROUP = group;
	}
}
