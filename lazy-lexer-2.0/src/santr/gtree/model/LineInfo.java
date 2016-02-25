package santr.gtree.model;

public class LineInfo {

	private int start;
	
	private int deep;
	
	private int end;

	private String token;
	
	private int id;
	
	private int leafId;
	
	private Runner runner;
	
	private GTree express;
	
	private GTree tokenLBS;
	
	private boolean isNeedMatch = true;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLeafId() {
		return leafId;
	}

	public void setLeafId(int leafId) {
		this.leafId = leafId;
	}

	public Runner getRunner() {
		return runner;
	}

	public void setRunner(Runner runner) {
		this.runner = runner;
	}

	public GTree getExpress() {
		return express;
	}

	public void setExpress(GTree express) {
		this.express = express;
	}

	public GTree getTokenLBS() {
		return tokenLBS;
	}

	public void setTokenLBS(GTree tokenLBS) {
		this.tokenLBS = tokenLBS;
	}

	public boolean isNeedMatch() {
		return isNeedMatch;
	}

	public void setNeedMatch(boolean isNeedMatch) {
		this.isNeedMatch = isNeedMatch;
	}
	
}
