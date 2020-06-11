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

public class Rename extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private Label title, tips;
	private String path, oldValue;
	private TextField name;
	private VBox content;
	private Button confirm, cancel;
	private ObservableList<FileCell> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public Rename(String path, String oldValue, ObservableList<FileCell> list) {
		oldValue = oldValue.substring(oldValue.indexOf(".") + 1);
		
		this.list = list;
		this.path = path;
		this.oldValue = oldValue;
		
		BorderPane main = new BorderPane();
		
		content = new VBox();
		title = new Label(rbx.def("renameEnter"));
		name = new TextField(oldValue);
		tips = new Label();
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, name);
		
		HBox btnBox = new HBox();
		confirm = new Button(rbx.def("confirm"));
		cancel = new Button(rbx.def("cancel"));
		btnBox.setPadding(new Insets(6, 12, 6, 12));
		btnBox.setSpacing(14);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btnBox.getChildren().addAll(confirm, cancel);
		
		main.setCenter(content);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/warn.png"));
		setScene(scene);
		setTitle(rbx.def("rename"));
		setWidth(260);
		setHeight(140);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();

		confirm.setFocusTraversable(false);
		cancel.setFocusTraversable(false);

		main.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) confirm();
		});
		confirm.setOnAction(event -> confirm());
		cancel.setOnAction(event -> close());
		name.setOnKeyPressed(evnet -> {
			confirm.setDisable(false);
			cancel.setDisable(false);
			content.getChildren().remove(tips);
			setHeight(140);
		});
	}
	
	private void confirm() {
		confirm.setDisable(true);
		
		// 检查命名
		String oldName;
		for (int i = 0; i < list.size(); i++) {
			oldName = list.get(i).getName();
			if (name.getText().equals(oldName.substring(oldName.indexOf(".") + 1))) {
				content.getChildren().add(tips);
				tips.setText(rbx.def("renameExist"));
				setHeight(170);
				return;
			}
		}

		cancel.setDisable(true);
		
		RenameFile file = new RenameFile(path, oldValue, name.getText());
		PublicRequest request = new PublicRequest("rename", new Gson().toJson(file));
		request.valueProperty().addListener((list, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) isFinish.set(true);
			close();
		});
		request.start();
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