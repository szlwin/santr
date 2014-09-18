package santr.v3.parser;

import santr.gtree.model.TerminalData;
import santr.gtree.model.TokenChar;
import santr.gtree.model.enume.TOKENTYPE;


public class ExpressLexer {
	
	public ExpressLexer() {
	}
	
	public TokenStream lexer(TokenChar tokenCharArr[],int indexStart,int indexEnd,char[] expressCharArr,TerminalData[] node){
		
		TokenStream tokenStream = new TokenStream();
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
				flowCount = checkEnd(expressCharArr,tmpNode.getEndFlag(),flowCount,pos)+1;
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
				tokenStr.setText(String.valueOf(expressCharArr, pos, flowCount));
				tokenStr.setDataType(tmpNode.getType());
				tokenStream.add(tokenStr);
				
				index++;
				startPos = nextPos = pos+flowCount+tmpNode.getEndFlag().length;
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
						//tokenStr.setId(-1);
						tokenStream.add(tokenStr);
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
						tokenStream.add(tokenRtree);
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

		TokenString tokenStrLast = tokenStream.getLast();
		if(tokenStrLast.getEnd() < indexEnd){
			TokenString tokenStr = new TokenString();
			tokenStr.setStart(startPos);
			
			tokenStr.setEnd(indexEnd);
			tokenStr.setType(TOKENTYPE.STR);
			tokenStr.setText(getString(expressCharArr,startPos,indexEnd-startPos));
			tokenStr.setIndex(index);
			//tokenStr.setId(-1);
			tokenStream.add(tokenStr);
			//treeWorker.work(tokenStr);
			//System.out.println(String.valueOf(expressCharArr,tokenStr.getStart(),tokenStr.getEnd()-tokenStr.getStart()));
		}
		return tokenStream;
	}
	
	private String getString(char[] expressCharArr,int offset,int count){
				   
		return String.valueOf(expressCharArr, offset, count);
	}

	private int checkEnd(char[] expressCharArr,char[] endChar,int flowCount,int pos){
		
		int eCount = 0;

		while(expressCharArr[pos+flowCount] != endChar[0]){
			//System.out.println(expressCharArr[pos+flowCount]);
			flowCount++;
		}
		
		while(eCount+1 < endChar.length
				&& expressCharArr[pos+flowCount+eCount] == endChar[eCount+1]){
			eCount++;
			flowCount++;
		}
		
		if(eCount+1 != endChar.length){
			
			checkEnd(expressCharArr,endChar,flowCount,pos);
		}
		return flowCount;
	}
}
