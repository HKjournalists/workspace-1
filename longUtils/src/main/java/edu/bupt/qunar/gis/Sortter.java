package edu.bupt.qunar.gis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

public class Sortter <T> implements Iterable<Entry<Float, T>>{

	ArrayList<Wrapper<T>> list = new ArrayList<Wrapper<T>>();

	public Sortter(){}
	
	public void add(float order,T value){
		list.add(new Wrapper<T>(order,value));
	}

	public void sort(){
		Collections.sort(list);
	}
	
	public void reverse(){
		Collections.reverse(list);
	}
	
	public T getFirst(){
		if(list.isEmpty())
			return null;
		return list.get(0).value;
	}
	
	public void toArray(T[] arr){
		if(arr.length != list.size())
			throw new IllegalArgumentException("arr length must equals "+list.size());
		for(int i=0;i<list.size();i++)
			arr[i] = list.get(i).value;
	}

	public Entry<Float,T> getT(){
		return list.iterator().next();
	}

	@Override
	public Iterator<Entry< Float, T>> iterator() {
		final Iterator<Wrapper<T>> it = list.iterator();
		
		return new Iterator<Entry< Float, T>>(){

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Entry<Float, T> next() {
				return it.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	private static class Wrapper<T> implements Entry<Float,T>,Comparable<Wrapper<T>>{
		final float order;
		final T value;
		Wrapper(float order,T value){
			this.order = order;
			this.value = value;
		}
		
		@Override
		public int compareTo(Wrapper<T> o) {
			float thisVal = this.order;
			float anotherVal = o.order;
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		
		@Override
		public Float getKey() {
			return order;
		}
		@Override
		public T getValue() {
			return value;
		}
		@Override
		public T setValue(T value) {
			throw new UnsupportedOperationException();
		}
	}


}
