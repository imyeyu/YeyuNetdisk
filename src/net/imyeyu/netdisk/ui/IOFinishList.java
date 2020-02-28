package net.imyeyu.netdisk.ui;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.util.FileFormat;

public class IOFinishList extends ListView<IOHistory> {
	
	private HBox file;
	private Label name;
	private Button view;
	private ImageView ioIcon, icon;
	private BorderPane main;
	private SimpleStringProperty show = new SimpleStringProperty();
	
	public IOFinishList() {
		setPadding(Insets.EMPTY);
		setStyle("-fx-background-insets: 0");
		setCellFactory(new Callback<ListView<IOHistory>, ListCell<IOHistory>>() {
			public ListCell<IOHistory> call(ListView<IOHistory> param) {
				ListCell<IOHistory> list = new ListCell<IOHistory>() {
					protected void updateItem(IOHistory item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty && item != null) {
							main = new BorderPane();
							
							file = new HBox();
							if (item.isLocal()) {
								ioIcon = new ImageView("net/imyeyu/netdisk/res/download.png");
							} else {
								ioIcon = new ImageView("net/imyeyu/netdisk/res/upload.png");
							}
							icon = new ImageView(FileFormat.getImage(item.getName().substring(item.getName().lastIndexOf(".") + 1)));
							name = new Label(item.getName());
							file.setAlignment(Pos.CENTER_LEFT);
							file.setSpacing(6);
							file.getChildren().addAll(ioIcon, icon, name);
							
							view = new Button("打开文件夹");
							view.setOnAction(event -> {
								if (item.isLocal()) {
									try {
										Runtime.getRuntime().exec("explorer.exe " + item.getPath());
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {
									show.set(item.getPath());
								}
							});
							view.setBackground(Background.EMPTY);
							main.setRight(view);
							main.setCenter(file);
							
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

	public SimpleStringProperty getShow() {
		return show;
	}
}