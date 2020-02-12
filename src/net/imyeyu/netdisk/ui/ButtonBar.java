package net.imyeyu.netdisk.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import net.imyeyu.utils.gui.BorderX;

public class ButtonBar extends FlowPane {
	
	private int radius = 2;
	private boolean isSetBorder = true;
	
	private void init(Button... button) {
		HBox box = new HBox();
		BorderX border = new BorderX("#B5B5B5", BorderX.SOLID, 1);
		border.setRadius(radius, false);
		
		Insets iconPadding = new Insets(4, 8, 4, 24);
		for (int i = 0; i < button.length; i++) {
			if (0 < i) button[i].setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).left());
			// 直接 setBackground 会覆盖 view 层的 css
			if (button[i].getStyle().equals("")) {
				button[i].setStyle("-fx-background-insets: 0");
			} else {
				if (!button[i].getText().equals("")) button[i].setPadding(iconPadding);
			}
		}
		if (isSetBorder) box.setBorder(border.def());
		box.getChildren().addAll(button);
		
		setAlignment(Pos.CENTER);
		setPrefWidth(Region.USE_PREF_SIZE);
		getChildren().add(box);
	}
	
	public void setBorder(boolean value) {
		this.isSetBorder = value;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void add(Button button) {
		init(button);
	}
	
	public void addAll(Button... button) {
		init(button);
	}
}