package com.ing.anvil.exam;

public class Priority implements IPriority {

	Integer priority;
	
	public Priority(Integer priority) {
		super();
		this.priority = priority;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	@Override
	public int compareTo(IPriority o) {
		return this.priority.compareTo(((Priority)o).getPriority());
	}

}
