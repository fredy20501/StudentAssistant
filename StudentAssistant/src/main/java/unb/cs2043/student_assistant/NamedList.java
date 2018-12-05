package unb.cs2043.student_assistant;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Generic interface that describes a named list.
 * Implemented by Schedule, Course, and Section classes.
 * Based on Tye Shutty's old Schedule class.
 * @author Frederic Verret
 */
@SuppressWarnings("serial")
public class NamedList<T> implements Serializable{

//-------Instance Variables--------//

	protected String name;
	protected ArrayList<T> list;
	
//-------Constructors--------//
	
	public NamedList(String name) {
		this.name = name;
		list = new ArrayList<T>();
	}
	
	public NamedList(NamedList<T> otherList) {
		this.name = otherList.getName();
		list = otherList.copyList();
	}
	
//-------Getters---------//
	
	public ArrayList<T> copyList() {
		return new ArrayList<T>(list);
	}
	
	public T getItem(int index) {
		return list.get(index);
	}
	
	public T getItemByName(String itemName) {
		itemName = itemName.toLowerCase();
		T result = null;
		for (int i=0; i<list.size() && result==null; i++) {
			T current = list.get(i);
			if (current.toString().toLowerCase().equals(itemName)) {
				result = current;
			}
		}
		return result;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSize() {
		return list.size();
	}
	
	public boolean contains(T a) {
		return list.contains(a);
	}
	
	public int indexOf(T a) {
		return list.indexOf(a);
	}
	
	public int indexOf(String a) {
		for(int x=0;x<list.size();x++) {
			if(list.get(x).toString().compareTo(a)==0) {
				return x;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(T obj) {
		return list.lastIndexOf(obj);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public String toString() {
		return name;
	}
	
//-----------Setters------------//
	
	public boolean setName(String name) {
		this.name=name;
		return true;
	}
	
	public void add(T one) {
		list.add(one);
	}
	
	public boolean remove(T one) {
		return list.remove(one);
	}
	
	public boolean remove(int index) {
		return null!=list.remove(index);
	}
	
	public void clear() {
		list.clear();
	}
}
