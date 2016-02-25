package santr.v4.parser;

import java.util.ArrayDeque;
import java.util.List;
import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.v4.parser.ParserTree;

public class TreeJumper {

	private ToolUtil toolUtil;
	
	// ParserTree currentTree;
	
	private LineInfo lineInfo;
	
	//private List<LineInfo> mList = new FastTable<LineInfo>();
	
	private ArrayDeque<LineInfo> mArray;
	
	private boolean isUp = false;
	
	//private boolean isEmpty = false;
	public TreeJumper(ToolUtil toolUtil){
		this.toolUtil = toolUtil;
	}
	/*
	public boolean enter(ParserTree currentTree){
		
		LineInfo lineInfo = seachFirstTree(currentTree);
		//count++;
		
		if(lineInfo == null){
			return false;
		}
		
		this.lineInfo = lineInfo;
		//mList.clear();
		//clear();
		return true;
		
	}*/
	
	public boolean jump(ParserTree rTree){

		LineInfo lineInfo = filter(rTree);
		
		if(lineInfo == null){
			return false;
		}
		isUp = lineInfo.getDeep() == -1;
		this.lineInfo = lineInfo;

		return true;
	}
	/*
	private LineInfo seachFirstTree(ParserTree currentTree){
		if(!mList.isEmpty()){
			int i = 0;
			for(; i < mList.size();i++){
				//GTree gTree = toolUtil.getTargetTree(mList.get(i));
				
				GTree gTree = mList.get(i).getExpress();
				
				if(!checkMatch(gTree)){
					mList.remove(i);
					i = i-1;
				}
			}
		}else{
			List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);

			if(lineInfoList == null){
				toolUtil.scan(currentTree);
				lineInfoList = toolUtil.getLineInfo(currentTree);
			}

			
			if(lineInfoList.size() == 1){
			
				return lineInfoList.get(0);
			}else{
				mList.addAll(lineInfoList);
				int i = 0;
				for(; i < mList.size();i++){
					LineInfo lineInfo = mList.get(i);
					
					//GTree cleaf = toolUtil.getTargetTree(lineInfo);
					GTree cleaf = lineInfo.getExpress();
					int deep = lineInfo.getDeep();
					
					if(deep>=0 ){
						if(!checkMatch(cleaf)){
							mList.remove(i);
							i = i-1;
						}
					}else{
						mList.remove(i);
						i = i-1;
					}
				}
			}
		}

		if(mList.size()==1){
			LineInfo lineInfo = mList.get(0);
			mList.clear();
			return lineInfo;
		}
		return null;
		
	}
	
	private LineInfo filter1(ParserTree currentTree){
		LineInfo upLineInfo = null;
		LineInfo errorLineInfo = null;
		if(mList.isEmpty()){
			List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);

			if(lineInfoList == null){
				toolUtil.scan(currentTree);
				lineInfoList = toolUtil.getLineInfo(currentTree);
			}
			
			if(lineInfoList.size() == 1 ){
				LineInfo lineInfo = lineInfoList.get(0);
				if(lineInfo.getDeep() == 0){
					if(currentTree.isEmpty()){
						return lineInfo;
					}else{
						GTree cleaf = lineInfo.getExpress();
					
						int index = cleaf.getToken().getTFlag(
								toolUtil.getTokenString().getId()).getLbs().getIndex();
						GTree lbs = cleaf.getgTreeArray()[index];
						if(currentTree.getRuleContext().checkExpress(lbs)){
							return lineInfo;
						}
					}
					errorLineInfo = lineInfo;
					toolUtil.scan(currentTree);
				}
			}
		}
		
		List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);
			
		if(lineInfoList.size() == 1){
		
			return lineInfoList.get(0);
		}else{
			mList.addAll(lineInfoList);
			int i = 0;
			for(; i < mList.size();i++){
				LineInfo lineInfo = mList.get(i);
				if(lineInfo == errorLineInfo){
					mList.remove(i);
					i = i-1;
					continue;
				}
				//GTree cleaf = toolUtil.getTargetTree(lineInfo);
				GTree cleaf = lineInfo.getExpress();
				int deep = lineInfo.getDeep();
				
				if(deep>=0 ){

					if(!checkMatch(cleaf)){
						mList.remove(i);
						i = i-1;
					}
				}else{
					upLineInfo = lineInfo;
					mList.remove(i);
					i = i-1;
				}
			}
		}
		
		if(mList.isEmpty()){
			return upLineInfo;
		}
		

		if(mList.size()==1){
			LineInfo lineInfo = mList.remove(0);
			//mList.clear();
			return lineInfo;
		}
		return null;
	}
	

	private LineInfo filter2(ParserTree currentTree){
		

		LineInfo upLineInfo = null;
		if(!mList.isEmpty()){
			int i = 0;
			for(; i < mList.size();i++){
				//GTree gTree = toolUtil.getTargetTree(mList.get(i));
				
				GTree gTree = mList.get(i).getExpress();
				
				if(!checkMatch(gTree)){
					mList.remove(i);
					i = i-1;
				}
			}
		}else{
			List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);

			if(lineInfoList == null){
				toolUtil.scan(currentTree);
				lineInfoList = toolUtil.getLineInfo(currentTree);
			}
			if(lineInfoList == null){
				//this.isUp = true;
				LineInfo lineInfo = new LineInfo();
				lineInfo.setDeep(-1);
				return new LineInfo();
			}
			LineInfo errorLineInfo = null;
			if(lineInfoList.size() == 1 ){
				LineInfo lineInfo = lineInfoList.get(0);
				if(lineInfo.getDeep() == 0){
					if(currentTree.isEmpty()){
						return lineInfo;
					}else{
						GTree cleaf = lineInfo.getExpress();
					
						int index = cleaf.getToken().getTFlag(
								toolUtil.getTokenString().getId()).getLbs().getIndex();
						GTree lbs = cleaf.getgTreeArray()[index];
						if(currentTree.getRuleContext().checkExpress(lbs)){
							return lineInfo;
						}
					}
					errorLineInfo = lineInfo;
					toolUtil.scan(currentTree);
					lineInfoList = toolUtil.getLineInfo(currentTree);
				}
			}

			
			if(lineInfoList.size() == 1){
			
				return lineInfoList.get(0);
			}else{
				mList.addAll(lineInfoList);
				int i = 0;
				for(; i < mList.size();i++){
					LineInfo lineInfo = mList.get(i);
					if(lineInfo == errorLineInfo){
						mList.remove(i);
						i = i-1;
						continue;
					}
					//GTree cleaf = toolUtil.getTargetTree(lineInfo);
					GTree cleaf = lineInfo.getExpress();
					int deep = lineInfo.getDeep();
					
					if(deep>=0 ){

						if(!checkMatch(cleaf)){
							mList.remove(i);
							i = i-1;
						}
					}else{
						upLineInfo = lineInfo;
						mList.remove(i);
						i = i-1;
					}
				}
			}
			
			if(mList.isEmpty()){
				return upLineInfo;
			}
		}

		if(mList.size()==1){
			LineInfo lineInfo = mList.get(0);
			mList.clear();
			return lineInfo;
		}
		return null;
	}*/

	private LineInfo filter(ParserTree currentTree){
		

		LineInfo upLineInfo = null;
		if(mArray!=null && !mArray.isEmpty()){
			LineInfo checkLineInfo = mArray.peek();
			while(checkLineInfo !=null){
				GTree gTree = checkLineInfo.getExpress();
				
				if(!checkMatch(gTree)){
					mArray.poll();
				}
			}
			/*
			int i = 0;
			for(; i < mList.size();i++){
				//GTree gTree = toolUtil.getTargetTree(mList.get(i));
				
				GTree gTree = mList.get(i).getExpress();
				
				if(!checkMatch(gTree)){
					mList.remove(i);
					i = i-1;
				}
			}*/
		}else{
			List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);

			if(lineInfoList == null){
				toolUtil.scan(currentTree);
				lineInfoList = toolUtil.getLineInfo(currentTree);
			}
			if(lineInfoList == null){
				LineInfo lineInfo = new LineInfo();
				lineInfo.setDeep(-2);
				lineInfo.setStart(currentTree.getRuleContext().getLeaf().getId());
				lineInfo.setLeafId(-1);
				lineInfo.setToken(toolUtil.getToken());
				lineInfo.setId(toolUtil.getTokenString().getId());
				return lineInfo;
			}
			LineInfo errorLineInfo = null;
			if(lineInfoList.size() == 1 ){
				LineInfo lineInfo = lineInfoList.get(0);
				if(lineInfo.getDeep() == 0){
					if(currentTree.isEmpty()){
						return lineInfo;
					}else{
						GTree cleaf = lineInfo.getExpress();
					
						//int index = cleaf.getToken().getTFlag(
						//		toolUtil.getTokenString().getId()).getLbs().getIndex();
						GTree lbs = cleaf.getToken().getTFlag(
								toolUtil.getTokenString().getId()).getLbs();
						if(currentTree.getRuleContext().checkExpress(lbs)){
							return lineInfo;
						}
					}
					errorLineInfo = lineInfo;
					toolUtil.scan(currentTree);
					lineInfoList = toolUtil.getLineInfo(currentTree);
				}
			}

			
			if(lineInfoList.size() == 1){
			
				return lineInfoList.get(0);
			}else{
				mArray =  new ArrayDeque<LineInfo>(lineInfoList.size());
				//mArray.addAll(lineInfoList);
				//mList.addAll(lineInfoList);
				
				int i = 0;
				for(; i < lineInfoList.size();i++){
					LineInfo lineInfo = lineInfoList.get(i);
					if(lineInfo == errorLineInfo){
						//mList.remove(i);
						//i = i-1;
						continue;
					}
					//GTree cleaf = toolUtil.getTargetTree(lineInfo);
					GTree cleaf = lineInfo.getExpress();
					int deep = lineInfo.getDeep();
					
					if(deep>=0 ){

						if(checkMatch(cleaf)){
							mArray.add(lineInfo);
							//i = i-1;
						}
					}else{
						upLineInfo = lineInfo;
						//mList.remove(i);
						//i = i-1;
					}
				}
			}
			
			if(mArray.isEmpty()){
				return upLineInfo;
			}
		}

		if(mArray.size()==1){
			LineInfo lineInfo = mArray.remove();
			//mList.clear();
			return lineInfo;
		}
		return null;
	}
	private boolean checkMatch(GTree gTree){
		
		return toolUtil.checkMatch(gTree);
	}

	public LineInfo getLineInfo() {
		return lineInfo;
	}

	public ParserTree getDestTree() {
		return null;
	}

	public boolean isUp() {
		return isUp;
	}
	
}
