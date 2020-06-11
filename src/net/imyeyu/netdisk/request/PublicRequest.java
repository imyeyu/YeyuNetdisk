package net.imyeyu.netdisk.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

import com.google.gson.Gson;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.utils.YeyuUtils;

public class PublicRequest extends Service<String> {
	
	private String key, value;
	private Socket socket;
	private Map<String, Object> config = Entrance.getConfig();
	private String ip, token; int port;

	public PublicRequest(String key, String value) {
		this.key = key;
		this.value = value;
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portPublic").toString());
		if (!Boolean.valueOf(config.get("eToken").toString())) {
			token = YeyuUtils.encode().generateBase(config.get("token").toString());
		} else {
			token = config.get("token").toString();
		}
	}
	
	protected Task<String> createTask() {
		return new Task<String>() {
			
			private int reTry = 0;
			private String flag = null, result = null;
			
			protected String call() throws Exception {
				return request();
			}
			
			private String request() throws Exception {
				try {
					if (reTry == 3) return null; // 尝试重连
					socket = new Socket();
					socket.connect(new InetSocketAddress(ip, port), 1000);
				} catch (SocketTimeoutException e) {
					reTry++;
					return request();
				}
				if (socket.isConnected()) {
					Request request = new Request();
					request.setToken(token);
					request.setKey(key);
					request.setValue(value);
					OutputStream os = socket.getOutputStream();
					os.write((new Gson().toJson(request) + "\r\n").getBytes("UTF-8"));

					InputStream is = socket.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					while (!(result = flag = br.readLine()).equals("finish")) {
						updateMessage(flag);
					}
					br.close();
					os.flush();
					os.close();
				}
				return result;
			}
		};
	}
	
	public String getRequestKey() {
		return key;
	}
	
	public String getRequestValue() {
		return value;
	}
}