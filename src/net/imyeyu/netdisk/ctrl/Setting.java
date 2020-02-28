package net.imyeyu.netdisk.ctrl;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.stage.DirectoryChooser;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.view.ViewSetting;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.YeyuUtils;

public class Setting extends ViewSetting {
	
	private ResourceBundle rb = Entrance.getRb();
	private Map<String, Object> config = Entrance.getConfig();

	public Setting() {
		String oldToken = config.get("token").toString();
		boolean oldEToken = Boolean.valueOf(config.get("eToken").toString());
		// 选择面板
		getGeneral().setOnAction(event -> getMain().setCenter(getGeneralPane()));
		getDev().setOnAction(event -> getMain().setCenter(getDevPane()));
		getAbout().setOnAction(event -> getMain().setCenter(getAboutPane()));
		
		// 选择下载文件夹
		getSetDLLocation().setOnAction(event -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle(rb.getString("fileSelectTypeFolder"));
			File dir = directoryChooser.showDialog(null);
			if (dir != null) getDlLocation().setText(dir.getAbsolutePath());
		});
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
			String newToken = getToken().getText();
			if (!oldToken.equals(newToken) || oldEToken != geteToken().isSelected()) {
				newToken = geteToken().isSelected() ? YeyuUtils.encode().generateBase(newToken) : newToken;
				config.put("token", newToken);
			}
			config.put("eToken", geteToken().isSelected());
			config.put("dlLocation", getDlLocation().getText());
			config.put("ip", getIp().getText());
			config.put("portPublic", getPortPublic().getText());
			config.put("portState", getPortState().getText());
			config.put("portUpload", getPortUpload().getText());
			config.put("portDownload", getPortDownload().getText());
			
			Entrance.setConfig(config);
			new Configer("Netdisk").set(config);
			close();
		});
		
		// 关闭
		getClose().setOnAction(event -> close());
	}
}