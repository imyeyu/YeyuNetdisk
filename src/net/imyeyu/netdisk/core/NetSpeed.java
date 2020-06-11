package net.imyeyu.netdisk.core;

import java.awt.TrayIcon;
import java.text.DecimalFormat;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.ui.ServerStateChart;
import net.imyeyu.netdisk.ui.SystemTrayX;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;

public class NetSpeed extends Service<String> {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private boolean isShutdown = false;
	private Upload upload;
	private Download download;
	private ServerStateChart netSpeed;
	
	private TrayIcon tray;

	public NetSpeed(Upload upload, Download download, ServerStateChart netSpeed) {
		this.upload = upload;
		this.download = download;
		this.netSpeed = netSpeed;
	}

	protected Task<String> createTask() {
		return new Task<String>() {
			protected String call() throws Exception {
				tray = SystemTrayX.getIcon();
				
				DecimalFormat format = new DecimalFormat("#,###.##");
				StringBuffer speed = new StringBuffer();
				double oldValue = 0, newValue = 0;
				while (!isShutdown) {
					newValue = upload.getTranspeed() + download.getTranspeed();
					netSpeed.setValue(newValue / 1024e2 - oldValue / 1024e2);
					speed.append(YeyuUtils.tools().netSpeedFormat(newValue - oldValue, format) + "/s");
					updateMessage(speed.toString());
					setTrayTips(rbx.def("speed") + speed.toString());
					speed.setLength(0);
					
					oldValue = newValue;
					Thread.sleep(1000);
				}
				return null;
			}
		};
	}
	
	/**
	 * 设置系统托盘提示
	 * 
	 * @param speed 当前传输速度
	 */
	private void setTrayTips(String speed) {
		int s = Upload.getListProperty().getSize();
		int i = s != 0 ? Upload.getIndex() + 1 : 0;
		String upload = rbx.def("uploadList") + i +  " / " + s;
		s = Download.getListProperty().getSize();
		i = s != 0 ? Download.getIndex() + 1 : 0;
		String download = rbx.def("downloadList") + i +  " / " + s;
		tray.setToolTip("iNetdisk - 夜雨云盘\n" + upload + "\n" + download + "\n" + speed);
	}

	public void shutdown() {
		this.isShutdown = true;
	}
}