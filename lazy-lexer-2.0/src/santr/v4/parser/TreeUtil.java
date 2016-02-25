package santr.v4.parser;

import java.util.List;

public class TreeUtil {

	public static int getType(ParserTree parserTree){
		return parserTree.getType();
	}
	
	public static String getText(ParserTree parserTree){
		return parserTree.getText();
	}
	
	public static List<ParserTree> getTreeList(ParserTree parserTree){
		return parserTree.getrTreeList();
	}
	
	public static ParserTree getLast(ParserTree parserTree){
		return parserTree.getLast();
	}
	
	public static ParserTree getParent(ParserTree parserTree){
		return parserTree.getParent();
	}
	
	public static void setFinish(RuleContext ruleContext) {
		ruleContext.setFinish(true);
	}

	public static boolean isFinish(RuleContext ruleContext) {
		return ruleContext.isFinish();
	}
}
