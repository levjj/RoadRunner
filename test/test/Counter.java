package test;

import rr.contracts.ThreadUnsafe;

public class Counter implements ThreadUnsafe {
	private int count = 0;
	
	public void inc() {
		count = get() + 1;
	}
	
	public int get() {
		return count;
	}
}