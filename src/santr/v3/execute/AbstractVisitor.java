package santr.v3.execute;

import java.util.List;

import santr.gtree.model.BData;
import santr.v3.parser.data.RTree;

import javolution.util.FastTable;


public abstract class AbstractVisitor implements Visitor{
	
	private RTree root;
	public Object getResult(){
		return root.getContext().getValue();
	}
	
	public void vist(RTree rTree){
		vist(rTree,null);

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
	
	protected final List<TreeInfo> getAllChild(Context context){
		List<RTree> subTreeList = context.getAllChild();
		List<TreeInfo> treeList = new FastTable<TreeInfo>();
		for(int i =0; i < subTreeList.size();i++){
			RTree rTree = subTreeList.get(i);
			TreeInfo treeInfo = new TreeInfo();
			treeInfo.setIndex(i);
			treeInfo.setName(rTree.getName());
			if(rTree.getType() == BData.FLAG_TYPE_TOKEN){
				treeInfo.setToken(true);
				treeInfo.setToken(rTree.getText());
			}
			treeList.add(treeInfo);
		}
		return treeList;
		
	}
	
	protected final List<Object> getAllChildValue(Context context){
		return getAllChildValue(context,null);
	}
	
	protected final List<Object> getAllChildValue(Context context,Object param){
		List<RTree> subTreeList = context.getAllChild();
		List<Object> treeList = new FastTable<Object>();
		
		for(int i = 0; i < subTreeList.size();i++){
			Object value = this.getChildValue(context, i, param);
			treeList.add(value);
		}
		return treeList;
		
	}
	
	protected final List<Object> getChildValueByName(Context context,String name){
		return getChildValueByName(context,name,null);
		
	}
	
	protected final List<Object> getChildValueByName(Context context,String name,Object param){
		List<RTree> subTreeList = context.getAllChild();
		List<Object> treeList = new FastTable<Object>();
		
		for(int i = 0; i < subTreeList.size();i++){
			RTree subTree = subTreeList.get(i);
			if(subTree.getName()!=null 
					&& name.equals(subTree.getName())){
				Object value = this.getChildValue(context,i,param);
				treeList.add(value);
			}
		}
		return treeList;
	}
	
	protected final Object getChildValue(Context context,int index,Object param){
		RTree rTree = context.getSubTree(index);
		if(rTree.getType() == BData.FLAG_TYPE_TOKEN){
			return rTree.getText();
		}
		if(context.isFinish()){
			return rTree.getContext().getValue();
		}else{
			vist(rTree,param);
			return rTree.getContext().getValue();
		}
	}
	
	protected final Object getChildValue(Context context,int index){
		return getChildValue(context,index,null);
	}
	
	protected final Object getChildParam(Context context,int index){
		RTree rTree = context.getSubTree(index);
		return rTree.getContext().getParam();
	}
	
	private void vist(RTree rTree, Object param) {
		if(rTree.getParentTree() == null){
			root = rTree;
		}
		
		Context context = createContext(rTree);
		rTree.setContext(context);
		
		if(param != null){
			context.setParam(param);
		}
		
		execute(rTree.getContext());
		context.setFinish(true);
		
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
	
	private Context createContext(RTree rTree){
		Context context =  new Context();
		context.setrTree(rTree);
		return context;
	}
}
