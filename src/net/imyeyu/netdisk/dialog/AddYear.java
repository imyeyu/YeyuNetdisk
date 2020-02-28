package net.imyeyu.netdisk.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleBooleanProperty;
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
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;

public class AddYear extends Stage {
	
	private Label title;
	private TextField year;
	private VBox content;
	private Button confirm, cancel;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public AddYear() {
		BorderPane main = new BorderPane();
		
		content = new VBox();
		title = new Label("请输入年份：");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		year = new TextField(dateFormat.format(new Date()));
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, year);
		
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
		setTitle("新建年份");
		setWidth(260);
		setHeight(140);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		show();

		confirm.setFocusTraversable(false);
		cancel.setFocusTraversable(false);

		year.textProperty().addListener((obs, oldValue, newValue) -> {
			if (YeyuUtils.encode().isNumber(newValue)) {
				year.setText(newValue);
			} else {
				year.setText(oldValue);
			}
		});
		
		confirm.setOnAction(event -> confirm());
		cancel.setOnAction(event -> close());
	}
	
	private void confirm() {
		PublicRequest request = new PublicRequest("addYear", year.getText());
		request.valueProperty().addListener((list, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) isFinish.set(true);
			close();
		});
		request.start();
	}
	
	public SimpleBooleanProperty isFinish() {
		return isFinish;
	}
}