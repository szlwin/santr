package santr.v4.execute;

import santr.parser.exception.ExecuteInvaildException;
import santr.v4.parser.ParserTree;
import santr.v4.parser.RuleContext;

public interface Visitor<E> {

	public void execute(RuleContext context) throws ExecuteInvaildException;
	
	public void vist(ParserTree rTree) throws ExecuteInvaildException;

	public void setParamer(E object);
	
	public E getParamer();
	//public Object getResult();
}
