package net.imyeyu.netdisk.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.netdisk.bean.UploadFile;
import net.imyeyu.utils.YeyuUtils;

public class Upload extends Service<Double> {
	
	private boolean isShutdown = false;
	private static SimpleListProperty<UploadFile> list = new SimpleListProperty<UploadFile>();
	private SimpleBooleanProperty finish = new SimpleBooleanProperty(false);

	private InputStream is;
	private OutputStream os;
	private static int iUpload = 0; // 正在传输的下标
	private Map<String, Object> config = Entrance.getConfig();
	private String ip, token; int port;
	private double transpeed = 0;

	public Upload() {
		list.set(FXCollections.observableArrayList());
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portUpload").toString());
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
					config = Entrance.getConfig();
					for (int i = 0; i < list.size(); i++) {
						Thread.sleep(500);
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
							request.setKey("upload");
							request.setValue(gson.toJson(list.get(i)));
							request(gson.toJson(request).toString());
							// 发送
							is = socket.getInputStream();
							BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
							if (br.readLine().equals("ready")) {
								iUpload = i;
								FileInputStream fis = new FileInputStream(new File(list.get(i).getFromPath()));
								DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
								byte[] bytes = new byte[4096];
								int length = 0;
								double progress = 0, fileSize = list.get(i).getSize();
								while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
									dos.write(bytes, 0, length);
									dos.flush();
									Thread.sleep(10); // 本地测试限速
									progress += length;
									transpeed += length;
									updateValue(progress);
									updateProgress(progress / fileSize, 1);
								}
								if (fis != null) fis.close();
								if (dos != null) dos.close();
								finish.setValue(!finish.getValue());
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

	public void add(List<File> files, String path) {
		UploadFile file;
		for (int i = 0; i < files.size(); i++) {
			file = new UploadFile(
				files.get(i).getName(),
				files.get(i).getAbsolutePath(),
				path,
				files.get(i).length()
			);
			list.get().add(file);
		}
	}
	
	public void add(UploadFile file) {
		list.get().add(file);
	}
	
	public static int getIndex() {
		return iUpload;
	}
	
	public void shutdown() {
		this.isShutdown = true;
	}
	
	public double getTranspeed() {
		return transpeed;
	}
	
	public static SimpleListProperty<UploadFile> getListProperty() {
		return list;
	}

	public SimpleBooleanProperty getFinish() {
		return finish;
	}
}