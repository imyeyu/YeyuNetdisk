package net.imyeyu.netdisk.dialog;

import java.util.List;

import com.google.gson.Gson;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.gui.BorderX;

public class DeleteFile extends Stage {
	
	private Label content;
	private Button confirm, cancel;
	private List<String> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public DeleteFile(List<String> list) {
		this.list = list;
		
		BorderPane main = new BorderPane();
		content = new Label("确认永久删除此对象吗？");
		content.setAlignment(Pos.CENTER);
		
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
		setTitle("警告");
		setWidth(260);
		setHeight(120);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();

		confirm.setFocusTraversable(false);
		cancel.setFocusTraversable(false);
		
		confirm.setOnAction(event -> confirm());
		cancel.setOnAction(event -> cancel());
	}
	
	private void confirm() {
		confirm.setDisable(true);
		cancel.setDisable(true);
		DeleteService service = new DeleteService(list);
		content.textProperty().bind(service.messageProperty());
		service.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) {
				isFinish.setValue(true);
				close();
			}
		});
		service.start();
	}
	
	private void cancel() {
		close();
	}
	
	public SimpleBooleanProperty isFinish() {
		return isFinish;
	}
}

class DeleteService extends Service<String> {
	
	private List<String> list;
	
	public DeleteService(List<String> list) {
		this.list = list;
	}
	
	protected Task<String> createTask() {
		
		return new Task<String>() {
			protected String call() throws Exception {
				PublicRequest request = new PublicRequest("delete", new Gson().toJson(list).toString());
				request.messageProperty().addListener((obs, oldValue, newValue) -> {
					if (newValue != null) updateMessage("正在删除..." + newValue + " / " + list.size());
				});
				request.valueProperty().addListener((obs, oldValue, newValue) -> {
					if (newValue != null) updateValue("finish");
				});
				request.start();
				return null;
			}
		};
	}
}