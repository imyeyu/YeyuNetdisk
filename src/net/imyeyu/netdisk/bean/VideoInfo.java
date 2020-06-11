package net.imyeyu.netdisk.bean;

import java.util.ArrayList;
import java.util.List;

public class VideoInfo {

	private String url;
	private String name;
	private double width;
	private double height;
	private int deg;
	
	/**
	 * 过滤列表，只保留 mp4 格式文件
	 * 
	 * @param list
	 * @return
	 */
	public List<FileCell> filter(List<FileCell> list) {
		List<FileCell> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().startsWith("mp4")) {
				result.add(list.get(i));
			}
		}
		return result;
	}
	
	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * 获取文件列表的隐藏名称，即类型+文件名
	 * 
	 * @return 文件名
	 */
	public String getFileCellName() {
		return "mp4." + name;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public int getDeg() {
		return deg;
	}

	/**
	 * URL 传云盘当前访问路径和文件名
	 * 
	 * @param url  请求地址
	 * @param ip   服务器 IP
	 * @param port 服务器端口
	 */
	public void setUrl(String url, String ip, String port) {
		this.url = "http://" + ip + ":" + port + url.replaceAll("\\\\", "/").replaceAll("//", "/").replaceAll(" ", "%20");
	}

	/**
	 * 直接转换成 URL，传云盘当前访问路径和文件名
	 * 
	 * @param url  请求地址
	 * @param ip   服务器 IP
	 * @param port 服务器端口
	 */
	public static String toUrl(String url, String ip, String port) {
		return "http://" + ip + ":" + port + url.replaceAll("\\\\", "/").replaceAll("//", "/").replaceAll(" ", "%20");
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setDeg(int deg) {
		this.deg = deg;
	}
}