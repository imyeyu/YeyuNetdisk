package net.imyeyu.netdisk.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.gui.BorderX;

public class Zip extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private Label title, tips;
	private Button confirm, skip, cancel;
	private String path;
	private boolean isFail = false;
	
	private VBox content;
	private TextField name;
	private ObservableList<FileCell> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public Zip(String path, ObservableList<FileCell> list) {
		this.path = path;
		this.list = list;
		
		BorderPane main = new BorderPane();
		content = new VBox();
		title = new Label(rbx.def("enterZipName"));
		name = new TextField();
		tips = new Label();
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, name);
		
		HBox btnBox = new HBox();
		confirm = new Button(rbx.def("confirm"));
		confirm.setDisable(true);
		skip = new Button(rbx.def("skip"));
		skip.setDisable(true);
		cancel = new Button(rbx.def("cancel"));
		btnBox.setPadding(new Insets(6, 12, 6, 12));
		btnBox.setSpacing(14);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btnBox.getChildren().addAll(confirm, skip, cancel);
		
		main.setCenter(content);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/7z.png"));
		setScene(scene);
		setTitle(rbx.def("zipFile"));
		setWidth(260);
		setHeight(140);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();

		confirm.setFocusTraversable(false);
		skip.setFocusTraversable(false);
		cancel.setFocusTraversable(false);
		
		confirm.setOnAction(event -> confirm());
		skip.setOnAction(event -> close());
		cancel.setOnAction(event -> close());
		
		main.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) confirm();
		});
		name.setOnKeyReleased(evnet -> {
			if (name.getText().equals("")) {
				confirm.setDisable(true);
			} else {
				confirm.setDisable(false);
			}
			cancel.setDisable(false);
			content.getChildren().remove(tips);
			setHeight(140);
		});
	}
	
	private void confirm() {
		name.setDisable(true);
		confirm.setDisable(true);
		cancel.setDisable(true);

		content.getChildren().add(tips);
		setHeight(170);

		// 检查命名
		String fileName;
		for (int i = 0; i < list.size(); i++) {
			fileName = list.get(i).getName();
			if (name.getText().equals(fileName.substring(fileName.indexOf(".") + 1))) {
				tips.setText(rbx.def("renameExist"));
				return;
			}
		}

		cancel.setDisable(true);
		
		List<String> zipFiles = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			fileName = list.get(i).getName();
			zipFiles.add(path + fileName.substring(fileName.indexOf(".") + 1));
		}
		// 请求压缩
		ZipFile file = new ZipFile(name.getText(), path, zipFiles);
		PublicRequest request = new PublicRequest("zip", new Gson().toJson(file));
		request.messageProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("fail")) {
				isFail = true;
				title.setText(rbx.def("zipFail"));
				name.setDisable(true);
				confirm.setDisable(true);
				skip.setDisable(true);
				cancel.setDisable(false);
			}
		});
		request.valueProperty().addListener((list, oldValue, newValue) -> {
			if (!isFail && newValue.equals("finish")) {
				isFinish.set(true);
				close();
			}
		});
		request.start();
	}
	
	public SimpleBooleanProperty isFinish() {
		return isFinish;
	}
	
	private class ZipFile {
		
		private String name;
		private String path;
		private List<String> list;
		
		public ZipFile(String name, String path, List<String> list) {
			this.name = name;
			this.path = path;
			this.list = list;
		}

		public String toString() {
			return "ZipFile [name=" + name + ", path=" + path + ", list=" + list + "]";
		}
	}
}