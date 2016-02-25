package santr.parser.exception;

import santr.v4.parser.TokenString;

public class ParserInvaildException extends Exception{

	public ParserInvaildException(TokenString tokenString) {
		super("Parser error,the '"+tokenString.getText()+"' is invaild!");
	}
	
	public ParserInvaildException(String text) {
		super("Parser error,the '"+text+"' is invaild!");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029578043039293438L;

}
