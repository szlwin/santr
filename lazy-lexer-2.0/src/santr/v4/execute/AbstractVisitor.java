package santr.v4.execute;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.parser.ParserTree;
import santr.v4.parser.RuleContext;
import santr.v4.parser.TreeUtil;


public abstract class AbstractVisitor<E> implements Visitor<E>{
	
	//private ParserTree root;
	//public Object getResult(){
	//	return root.getRuleContext().getValue();
	//}
	//private RuleContext reExecuteContext;
	
	private E paramer;
	public void vist(ParserTree rTree) throws ExecuteInvaildException{
		vist(rTree,null);

	}
	
	public void setParamer(E object){
		paramer = object;
	}
	
	public E getParamer(){
		return paramer;
	}
	/*
	private void vist(String name,Context context,Object param){
		List<RTree> rTreeList = context.getAllChild();

		for(int i = 0;i < rTreeList.size();i++){
			RTree subTree = rTreeList.get(i);
			if((subTree.getType() == BData.FLAG_TYPE_VAR 
					|| subTree.getType() == BData.FLAG_TYPE_NODE
					|| subTree.getType() == 0)
					&& subTree.getName().equals(name)){
				this.vist(subTree,param);
			}
		}
	}*/
	
	protected final ParserTree[] getAllChild(RuleContext context){
		//List<TreeInfo> treeList = new FastTable<TreeInfo>();
		/*for(int i =0; i < subTreeList.size();i++){
			ParserTree rTree = subTreeList.get(i);
			TreeInfo treeInfo = new TreeInfo();
			treeInfo.setIndex(i);
			treeInfo.setName(rTree.getName());
			if(rTree.getType() == BData.FLAG_TYPE_TOKEN){
				treeInfo.setToken(true);
				treeInfo.setToken(rTree.getText());
			}
			treeList.add(treeInfo);
		}*/
		return context.getAllChild();
		
	}
	
	protected final Object[] getAllChildValue(RuleContext context) throws ExecuteInvaildException{
		return getAllChildValue(context,null);
	}
	
	protected final Object[] getAllChildValue(RuleContext context,Object param) throws ExecuteInvaildException{
		ParserTree[] subTreeList = context.getAllChild();
		Object[] treeList = new Object[subTreeList.length];
		
		for(int i = 0; i < subTreeList.length;i++){
			Object value = this.getChildValue(context, i, param);
			treeList[i]=value;
		}
		return treeList;
		
	}
	
	protected final Object[] getChildValueByName(RuleContext context,String name) throws ExecuteInvaildException{
		return getChildValueByName(context,name,null);
		
	}
	
	protected final Object[] getChildValueByName(RuleContext context,String name,Object param) throws ExecuteInvaildException{
		//ParserTree[] subTreeList = context.getAllChild();
		Object[] treeList = new Object[context.getChildCount()];
		
		for(int i = 0; i < context.getChildCount();i++){
			ParserTree subTree = context.getChild(i);
			if(subTree.getName()!=null 
					&& name.equals(subTree.getName())){
				Object value = this.getChildValue(context,i,param);
				treeList[i]=value;
			}
		}
		return treeList;
	}
	
	protected final Object getChildValue(RuleContext context,int index,Object param) throws ExecuteInvaildException{
		ParserTree rTree = context.getChild(index);
		return getChildValue(rTree,param);
	}
	
	protected final Object getChildValue(ParserTree rTree,Object param) throws ExecuteInvaildException{
		RuleContext context = rTree.getRuleContext();
		if(rTree.isToken()){
			return context.getText();
		}
		if(TreeUtil.isFinish(context) && param==null){
			return rTree.getRuleContext().getValue();
		}else{
			vist(rTree,param);
			//if(context.isNeedReExecute()){
			//	this.execute(context);
			//}
			return rTree.getRuleContext().getValue();
		}
	}
	protected final Object getChildValue(ParserTree rTree) throws ExecuteInvaildException{
		
		return getChildValue(rTree,null);
		
	}

	protected final Object getChildValue(RuleContext context,int index) throws ExecuteInvaildException{
		return getChildValue(context,index,null);
	}
	
	protected final Object getChildParam(RuleContext context,int index){
		ParserTree rTree = context.getChild(index);
		return getChildParam(rTree.getRuleContext());
	}
	
	protected final Object getChildParam(RuleContext context){
		//ParserTree rTree = context.getChild(index);
		//context.setNeedReExecute(true);
		//context.getParserTree().getParent().getRuleContext().setNeedReExecute(true);
		return context.getParam();
	}
	
	private void vist(ParserTree rTree, Object param) throws ExecuteInvaildException {
		//if(rTree.getParent() == null){
		//	root = rTree;
		//}
		
		RuleContext context = rTree.getRuleContext();
		//rTree.setContext(context);
		
		if(param != null){
			context.setParam(param);
		}
		
		execute(rTree.getRuleContext());
		
		//if(context.isNeedReExecute()){
		//	context.getParserTree().getParent().getRuleContext().setNeedReExecute(true);
		//	context.setNeedReExecute(false);
		//}
		//context.setFinish(true);
		TreeUtil.setFinish(context);
		
		/*List<RTree> rTreeList = rTree.getrTreeList();
		
		for(int i = 0; i < rTreeList.size();i++){
			RTree subTree = rTreeList.get(i);
			if((subTree.getType() == BData.FLAG_TYPE_VAR 
					|| subTree.getType() == BData.FLAG_TYPE_NODE
					|| subTree.getType() == 0)
					&& !subTree.isOk()){
				vistor(subTree);
			}
		}*/

	}
}
