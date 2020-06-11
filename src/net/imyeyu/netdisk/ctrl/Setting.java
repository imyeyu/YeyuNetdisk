package net.imyeyu.netdisk.ctrl;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.stage.DirectoryChooser;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.view.ViewSetting;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.Lang;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;

public class Setting extends ViewSetting {
	
	private ResourceBundleX rbx = Entrance.getRb();
	private Map<String, Object> config = Entrance.getConfig();
	
	private SimpleBooleanProperty isUpdateRestart = new SimpleBooleanProperty(false);
	private AudioClip ac = Applet.newAudioClip(this.getClass().getClassLoader().getResource("net/imyeyu/netdisk/res/finish.wav"));

	public Setting() {
		String oldToken = config.get("token").toString();
		boolean oldEToken = Boolean.valueOf(config.get("eToken").toString());
		// 选择面板
		getGeneral().setOnAction(event -> getMain().setCenter(getGeneralPane()));
		getServer().setOnAction(event -> getMain().setCenter(getDevPane()));
		getAbout().setOnAction(event -> getMain().setCenter(getAboutPane()));
		
		// 选择下载文件夹
		getSetDLLocation().setOnAction(event -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle(rbx.def("fileSelectTypeFolder"));
			File path = directoryChooser.showDialog(null);
			if (path != null) getDlLocation().setText(path.getAbsolutePath());
		});
		// 打开下载文件夹
		getOpenDLLocation().setOnAction(event -> {
			String path = getDlLocation().getText();
			path = path.charAt(1) != ':' ? System.getProperty("user.dir") + File.separator + path : path;
			try {
				Runtime.getRuntime().exec("explorer.exe " + path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		// 选择缓存文件夹
		getSetCache().setOnAction(event -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle(rbx.def("fileSelectTypeFolder"));
			File path = directoryChooser.showDialog(null);
			if (path != null) getCache().setText(path.getAbsolutePath());
		});
		// 打开缓存文件夹
		getOpenCache().setOnAction(event -> {
			String path = getCache().getText();
			path = path.charAt(1) != ':' ? System.getProperty("user.dir") + File.separator + path : path;
			try {
				Runtime.getRuntime().exec("explorer.exe " + path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		// 勾选音效提示
		getSound().setOnAction(event -> {
			if (getSound().isSelected()) {
				ac.play();
			}
		});
		// 版本更新
		CheckVersion request = new CheckVersion();
		request.valueProperty().addListener((obs, oldValue, newValue) ->{
			if (!newValue.equals(getVersionValue())) {
				getVersion().setText(rbx.def("settingNowV") + getVersionValue() + rbx.def("settingNewV") + newValue + rbx.def("settingNewVClick"));
				getVersion().setCursor(Cursor.HAND);
				getVersion().setOnMouseClicked(event -> {
					try {
						YeyuUtils.network().openURIInBrowser(new URL("https://imyeyu.net/article/public/aid126.html").toURI());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} else {
				getVersion().setText("v" + getVersionValue());
			}
		});
		request.start();
		// 博客
		getBlog().setOnMouseClicked(event -> {
			try {
				YeyuUtils.network().openURIInBrowser(new URL("https://www.imyeyu.net").toURI());
			} catch (Exception e) {
				YeyuUtils.gui().exception(e);
			}
		});
		// 保存
		getSave().setOnAction(event -> {
			isUpdateRestart.set(updateRestartSetting("language", Lang.toCode(getLang().getSelectionModel().getSelectedItem())));
			String newToken = getToken().getText();
			if (!oldToken.equals(newToken) || oldEToken != geteToken().isSelected()) {
				newToken = geteToken().isSelected() ? YeyuUtils.encode().generateBase(newToken) : newToken;
				isUpdateRestart.set(updateRestartSetting("token", newToken));
			}
			isUpdateRestart.set(updateRestartSetting("eToken", geteToken().isSelected()));
			config.put("dlLocation", getDlLocation().getText());
			config.put("cache", getCache().getText());
			config.put("openIOList", getIOList().isSelected());
			config.put("sound", getSound().isSelected());
			config.put("exitOnClose", getExit().isSelected());
			Platform.setImplicitExit(getExit().isSelected());
			isUpdateRestart.set(updateRestartSetting("ip", getIp().getText()));
			isUpdateRestart.set(updateRestartSetting("portPublic", getPortPublic().getText()));
			isUpdateRestart.set(updateRestartSetting("portState", getPortState().getText()));
			isUpdateRestart.set(updateRestartSetting("portUpload", getPortUpload().getText()));
			isUpdateRestart.set(updateRestartSetting("portDownload", getPortDownload().getText()));
			isUpdateRestart.set(updateRestartSetting("portHTTP", getPortHTTP().getText()));
			
			Entrance.setConfig(config);
			new Configer("iNetdisk").set(config);
			close();
		});
		
		// 关闭
		getClose().setOnAction(event -> close());
	}

	/**
	 * 更新服务器配置
	 * 
	 * @param k 键
	 * @param v 值
	 * @return
	 */
	private boolean updateRestartSetting(String k, Object v) {
		boolean result = !config.get(k).toString().equals(v.toString());
		config.put(k, v);
		return result;
	}
	
	public SimpleBooleanProperty isUpdateRestart() {
		return isUpdateRestart;
	}
}

class CheckVersion extends Service<String> {

	protected Task<String> createTask() {
		return new Task<String>() {
			protected String call() throws Exception {
				return YeyuUtils.network().sendGet("https://www.imyeyu.net/java/netdisk/version", "");
			}
		};
	}
}