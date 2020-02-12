package net.imyeyu.netdisk.ctrl;

import java.util.Map;
import java.util.ResourceBundle;

import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.view.ViewSetting;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.YeyuUtils;

public class Setting extends ViewSetting {

	public Setting(ResourceBundle rb) {
		super(rb);
		
		getGeneral().setOnAction(event -> getMain().setCenter(getGeneralPane()));
		getDev().setOnAction(event -> getMain().setCenter(getDevPane()));
		getAbout().setOnAction(event -> getMain().setCenter(getAboutPane()));
		
		// 保存
		getSave().setOnAction(event -> {
			String token = getToken().getText();
			token = geteToken().isSelected() ? YeyuUtils.encode().generateBase(token) : token;
			
			Map<String, Object> config = Entrance.getConfig();
			config.put("token", token);
			config.put("eToken", geteToken().isSelected());
			config.put("ip", getIp().getText());
			config.put("portPublic", getPortPublic().getText());
			config.put("portState", getPortState().getText());
			config.put("portUpload", getPortUpload().getText());
			config.put("portDownload", getPortDownload().getText());
			
			Entrance.setConfig(config);
			new Configer("NetDisk").set(config);
			close();
		});
		
		// 关闭
		getClose().setOnAction(event -> close());
	}
}
