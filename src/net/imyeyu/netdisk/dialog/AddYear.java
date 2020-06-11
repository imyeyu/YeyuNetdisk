package net.imyeyu.netdisk.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
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
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;

public class AddYear extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private Label title;
	private TextField year;
	private VBox content;
	private Button confirm, cancel;
	private List<String> list;
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);

	public AddYear(List<String> list) {
		this.list = list;
		BorderPane main = new BorderPane();
		
		content = new VBox();
		title = new Label(rbx.def("photoEnterYear"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		year = new TextField(dateFormat.format(new Date()));
		content.setSpacing(8);
		content.setPadding(new Insets(8));
		content.getChildren().addAll(title, year);
		
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
		setTitle(rbx.def("photoNewYear"));
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
		year.textProperty().addListener((obs, oldValue, newValue) -> {
			title.setText(rbx.def("photoEnterYear"));
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
		for (int i = 0; i < list.size(); i++) {
			if (year.getText().equals(list.get(i))) {
				title.setText(rbx.def("photoEnterYear") + year.getText() + rbx.l("photoExist"));
				return;
			}
		}
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