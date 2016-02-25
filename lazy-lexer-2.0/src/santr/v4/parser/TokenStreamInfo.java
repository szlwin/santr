package santr.v4.parser;

import santr.v4.parser.TokenString;

public class TokenStreamInfo {
	
	public static final int TYPE_STR = 1;

	public static final int TYPE_TOKEN = 2;

	//private Queue<TokenString> tokenQueue = new ArrayDeque<TokenString>();
	//private List<TokenString> tokenList = new ArrayList<TokenString>();
	private TokenString tokenStringArr[];
	
	private int cIndex = 0;
	
	private int lIndex = -1;
	
	private int size = 0;
	private TokenString last;
	private TokenString head;
	private TokenString current;
	
	public boolean isFinish = false;
	public TokenStreamInfo(){
		head = new TokenString();
		current = head;
	}
	
	public int getSize(){
		return size;
	}
	
	public TokenString getNext(int index){
		if(index < 0){
			return null;
		}

		return tokenStringArr[index];
		//return tokenList.get(index);
	}
	
	public int getCurrentIndex(){

		return cIndex;
	}
	
	public int getLexerIndex(){

		return lIndex;
		
	}
	// TokenString getNext(){
	//	System.out.print("qwqqqwwq:");
	//	return null;
	//}
	
	public TokenString getNext(){

		if(tokenStringArr == null){
			//size = tokenQueue.size();
			tokenStringArr = new TokenString[size];
			//tokenQueue.toArray(tokenStringArr);
			current = head.next;
		}
		if(cIndex == size){
			return null;
		}
		
		//TokenString tokenStr = tokenList.get(cIndex);
		TokenString tokenStr = current;
		tokenStringArr[cIndex] = tokenStr;
		current =current.next;
		//if(tokenStr != null){
		//	System.out.print(tokenStr.getText()+" ");
		//}
		
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
		
		current.next = tokenStr;
		current = tokenStr;
		
		last = tokenStr;

		//tokenList.add(tokenStr);
		//tokenQueue.add(tokenStr);
		size++;
	}
	public TokenString getNextOne(){
		if(current==null){
			return null;
		}
		return current;
	}
	//public List<TokenStr> get(int start,int end){
	//	List<TokenStr> subTokenList = new ArrayList<TokenStr>();

	//	return subTokenList;
	//}
}
