package net.imyeyu.netdisk.bean;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileCell {

	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty date = new SimpleStringProperty();
	private SimpleStringProperty size = new SimpleStringProperty();
	private SimpleLongProperty sizeLong = new SimpleLongProperty();
	
	public FileCell() {
		super();
	}
	
	public FileCell(String name, String date, String size, long sizeLong) {
		this.name.set(name);
		this.date.set(date);
		this.size.set(size);
		this.sizeLong.set(sizeLong);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getSize() {
		return size.get();
	}

	public void setSize(String size) {
		this.size.set(size);
	}

	public long getSizeLong() {
		return sizeLong.get();
	}

	public void setSizeLong(long sizeLong) {
		this.sizeLong.set(sizeLong);
	}
	
	public SimpleStringProperty getNameProperty() {
		return name;
	}
	
	public SimpleStringProperty getDateProperty() {
		return date;
	}
	
	public SimpleStringProperty getSizeProperty() {
		return size;
	}
	
	public SimpleLongProperty getSizeLongProperty() {
		return sizeLong;
	}
}