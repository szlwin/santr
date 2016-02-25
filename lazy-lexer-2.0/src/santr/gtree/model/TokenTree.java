package santr.gtree.model;

import javolution.util.FastMap;

public class TokenTree {

	private FastMap<Integer,TokenTree> tokenTreeMap = new FastMap<Integer,TokenTree>();
	
	private TokenChar tokenChar;

	private boolean isLeaf = false;
	
	public void addTree(TokenChar tokenChar){
		
		char[] charArr = tokenChar.getTokenCharArr();
		int index = 0;
		this.addChar(charArr, index, tokenChar);
	}

	public TokenChar getTokenChar() {
		return tokenChar;
	}

	public void setTokenChar(TokenChar tokenChar) {
		this.isLeaf = true;
		this.tokenChar = tokenChar;
	}
	
	public TokenTree getNext(char charT){
		return tokenTreeMap.get((int) charT);
		
	}
	protected void addChar(char[] charArr,int index,TokenChar tokenChar){

		char charT = charArr[index];

		if(!tokenTreeMap.containsKey((int)charT)){
			TokenTree tokenTree = new TokenTree();
			tokenTreeMap.put((int) charT, tokenTree);
		}
		
		if(index == charArr.length-1){
			tokenTreeMap.get((int) charT)
				.setTokenChar(tokenChar);
			return;
		}
		index++;
		tokenTreeMap.get((int) charT)
			.addChar(charArr, index, tokenChar);
	}

	public boolean isLeaf() {
		return isLeaf;
	}
	
}
