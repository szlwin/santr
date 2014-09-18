package santr.gtree.model;

import santr.gtree.model.enume.TOKENTYPE;

public class TokenChar {

	private int id;
	
	private char[] tokenCharArr;
	
	private TOKENTYPE type;

	private String tokenStr;
	
	public TokenChar(){
		
	}
	
	
	public TokenChar(String str, char[] tokenCharArr,TOKENTYPE type){
		this.tokenCharArr = tokenCharArr;
		this.tokenStr = str;
		this.type = type;
	}
	
	public char[] getTokenCharArr() {
		return tokenCharArr;
	}

	public void setTokenCharArr(char[] tokenCharArr) {
		this.tokenCharArr = tokenCharArr;
	}

	public TOKENTYPE getType() {
		return type;
	}

	public void setType(TOKENTYPE type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTokenStr() {
		return tokenStr;
	}
	
	
}
