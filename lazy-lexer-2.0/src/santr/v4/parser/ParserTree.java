package santr.v4.parser;

import java.util.List;

import santr.common.util.collections.SimpleTreeList;
import santr.gtree.model.BData;
import santr.gtree.model.GTree;

public class ParserTree{

	/**
	 * 
	 */
	//private static final long serialVersionUID = -7588880872285526608L;

	private List<ParserTree> rTreeList;
	
	private ParserTree parent;
	
	private RuleContext ruleContext;
	
	private String text;
    
    private ParserTree last;
    
    private int type;
    
    private boolean isRoot = false;
    
    private ParserTree preTree;
    
    public ParserTree(){
    	
    }
    
	protected ParserTree(ParserTree parent) {
		parent.addRTree(this);
	}

	protected boolean isEmpty(){
		return last == null;
	}
	
	protected List<ParserTree> getrTreeList() {
		return rTreeList ;
	}

	protected void addRTree(ParserTree rTree) {
		if(rTreeList == null){
			int initSize = ruleContext.getLeaf().getgTreeArray().length;
			rTreeList = new SimpleTreeList(initSize,initSize);
		}
		rTree.setParent(this);
		this.last = rTree;
		this.rTreeList.add(rTree);
	}

	protected ParserTree getParent() {
		return parent;
	}

	protected void setParent(ParserTree parentTree) {
		this.parent = parentTree;
	}

	protected String getText() {
		return text;
	}

	protected void setText(String text) {
		this.text = text;
	}
	
	protected ParserTree getLast(){
		return this.last;
	}

	protected void setLast(ParserTree newTree) {
		rTreeList.remove(this.last);
		this.addRTree(newTree);
	}

	public RuleContext getRuleContext() {
		return ruleContext;
	}
	
	protected void setRuleContext(RuleContext ruleContext) {
		this.ruleContext = ruleContext;
	}

	protected int getType() {
		return type;
	}

	protected void setType(int type) {
		this.type = type;
	}

	protected boolean isRoot() {
		return isRoot;
	}

	protected void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	protected void refreshLeaf(GTree gTree){
		this.getRuleContext().setLeaf(gTree);
		
	}
	
	protected void setTree(int index,ParserTree parserTree){
		this.rTreeList.set(index, parserTree);
	}

	protected ParserTree getPreTree() {
		return preTree;
	}

	protected void setPreTree(ParserTree preTree) {
		this.preTree = preTree;
	}
	
	protected void removeLast(){
		this.getrTreeList().remove(last);
		last = null;
	}
	
	public boolean isToken() {
		return getType() == BData.FLAG_TYPE_TOKEN;
	}

	public String getToken() {
		return this.getText();
	}
	
	public String getName(){
		return this.ruleContext.getName();
	}
}
