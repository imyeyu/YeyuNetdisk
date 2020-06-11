package net.imyeyu.netdisk.ui;

import javafx.scene.control.Button;
import net.imyeyu.utils.gui.BorderX;

public class NavButton extends Button {
	
	private void init(String text, double width, double height) {
		setText(text);
		setPrefWidth(width);
		setPrefHeight(height);
		setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).bottom());
		setStyle("-fx-background-color: #E4E4E4; -fx-background-insets: 0");
		setOnMouseEntered(event -> setStyle("-fx-background-color: #F4F4F4"));
		setOnMouseExited(event -> setStyle("-fx-background-color: #E4E4E4"));
		setOnMousePressed(event -> setStyle("-fx-background-color: #D7D7D7"));
		setOnMouseReleased(event -> setStyle("-fx-background-color: #F4F4F4"));
	}
	
	public NavButton(String text) {
		init(text, 155, 36);
	}
	
	public NavButton(String text, double width, double height) {
		init(text, width, height);
	}
}