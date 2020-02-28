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
import net.imyeyu.netdisk.bean.DownloadFile;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.utils.YeyuUtils;

public class Download extends Service<Double> {
	
	private boolean isShutdown = false;
	private static SimpleListProperty<DownloadFile> list = new SimpleListProperty<DownloadFile>();

	private InputStream is;
	private OutputStream os;
	private static int iDownload = 0; // 正在传输的下标
	private Map<String, Object> config = Entrance.getConfig();
	private File dlFolder;
	private String ip, token; int port;
	private double transpeed = 0; // 传输总数据，用于测速
	
	public Download() {
		list.set(FXCollections.observableArrayList());
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portDownload").toString());
		if (!Boolean.valueOf(config.get("eToken").toString())) {
			token = YeyuUtils.encode().generateBase(config.get("token").toString());
		} else {
			token = config.get("token").toString();
		}
		
		dlFolder = new File(config.get("dlLocation").toString());
		dlFolder.mkdirs();
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
							DownloadFile file = list.get(i);
							// 唤醒
							os = socket.getOutputStream();
							Request request = new Request();
							request.setToken(token);
							request.setKey("download");
							request.setValue(gson.toJson(file));
							request(gson.toJson(request).toString());
							// 接收服务端验证结果
							is = socket.getInputStream();
							BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
							if (br.readLine().equals("ready")) {
								iDownload = i;
								// 接收数据流
								String dlPath = dlFolder + file.getDlPath();
								DataInputStream dis = new DataInputStream(is);
								FileOutputStream fos = new FileOutputStream(dlPath + File.separator + file.getName());
								byte[] bytes = new byte[4096];
								int length = 0;
								double progress = 0, fileSize = file.getSize();
								while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
									fos.write(bytes, 0, length);
									fos.flush();
									Thread.sleep(10); // 本地测试限速
									progress += length;
									transpeed += length;
									updateValue(progress);
									updateProgress(progress / fileSize, 1);
								}
								fos.flush();
								// 传输完成
								if (fos != null) fos.close();
								if (dis != null) dis.close();
								
								// # 号区分返回路径和占位符（需要个不断变化的数作占位符，不然 Property 不会监听到）
								// 占位符在前面可以缩短 subString 代码
								updateMessage(i + "#" + dlPath);
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
		if (os != null) os.write((data.toString() + "\r\n").getBytes("UTF-8"));
	}

	/**
	 * 添加下载队列
	 * 
	 * @param files 目标文件列表（取于主界面视图）
	 * @param targetPath 目标路径
	 * @param dlPath 下载相对位置（相对于设置的下载文件夹，不可越级）
	 */
	public void add(List<FileCell> files, String targetPath, String dlPath) {
		String name;
		DownloadFile file;
		for (int i = 0; i < files.size(); i++) {
			name = files.get(i).getName();
			file = new DownloadFile(
				name.substring(name.indexOf(".") + 1),
				targetPath,
				dlPath,
				files.get(i).getSizeLong()
			);
			list.get().add(file);
		}
	}
	
	// 直接添加队列
	public void add(DownloadFile file) {
		list.get().add(file);
	}
	
	public static int getIndex() {
		return iDownload;
	}
	
	public void shutdown() {
		this.isShutdown = true;
	}
	
	public double getTranspeed() {
		return transpeed;
	}
	
	public static SimpleListProperty<DownloadFile> getListProperty() {
		return list;
	}
}