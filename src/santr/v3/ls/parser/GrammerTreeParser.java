package santr.v3.ls.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import santr.gtree.model.BData;
import santr.gtree.model.ExpressGraph;
import santr.gtree.model.GTree;
import santr.gtree.model.GrammarInfo;
import santr.gtree.model.TFlag;
import santr.gtree.model.TerminalData;
import santr.gtree.model.Token;
import santr.gtree.model.TokenChar;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.gtree.model.enume.TOKENTYPE;

import javolution.util.FastTable;


public class GrammerTreeParser {

	private GrammarInfo grammarInfo;
	
	private int id = 0;
	
	private boolean isFirst = true;
	
	//private char SPACE = ' ';
	
	private String END_FLAG = ";";
	
	private String EXPRESS_ = ":";
	
	private String WS = "WS";
	
	private StringBuffer strBuffer = new StringBuffer();
	
	private List<TerminalData> terminalDataList = new ArrayList<TerminalData>();
	
	private List<GTree> gTreeAllList = new FastTable<GTree>();
	
	public GrammarInfo parse(String filePath) throws IOException{
		
		grammarInfo = new GrammarInfo();
		
		BufferedReader fileStream = getFileStream(filePath);
		String lineStr = fileStream.readLine().trim();

		while(lineStr != null){
			if(!"".equals(lineStr)){
				
				strBuffer.append(lineStr);
				
				if(lineStr.endsWith(END_FLAG)){
					int length = strBuffer.length();
					String newLine = strBuffer.toString()
							.substring(0, length-1)
							.trim();
					
					strBuffer.delete(0, strBuffer.length());
					
					parseLine(newLine);
				}
			}
			
			
			lineStr = fileStream.readLine();
		}
		grammarInfo.setgTree(grammarInfo.getgTreeMap()
				.get(grammarInfo.getRoot()));
		
		ExpressGraph expressGraph = new ExpressGraph();
		expressGraph.convert(grammarInfo);
		
		grammarInfo.setExpressGraph(expressGraph);
		
		TokenChar[] tokenCharArr = new TokenChar[grammarInfo.getTokenList().size()];
		grammarInfo.getTokenList().toArray(tokenCharArr);
		grammarInfo.setTokenCharArr(tokenCharArr);
		
		TerminalData[] terminalDataArr = new TerminalData[terminalDataList.size()];
		terminalDataList.toArray(terminalDataArr);
		grammarInfo.setTerminalDataArr(terminalDataArr);
		

		GTree gTreeArr[] = new GTree[grammarInfo.getTreeList().size()];
		grammarInfo.getTreeList().toArray(gTreeArr);
		//while(it.hasNext()){
		//	GTree gTree = it.next();
		//	gTreeArr[gTree.getId()] = gTree;
		//}
		grammarInfo.setgTreeArr(gTreeArr);
		return grammarInfo;
	}
	
	private BufferedReader getFileStream(String filePath) throws FileNotFoundException{
		BufferedReader file 
			= new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath)));
		return file;
	}
	
	private void parseLine(String str){
		String lineStr = str.trim();
		if(isFirst){
			//parserRoot(lineStr);
			isFirst = false;
			return;
		}else{
			parseExpress(lineStr);
		}
	}
	/*
	private void parserRoot(String lineStr){
		char[] charArr = lineStr.toCharArray();
		int index = 0;
		int end = charArr.length-1;
		
		for(int i = end; i > 0;i--){
			if(charArr[i] == SPACE){
				index = i+1;
				break;
			}
		}
		//if(grammarInfo.getRoot() == null){
			String root = getString(index,end,charArr);
			grammarInfo.setName(root);
			//System.out.println(grammarInfo.getName());
		//}

	}*/
	
	private String getString(int start,int end,char[] charArr){
		return String.valueOf(charArr, start, end - start);
	}
	
	private void parseWS(String wsString){
		char[] wsChar = wsString.substring(1,wsString.length()-1).toCharArray();
		for(int i = 0; i < wsChar.length;i++){
			grammarInfo.addToken(new TokenChar(null,new char[]{wsChar[i]},TOKENTYPE.WS));
		}
	}
	private void parseExpress(String lineStr){
		//System.out.println(lineStr);
		int index = lineStr.indexOf(EXPRESS_);
		String expName = lineStr.substring(0,index).trim();
		String expressTreeStr = lineStr.substring(index+1).trim();
		
		if(expName.equals("prog")){
			grammarInfo.setName(expressTreeStr);
			return;
		}
		boolean isNode = false;
		if(expName.startsWith("@")){
			expName = expName.substring(1);
			isNode = true;
		}
		
		
		if(expName.equals(WS)){
			parseWS(expressTreeStr);
			return;
		}
		if(grammarInfo.getgTreeMap().isEmpty()){
			grammarInfo.setRoot(expName);
		}
		//System.out.println(expName);
		
		GTree gTree = null;
		if(grammarInfo.getgTreeMap().containsKey(expName)){
			gTree = grammarInfo.getgTreeMap().get(expName);
		}else{
			gTree = createGTree(expName,GTYPE.NULL);
		}
		if(isNode){
			gTree.setType(GTYPE.NODE);
			parseNode(expName,gTree,expressTreeStr);
		}else{
			expressTreeStr = expressTreeStr + "|";
			//currentGTree = gTree;
			
			parseTree(gTree,expressTreeStr);
			//System.out.println(gTree.getName());
		}
	}
	
	private int treeIndex;
	List<GTree> gTreeList = new ArrayList<GTree>();
	private void parseLTree(GTree pTree,String expressTreeStr,int group,GROUPTYPE groupType){
		char charStr[] = expressTreeStr.toCharArray();
		//0:STR 1:() 2:''
		int nextflag = 0;
		int preflag = 0;
		
		int index = 0;
		
		int stratIndex = index;
		int endIndex = 0;
		
		
		boolean isStart = false;
		
		boolean isEStart = false;
		
		int eStartIndex = 0;
		
		int tmpGroup =0;
		//int treeIndex = 0;
		for(;index < charStr.length;index++){
			switch(charStr[index]){
			    case ' ':
					if(nextflag == 0){
						endIndex = index;
						if(index > 0 && eStartIndex!=endIndex){
							GTree gTree3 = parseSubTree(getString(eStartIndex, endIndex, charStr)
									.trim());
							gTree3.setGroup(group);
							gTree3.setGroupType(groupType);
							gTreeList.add(gTree3);
							//String subStr = String.valueOf(charStr,stratIndex,index-stratIndex);
							//System.out.println("dss:"+subStr);
							//parseLTree(pTree,subStr,group,groupType);
							treeIndex++;
						}

						
						//gTree3.setParent(pTree);
						eStartIndex = index+1;
						stratIndex = index+1;
						//nextflag = 1;
						
						isEStart = true;
					}
			    	break;
				case '(':
					
					if(nextflag != 0){
						continue;
					}
					boolean isSub = false;
					if(charStr[index+1] !='\''){
						isSub = true;
					}else{
						for(int i = index+1;i < charStr.length;i++){
							if(charStr[i] == ' '){
								if(charStr[i-1] == '\''){
									isSub = true;
									break;
								}
							}
						}
					}
					if(isSub){
						for(int i = index+1;i < charStr.length;i++){
							if(charStr[i] == '?'
									|| charStr[i] == '*'
									|| charStr[i] == '+'){
								if(charStr[i-1] == ')'
										&& (i == charStr.length-1 || charStr[i+1]==' ')){
									String subStr = String.valueOf(charStr,index+1,i-index-2);
									tmpGroup++;
									GROUPTYPE tmpGroupType = GROUPTYPE.NULL;
									if(charStr[i] == '?'){
										tmpGroupType = GROUPTYPE.ZERO_OR_ONE;
									}else if(charStr[i] == '*'){
										tmpGroupType = GROUPTYPE.ZERO_OR_MANY;
									}else if(charStr[i] == '+'){
										tmpGroupType = GROUPTYPE.ONE_OR_MANY;
									}
									parseLTree(pTree,subStr,tmpGroup,tmpGroupType);
									index = i;
									stratIndex = index+1;
									nextflag = 0;
									eStartIndex = index+1;
									isEStart = true;
									break;
								}
							}
							
						}
					}else{
						//eStartIndex = index+1;
						stratIndex = index+1;
						nextflag = 1;
						isEStart = false;
					}


					break;
				case ')':
					if(nextflag == 1
						&& !isStart){
						endIndex = index;
						GTree gTree1 = parseToken(getString(stratIndex, endIndex, charStr)
								.trim(),treeIndex);
						treeIndex++;
						//gTree1.setParent(pTree);
						gTree1.setGroup(group);
						gTree1.setGroupType(groupType);
						gTreeList.add(gTree1);					
						stratIndex = index+1;
						nextflag = 0;
						
						eStartIndex = index+1;
						isEStart = true;
					}
					break;
				case '\'':

					if(nextflag == 0 ){
						stratIndex = index+1;
						preflag = nextflag;
						nextflag = 2;
						
						if(isEStart){
							if(eStartIndex != index){
								GTree gTree3 = parseSubTree(getString(eStartIndex, index, charStr)
										.trim());
								gTree3.setGroup(group);
								gTree3.setGroupType(groupType);
								treeIndex++;
								gTreeList.add(gTree3);
							}

							isEStart = false;
						}
					}else if(nextflag == 2){
						endIndex = index;
						GTree gTree2 = parseToken(getString(stratIndex, endIndex, charStr)
								.trim(),treeIndex);
						treeIndex++;
						gTree2.setGroup(group);
						gTree2.setGroupType(groupType);
						gTree2.setParent(pTree);
						gTreeList.add(gTree2);
						stratIndex = index+1;
						nextflag = preflag;
						if(preflag == 0  ){
							isEStart = true;
							eStartIndex = index+1;
						}
					}else if(nextflag == 1){
						isStart = !isStart;
					}
					break;
			}
			
		}
		
		if(eStartIndex < charStr.length -1){
			GTree gTree = parseSubTree(getString(eStartIndex, charStr.length, charStr)
					.trim());
			gTree.setParent(pTree);
			gTree.setGroup(group);
			gTree.setGroupType(groupType);
			gTreeList.add(gTree);
		}
		
		GTree[] gTreeArray =  new GTree[gTreeList.size()];
		
		gTreeList.toArray(gTreeArray);
		
		pTree.setgTreeArray(gTreeArray);
		
		int flag = 0;
		int startFlag = 1;
		gTreeArray[0].setIndex(0);
		gTreeArray[0].setFlag(0);
		gTreeArray[0].setParent(pTree);
		for(int i = 1;i < gTreeArray.length;i++){
			GTree gTree = gTreeArray[i];
			gTree.setIndex(i);
			gTree.setParent(pTree);
			if(gTree.getGroup() == 0){
				gTree.setFlag(startFlag);
				startFlag++;
			}else{
				if(gTreeArray[i].getGroup()!=gTreeArray[i-1].getGroup()){
					flag = startFlag;
				}
				gTree.setFlag(flag);
				flag++;
			}
			
		}
		if(!tokenList.isEmpty()){
			Token token = new Token();
			for(int i = 0; i < tokenList.size();i++){
				TFlag tFlag = tokenList.get(i);
				token.putTFlag(tFlag.getId(), tFlag);
			}
			
			TFlag[] tFlagArray =  new TFlag[tokenList.size()];
			
			tokenList.toArray(tFlagArray);
			
			token.setTflag(tFlagArray);
			pTree.setToken(token);
		}

		
		
		//tokenIndex = 0;
	}
	
	
	private void parseTree(GTree pTree,String expressTreeStr){
		char charStr[] = expressTreeStr.toCharArray();
		//0:STR 1:() 2:'' 3:
		int nextflag = 0;
		int preflag = 0;
		int index = 0;
		
		//int stratIndex = index;
		//int endIndex = 0;
		
		boolean isStart = true;
		
		int subStratIndex = 0;
		List<GTree> gTreeList = new ArrayList<GTree>();
		for(;index < charStr.length;index++){
			switch(charStr[index]){
				case '|':
					if(nextflag > 0){
						continue;
					}
					
					//endIndex = index;
					GTree lTree = new GTree();
					gTreeList.add(lTree);
					parseLTree(lTree,getString(subStratIndex, index, charStr)
							.trim(),0,GROUPTYPE.NULL);
					//stratIndex = index+1;
					subStratIndex = index +1;
					this.gTreeList.clear();
					this.tokenList.clear();
					treeIndex = 0;
					/*
					if(isLStart){
						stratIndex = index+1;
						isLStart = false;
					}else{
						endIndex = index;
						GTree lTree = new GTree();
						gTreeList.add(lTree);
						parseLTree(lTree,getString(stratIndex, endIndex, charStr)
								.trim());
						stratIndex = index+1;
						isLStart = true;
						
					}*/
					break;
				case '(':
					if(nextflag == 0){
						nextflag = 1;
					}
					break;
				case ')':
					if(nextflag == 1)
						nextflag = 0;
					break;
				case '\'':
					if(nextflag == 0 
						|| nextflag ==1 
						|| nextflag == 2){
						
						if(isStart){
							preflag = nextflag;
							isStart = false;
							nextflag = 2;
						}else{
							isStart = true;
							nextflag = preflag;
						}
					}
					break;
			}
		}
		

		if(gTreeList.size() == 1){
			GTree gArr[] = gTreeList.get(0).getgTreeArray();
			for(int i = 0; i < gArr.length;i++){
				gArr[i].setParent(pTree);
			}
			pTree.setToken(gTreeList.get(0).getToken());
			pTree.setgTreeArray(gArr);
		}else{
			GTree[] gTreeArray =  new GTree[gTreeList.size()];
			
			gTreeList.toArray(gTreeArray);
			
			for(int i = 0; i < gTreeArray.length;i++){
				gTreeArray[i].setId(id);
				gTreeArray[i].setName(pTree.getName());
	
				gTreeArray[i].setParent(pTree);
				gTreeArray[i].setType(GTYPE.LEAF);
				gTreeArray[i].setRel(gTreeArray[i]);
				id++;
				
				gTreeAllList.add(gTreeArray[i]);
				grammarInfo.addTree(gTreeArray[i]);
				//if(checkIsAllLbs(gTreeArray[i])){
					//copy(gTreeArray[i]);
				//}
			}
			pTree.setgTreeArray(gTreeArray);
		}

	}
	private void copy(GTree gTree){
		GTree[] gTreeArray =  gTree.getgTreeArray();
		for(int i = 0; i < gTreeArray.length;i++){
			GTree tmpGTree = gTreeArray[i];
			if(tmpGTree.getType() == GTYPE.LBS && 
					tmpGTree.getRel().getType() == GTYPE.EXPRESS 
					&& tmpGTree.getGroup() == 0){
				gTreeArray[i] = tmpGTree.getRel();
				
				//tmpGTree.setToken(tmpGTree.getRel().getToken());
				//tmpGTree.addAllNode(tmpGTree.getRel().getNodeList());
				//tmpGTree.setId(id);
				//id++;
				
				//gTreeAllList.add(tmpGTree);
				//grammarInfo.addTree(tmpGTree);
				break;
			}
		}
	}
	private boolean checkIsAllLbs(GTree gTree){
		GTree[] gTreeArray =  gTree.getgTreeArray();
		for(int i = 0; i < gTreeArray.length;i++){
			if(gTreeArray[i].getType() == GTYPE.EXPRESS){
				return false;
			}
		}
		return true;
	}
	private GTree parseSubTree(String str){
		//if(str.startsWith("@")){
		//	str = str.substring(1);
		//	return createNode(str);
		//}
		return createGTree(str,GTYPE.NULL);
	}

	
	private GTree createNode(String str) {
		GTree gTree = createGTree(str,GTYPE.NODE);
		
		
		if(!this.grammarInfo.getgTreeMap().containsKey(str)){
			gTree.setId(id);
			gTreeAllList.add(gTree);
			id++;
			this.grammarInfo.getgTreeMap().put(str, gTree);
			grammarInfo.addTree(gTree);
		}

		return gTree;
	}

	private GTree createGTree(String expName,GTYPE type){
		GTree gTree = new GTree();
		gTree.setName(expName);
		gTree.setRelName(expName);
		if(type != GTYPE.NULL){
			gTree.setType(type);
			if(type == GTYPE.TOKEN){
				gTree.setRel(gTree);
			}
		}else{
			
			if(grammarInfo.getgTreeMap().containsKey(expName)){
				gTree.setType(GTYPE.LBS);
				gTree.setRel(grammarInfo.getgTreeMap().get(expName));
			}else{
				gTree.setType(GTYPE.EXPRESS);
				gTree.setId(id);
				gTree.setRel(gTree);
				/*GTree egTree = new GTree();
				egTree.setId(id);
				egTree.setType(GTYPE.EXPRESS);
				egTree.setName(expName);
				egTree.setRelName(expName);*/
				//gTree.setDeep(deep);
				//deep++;
				gTreeAllList.add(gTree);
				id++;
				
				grammarInfo.getgTreeMap().put(expName, gTree);
				grammarInfo.addTree(gTree);
				
			}
		}
		
		
		
		return gTree;
	}
	
	private List<TFlag> tokenList = new ArrayList<TFlag>();
	
	//private int tokenIndex = 0;
	
	private void parseTokenGroup(String str, int treeIndex, GTree gTree){
		
		int index = 0;
		
		int stratIndex = index;
		int endIndex = 0;
		char charStr[] = str.toCharArray();
		boolean isStart = true;
		
		for(;index < charStr.length;index++){
			switch(charStr[index]){
				case '\'':
					if(isStart){
						stratIndex = index+1;
						isStart = false;
					}else{
						endIndex = index;
						
						String tokenStr = this.getString(stratIndex, endIndex, charStr);
						TFlag tFlag = new TFlag();
						tFlag.setTokenFlag(tokenStr);
						//tFlag.setIndex(treeIndex);
						tFlag.setLbs(gTree);
						
						if(!tMap.containsKey(tokenStr)){
							tMap.put(tokenStr, tokenId);
							tFlag.setId(tokenId);
							tokenId++;
							TokenChar tokenChar = new TokenChar(tFlag.getTokenFlag(),tFlag.getTokenFlag().toCharArray(),TOKENTYPE.TOKEN);
							tokenChar.setId(tFlag.getId());
							this.grammarInfo.addToken(tokenChar);
						}else{
							tFlag.setId(tMap.get(tokenStr));
						}
						
						tokenList.add(tFlag);
						isStart = true;
						if(index+1 < charStr.length){
							if(charStr[index] == '|'){
								index++;
								stratIndex = index;
							}
						}
					}
					break;
			}
			
		}
		
	}
	
	private Map<String,Integer> tMap = new HashMap<String,Integer>();
	
	private int tokenId = 1;
	
	private GTree parseToken(String str, int treeIndex){
		GTree gTree = createGTree(str,GTYPE.TOKEN);
		if(str.startsWith("'")){
			parseTokenGroup(str,treeIndex,gTree);
		}else{
			TFlag tFlag = new TFlag();
			tFlag.setTokenFlag(str);
			//tFlag.setIndex(treeIndex);
			tFlag.setLbs(gTree);
			if(!tMap.containsKey(str)){
				tMap.put(str, tokenId);
				tFlag.setId(tokenId);
				tokenId++;
				TokenChar tokenChar = new TokenChar(tFlag.getTokenFlag(),tFlag.getTokenFlag().toCharArray(),TOKENTYPE.TOKEN);
				tokenChar.setId(tFlag.getId());
				this.grammarInfo.addToken(tokenChar);
			}else{
				tFlag.setId(tMap.get(str));
			}
			tokenList.add(tFlag);
			
		}
		
		return gTree;

	}
	
	private void parseNode(String name,GTree gTree,String str){
		int index = str.indexOf("#STRING");
		if(str.indexOf("#STRING")>=0){
			TerminalData node = new TerminalData();
			
			
			if(index!=0){
				String startStr = str.substring(0, index).replaceAll(" ", "");
				char[] startFlag = startStr.substring(1,startStr.length()-1).toCharArray();
				
				String endStr = str.substring(index+7).replaceAll(" ", "");
				char[] endFlag = endStr
						.substring(1,endStr.length()-1).toCharArray();
				
				node.setStartFlag(startFlag);
				node.setEndFlag(endFlag);
			}
			node.setType(BData.DATA_TYPE_STR);
			terminalDataList.add(node);
			gTree.setTerminalData(node);
		}else{
			TerminalData node = new TerminalData();
			node.setMatch(str);
			gTree.setTerminalData(node);
		}

	}
}