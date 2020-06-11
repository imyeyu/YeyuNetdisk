package net.imyeyu.netdisk.bean;

public class UploadFile {

	private String name;
	private String fromPath;
	private String toPath;
	private long size;

	/**
	 * 文件上传对象
	 * 
	 * @param name     文件名
	 * @param fromPath 本地路径
	 * @param toPath   目标路径
	 * @param size     文件大小
	 */
	public UploadFile(String name, String fromPath, String toPath, long size) {
		this.name = name;
		this.fromPath = fromPath;
		this.toPath = toPath;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromPath() {
		return fromPath;
	}

	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}

	public String getToPath() {
		return toPath;
	}

	public void setToPath(String toPath) {
		this.toPath = toPath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}