package net.imyeyu.netdisk.ui;

import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.imyeyu.utils.gui.BorderX;

public class TipsPane extends BorderPane {
	
	private SimpleIntegerProperty items = new SimpleIntegerProperty();
	private SimpleIntegerProperty selected = new SimpleIntegerProperty();
	private Label speed, tips;

	public TipsPane(ResourceBundle rb) {
		Insets insets = new Insets(2, 6, 2, 6);
		Border border = new BorderX("#B5B5B5", BorderX.SOLID, 1).right();
		// 下载速度
		speed = new Label("下载速度");
		speed.setPadding(insets);
		speed.setBorder(border);
		
		GridPane textPane = new GridPane();
		// 列表状态
		HBox file = new HBox();
		Label tipsItems = new Label();
		items.addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() != 0) {
					tipsItems.setText(newValue.intValue() + " " + rb.getString("mainTipsItems"));
				} else {
					tipsItems.setText("");
				}
			}
		});
		Label tipsSelected = new Label();
		selected.addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() != 0) {
					tipsSelected.setText(newValue.intValue() + " " + rb.getString("mainTipsSelected"));
				} else {
					tipsSelected.setText("");
				}
			}
		});
		
		tipsSelected.setPadding(new Insets(0, 0, 0, 16));
		file.setBorder(border);
		file.setPadding(insets);
		file.getChildren().addAll(tipsItems, tipsSelected);
		// 通用提示
		tips = new Label("通用提示");
		tips.setPadding(insets);

		textPane.add(file, 0, 0);
		textPane.add(tips, 1, 0);

		setLeft(speed);
		setCenter(textPane);
	}

	public void setItems(int value) {
		this.items.set(value);
	}

	public void setSelected(int value) {
		this.selected.set(value);
	}

	public Label getSpeed() {
		return speed;
	}

	public Label getTips() {
		return tips;
	}
}