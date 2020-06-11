package net.imyeyu.netdisk.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;

public class TipsPane extends BorderPane {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private SimpleIntegerProperty items = new SimpleIntegerProperty();
	private SimpleIntegerProperty selected = new SimpleIntegerProperty();
	private Label speed, tips;

	public TipsPane() {
		Font font = Font.font(13);
		Insets insets = new Insets(2, 6, 2, 6);
		Border border = new BorderX("#B5B5B5", BorderX.SOLID, 1).right();
		// 传输速度
		HBox speedBox = new HBox();
		Label speedIcon = new Label();
		speedIcon.setPrefSize(16, 16);
		YeyuUtils.gui().setBg(speedIcon, "net/imyeyu/netdisk/res/ioList.png", 16, 2, 2);
		speed = new Label("0 B/s");
		speed.setFont(font);
		speed.setPadding(insets);
		speed.setBorder(border);
		speedBox.getChildren().addAll(speedIcon, speed);
		
		GridPane textPane = new GridPane();
		// 列表状态
		HBox file = new HBox();
		Label tipsItems = new Label();
		tipsItems.setFont(font);
		items.addListener((obs, oldValue, newValue) -> {
			if (newValue.intValue() != 0) {
				tipsItems.setText(newValue.intValue() + rbx.l("mainTipsItems"));
			} else {
				tipsItems.setText("");
			}
		});
		Label tipsSelected = new Label();
		tipsSelected.setFont(font);
		selected.addListener((obs, oldValue, newValue) -> {
			if (newValue.intValue() != 0) {
				tipsSelected.setText(newValue.intValue() + rbx.l("mainTipsSelected"));
			} else {
				tipsSelected.setText("");
			}
		});
		
		tipsSelected.setPadding(new Insets(0, 0, 0, 16));
		file.setBorder(border);
		file.setPadding(insets);
		file.getChildren().addAll(tipsItems, tipsSelected);
		// 通用提示
		tips = new Label();
		tips.setFont(font);
		tips.setPadding(insets);

		textPane.add(file, 0, 0);
		textPane.add(tips, 1, 0);

		setLeft(speedBox);
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