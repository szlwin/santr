package santr.v3.parser;

import santr.gtree.model.enume.TOKENTYPE;

public class TokenString {
	
	private int index;
	
	private int start;
	
	private int end;
	
	private TOKENTYPE type;
	
	private boolean isLexer;
	
	private String text;
	
	private int id;
	
	private int dataType;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public TOKENTYPE getType() {
		return type;
	}

	public void setType(TOKENTYPE type) {
		this.type = type;
	}

	public boolean isLexer() {
		return isLexer;
	}

	public void setLexer(boolean isLexer) {
		this.isLexer = isLexer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	
}
