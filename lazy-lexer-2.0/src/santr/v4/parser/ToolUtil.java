package santr.v4.parser;

import java.util.List;

import santr.gtree.model.BData;
import santr.gtree.model.GTree;
import santr.gtree.model.GrammarInfo;
import santr.gtree.model.LineInfo;
import santr.gtree.model.TFlag;
import santr.gtree.model.Token;
import santr.gtree.model.enume.GTYPE;
import santr.gtree.model.enume.TOKENTYPE;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.parser.TokenString;
import santr.v4.parser.ParserTree;
import santr.v4.parser.RuleContext;

class ToolUtil {
	
	private GrammarInfo grammarInfo;
	
	private TokenString tokenStr;
	
	private TokenStreamInfo tokenStreamInfo;
	
	protected ToolUtil(GrammarInfo grammarInfo, TokenStreamInfo tokenStreamInfo){
		this.grammarInfo = grammarInfo;
		this.tokenStreamInfo = tokenStreamInfo;
	}
	
	protected TokenStreamInfo getTokenStream() {
		return tokenStreamInfo;
	}

	protected void setTokenString(TokenString tokenStr){
		
		this.tokenStr = tokenStr;
	}
	
	protected TokenString getTokenString(){
		return this.tokenStr;
	}
	
	protected String getToken(){
		return this.tokenStr.getText();
	}
	
	protected boolean isEmpty(ParserTree rTree){
		return rTree.getLast() == null;
	}
	
	//protected void setLeafByExpress(RTree rTree){
	//	GTree gTree = grammarInfo.getgTree();
	//	if(gTree.getToken() != null)
			//rTree.setLeaf(gTree);
	//}
	
	protected boolean checkIsToken(){
		return tokenStr.getType() == TOKENTYPE.TOKEN;
		
	}
	
	protected void initRoot(ParserTree root) {
		initTree(root,grammarInfo.getgTree());
	}

	protected ParserTree createRoot() {
		return this.createTree(grammarInfo.getgTree());
	}
	
	protected ParserTree enterFirstTree(ParserTree root) {
		ParserTree child = root;
		GTree gChild = child.getRuleContext().getFirstChild();

		while(gChild !=null 
				//&& !gChild.isExitFork() 
				// && gChild.getgTreeArray()[0].getRel().getType() != GTYPE.NODE
				 ){
			child =  createTree(child,gChild);
			gChild = child.getRuleContext().getFirstChild();
		}
		
		return child;
		
	}
	
	protected ParserTree createTree(ParserTree parent,GTree gTree){
		ParserTree rTree = new ParserTree(parent);
		initTree(rTree,gTree);
		return rTree;
	}

	protected ParserTree createTree(GTree gTree){
		ParserTree rTree = new ParserTree();
		initTree(rTree,gTree);
		return rTree;
	}
	
	public void addTokneTree(ParserTree currentTree) throws ExecuteInvaildException {
		this.addTokneTree(currentTree, tokenStr);
		
	}
	
	protected boolean checkMatch(GTree pGtree) {
		GTree parentTree = pGtree.getRel();
		GTree gTreeArr[] = parentTree.getgTreeArray();
		Token tokenFlag = parentTree.getToken();
		
		int count = tokenStreamInfo.getCurrentIndex() - tokenStreamInfo.getLexerIndex()-1;
		int unLindex = tokenStreamInfo.getCurrentIndex()-1;
		boolean isMatch = true;
		if(gTreeArr.length < count){
			return false;
		}
		while(count > 0){
			TokenString token = tokenStreamInfo.getNext(unLindex);
			if(token.getType() != TOKENTYPE.TOKEN){
				unLindex--;
				count--;
				continue;
			}
			GTree gTree = gTreeArr[count-1].getRel();

			if(token.getType() == TOKENTYPE.TOKEN){
				if(gTree.getType() != GTYPE.TOKEN){
					isMatch = false;
					break;
				}else{
					TFlag tflag = tokenFlag.getTFlag(token.getId());
					if(tflag == null 
							|| tflag.getLbs().getFlag()!=count-1
							){
						isMatch = false;
						break;
					}
				}
			}
			
			unLindex--;
			count--;
		}

		return isMatch;
	}

	public GTree getTree(LineInfo lineInfo) {
		int id = lineInfo.getLeafId();
		if(id == -1){
			id = lineInfo.getEnd();
		}
		return grammarInfo.getTree(id);
	}

	public GTree getTargetTree(LineInfo lineInfo) {

		return grammarInfo.getTree(lineInfo.getEnd());
	}
	
	public List<LineInfo> getLineInfo(ParserTree currentTree) {
		//System.out.println(currentTree.getName()+":"+this.tokenStr.getText());
		//return currentTree.getRuleContext().getLeaf().get(this.tokenStr.getId());
		if(this.tokenStr.getType() == TOKENTYPE.STR){
			return null;
		}else{
			return currentTree.getRuleContext().getLineInfo(this.tokenStr.getId());
		}
		
	}

	public void scan(ParserTree currentTree) {
		currentTree.getRuleContext()
			.scan(this.getTokenString().getId(), this.getToken());
	}

	public void addTerminalTree(ParserTree tree) throws ExecuteInvaildException {
		this.addTerminalTree(tree, tokenStr);
	}

	public void addPreTerminalTree(ParserTree tree) throws ExecuteInvaildException {
		
		TokenString preTokenStr  = this.tokenStreamInfo.getNext(tokenStr.getIndex()-1);
		
		if(preTokenStr!=null 
				&& !preTokenStr.isLexer()){
			GTree gtree = tree.getRuleContext().getLeaf();
			GTree preTree = gtree.getgTreeArray()[gtree.getToken().getTFlag(tokenStr.getId()).getLbs().getIndex()-1];
			ParserTree childTree = this.createTree(tree, preTree);
			this.enterFirstTree(childTree).getRuleContext().addTerminalNode(preTokenStr);
			//tree.getRuleContext().addTerminalNode(preTokenStr);
			
			setTokenStrFinish(preTokenStr);
		}

	}
	
	public void addLastTree(ParserTree tree) throws ExecuteInvaildException {
		int unLindex = tokenStreamInfo.getLexerIndex()+1;
		//int count = 0;
		//GTree[] gTreeArr = currentTree.getRuleContext().getLeaf().getgTreeArray();
		while(unLindex < tokenStreamInfo.getCurrentIndex()-1){
			TokenString tmpToken = tokenStreamInfo.getNext(unLindex);
			
			tree.getRuleContext().addTerminalNode(tmpToken);
			
			setTokenStrFinish(tmpToken);
			
			//count++;
			unLindex++;
		}
	}

	
	private void initTree(ParserTree rTree,GTree gTree){
		
		RuleContext ruleContext = new RuleContext();
		ruleContext.setParserTree(rTree);
		ruleContext.setLbs(gTree);
		rTree.setType(BData.FLAG_TYPE_VAR);
		rTree.setRuleContext(ruleContext);
		
	}

	public void addAllPreChild(ParserTree currentTree) throws ExecuteInvaildException {
		int unLindex = tokenStreamInfo.getLexerIndex()+1;
		int count = 0;
		GTree[] gTreeArr = currentTree.getRuleContext().getLeaf().getgTreeArray();
		while(unLindex < tokenStreamInfo.getCurrentIndex()-1){
			TokenString tmpToken = tokenStreamInfo.getNext(unLindex);
			
			if(tmpToken.getType() == TOKENTYPE.TOKEN){
				addTokneTree(currentTree);
			}else{
				ParserTree parserTree = this.createTree(currentTree, gTreeArr[count]);
				this.enterFirstTree(parserTree).getRuleContext().addTerminalNode(tmpToken);
				//addTerminalTree(parserTree,tmpToken);
			}
			count++;
			unLindex++;
		}
		
	}

	public void addTerminalTree(ParserTree parserTree, TokenString tokenStr) throws ExecuteInvaildException {
		parserTree.getRuleContext().addTerminalNode(tokenStr);
		
		setTokenStrFinish(tokenStr);
	}

	private void setTokenStrFinish(TokenString tokenStr) {
		tokenStr.setLexer(true);
		//System.out.print("token:"+tokenStr.getText()+" ");
		this.tokenStreamInfo.setLexerIndex(tokenStr.getIndex());
		
	}

	public void addTokneTree(ParserTree currentTree, TokenString tokenString) throws ExecuteInvaildException {
		//System.out.println("1236:"+tokenStr.getText());
		currentTree.getRuleContext().addToken(tokenString);
		this.setTokenStrFinish(tokenString);
		
	}
	/*
	public void addPreTerminalTree(ParserTree currentTree, GTree node,
			TokenString tokenString) throws ParserInvaildException {
		currentTree.getRuleContext().addTerminalNode(node,tokenString);
		
		setTokenStrFinish(tokenString);
		
	}*/

	public void addAllLastTree(ParserTree currentTree) throws ExecuteInvaildException {
		int unLindex = tokenStreamInfo.getLexerIndex()+1;
		//int count = 0;
		//GTree[] gTreeArr = currentTree.getRuleContext().getLeaf().getgTreeArray();
		
		while(unLindex<tokenStreamInfo.getSize()){
			TokenString tmpToken = tokenStreamInfo.getNext(unLindex);
			currentTree.getRuleContext().addTerminalNode(tmpToken);
			
			setTokenStrFinish(tmpToken);
			unLindex++;
		};
		
	}

}
