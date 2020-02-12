package net.imyeyu.netdisk.dialog;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FolderSelector extends Stage {

	public FolderSelector() {
		
		BorderPane main = new BorderPane();
		
		Scene scene = new Scene(main);
		setScene(scene);
		setTitle("选择文件夹");
		setWidth(280);
		setHeight(360);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();
	}
}