package net.imyeyu.netdisk.ctrl;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Screen;
import net.imyeyu.netdisk.request.ImgRequest;
import net.imyeyu.netdisk.view.ViewImg;
import net.imyeyu.utils.gui.BorderX;

public class Img extends ViewImg {

	private double ox = 0, oy = 0, cx = 0, cy = 0, scale = 1;

	public Img(String path) {
		super(path);
		
		// 点击图像
		getMain().setOnMousePressed(event -> {
			getMain().setCursor(Cursor.CLOSED_HAND);
			ox = event.getX();
			oy = event.getY();
			cx = event.getScreenX();
			cy = event.getScreenY();
		});
		// 拖动图像
		getMain().setOnMouseDragged(event -> {
			setX(event.getScreenX() - ox);
			setY(event.getScreenY() - oy);
		});
		// 释放图像
		getMain().setOnMouseReleased(event -> {
			getMain().setCursor(Cursor.DEFAULT);
			if (cx == event.getScreenX() && cy == event.getScreenY()) close();
		});
		// 滚轮事件
		getMain().addEventFilter(ScrollEvent.SCROLL, event -> {
			if (!(getWidth() + event.getDeltaY() < 260)) {
				if (getHeight() < getWidth()) {
					setX(getX() + -event.getDeltaY() / 2);
					setY(getY() + -event.getDeltaY() / 2 * scale);
					setWidth(getWidth() + event.getDeltaY());
					setHeight(getHeight() + event.getDeltaY() * scale);
					getImg().setFitWidth(getWidth() + 1);
					getImg().setFitHeight(getHeight() + 1);
				} else {
					setX(getX() + -event.getDeltaY() / 2 * scale);
					setY(getY() + -event.getDeltaY() / 2);
					setWidth(getWidth() + event.getDeltaY() * scale);
					setHeight(getHeight() + event.getDeltaY());
					getImg().setFitWidth(getWidth() + 1);
					getImg().setFitHeight(getHeight() + 1);
				}
			}
		});
		// 获取图片
		ImgRequest request = new ImgRequest("getImg", path, true);
		request.valueProperty().addListener((obs, oldImg, newImg) -> {
			if (newImg != null) {
				ImageView img = new ImageView(newImg);
				setImg(img);
				setWidth(newImg.getWidth());
				setHeight(newImg.getHeight());
				if (newImg.getHeight() < newImg.getWidth()) { // 横向
					scale = newImg.getHeight() / newImg.getWidth();
					if (1600 < newImg.getWidth() && 960 < 1600 * scale) {
						scale = newImg.getWidth() / newImg.getHeight();
						setHeight();
					} else {
						setWidth();
					}
				} else { // 纵向
					scale = newImg.getWidth() / newImg.getHeight();
					if (960 < newImg.getHeight() && 1600 < 960 * scale) {
						scale = newImg.getHeight() / newImg.getWidth();
						setWidth();
					} else {
						setHeight();
					}
				}
				Rectangle2D screen = Screen.getPrimary().getVisualBounds();
				setX(screen.getMaxX() / 2 - getWidth() / 2);
				setY(screen.getMaxY() / 2 - getHeight() / 2);
				getMain().setBorder(BorderX.EMPTY);
				getMain().setCenter(img);
			}
		});
		request.start();
	}
	
	private void setWidth() {
		setWidth(1600);
		setHeight(1600 * scale);
		getImg().setFitWidth(getWidth() + 1);
		getImg().setFitHeight(getHeight() + 1);
	}
	
	private void setHeight() {
		setWidth(960 * scale);
		setHeight(960);
		getImg().setFitWidth(getWidth() + 1);
		getImg().setFitHeight(getHeight() + 1);
	}
}