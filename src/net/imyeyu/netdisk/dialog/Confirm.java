package net.imyeyu.netdisk.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Confirm extends Stage {
	
	private Button confirm, deny;

	public Confirm(String title, String content, boolean showDeny) {

		BorderPane main = new BorderPane();
		
		FlowPane contentPane = new FlowPane();
		Label text = new Label(content);
		contentPane.setAlignment(Pos.CENTER);
		contentPane.getChildren().add(text);
		
		HBox btnBox = new HBox();
		confirm = new Button("确定");
		deny = new Button("否");
		Button cancel = new Button("取消");
		btnBox.setAlignment(Pos.CENTER);
		btnBox.setSpacing(6);
		if (showDeny) {
			btnBox.getChildren().addAll(confirm, deny, cancel);
		} else {
			btnBox.getChildren().addAll(confirm, cancel);
		}
		
		BorderPane.setAlignment(confirm, Pos.CENTER);
		main.setPadding(new Insets(8));
		main.setCenter(contentPane);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/warn.png"));
		setScene(scene);
		setTitle(title);
		setWidth(260);
		setHeight(120);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();
		
		cancel.setOnAction(event -> close());
	}
	
	public void initConfirm(Confirm self, EventHandler<ActionEvent> event) {
		confirm.setOnAction(event);
	}
	
	public void initDeny(Confirm self, EventHandler<ActionEvent> event) {
		deny.setOnAction(event);
	}
}