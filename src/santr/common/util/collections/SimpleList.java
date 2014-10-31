package santr.common.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SimpleList<E> implements List<E> {

	private int size = 0;
	
	private  E[] elementData;
	
	private int DEFAULT_SIZE = 2;
	
	private int ARRAY_SIZE = 0;

	public SimpleList(){
		elementData = (E[])new Object[DEFAULT_SIZE];
		ARRAY_SIZE = DEFAULT_SIZE;
	}
	
	public SimpleList(int size,int autoSize){
		elementData = (E[])new Object[size];
		ARRAY_SIZE = autoSize;
	}
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean contains(Object o) {
		return false;
	}

	public Iterator<E> iterator() {
		E[] eData = (E[])new Object[size];
		System.arraycopy(elementData, 0, eData, 0, size);
		SimpleIterator<E> simpleIterator = new SimpleIterator<E>(eData);
		return simpleIterator;
	}

	public Object[] toArray() {
		return null;
	}

	public <T> T[] toArray(T[] a) {
		int count = size;
		if(a.length < size){
			count = a.length;
		}
		System.arraycopy(elementData, 0, a, 0, count);
		return a;
	}

	public boolean add(E e) {
		if(ARRAY_SIZE == size){
			copy();
		}
		elementData[size] = e;
		size++;
		return true;
	}

	private void copy(){
		E[] eData = (E[])new Object[DEFAULT_SIZE+elementData.length];
		System.arraycopy(elementData, 0, eData, 0, elementData.length);
		elementData = eData;
		ARRAY_SIZE = elementData.length;
	}
	
	public boolean remove(Object o) {
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		return false;
	}

	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
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

	public E get(int index) {
		return elementData[index];
	}

	public E set(int index, E element) {
		return null;
	}

	public void add(int index, E element) {
		
	}

	public E remove(int index) {
		return null;
	}

	public int indexOf(Object o) {
		return 0;
	}

	public int lastIndexOf(Object o) {
		return 0;
	}

	public ListIterator<E> listIterator() {
		return null;
	}

	public ListIterator<E> listIterator(int index) {
		return null;
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return null;
	}



}
