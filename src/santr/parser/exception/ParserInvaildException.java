package santr.parser.exception;

import santr.v3.parser.TokenString;

public class ParserInvaildException extends Exception{

	public ParserInvaildException(String string) {
		super(string);
	}

	public ParserInvaildException(TokenString tokenString) {
		super("Parser error,the '"+tokenString.getText()+"' is invaild!");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8029578043039293438L;

}
