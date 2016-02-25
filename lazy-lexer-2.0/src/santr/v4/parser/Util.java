package santr.v4.parser;

import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.gtree.model.Runner;
import santr.gtree.model.enume.GTYPE;

public class Util {

	private LineInfo createLine(GTree first,int tId,String token,GTree gTree,Runner runner,int deep){
		LineInfo lineInfo = new LineInfo();
		lineInfo.setStart(first.getId());
		if(gTree.getType() == GTYPE.LEAF){
			lineInfo.setEnd(gTree.getParent().getId());
			lineInfo.setLeafId(gTree.getId());
		}else{
			lineInfo.setEnd(gTree.getId());
			lineInfo.setLeafId(-1);
		}
		if(runner!=null && runner.isLoop()){
			lineInfo.setRunner(runner);
		}
		
		lineInfo.setId(tId);
		lineInfo.setDeep(deep);
		lineInfo.setToken(token);
		
		return lineInfo;
	}
}
