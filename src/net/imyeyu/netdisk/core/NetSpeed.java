package net.imyeyu.netdisk.core;

import java.text.DecimalFormat;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.utils.YeyuUtils;

public class NetSpeed extends Service<String> {
	
	private boolean isShutdown = false;
	private Upload upload;
	private Download download;

	public NetSpeed(Upload upload, Download download) {
		this.upload = upload;
		this.download = download;
	}

	protected Task<String> createTask() {
		return new Task<String>() {
			protected String call() throws Exception {
				DecimalFormat format = new DecimalFormat("#,###.##");
				double oldValue = 0, newValue = 0;
				while (!isShutdown) {
					newValue = upload.getTranspeed() + download.getTranspeed();
					updateMessage(YeyuUtils.tools().netSpeedFormat(newValue - oldValue, format) + "/s");
					oldValue = newValue;
					Thread.sleep(1000);
				}
				return null;
			}
		};
	}

	public void shutdown() {
		this.isShutdown = true;
	}
}