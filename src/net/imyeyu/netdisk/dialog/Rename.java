package net.imyeyu.netdisk.dialog;

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

public class Rename extends Stage {
	
//	private ResourceBundle rb;
	
	private Label title, tips;
	private String path, oldValue;
	private TextField name;
	private VBox content;
	private Button confirm, cancel;
	private ObservableList<FileCell> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public Rename(String path, String oldValue, ObservableList<FileCell> list) {
//		this.rb = Entrance.getRb();
		oldValue = oldValue.substring(oldValue.indexOf(".") + 1);
		
		this.list = list;
		this.path = path;
		this.oldValue = oldValue;
		
		BorderPane main = new BorderPane();
		
		content = new VBox();
		title = new Label("请输入新的名称：");
		name = new TextField(oldValue);
		tips = new Label();
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, name);
		
		HBox btnBox = new HBox();
		confirm = new Button("确认");
		cancel = new Button("取消");
		btnBox.setPadding(new Insets(6, 12, 6, 12));
		btnBox.setSpacing(14);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btnBox.getChildren().addAll(confirm, cancel);
		
		main.setCenter(content);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		setScene(scene);
		setTitle("重命名");
		setWidth(260);
		setHeight(140);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();

		confirm.setFocusTraversable(false);
		cancel.setFocusTraversable(false);
		
		confirm.setOnAction(event -> confirm());
		cancel.setOnAction(event -> cancel());
		name.setOnKeyPressed(evnet -> {
			confirm.setDisable(false);
			cancel.setDisable(false);
			content.getChildren().remove(tips);
			setHeight(140);
		});
	}
	
	private void confirm() {
		confirm.setDisable(true);
		cancel.setDisable(true);
		
		for (int i = 0; i < list.size(); i++) {
			if (name.getText().equals(oldValue)) {
				content.getChildren().add(tips);
				tips.setText("该命名对象已存在当前目录");
				setHeight(170);
				return;
			}
		}
		
		RenameFile file = new RenameFile(path, oldValue, name.getText());
		PublicRequest request = new PublicRequest("rename", new Gson().toJson(file));
		request.valueProperty().addListener((list, oldValue, newValue) -> {
			if (newValue != null && Boolean.valueOf(newValue)) isFinish.set(true);
			close();
		});
		request.start();
	}
	
	private void cancel() {
		close();
	}
	
	public SimpleBooleanProperty isFinish() {
		return isFinish;
	}
	
	private class RenameFile {
		
		private String path;
		private String oldValue;
		private String newValue;
		
		public RenameFile(String path, String oldValue, String newValue) {
			this.path = path;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public String toString() {
			return "RenameFile [path=" + path + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
		}
	}
}