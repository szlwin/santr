package santr.gtree.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;

import santr.common.util.collections.SimpleList;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.v4.parser.TokenString;



public class GTree extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9214839228261850320L;
	
	private String name;
	
	private GTree parent;
	
	private GTree[] gTreeArray;
	
	private GTYPE type;
	
	private Token token;
	
	private GTree rel;
	
	private int id = -1;
	
	private TerminalData terminalData;
	
	private List<GTree> nodeList;
	
	private List<GTree> selfNodeList;
	
	private List<GTree> selfFirstList;
	
	private int flag;
	
	private int index;
	
	private int group;
	
	private GROUPTYPE groupType;
	
	private int level = 0;
	
	private boolean isLast = false;
	
	private boolean isExitFork = false;
	
	private Map<Integer,List<LineInfo>> allForkGraph;
	
	private boolean isExistLevel = false;
	
	private List<GTree> lbsList;
	
	private Map<Integer,GTree> skipMap;
	private Map<Integer,GTree> lbsMap;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GTree getParent() {
		return parent;
	}

	public void setParent(GTree parent) {
		this.parent = parent;
	}

	public GTree[] getgTreeArray() {
		return gTreeArray;
	}

	public void setgTreeArray(GTree[] gTreeArray) {
		
		this.gTreeArray = gTreeArray;
		
	}

	public GTYPE getType() {
		return type;
	}

	public void setType(GTYPE type) {
		this.type = type;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getMatch() {
		return this.terminalData.getMatch();
	}
	
	//self and not first
	public GTree getSelfNodeByToken(TokenString token){
		if(this.rel.getSelfNodeList() == null)
			return null;
		for(int i = 0; i < this.rel.getSelfNodeList().size();i++){
			GTree node = this.rel.getSelfNodeList().get(i);
			
			if(node.getRel().getTerminalData().matcher(token)){
				return node;
			}
			
		}
		return null;
	}
	
	//self and first
	public GTree getSelfFirstNodeByToken(TokenString token){
		if(this.rel.getSelfFirstList() == null)
			return null;
		for(int i = 0; i < this.rel.getSelfFirstList().size();i++){
			GTree node = this.rel.getSelfFirstList().get(i);
			
			if(node.getRel().getTerminalData().matcher(token)){
				return node;
			}
			
		}
		return null;
	}
	
	//child and first
	public List<GTree> getFirstNodeByToken(TokenString token){
		List<GTree> gTreeList = new FastTable<GTree>();
		
		
		if(this.rel.getType() == GTYPE.NODE){
			if(terminalData.matcher(token))
				gTreeList.add(this);
				return gTreeList;
		}
		
		for(int i = 0; i < this.rel.getNodeList().size();i++){
			GTree node = this.rel.getNodeList().get(i);
			
			if(node.getRel().getTerminalData().matcher(token)){
				gTreeList.add(node) ;
			}
			
		}
		return gTreeList;
	}
	
	public GTree getNodeByToken(TokenString token,boolean isFirst){
		
		if(this.rel.getType() == GTYPE.NODE){
			if(terminalData.matcher(token))
				return this;
		}
		

		for(int i = 0; i < this.rel.getNodeList().size();i++){
			GTree node = this.rel.getNodeList().get(i);
			//System.out.println(node.getIndex()+":"+node.getName()+":"+this.getName()+":"+node.getParent().getName());
			if(isFirst
					&& node.getIndex() > 0){
				continue;
			}
			
			if(node.getRel().getTerminalData().matcher(token)){
				return node;
			}
		}
		return null;
		
	}
	
	public GTree getRel() {
		return rel;
	}

	public void setRel(GTree rel) {
		if(this.type == GTYPE.LEAF){
			this.rel = this;
		}else{
			this.rel = rel;
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	private void addSelfNode(GTree gtree){
		if(selfNodeList == null){
			selfNodeList = new SimpleList<GTree>();
		}
		
		if(!selfNodeList.contains(gtree)){
			//System.out.println(this.getIndex()+":"+this.getName()+":"+gtree.getName());
			selfNodeList.add(gtree);
		}
	}
	private void addSelfFirstNode(GTree gtree){
		
		if(selfFirstList == null){
			selfFirstList = new SimpleList<GTree>();
		}
		
		if(!selfFirstList.contains(gtree)){
			//System.out.println(this.getIndex()+":"+this.getName()+":"+gtree.getName());
			selfFirstList.add(gtree);
		}
	}
	
	private void addFirstNode(GTree gtree){
		if(nodeList == null){
			nodeList = new SimpleList<GTree>();
		}
		
		if(!nodeList.contains(gtree)){
			
			//System.out.println(this.getIndex()+":"+this.getName()+":"+gtree.getName());
			
				nodeList.add(gtree);
			
			
		}
	}
	
	public void addNode(GTree gtree){
		
		if(gtree.getParent() == this
				|| (gtree.getParent().getType() == GTYPE.LEAF 
				&& gtree.getParent().getParent() == this)){
			//if(gtree.getType() == GTYPE.LBS){
			//	gtree = gtree.getRel();
			//}
			if(gtree.getIndex() == 0){
				this.addSelfFirstNode(gtree);
			}else{
				this.addSelfNode(gtree);
			}
		}else{
			if(gtree.getIndex() == 0)
				this.addFirstNode(gtree);
		}
		
	}
	public void addAllNode(List<GTree> nList){
		for(int i = 0;i < nList.size();i++){
			addNode(nList.get(i));
		}
	}
	
	public TerminalData getTerminalData() {
		return terminalData;
	}

	public void setTerminalData(TerminalData terminalData) {
		this.terminalData = terminalData;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public GROUPTYPE getGroupType() {
		return groupType;
	}

	public void setGroupType(GROUPTYPE groupType) {
		this.groupType = groupType;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<GTree> getNodeList() {
		return nodeList;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isTerminalNode(){
		return this.getRel().type == GTYPE.NODE;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public void add(int id,LineInfo lineInfo){
		//if(lineInfo.getDeep()==-1){
		//	return;
		//}
		if(allForkGraph == null){
			allForkGraph = new FastMap<Integer,List<LineInfo>>();
		}

		if(!allForkGraph.containsKey(id)){
			List<LineInfo> list = new SimpleList<LineInfo>();
			list.add(lineInfo);
			allForkGraph.put(id, list);
		}else{
			List<LineInfo> list = allForkGraph.get(id);
			boolean isExist =false;
			for(int j = 0; j < list.size();j++){
				LineInfo lInfo = list.get(j);
				if(lInfo.getToken().equals(lineInfo.getToken())
						&& lInfo.getEnd() == lineInfo.getEnd()
						&& lInfo.getDeep() == lineInfo.getDeep()
						&& lInfo.getLeafId() == lineInfo.getLeafId()){
					isExist = true;
					break;
				}//else if(lInfo.getExpress() == lineInfo.getExpress()
				//		&& lInfo.getDeep() == lineInfo.getDeep()){
				//	isExist = true;
				//	break;
				//}
			}
			
			if(!isExist){
				list.add(lineInfo);
				/*TFlag tFlag = lineInfo.getExpress().getToken().getTFlag(id);
				
				if(tFlag.getLbs().getGroup() == 0
						&& tFlag.getLbs().getIndex() == 0){
					if(firstToken ==null){
						firstToken = new FastMap<Integer,GTree>();
						
					}
					firstToken.put(id, lineInfo.getExpress());
				}*/
			}
		}
	}
	
	public GTree getNext(){
		if(this.isLast() 
				|| this.getParent() == null
				|| this.getParent().getgTreeArray().length == this.getIndex()+1)
			return null;

		
		return this.getParent().getgTreeArray()[this.getIndex()+1];
	}
	
	public List<LineInfo> get(int id){
		if(allForkGraph == null){
			return null;
		}
		return allForkGraph.get(id);
	}
	
	public boolean isExitFork() {
		return isExitFork;
	}

	public void setExitFork(boolean isExitFork) {
		this.isExitFork = isExitFork;
	}

	public Map<Integer, List<LineInfo>> getAllForkGraph() {
		return allForkGraph;
	}

	public void println(){
		if(allForkGraph == null){
			return;
		}
		Collection<List<LineInfo>> c = allForkGraph.values();
		Iterator<List<LineInfo>> it = c.iterator();
		while(it.hasNext()){
			List<LineInfo> lineInfoList = it.next();
			for(int j = 0; j < lineInfoList.size();j++){
				LineInfo lineInfo = lineInfoList.get(j);
				System.out.println("start="+lineInfo.getStart()
						+",end="+lineInfo.getEnd()
						+",token="+lineInfo.getToken()
						+",deep="+lineInfo.getDeep()
						+",leaf="+lineInfo.getLeafId()
						+",gExpress="+lineInfo.getExpress().getId()
						//+",express="+lineInfo.getExpress().getId()
						);
			}
		}
	}

	public boolean isExistLevel() {
		return isExistLevel;
	}

	public void setExistLevel(boolean isExistLevel) {
		this.isExistLevel = isExistLevel;
	}

	public List<GTree> getSelfNodeList() {
		return selfNodeList;
	}

	public List<GTree> getSelfFirstList() {
		return selfFirstList;
	}

	public List<GTree> getLbsList() {
		return lbsList;
	}
	
	public void addLBS(GTree lbs){
		if(lbsList == null){
			lbsList = new SimpleList<GTree>();
		}
		lbsList.add(lbs);
	}
	
	public void addSkipToken(int id){
		if(skipMap == null){
			skipMap =  new FastMap<Integer, GTree>();
		}
		skipMap.put(id, null);
	}
	
	public boolean isSkipToken(int id){
		if(this.getType() == GTYPE.LEAF){
			return this.getParent().isSkipToken(id);
		}
		if(skipMap == null){
			skipMap =  new FastMap<Integer, GTree>();
			for(GTree subTree:gTreeArray){
				if(subTree.getToken()!=null){
					TFlag tflagArr[] = subTree.getToken().getTflag();
					for(TFlag tFlag:tflagArr){
						if(tFlag.getLbs().getParent().getLevel()>0){
							skipMap.put(tFlag.getId(), tFlag.getLbs());
						}
					}
				}
			}
		}
		//System.out.println(gTreeArray);
		//System.out.println(skipMap);
		return skipMap.containsKey(id);
	}
	public GTree getSkipTokenTree(int id){
		if(this.getType() == GTYPE.LEAF){
			return this.getParent().getSkipTokenTree(id);
		}
		if(skipMap == null){
			skipMap =  new FastMap<Integer, GTree>();
			for(GTree subTree:gTreeArray){
				if(subTree.getToken()!=null){
					TFlag tflagArr[] = subTree.getToken().getTflag();
					for(TFlag tFlag:tflagArr){
						if(tFlag.getLbs().getParent().getLevel()>0){
							skipMap.put(tFlag.getId(), tFlag.getLbs());
						}
					}
				}
			}
		}
		return skipMap.get(id);
	}
	
	public void addSubLBS(GTree gTree){
		if(lbsMap == null){
			lbsMap = new FastMap<Integer, GTree>();
		}
		lbsMap.put(gTree.getRel().getId(), gTree);
		if(this.getType() == GTYPE.LEAF){
			this.getParent().addSubLBS(gTree);
		}
	}
	public GTree getSubLBS(GTree gTree){
		if(lbsMap == null){
			return null;
		}
		if(gTree.getRel().getType()==GTYPE.LEAF){
			return lbsMap.get(gTree.getRel().getParent().getId());
		}
		return lbsMap.get(gTree.getRel().getId());
	}

	public GTree getNodeByToken(santr.v3.parser.TokenString tokenString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
