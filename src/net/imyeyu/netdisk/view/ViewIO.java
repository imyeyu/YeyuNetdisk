package net.imyeyu.netdisk.view;

import java.util.ResourceBundle;

import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.ui.IOFinishList;
import net.imyeyu.netdisk.ui.IOList;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.utils.gui.BorderX;

public class ViewIO extends Stage {

	private ResourceBundle rb;
	
	private IOList uploadList, downloadList;
	private IOFinishList finishList;
	private NavButton upload, download, finish, clear;
	private BorderPane main, left;
	
	public ViewIO() {
		this.rb = Entrance.getRb();
		
		// 导航
		left = new BorderPane();
		VBox nav = new VBox();
		upload = new NavButton(rb.getString("ioListUpload"), 100, 32);
		download = new NavButton(rb.getString("ioListDownload"), 100, 32);
		finish = new NavButton(rb.getString("ioListFinish"), 100, 32);

		nav.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).bottom());
		nav.getChildren().addAll(upload, download, finish);
		
		clear = new NavButton(rb.getString("ioListClear"), 100, 26);
		clear.setPadding(new Insets(0, 0, 4, 0));
		clear.setBorder(Border.EMPTY);
		
		left.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		left.setCenter(nav);
		left.setBottom(clear);
		
		// 上传列表
		ObservableList<IOCell> obsUploadList = FXCollections.observableArrayList(new Callback<IOCell, Observable[]>() {
			public Observable[] call(IOCell param) {
				return new SimpleDoubleProperty[] {param.getSizeProperty(), param.getPercentProperty()};
			}
		});
		uploadList = new IOList(obsUploadList);
		
		// 下载列表
		ObservableList<IOCell> obsDownloadList = FXCollections.observableArrayList(new Callback<IOCell, Observable[]>() {
			public Observable[] call(IOCell param) {
				return new SimpleDoubleProperty[] {param.getSizeProperty(), param.getPercentProperty()};
			}
		});
		downloadList = new IOList(obsDownloadList);
		
		// 已完成列表
		finishList = new IOFinishList();
		
		main = new BorderPane();
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		main.setLeft(left);
		
		Scene scene = new Scene(main);
		String pbCSS = this.getClass().getResource("/net/imyeyu/netdisk/res/IOList.css").toExternalForm();
		scene.getStylesheets().add(pbCSS);
		setTitle("传输列表");
		getIcons().add(new Image("net/imyeyu/netdisk/res/ioList.png"));
		setMinWidth(460);
		setMinHeight(270);
		setWidth(460);
		setHeight(270);
		initModality(Modality.APPLICATION_MODAL);
		setScene(scene);
		show();
		
		upload();
	}

	public void upload() {
		left.setBottom(null);
		main.setCenter(uploadList);
	}
	
	public void download() {
		left.setBottom(null);
		main.setCenter(downloadList);
	}
	
	public void finish() {
		left.setBottom(clear);
		main.setCenter(finishList);
	}

	public IOList getUploadList() {
		return uploadList;
	}

	public IOList getDownloadList() {
		return downloadList;
	}

	public IOFinishList getFinishList() {
		return finishList;
	}

	public NavButton getUpload() {
		return upload;
	}

	public NavButton getDownload() {
		return download;
	}

	public NavButton getFinish() {
		return finish;
	}

	public NavButton getClear() {
		return clear;
	}

	public BorderPane getMain() {
		return main;
	}
}