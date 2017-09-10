package santr.gtree.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import santr.v4.parser.TokenString;



public class TerminalData {
	
	private ThreadLocal<Matcher> matcherLocal = new ThreadLocal<Matcher>();
	
	private char[] startFlag;
	
	private char[] endFlag;
	
	private String match;
	
	private char[][] ESC;

	private Pattern pattern;
	//private Matcher matcher;
	
	private int type;

	private boolean isMatch = false;
	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		if(match != null){
			isMatch = true;
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
		if(!isMatch){
			return token.getDataType() == this.type;
		}else{
			Matcher matcher = matcherLocal.get();
			if(matcher == null){
				matcher = pattern.matcher(token.getText());
				matcherLocal.set(matcher);
			}
			matcher.reset(token.getText());
			return matcher.matches();
			//if(matcher == null){
				//matcher = pattern.matcher(token.getText());
			//}else{
			//	matcher.reset(token.getText());
			//}
			//return matcher.matches();
			//return pattern.matcher(token.getText()).matches();
		}

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
