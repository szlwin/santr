package santr.gtree.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import santr.v3.parser.TokenString;



public class TerminalData {
	
	private char[] startFlag;
	
	private char[] endFlag;
	
	private String match;
	
	private char[][] ESC;

	private Pattern pattern;
	private Matcher matcher;
	
	private int type;

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		if(match != null){
			this.match = match;
			pattern = Pattern.compile(match);
		}
	}

	public char[][] getESC() {
		return ESC;
	}

	public void setESC(char[][] eSC) {
		ESC = eSC;
	}

	public boolean matcher(TokenString token) {
		if(match == null 
				&& token.getDataType() == this.type){
			return true;
		}
		if(matcher == null){
			matcher = pattern.matcher(token.getText());
		}else{
			matcher.reset(token.getText());
		}
		return matcher.matches();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public char[] getStartFlag() {
		return startFlag;
	}

	public void setStartFlag(char[] startFlag) {
		this.startFlag = startFlag;
	}

	public char[] getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(char[] endFlag) {
		this.endFlag = endFlag;
	}
	
	
}