package net.imyeyu.netdisk.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.google.gson.Gson;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.coobird.thumbnailator.Thumbnails;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.utils.YeyuUtils;

public class ImgRequest extends Service<Image> {

	private String key, value;
	private Socket socket;
	private Map<String, Object> config = Entrance.getConfig();
	private String ip, token;
	int port;
	boolean isCompressImg = false;

	/**
	 * isCompressImg 为 true 时不执行压缩
	 * 
	 * @param key
	 * @param value
	 * @param isCompressImg
	 */
	public ImgRequest(String key, String value, boolean isCompressImg) {
		this.key = key;
		this.value = value;
		this.isCompressImg = isCompressImg;
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portPublic").toString());
		if (!Boolean.valueOf(config.get("eToken").toString())) {
			token = YeyuUtils.encode().generateBase(config.get("token").toString());
		} else {
			token = config.get("token").toString();
		}
	}

	protected Task<Image> createTask() {
		return new Task<Image>() {
			protected Image call() throws Exception {
				Image img = null;
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 8000);
				if (socket.isConnected()) {
					Request request = new Request();
					request.setToken(token);
					request.setKey(key);
					request.setValue(value);
					OutputStream os = socket.getOutputStream();
					os.write((new Gson().toJson(request) + "\r\n").getBytes("UTF-8"));
					// 接收图片流
					if (isCompressImg) { // 直接收图
						img = new Image(socket.getInputStream());
					} else { // 压缩收图
						// 克隆输入流
						ByteArrayOutputStream baos = cloneInputStream(socket.getInputStream());
						InputStream ifIS = new ByteArrayInputStream(baos.toByteArray());
						InputStream outIS = new ByteArrayInputStream(baos.toByteArray());
						img = new Image(ifIS);
						OutputStream bs = new ByteArrayOutputStream();
						if (img.getWidth() < img.getHeight()) {
							Thumbnails.of(outIS).width(128).toOutputStream(bs);
						} else {
							Thumbnails.of(outIS).height(128).toOutputStream(bs);
						}
						img = new Image(parse(bs));
						System.gc();
						bs.close();
						outIS.close();
						ifIS.close();
						baos.close();
					}
					os.flush();
					os.close();
				}
				return img;
			}
		};
	}

	// 输出转输入
	private ByteArrayInputStream parse(OutputStream out) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos = (ByteArrayOutputStream) out;
		ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
		return swapStream;
	}

	// 克隆输入流
	private ByteArrayOutputStream cloneInputStream(InputStream input) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		while ((len = input.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		return baos;
	}
}