package santr.common.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import santr.v4.parser.ParserTree;

public class SimpleTreeList implements List<ParserTree> {

	private int size = 0;
	
	private transient ParserTree[] elementData;
	
	private transient int DEFAULT_SIZE = 2;
	
	private transient int ARRAY_SIZE = 0;
	
	private transient int AUTO_SIZE = 2;
	
	public SimpleTreeList(){
		elementData = new ParserTree[DEFAULT_SIZE];
		ARRAY_SIZE = DEFAULT_SIZE;
	}
	
	public SimpleTreeList(int size,int autoSize){
		elementData = new ParserTree[size];
		AUTO_SIZE = autoSize;
	}
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean contains(Object o) {
		for(Object element : elementData ){
			if(element == o ){
				return true;
			}
		}
		return false;
	}

	public Iterator<ParserTree> iterator() {
		ParserTree[] eData = new ParserTree[size];
		System.arraycopy(elementData, 0, eData, 0, size);
		SimpleIterator<ParserTree> simpleIterator = new SimpleIterator<ParserTree>(eData);
		return simpleIterator;
	}

	public Object[] toArray() {
		return elementData;
	}

	public <T> T[] toArray(T[] a) {
		int count = size;
		if(a.length < size){
			count = a.length;
		}
		System.arraycopy(elementData, 0, a, 0, count);
		return a;
	}

	public boolean add(ParserTree e) {
		if(ARRAY_SIZE == size){
			copy();
		}
		elementData[size] = e;
		size++;
		return true;
	}

	private void copy(){
		ParserTree[] eData = new ParserTree[AUTO_SIZE+elementData.length];
		System.arraycopy(elementData, 0, eData, 0, elementData.length);
		elementData = eData;
		ARRAY_SIZE = elementData.length;
	}
	
	public boolean remove(Object o) {
		if(size == 1){
			Object element = elementData[0];
			if(element == o){
				element = null;
				size = size -1;
				return true;
			}
		}else{
			for(int i = size-1 ; i >0;i-- ){
				Object element = elementData[i];
				if(element == o){
					element = null;
					size = size -1;
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	public boolean addAll(Collection<? extends ParserTree> c) {
		return false;
	}

	public boolean addAll(int index, Collection<? extends ParserTree> c) {
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		return false;
	}

	public void clear() {
		
	}

	public ParserTree get(int index) {
		return elementData[index];
	}

	public ParserTree set(int index, ParserTree element) {
		elementData[index] = element;
		return null;
	}

	public void add(int index, ParserTree element) {
		
	}

	public ParserTree remove(int index) {
		return null;
	}

	public int indexOf(Object o) {
		return 0;
	}

	public int lastIndexOf(Object o) {
		return 0;
	}

	public ListIterator<ParserTree> listIterator() {
		return null;
	}

	public ListIterator<ParserTree> listIterator(int index) {
		return null;
	}

	public List<ParserTree> subList(int fromIndex, int toIndex) {
		return null;
	}

	//@Override
	//public boolean contains(Object o) {
		// TODO Auto-generated method stub
	//	return false;
	//}



}
