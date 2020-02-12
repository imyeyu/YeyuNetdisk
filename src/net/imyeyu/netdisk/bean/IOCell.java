package net.imyeyu.netdisk.bean;

import javafx.beans.property.SimpleDoubleProperty;

public class IOCell {

	private String name;
	private String path;
	private double maxSize;
	private boolean isLocal;
	private SimpleDoubleProperty size = new SimpleDoubleProperty();
	private SimpleDoubleProperty percent = new SimpleDoubleProperty();
	
	public IOCell() {}
	
	public IOCell(String name, double maxSize, double percent) {
		this.name = name;
		this.maxSize = maxSize;
		this.percent.set(percent);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public double getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(double maxSize) {
		this.maxSize = maxSize;
	}
	
	public boolean isLocal() {
		return isLocal;
	}
	
	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}
	
	public double getSize() {
		return size.get();
	}
	
	public void setSize(double size) {
		this.size.set(size);
	}
	
	public double getPercent() {
		return percent.get();
	}
	
	public void setPercent(double percent) {
		this.percent.set(percent);
	}
	
	
	public SimpleDoubleProperty getSizeProperty() {
		return size;
	}
	
	public SimpleDoubleProperty getPercentProperty() {
		return percent;
	}

	public String toString() {
		return "IOCell [name=" + name + ", path=" + path + ", maxSize=" + maxSize + ", isLocal=" + isLocal + ", size="
				+ size + ", percent=" + percent + "]";
	}
}