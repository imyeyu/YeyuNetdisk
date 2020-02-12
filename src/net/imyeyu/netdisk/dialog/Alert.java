package net.imyeyu.netdisk.dialog;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Alert extends Stage {
	
	public Alert(String title, String content) {
		BorderPane main = new BorderPane();
		
		FlowPane contentPane = new FlowPane();
		Label text = new Label(content);
		contentPane.setAlignment(Pos.CENTER);
		contentPane.getChildren().add(text);
		
		Button ok = new Button("确定");
		main.setCenter(contentPane);
		main.setBottom(ok);
		
		Scene scene = new Scene(main);
		setScene(scene);
		setTitle(title);
		setWidth(260);
		setHeight(140);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();
	}
}
