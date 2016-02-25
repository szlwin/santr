package santr.v3.execute;

import santr.parser.exception.ExecuteInvaildException;
import santr.v3.parser.data.RTree;

public interface Visitor {

	public void execute(Context context) throws ExecuteInvaildException;
	
	public void vist(RTree rTree) throws ExecuteInvaildException;
}
