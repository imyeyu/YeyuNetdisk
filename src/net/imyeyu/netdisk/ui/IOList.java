package net.imyeyu.netdisk.ui;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import net.imyeyu.netdisk.bean.DownloadFile;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.bean.UploadFile;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.util.FileFormat;
import net.imyeyu.utils.YeyuUtils;

public class IOList extends ListView<IOCell>{
	
	private DecimalFormat format = new DecimalFormat("#,###");
	private AnchorPane main;
	private ProgressBar pb;
	private HBox file;
	private ImageView icon;
	private Label name, size;
	
	public IOList(ObservableList<IOCell> list) {
		super(list);
		Background bgCancel = new Background(new BackgroundImage(
			new Image("net/imyeyu/netdisk/res/cancel.png"),
			BackgroundRepeat.NO_REPEAT,
			BackgroundRepeat.NO_REPEAT,
			BackgroundPosition.CENTER,
			BackgroundSize.DEFAULT
		));
		setPadding(Insets.EMPTY);
		setStyle("-fx-background-insets: 0");
		setCellFactory(new Callback<ListView<IOCell>, ListCell<IOCell>>() {
			
			private ObservableList<IOCell> obsList = list;
			private Button cancel;
			
			public ListCell<IOCell> call(ListView<IOCell> param) {
				ListCell<IOCell> list = new ListCell<IOCell>() {
					protected void updateItem(IOCell item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty && item != null) {
							main = new AnchorPane();
							main.setPrefHeight(30);
							// 进度条
							pb = new ProgressBar(-1);
							pb.progressProperty().bind(item.getPercentProperty());
							AnchorPane.setTopAnchor(pb, -4d);
							AnchorPane.setLeftAnchor(pb, -10d);
							AnchorPane.setRightAnchor(pb, -10d);
							AnchorPane.setBottomAnchor(pb, -4d);
							// 文件名
							file = new HBox();
							icon = new ImageView(FileFormat.getImage(item.getName().substring(item.getName().lastIndexOf(".") + 1)));
							name = new Label(item.getName());
							file.setSpacing(6);
							file.getChildren().addAll(icon, name);
							AnchorPane.setTopAnchor(file, 6d);
							AnchorPane.setLeftAnchor(file, 4d);
							AnchorPane.setRightAnchor(file, 140d);
							// 文件大小进度
							size = new Label();
							size.textProperty().bind(new SimpleStringProperty(
								formatSize(item.getSizeProperty().doubleValue()) + " / " + formatSize(item.getMaxSize())
							));
							AnchorPane.setTopAnchor(size, 6d);
							AnchorPane.setRightAnchor(size, 32d);
							// 取消任务
							cancel = new Button();
							cancel.setPrefSize(16, 16);
							cancel.setPadding(Insets.EMPTY);
							cancel.setBackground(bgCancel);
							AnchorPane.setTopAnchor(cancel, 6d);
							AnchorPane.setRightAnchor(cancel, 4d);
							cancel.setOnAction(event -> {
								SimpleListProperty<UploadFile> uplaod = Upload.getListProperty();
								for (int i = 0; i < uplaod.size(); i++) {
									if (item.getName().equals(uplaod.get(i).getName())) {
										obsList.remove(i);
										Upload.getListProperty().remove(i);
										return;
									}
								}
								SimpleListProperty<DownloadFile> download = Download.getListProperty();
								for (int i = 0; i < download.size(); i++) {
									if (item.getName().equals(download.get(i).getName())) {
										obsList.remove(i);
										Download.getListProperty().remove(i);
										return;
									}
								}
							});
							
							main.getChildren().addAll(pb, file, size);
							if (pb.getProgress() == -1) main.getChildren().add(cancel);
							
							setPrefWidth(Region.USE_PREF_SIZE);
							this.setGraphic(main);
						} else {
							this.setGraphic(null);
						}
					}
				};
				return list;
			}
		});
	}
	
	private String formatSize(double size) {
		return YeyuUtils.tools().storageFormat(size, format);
	}
}