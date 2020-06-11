package net.imyeyu.netdisk.bean;

public class DownloadFile {

	private String name;
	private String targetPath;
	private String dlPath;
	private long size;

	/**
	 * 下载文件对象
	 * 
	 * @param name       文件名
	 * @param targetPath 目标路径（文件位于云服务器的路径）
	 * @param dlPath     下载路径
	 * @param size       文件大小
	 */
	public DownloadFile(String name, String targetPath, String dlPath, long size) {
		this.name = name;
		this.targetPath = targetPath;
		this.dlPath = dlPath;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getDlPath() {
		return dlPath;
	}

	public void setDlPath(String dlPath) {
		this.dlPath = dlPath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}