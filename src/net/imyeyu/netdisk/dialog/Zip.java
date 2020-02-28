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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.gui.BorderX;

public class Zip extends Stage {
	
//	private ResourceBundle rb;
	
	private Label title, tips;
	private String path;
	private TextField name;
	private VBox content;
	private Button confirm, skip, cancel;
	private ObservableList<FileCell> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public Zip(String path, ObservableList<FileCell> list) {
		this.path = path;
		this.list = list;
		
		BorderPane main = new BorderPane();
		content = new VBox();
		title = new Label("请输入压缩包名称：");
		name = new TextField();
		tips = new Label();
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, name);
		
		HBox btnBox = new HBox();
		confirm = new Button("确认");
		skip = new Button("跳过");
		skip.setDisable(true);
		cancel = new Button("取消");
		btnBox.setPadding(new Insets(6, 12, 6, 12));
		btnBox.setSpacing(14);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btnBox.getChildren().addAll(confirm, skip, cancel);
		
		main.setCenter(content);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		setScene(scene);
		setTitle("压缩文件");
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
		
		name.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue.equals("")) confirm.setDisable(true);
		});
		name.setOnKeyPressed(evnet -> {
			confirm.setDisable(false);
			skip.setDisable(true);
			cancel.setDisable(false);
			content.getChildren().remove(tips);
			setHeight(140);
		});
	}
	
	private void confirm() {
		name.setDisable(true);
		confirm.setDisable(true);
		skip.setDisable(false);

		content.getChildren().add(tips);
		setHeight(170);

		// 检查命名
		String fileName;
		for (int i = 0; i < list.size(); i++) {
			fileName = list.get(i).getName();
			if (name.getText().equals(fileName.substring(fileName.indexOf(".") + 1))) {
				tips.setText("该命名对象已存在当前目录");
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
			System.out.println(newValue);
		});
		request.valueProperty().addListener((list, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) isFinish.set(true);
			close();
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