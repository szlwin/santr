package santr.v4.parser;

import santr.gtree.model.BData;
import santr.gtree.model.TerminalData;
import santr.gtree.model.TokenChar;
import santr.gtree.model.TokenTree;
import santr.gtree.model.enume.TOKENTYPE;


public class ExpressLexer {
	
	private TokenStreamInfo tokenStreamInfo = new TokenStreamInfo();
	
	public ExpressLexer() {
	}
	
	public ExpressLexer(TokenChar tokenCharArr[],char[] expressCharArr,TerminalData[] node){
		this.indexEnd = expressCharArr.length;
		this.node = node;
		this.expressCharArr = expressCharArr;
		this.tokenCharArr = tokenCharArr;
		tokenCharArray = new char[tokenCharArr.length][];
		
		for(int i = 0;i < tokenCharArr.length;i++){
			tokenCharArray[i] = tokenCharArr[i].getTokenCharArr();
		}
	}
	
	public TokenStreamInfo getTokenStream(){
		return tokenStreamInfo;
	}
	private TokenChar tokenCharArr[];
	
	private char tokenCharArray[][];
	
	private char[] expressCharArr;
	
	private TerminalData[] node;
	
	//private int flowCount = 0;
	
	private int index = 0;
	
	private int pos = 0;
	
	private int nextPos = 0;

	private int startPos = 0;
	
	private boolean isFlag = false;
	
	private int indexEnd;
	
	public TokenStreamInfo lexer(TokenTree tokenTree,int indexStart,int indexEnd,char[] expressCharArr,TerminalData[] node,char[] ws){
		//TokenTree tmpTokenTree = null;
		TokenChar tokenChar = null;
		TokenStreamInfo tokenStreamInfo = new TokenStreamInfo();
		//char tokenCharArray[][] = new char[tokenCharArr.length][];
		
		//for(int i = 0;i < tokenCharArr.length;i++){
		//	tokenCharArray[i] = tokenCharArr[i].getTokenCharArr();
		//}
		int flowCount = 0;
		int index = 0;
		int pos = indexStart;
		int nextPos = indexStart;
	
		int startPos = indexStart;
		
		boolean isFlag = false;
		TerminalData tmpNode = null;
		//boolean isEnd = false;
		int wsIndex = -1;
		while(pos< indexEnd && nextPos<indexEnd){
			//int count = -1;
			pos = nextPos;
			//boolean isCan = false;
			
			if(isFlag){
				flowCount = checkEnd(expressCharArr,tmpNode.getEndFlag(),flowCount,pos);
				TokenString tokenStr = new TokenString();
				
				tokenStr.setStart(pos);
				tokenStr.setEnd(pos+flowCount);
				tokenStr.setType(TOKENTYPE.STR);
				tokenStr.setIndex(index);
				tokenStr.setText(String.valueOf(expressCharArr, pos, flowCount-tmpNode.getEndFlag().length));
				tokenStr.setDataType(tmpNode.getType());
				tokenStreamInfo.add(tokenStr);
				
				index++;
				startPos = nextPos = pos+flowCount;
				isFlag = false;
				flowCount = 0;
				continue;
			}
			
			if(!isFlag){
				TokenTree tmpTokenTree = tokenTree;
				tokenChar = null;
				while(tmpTokenTree!=null){
					if(tmpTokenTree.isLeaf()){
						TokenTree next = null;
						if(pos+flowCount<indexEnd){
							next = tmpTokenTree.getNext(expressCharArr[pos+flowCount]);
						}
						if(next != null){
							tmpTokenTree = next;
							flowCount++;
							continue;
						}else{
							tokenChar = tmpTokenTree.getTokenChar();
							break;
						}

					}
					if(!(pos+flowCount<indexEnd)){
						break;
					}
					tmpTokenTree = tmpTokenTree.getNext(expressCharArr[pos+flowCount]);
					flowCount++;
				}
				

				if(tokenChar != null){
					if(tokenChar.isKeyWord()
							&& pos+flowCount<indexEnd){						if((pos !=0 && tokenStreamInfo.getLast()==null)){
						if((pos !=0 && tokenStreamInfo.getLast()==null)){
							nextPos = pos+flowCount;
							flowCount = 0;
							
							continue;
						}
						
						TokenString tokenString = tokenStreamInfo.getLast();
						if((pos-1 != wsIndex && tokenString.getType()!=TOKENTYPE.TOKEN)
								|| ws[expressCharArr[pos+flowCount]]=='\0'){
							startPos = pos;
							nextPos = pos+flowCount;
							flowCount =0;
							continue;
						}
					}
					if(startPos != pos){
						TokenString tokenStr = new TokenString();
						tokenStr.setStart(startPos);
						
						tokenStr.setEnd(pos);
						tokenStr.setType(TOKENTYPE.STR);
						
						tokenStr.setIndex(index);
						tokenStr.setText(getString(expressCharArr,startPos, pos-startPos));
						setDataType(tokenStr);
						//tokenStr.setId(-1);
						tokenStreamInfo.add(tokenStr);
						//treeWorker.work(tokenStr);
						//System.out.println(String.valueOf(expressCharArr,tokenStr.getStart(),tokenStr.getEnd()-tokenStr.getStart()));
						index++;
					}

					if(tokenChar.getType() == TOKENTYPE.TOKEN){
					
						//System.out.println(String.valueOf(expressCharArr,pos,count));
						TokenString tokenRtree = new TokenString();
						tokenRtree.setStart(pos);
						//TFlag tFlag = flagMap.get(token);
						tokenRtree.setType(TOKENTYPE.TOKEN);
						tokenRtree.setId(tokenChar.getId());
						//tokenRtree.setName(tFlag.getTokenFlag());
						tokenRtree.setEnd(pos+flowCount);
						tokenRtree.setIndex(index);
						//tokenRtree.setId(j);
						tokenRtree.setText(tokenChar.getTokenStr());
						tokenStreamInfo.add(tokenRtree);
						//treeWorker.work(tokenRtree);
						index++;
						
					}else if(tokenChar.getType() == TOKENTYPE.WS){
						//pos = pos;
						pos = pos+skipWS(pos+1,expressCharArr,ws);
						wsIndex = pos;
						//int start = pos++;
					}
					if(tokenChar.isKeyWord()){
						int next = pos+flowCount+1;
						startPos = nextPos = next+skipWS(next,expressCharArr,ws);
						//startPos = nextPos = next;
						wsIndex = nextPos-1;
					}else{
						startPos = nextPos = pos+flowCount;
					}
					
					//isCan = true;
					flowCount =0;
					//break;
					
					continue;
				}else{
					
					if(node !=null){
						for(int k = 0; k< node.length;k++){
							char startChar[] = node[k].getStartFlag();
							if(startChar == null){
								continue;
							}
							int sCount = 0;
							while(sCount < startChar.length
									&& expressCharArr[pos+sCount] == startChar[sCount]){
								sCount++;
							}
							if(sCount == startChar.length){
								
							//if(node[k].getStartFlag() == expressCharArr[pos]){
								tmpNode = node[k];
								isFlag = true;
								nextPos = pos+sCount;
								break;
							}
						}
					}
					
					if(!isFlag){
						nextPos = pos +1;
						//count = -1;
						flowCount = 0;
					}
				}
			}
			

			
			//if(isFlag){
			//	continue;
			//}
			
			//for(int j = 0;j < tokenCharArr.length;j++){
				
				//String token = tokenStringkArr[j];
				//char[] tokenArr = tokenCharArr[j].getTokenCharArr();
				//char[] tokenArr = tokenCharArray[j];
				//int tokenSize = tokenArr.length;
			

			//}

			//if(!isCan){
			//	nextPos = pos +1;
			//}
			
			
		}

		TokenString tokenStrLast = tokenStreamInfo.getLast();
		if(tokenStrLast.getEnd() < indexEnd){
			TokenString tokenStr = new TokenString();
			tokenStr.setStart(startPos);
			
			tokenStr.setEnd(indexEnd);
			tokenStr.setType(TOKENTYPE.STR);
			tokenStr.setText(getString(expressCharArr,startPos,indexEnd-startPos));
			tokenStr.setIndex(index);
			setDataType(tokenStr);
			//tokenStr.setId(-1);
			tokenStr.setLast(true);
			tokenStreamInfo.add(tokenStr);
			//treeWorker.work(tokenStr);
			//System.out.println(String.valueOf(expressCharArr,tokenStr.getStart(),tokenStr.getEnd()-tokenStr.getStart()));
		}else{
			tokenStrLast.setLast(true);
		}
		
		return tokenStreamInfo;
	}
	
	public TokenStreamInfo lexer(TokenChar tokenCharArr[],int indexStart,int indexEnd,char[] expressCharArr,TerminalData[] node){
		
		TokenStreamInfo tokenStreamInfo = new TokenStreamInfo();
		char tokenCharArray[][] = new char[tokenCharArr.length][];
		
		for(int i = 0;i < tokenCharArr.length;i++){
			tokenCharArray[i] = tokenCharArr[i].getTokenCharArr();
		}
		int flowCount = 0;
		int index = 0;
		int pos = indexStart;
		int nextPos = indexStart;
	
		int startPos = indexStart;
		
		boolean isFlag = false;
		TerminalData tmpNode = null;
		while(pos< indexEnd && nextPos<indexEnd){
			int count = 0;
			pos = nextPos;
			boolean isCan = false;
			
			if(isFlag){
				flowCount = checkEnd(expressCharArr,tmpNode.getEndFlag(),flowCount,pos);
				//int eCount = 0;
				
				//char[] endChar = tmpNode.getEndFlag();

				//while(expressCharArr[pos+flowCount] != endChar[0]){
					//System.out.println(expressCharArr[pos+flowCount]);
				//	flowCount++;
				//}
				
				//while(expressCharArr[pos+flowCount+eCount] == endChar[1]){
				//	eCount++;
				//	flowCount++;
				//}
				
				//if(eCount+1 != endChar.length){
				//	eCount = 0;

				//}
				TokenString tokenStr = new TokenString();
				
				tokenStr.setStart(pos);
				tokenStr.setEnd(pos+flowCount);
				tokenStr.setType(TOKENTYPE.STR);
				tokenStr.setIndex(index);
				tokenStr.setText(String.valueOf(expressCharArr, pos, flowCount-tmpNode.getEndFlag().length));
				tokenStr.setDataType(tmpNode.getType());
				tokenStreamInfo.add(tokenStr);
				
				index++;
				startPos = nextPos = pos+flowCount;
				isFlag = false;
				flowCount = 0;
				continue;
			}
			
			if(node !=null){
				for(int k = 0; k< node.length;k++){
					char startChar[] = node[k].getStartFlag();
					if(startChar == null){
						continue;
					}
					int sCount = 0;
					while(sCount < startChar.length
							&& expressCharArr[pos+sCount] == startChar[sCount]){
						sCount++;
					}
					if(sCount == startChar.length){
						
					//if(node[k].getStartFlag() == expressCharArr[pos]){
						tmpNode = node[k];
						isFlag = true;
						nextPos = pos+sCount;
						break;
					}
				}
			}

			
			if(isFlag){
				continue;
			}
			
			for(int j = 0;j < tokenCharArr.length;j++){
				
				//String token = tokenStringkArr[j];
				//char[] tokenArr = tokenCharArr[j].getTokenCharArr();
				
				char[] tokenArr = tokenCharArray[j];
				
				int tokenSize = tokenArr.length;
				
				while(count < tokenSize
						&& pos+flowCount < indexEnd
						&& tokenArr[count] == expressCharArr[pos+flowCount]){
					flowCount++;
					count++;
				}

				if(count == tokenSize){

					if(startPos != pos){
						TokenString tokenStr = new TokenString();
						tokenStr.setStart(startPos);
						
						tokenStr.setEnd(pos);
						tokenStr.setType(TOKENTYPE.STR);
						
						tokenStr.setIndex(index);
						tokenStr.setText(getString(expressCharArr,startPos, pos-startPos));
						setDataType(tokenStr);
						//tokenStr.setId(-1);
						tokenStreamInfo.add(tokenStr);
						//treeWorker.work(tokenStr);
						//System.out.println(String.valueOf(expressCharArr,tokenStr.getStart(),tokenStr.getEnd()-tokenStr.getStart()));
						index++;
					}

					if(tokenCharArr[j].getType() == TOKENTYPE.TOKEN){
					
						//System.out.println(String.valueOf(expressCharArr,pos,count));
						TokenString tokenRtree = new TokenString();
						tokenRtree.setStart(pos);
						//TFlag tFlag = flagMap.get(token);
						tokenRtree.setType(TOKENTYPE.TOKEN);
						tokenRtree.setId(tokenCharArr[j].getId());
						//tokenRtree.setName(tFlag.getTokenFlag());
						tokenRtree.setEnd(pos+count);
						tokenRtree.setIndex(index);
						//tokenRtree.setId(j);
						tokenRtree.setText(tokenCharArr[j].getTokenStr());
						tokenStreamInfo.add(tokenRtree);
						//treeWorker.work(tokenRtree);
						index++;
					}
					startPos = nextPos = pos+tokenArr.length;
					isCan = true;
					flowCount =0;
					break;
					
					
				}else{
					
					count = 0;
					flowCount = 0;
					continue;
				}
			}

			if(!isCan){
				nextPos = pos +1;
			}
			
			
		}

		TokenString tokenStrLast = tokenStreamInfo.getLast();
		if(tokenStrLast.getEnd() < indexEnd){
			TokenString tokenStr = new TokenString();
			tokenStr.setStart(startPos);
			
			tokenStr.setEnd(indexEnd);
			tokenStr.setType(TOKENTYPE.STR);
			tokenStr.setText(getString(expressCharArr,startPos,indexEnd-startPos));
			tokenStr.setIndex(index);
			setDataType(tokenStr);
			//tokenStr.setId(-1);
			tokenStr.setLast(true);
			tokenStreamInfo.add(tokenStr);
			
			//treeWorker.work(tokenStr);
			//System.out.println(String.valueOf(expressCharArr,tokenStr.getStart(),tokenStr.getEnd()-tokenStr.getStart()));
		}else{
			tokenStrLast.setLast(true);
		}
		
		
		TokenString tokenStr = tokenStreamInfo.getNext();
		while(tokenStr != null){
			System.out.println(tokenStr.getText());
			
			tokenStr = tokenStreamInfo.getNext();
		};
		
		return tokenStreamInfo;
	}
	

	
	private String getString(char[] expressCharArr,int offset,int count){
				   
		return String.valueOf(expressCharArr, offset, count);
	}

	private int checkEnd(char[] expressCharArr,char[] endChar,int flowCount,int pos){
		
		

		while(expressCharArr[pos+flowCount] != endChar[0]){
			//System.out.println(expressCharArr[pos+flowCount]);
			flowCount++;
		}
		flowCount++;
		int eCount = 1;
		while(eCount < endChar.length
				&& expressCharArr[pos+flowCount] == endChar[eCount]){
			eCount++;
			flowCount++;
		}
		
		if(eCount != endChar.length){
			
			return checkEnd(expressCharArr,endChar,flowCount,pos);
		}
		return flowCount;
	}
	
	private void setDataType(TokenString tokenString){
		if(isNumeric(tokenString.getText())){
			tokenString.setDataType(BData.DATA_TYPE_NUMBER);
		}else{
			tokenString.setDataType(BData.DATA_TYPE_STR);
		}
	}
	
	private boolean isNumeric(String str){
	   for(int i=str.length();--i>=0;){
	      int chr=str.charAt(i);
	      if((chr<48 && chr !=46) || chr>57)
	         return false;
	   }
	   return true;
	}
	
	private int skipWS(int start, char[] expressCharArr,char[] ws){
		int count = 0;
		while( start+count < expressCharArr.length
				&& ws[expressCharArr[start+count]]!='\0'){
			count++;
		}
		return count;
	}
}
