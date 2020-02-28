package net.imyeyu.netdisk.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.ToolTipsX;

public class Photo extends AnchorPane {

	private SimpleBooleanProperty isSelect = new SimpleBooleanProperty(false);
	
	private ImageView img;
	
	public Photo(String name) {
		Label check = new Label();
		check.setTooltip(new ToolTipsX(name));
		AnchorPane.setTopAnchor(check, 0d);
		AnchorPane.setLeftAnchor(check, 0d);
		AnchorPane.setRightAnchor(check, 0d);
		AnchorPane.setBottomAnchor(check, 0d);
		
		img = new ImageView();
		
		getChildren().addAll(img, check);
		
		Border selectBorder = new BorderX("#FF7A9B", BorderX.SOLID, 3).def();
		isSelect.addListener(event -> {
			if (isSelect.get()) {
				check.setBorder(selectBorder);
			} else {
				check.setBorder(BorderX.EMPTY);
			}
		});
	}
	
	public void toggleSelect() {
		this.isSelect.set(!isSelect.get());
	}
	
	public void unSelect() {
		this.isSelect.set(false);
	}
	
	public boolean isSelect() {
		return this.isSelect.get();
	}
	
	public void setImage(Image img) {
		this.img.setImage(img);
	}
	
	public void setViewport(Rectangle2D rec) {
		this.img.setViewport(rec);
	}
}