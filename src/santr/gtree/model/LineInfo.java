package santr.gtree.model;

public class LineInfo {

	private int start;
	
	private int deep;
	
	private int end;

	private String token;
	
	private int id;
	
	private int leafId;
	
	private Runner runner;
	
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
	
}
