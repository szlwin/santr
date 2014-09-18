package santr.v3.execute;

import santr.v3.parser.data.RTree;

public interface Visitor {

	public void execute(Context context);
	
	public void vist(RTree rTree);
}
