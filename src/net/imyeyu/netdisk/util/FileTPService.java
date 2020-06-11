package net.imyeyu.netdisk.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.ResourceBundleX;

public class FileTPService extends Service<String> {

	private ResourceBundleX rbx = Entrance.getRb();
	
	private String path, flag;
	private List<String> list;
	
	public FileTPService(String path, List<String> list, String flag) {
		this.path = path;
		this.flag = flag;
		this.list = list;
	}
	
	protected Task<String> createTask() {
		
		return new Task<String>() {
			protected String call() throws Exception {
				Map<String, Object> map = new HashMap<>();
				map.put("path", path);
				map.put("list", list);
				
				updateMessage(rbx.def("running"));
				
				PublicRequest request = new PublicRequest(flag, new Gson().toJson(map).toString());
				request.valueProperty().addListener((obs, oldValue, newValue) -> {
					updateValue(newValue);
				});
				request.start();
				return null;
			}
		};
	}
}