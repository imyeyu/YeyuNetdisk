package net.imyeyu.netdisk.ui;

import java.text.DecimalFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAction;
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
import javafx.util.Callback;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.util.FileIcon;
import net.imyeyu.utils.YeyuUtils;

public class IOList extends ListView<IOCell>{
	
	private DecimalFormat format = new DecimalFormat("#,###");
	
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
			
			int i = 0;
			private ObservableList<IOCell> obsList = list;
			private Button cancel;
			
			public ListCell<IOCell> call(ListView<IOCell> param) {
				ListCell<IOCell> list = new ListCell<IOCell>() {
					
					public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
						super.executeAccessibleAction(action, parameters);
						System.out.println("action");
					}

					protected void updateItem(IOCell item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty && item != null) {
							AnchorPane main = new AnchorPane();
							main.setPrefHeight(30);
							// 进度条
							ProgressBar pb = new ProgressBar(-1);
							pb.progressProperty().bind(item.getPercentProperty());
							AnchorPane.setTopAnchor(pb, -4d);
							AnchorPane.setLeftAnchor(pb, -10d);
							AnchorPane.setRightAnchor(pb, -10d);
							AnchorPane.setBottomAnchor(pb, -4d);
							// 文件名
							HBox file = new HBox();
							String format = item.getName().substring(item.getName().lastIndexOf(".") + 1);
							ImageView icon = new ImageView(FileIcon.getImage(format));
							Label name = new Label(item.getName());
							file.setSpacing(6);
							file.getChildren().addAll(icon, name);
							AnchorPane.setTopAnchor(file, 6d);
							// 文件大小进度
							Label size = new Label();
							String maxSize = formatSize(item.getMaxSize());
							size.textProperty().bind(new SimpleStringProperty(
								formatSize(item.getSizeProperty().doubleValue()) + " / " + maxSize
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

							/*
							 * 取消动作
							 * 以下代码可以正常执行，但存在 bug
							 * 理论上不可取消正在传输的任务
							 * 
							 */
							cancel.setOnAction(event -> {
//								for (int j = 0; j < obsList.size(); j++) {
//									if (item.getName().equals(Upload.getListProperty().get(j).getName())) {
//										obsList.remove(j);
//										Upload.getListProperty().remove(j);
//										return;
//									}
//								}
							});
							main.getChildren().addAll(pb, file, size);
							if (1 < i) main.getChildren().add(cancel);
							this.setGraphic(main);
							
							i++;
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