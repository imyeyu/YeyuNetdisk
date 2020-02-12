package net.imyeyu.netdisk.bean;

public class IOHistory {

	private String name;
	private String path;
	private boolean isLocal;

	public IOHistory(String name, String path, boolean isLocal) {
		this.name = name;
		this.path = path;
		this.isLocal = isLocal;
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

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}
}