package santr.v3.parser.data;

import java.util.List;

import santr.gtree.model.GTree;
import santr.v3.execute.Context;

import javolution.util.FastTable;


public class RTree{

	/**
	 * 
	 */
	//private static final long serialVersionUID = -7588880872285526608L;

	private List<RTree> rTreeList;

	private String name;
	
	private int type;
	
	private RTree parentTree;
	
	private GTree leaf;
	
	private Context contenxt;
	
	private String text;
	
    private GTree lbs;
    
    private RTree last;
    
    public RTree(){
    	rTreeList = new FastTable<RTree>();
    }
    
	public List<RTree> getrTreeList() {
		return rTreeList;
	}

	public void addRTree(RTree rTree) {
		rTree.setParentTree(this);
		this.last = rTree;
		this.rTreeList.add(rTree);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public RTree getParentTree() {
		return parentTree;
	}

	public void setParentTree(RTree parentTree) {
		this.parentTree = parentTree;
	}

	public GTree getLeaf() {
		return leaf;
	}

	public void setLeaf(GTree leaf) {
		this.leaf = leaf;
	}

	public Context getContext() {
		return contenxt;
	}

	public void setContext(Context contenxt) {
		this.contenxt = contenxt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public GTree getLbs() {
		return lbs;
	}

	public void setLbs(GTree lbs) {
		this.lbs = lbs;
	}
	
	public RTree getLast(){
		return this.last;
	}
	
}
