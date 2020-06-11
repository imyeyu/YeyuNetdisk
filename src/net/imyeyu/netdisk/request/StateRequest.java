package net.imyeyu.netdisk.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.google.gson.Gson;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.Request;
import net.imyeyu.utils.YeyuUtils;

public class StateRequest extends Service<String> {
	
	private Socket socket;
	private Map<String, Object> config = Entrance.getConfig();
	private String ip, token; int port;
	private boolean isShutdown = false;
	
	public StateRequest() {
		ip = config.get("ip").toString();
		port = Integer.valueOf(config.get("portState").toString());
		if (!Boolean.valueOf(config.get("eToken").toString())) {
			token = YeyuUtils.encode().generateBase(config.get("token").toString());
		} else {
			token = config.get("token").toString();
		}
	}

	protected Task<String> createTask() {
		return new Task<String>() {
			protected String call() throws Exception {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 5000);
				if (socket.isConnected()) {
					Request request = new Request();
					request.setToken(token);
					OutputStream os = socket.getOutputStream();
					os.write((new Gson().toJson(request) + "\r\n").getBytes("UTF-8"));
					
					InputStream is = socket.getInputStream();
					BufferedReader br;
					String result;
					while (!isShutdown) {
						br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
						result = br.readLine();
						if (!result.equals("null")) {
							updateValue(result);
						} else {
							Thread.sleep(200);
						}
					}
					is.close();
					os.close();
					socket.close();
				}
				return null;
			}
		};
	}
	
	public void shutdown() {
		this.isShutdown = true;
	}
}