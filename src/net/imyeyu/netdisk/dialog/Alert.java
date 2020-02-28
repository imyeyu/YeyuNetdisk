package net.imyeyu.netdisk.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
		
		BorderPane.setAlignment(ok, Pos.CENTER);
		main.setPadding(new Insets(8));
		main.setCenter(contentPane);
		main.setBottom(ok);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/warn.png"));
		setScene(scene);
		setTitle(title);
		setWidth(260);
		setHeight(120);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();
		
		ok.setOnAction(event -> close());
	}
}