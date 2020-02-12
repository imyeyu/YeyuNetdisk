package net.imyeyu.netdisk;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.ctrl.Main;
import net.imyeyu.utils.Configer;

/**
 * 夜雨云盘
 * <br />
 * 
 * @author Yeyu
 *
 */
public class Entrance {

	static ResourceBundle rb;
	static Map<String, Object> config;
	static List<IOCell> history;
	
	public static void main(String[] args) {
		config = new Configer("net/imyeyu/netdisk/res/NetDisk.ini").get();
		Locale.setDefault(new Locale("zh", "CN"));
		rb = ResourceBundle.getBundle("lang/language");
		
		Application.launch(Main.class, args);
	}

	public static ResourceBundle getRb() {
		return rb;
	}
	
	public static Map<String, Object> getConfig() {
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