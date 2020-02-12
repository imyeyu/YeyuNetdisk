package net.imyeyu.netdisk.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class ServerState extends HBox {
	
	private ProgressBar pb;

	public ServerState(String labelText, double width) {
		Label label = new Label(labelText);
		pb = new ProgressBar();
		pb.setPrefWidth(width);
		
		setPrefWidth(Region.USE_PREF_SIZE);
		setPadding(new Insets(2, 6, 2, 6));
		setAlignment(Pos.CENTER_RIGHT);
		getChildren().addAll(label, pb);
	}
	
	public ProgressBar getPB() {
		return pb;
	}
}