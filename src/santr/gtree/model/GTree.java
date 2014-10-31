package santr.gtree.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import santr.common.util.collections.SimpleList;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.v3.parser.TokenString;



public class GTree extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9214839228261850320L;

	private Map<String,GTree> toKenMap = new HashMap<String,GTree>();
	
	private String name;
	
	private GTree parent;
	
	private GTree[] gTreeArray;
	
	private GTYPE type;
	
	private Token token;
	
	private String relName;
	
	//private String match;
	
	private GTree rel;
	
	private int id = -1;
	
	private TerminalData terminalData;
	
	private List<GTree> nodeList = new SimpleList<GTree>();
	
	private int flag;
	
	private int index;
	
	private int group;
	
	private GROUPTYPE groupType;
	
	private int level = 0;
	
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

	public Map<String, GTree> getToKenMap() {
		return toKenMap;
	}

	public void setToKenMap(Map<String, GTree> toKenMap) {
		this.toKenMap = toKenMap;
	}

	public String getMatch() {
		return this.terminalData.getMatch();
	}
	
	public GTree getNodeByToken(TokenString token,int index){
		
		for(int i = 0; i < this.nodeList.size();i++){
			GTree node = nodeList.get(i);

			if(node.getRel().getTerminalData().matcher(token)){
				return node;
			}
		}
		/*
		if(this.type == GTYPE.EXPRESS
				|| this.type == GTYPE.LEAF){
			
			if(!nodeList.isEmpty()){
				for(int i = 0; i < this.nodeList.size();i++){
					GTree node = nodeList.get(i);

					if(node.getTerminalData().matcher(token)){
						return node;
					}
				}
			}else{
				if(gTreeArray !=null){
					if(gTreeArray[index].getType() == GTYPE.LEAF){
						for(int i = 0; i < this.gTreeArray.length;i++){
							GTree node = gTreeArray[i].getNodeByToken(token);;

							if(node!=null){
								return node;
							}
						}
					}else{
						if(gTreeArray[index].getGroup() == 0 
								|| gTreeArray[index].getGroupType() == GROUPTYPE.ONE_OR_MANY)
							 return gTreeArray[index].getNodeByToken(token);
					}
				}
			}
		}*/
		//if(this.type == GTYPE.LBS){
		//	return this.getRel().getNodeByToken(token);
		//}
				
		if(this.type == GTYPE.NODE){
			if(terminalData.matcher(token))
				return this;
		}
		return null;
		
	}
	
	public GTree getNodeByToken(TokenString token){
		return getNodeByToken(token,0);
		
	}
	/*
	public GTree getTreeByToken(String token,String name){
		if(name!=null && this.name.equals(name)){
			return null;
		}
		
		if(this.type == GTYPE.EXPRESS){

			for( int i = 0 ; i < gTreeArray.length ;i++){ 
				if(gTreeArray[i].getToken() != null
						&& gTreeArray[i].getToken().getTFlagMap().containsKey(token)){
					return gTreeArray[i];
				}
			}
			
			for( int i = 0 ; i < gTreeArray.length ;i++){ 
				GTree gg = gTreeArray[i].getTreeByToken(token,name);
				if(gg!=null){
					return gg;
				}
			}
		}
		
		if(this.type == GTYPE.LBS){
			GTree rr =  getRel().getTreeByToken(token,name);
			if(rr != null){
				return rr;
			}
		}
		
		if(this.type == GTYPE.LEAF){

			if(this.token !=null && this.token
					.getTFlagMap().containsKey(token)){
					return this;
			}else{
				for( int i = 0 ; i < gTreeArray.length ;i++){
					GTree tmp = gTreeArray[i];
					if(tmp.getType() == GTYPE.EXPRESS
							|| tmp.getType() == GTYPE.LBS){
						GTree rr = tmp.getTreeByToken(token,name);
						if(rr !=null)
							return rr;
					}
				}
			}
		}

		return null;
	}*/

	public boolean isExistToken(int id){
		return this.getToken().containsKey(id);
	}
	
	public GTree getTreeByTokenN(int token,String name){
		if(name!=null && this.name.equals(name)){
			return null;
		}
		
		if(this.type == GTYPE.EXPRESS){

			if(this.getToken() != null
					&& this.getToken().containsKey(token)){
				return this;
			}
			else{
				for( int i = 0 ; i < gTreeArray.length ;i++){ 
					if(gTreeArray[i].getType() != GTYPE.NODE){
						GTree gg = gTreeArray[i].getTreeByTokenN(token,name);
						if(gg!=null){
							return gg;
						}
					}
				}
			}
		}
		
		
		if(this.type == GTYPE.LEAF){

			if(this.token !=null && this.token.containsKey(token)){
					return this;
			}else{
				for( int i = 0 ; i < gTreeArray.length ;i++){
					GTree tmp = gTreeArray[i];
					if(tmp.getType() == GTYPE.EXPRESS
							|| tmp.getType() == GTYPE.LBS){
						GTree rr = tmp.getTreeByTokenN(token,name);
						if(rr !=null)
							return rr;
					}
				}
			}
		}
		return null;
	}
	
	public GTree getRel() {
		return rel;
	}

	public void setRel(GTree rel) {
		this.rel = rel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void addNode(GTree gtree){
		for(int i = 0; i  < nodeList.size();i++){
			if(nodeList.get(i).getName().equals(gtree.getName())){
				return;
			}
		}
		//if(!nodeList.contains(gtree)){
			nodeList.add(gtree);
		//}
		
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

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
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
	
}
