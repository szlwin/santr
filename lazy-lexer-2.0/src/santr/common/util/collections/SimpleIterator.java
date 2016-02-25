package santr.common.util.collections;

import java.util.Iterator;

public class SimpleIterator<E> implements Iterator<E>{
	private E[] eData;
	
	private int size = 0;
	
	private int offset = -1;
	
	public SimpleIterator(E[] e){
		eData = e;
		size = eData.length;
	}
	public boolean hasNext() {
		return offset < size-1;
	}

	public E next() {
		offset++;
		return eData[offset];
	}

	public void remove() {
		
	}

}
