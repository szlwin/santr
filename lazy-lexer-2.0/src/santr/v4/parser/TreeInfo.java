package santr.v4.parser;

import santr.v3.parser.data.RTree;

 class TreeInfo{
	
	private RTree tree;
	
	private RTree last;

	public RTree getTree() {
		return tree;
	}

	public void setTree(RTree tree) {
		this.tree = tree;
	}

	public RTree getLast() {
		return last;
	}

	public void setLast(RTree last) {
		this.last = last;
	}
}