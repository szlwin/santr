package santr.parser.exception;

import santr.v4.parser.TokenString;

public class ExecuteInvaildException extends Exception{
	
	public ExecuteInvaildException(String text) {
		super(text);
	}
	
	public ExecuteInvaildException(String text,Exception e) {
		super(text,e);
	}
	
	public ExecuteInvaildException(TokenString tokenString) {
		super("Parser error,the '"+tokenString.getText()+"' is invaild!");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029578043039293438L;

}
