package net.imyeyu.netdisk;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.ctrl.Main;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.ResourceBundleX;

/**
 * 夜雨云盘
 * <br />
 * 
 * @author Yeyu
 *
 */
public class Entrance {

	static ResourceBundleX rb;
	static Map<String, Object> config;
	static List<IOCell> history;
	
	public static void main(String[] args) {
		// 禁止 DPI 缩放
		System.setProperty("prism.allowhidpi", "false");
		// 配置
		config = new Configer("net/imyeyu/netdisk/res/iNetdisk.ini").get();
		// 语言
		setLang(config.get("language").toString());
		rb = new ResourceBundleX(ResourceBundle.getBundle("lang/language"));
		// 检查环境
		checkFolder(config.get("dlLocation").toString());
		checkFolder(config.get("cache").toString());
		
		// 启动
		Application.launch(Main.class, args);
	}
	
	private static void checkFolder(String path) {
		path = path.charAt(1) != ':' ? System.getProperty("user.dir") + File.separator + path : path;
		(new File(path)).mkdirs();
	}
	
	private static void setLang(String lang) {
		String[] l = lang.split("_");
		Locale.setDefault(new Locale(l[0], l[1]));
	}

	public static ResourceBundleX getRb() {
		return rb;
	}
	
	public static Map<String, Object> getConfig() {
		if (config.get("defaultUploadFolder").equals("")) {
			config.put("defaultUploadFolder", System.getProperty("user.dir"));
		}
		return config;
	}
	
	public static void setConfig(Map<String, Object> config) {
		Entrance.config = config;
	}
	
	public static List<IOCell> getHistory() {
		return history;
	}
	
	public static void setHistory(List<IOCell> history) {
		Entrance.history = history;
	}
}