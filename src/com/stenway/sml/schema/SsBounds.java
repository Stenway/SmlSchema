package com.stenway.sml.schema;

public class SsBounds {
	public Integer Min;
	public Integer Max;
	
	public SsBounds() {
		
	}
	
	public SsBounds(Integer min, Integer max) {
		Min = min;
		Max = max;
	}
	
	public void set(Integer min, Integer max) {
		Min = min;
		Max = max;
	}
	
	public void setOptional() {
		set(0, 1);
	}
	
	public void setRequired() {
		set(1, 1);
	}
	
	public void setRepeatStar() {
		set(0, null);
	}
	
	public void setRepeatPlus() {
		set(1, null);
	}
	
	public void setFixedSize(int size) {
		set(size, size);
	}
	
	public boolean isOptional() {
		return Min != null && Max != null && Min == 0 && Max == 1;
	}
	
	public boolean isRequired() {
		return Min != null && Max != null && Min == 1 && Max == 1;
	}
	
	public boolean isRepeatStar() {
		return Min != null && Min == 0 && Max == null;
	}
	
	public boolean isRepeatPlus() {
		return Min != null && Min == 1 && Max == null;
	}
	
	public boolean isUnbound() {
		return Min == null && Max == null;
	}
	
	public boolean isFixedSize() {
		return Min != null && Max != null && Min == Max;
	}
}
