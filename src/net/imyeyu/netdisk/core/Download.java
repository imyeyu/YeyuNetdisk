package net.imyeyu.netdisk.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.utils.YeyuUtils;

public class Download extends Service<Double> {
	
	public final static String DL_PATH = "D:\\";

	private boolean isShutdown = false;
	private SimpleListProperty<DownloadFile> list = new SimpleListProperty<DownloadFile>();
	private Map<String, Object> config = Entrance.getConfig();
	private String ip, token; int port;

	private InputStream is;
	private OutputStream os;

	public Download() {
		list.set(FXCollections.observableArrayList());
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portDownload").toString());
		if (!Boolean.valueOf(config.get("eToken").toString())) {
			token = YeyuUtils.encode().generateBase(config.get("token").toString());
		} else {
			token = config.get("token").toString();
		}
	}

	protected Task<Double> createTask() {
		return new Task<Double>() {
			protected Double call() throws Exception {
				Gson gson = new Gson();
				Socket socket = null;
				while (!isShutdown) {
					for (int i = 0; i < list.size(); i++) {
						Thread.sleep(1000);
						if (socket != null && socket.isConnected()) {
							socket.close();
							socket = null;
						}
						socket = new Socket();
						socket.connect(new InetSocketAddress(ip, port), 8000);
						if (socket.isConnected()) {
							// 唤醒
							os = socket.getOutputStream();
							Request request = new Request();
							request.setToken(token);
							request.setKey("download");
							request.setValue(gson.toJson(list.get(i)));
							request(gson.toJson(request).toString());
							// 接收服务端验证结果
							is = socket.getInputStream();
							BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
							if (br.readLine().equals("ready")) {
								// 接收数据流
								DataInputStream dis = new DataInputStream(is);
								FileOutputStream fos = new FileOutputStream(new File(DL_PATH) + list.get(i).getName());
								byte[] bytes = new byte[4096];
								int length = 0;
								double progress = 0, fileSize = list.get(i).getSize();
								while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
									fos.write(bytes, 0, length);
									fos.flush();
									progress += length;
//									Thread.sleep(10); // 本地测试限速
									updateValue(progress);
									updateProgress(progress / fileSize, 1);
								}
								if (fos != null) fos.close();
								if (dis != null) dis.close();
								updateMessage("finish" + i);
							}
						}
					}
					list.get().clear();
					Thread.sleep(3000);
				}
				return null;
			}
		};
	}

	private void request(Object data) throws IOException {
		if (os != null) os.write((data.toString() + "\r\n").getBytes());
	}

	public void add(List<FileCell> files, String path) {
		String name;
		DownloadFile file;
		for (int i = 0; i < files.size(); i++) {
			name = files.get(i).getName();
			file = new DownloadFile(name.substring(name.indexOf(".") + 1), path, files.get(i).getSizeLong());
			list.get().add(file);
		}
	}

	public class DownloadFile {
		
		private String name;
		private String path;
		private long size;
		
		public DownloadFile(String name, String path, long size) {
			this.name = name;
			this.path = path;
			this.size = size;
		}

		public String getName() {
			return name;
		}

		public long getSize() {
			return size;
		}

		public String toString() {
			return "DownloadFile [name=" + name + ", path=" + path + ", size=" + size + "]";
		}
	}
	
	public void shutdown() {
		this.isShutdown = true;
	}
	
	public SimpleListProperty<DownloadFile> getListProperty() {
		return list;
	}
}