package santr.v4.parser;

import santr.common.util.collections.SimpleList;



public class TokenStream {
	
	public static final int TYPE_STR = 1;

	public static final int TYPE_TOKEN = 2;

	private SimpleList<TokenString> tokenList = new SimpleList<TokenString>(10,6);
	
	private int cIndex = 0;
	
	private int lIndex = -1;
	
	private TokenString last;
	
	public TokenStream(){

	}
	
	public int getSize(){
		return tokenList.size();
	}
	
	public TokenString getNext(int index){
		return tokenList.get(index);
	}
	
	public boolean haseNext(){
		return cIndex < tokenList.size();
	}
	
	public int getCurrentIndex(){

		return cIndex;
	}
	
	public int getLexerIndex(){

		return lIndex;
	}
	
	public TokenString getNext(){
		//if(cIndex == tokenList.size()-1){
		//	return null;
		//}
		TokenString tokenStr = tokenList.get(cIndex);
		if(tokenStr != null){
			System.out.print(tokenStr.getText()+" ");
		}
		
		cIndex++;

		return tokenStr;
	}
	
	public TokenString getLast(){
		//TokenString tokenStr = tokenList.get(tokenList.size()-1);
		return last;
	}
	
	public void setLexerIndex(int index){
		//System.out.println("index:"+index);
		this.lIndex = index;
	}
	
	protected void add(TokenString tokenStr){
		last = tokenStr;
		tokenList.add(tokenStr);
	}
	
	//public List<TokenStr> get(int start,int end){
	//	List<TokenStr> subTokenList = new ArrayList<TokenStr>();

	//	return subTokenList;
	//}
}
