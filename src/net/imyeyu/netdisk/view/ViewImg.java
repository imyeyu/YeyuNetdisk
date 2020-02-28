package net.imyeyu.netdisk.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.imyeyu.utils.gui.BorderX;

public class ViewImg extends Stage {
	
	private ImageView img;
	private BorderPane main;

	public ViewImg(String path) {
		main = new BorderPane();
		Label onload = new Label("正在加载...");
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).def());
		main.setCenter(onload);
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/photo.png"));
		setScene(scene);
		setTitle("预览图片 - " + path);
		setWidth(220);
		setHeight(64);
		initStyle(StageStyle.UNDECORATED);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		show();
	}
	
	public void setImg(ImageView img) {
		this.img = img;
	}

	public ImageView getImg() {
		return img;
	}

	public BorderPane getMain() {
		return main;
	}
}