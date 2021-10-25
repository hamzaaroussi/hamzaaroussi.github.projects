package sma;

import java.util.HashSet;
import java.util.Set;

public class Segment {
	int base, size;
	boolean readWriteFlag;
	Set<Integer> sharedWith;
	
	
	public Segment(int base, int size) {
		this.base = base;
		this.size = size;
		this.sharedWith = new HashSet<Integer>();
	}
	
	public void rebase(int base) {
		this.base = base;
	}
	
	public void expand(int size) {
		this.size += size;
	}
	
	public void setReadWrite(boolean rw) {
		this.readWriteFlag = rw;
	}
	
	public void addSharedWith(int p) {
		this.sharedWith.add(p);
	}
	
	public void clearSharedWith() {
		this.sharedWith.clear();
	}
	
	public Set<Integer> getSharedWith() {
		return this.sharedWith;
	}
}
