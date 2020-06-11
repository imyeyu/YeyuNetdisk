package net.imyeyu.netdisk.ctrl;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import net.imyeyu.netdisk.request.ImgRequest;
import net.imyeyu.netdisk.view.ViewImg;
import net.imyeyu.utils.gui.BorderX;

public class Img extends ViewImg {

	private double ox = 0, oy = 0, cx = 0, cy = 0, scale = 1;

	public Img(String path) {
		super(path);
		
		DropShadow dropshadow = new DropShadow();
		dropshadow.setRadius(5);
		dropshadow.setOffsetX(0);
		dropshadow.setOffsetY(0);
		dropshadow.setSpread(.05);
		dropshadow.setColor(Color.valueOf("#000000DD"));
		
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
			if (!(getWidth() + event.getDeltaY() < 16)) {
				if (getImg().getFitHeight() < getImg().getFitWidth()) {
					setX(getX() + -event.getDeltaY() / 2);
					setY(getY() + -(event.getDeltaY() / 2 * scale));
					setWidth(getWidth() + event.getDeltaY());
					setHeight(getHeight() + event.getDeltaY() * scale);
					getImg().setFitWidth(getWidth() - 10);
					getImg().setFitHeight(getHeight() - 10);
				} else {
					setX(getX() + -(event.getDeltaY() / 2 * scale));
					setY(getY() + -event.getDeltaY() / 2);
					setWidth(getWidth() + event.getDeltaY() * scale);
					setHeight(getHeight() + event.getDeltaY());
					getImg().setFitWidth(getWidth() - 10);
					getImg().setFitHeight(getHeight() - 10);
				}
			}
		});
		// 获取图片
		ImgRequest request = new ImgRequest("getImg", path, true);
		getPB().progressProperty().bind(request.progressProperty());
		getOnload().textProperty().bind(request.messageProperty());
		request.valueProperty().addListener((obs, oldImg, newImg) -> {
			if (newImg != null) {
				ImageView img = new ImageView(newImg);
				setImg(img);
				
				double imgW = Double.valueOf(newImg.getWidth());
				double imgH = Double.valueOf(newImg.getHeight());
				
				setWidth(imgW + 10);
				setHeight(imgH + 10);
				
				if (imgH < imgW) { // 横向
					scale = imgH / imgW; // 宽高比
					if (1600 < imgW || 820 < imgH) {
						if (1600 < imgW || 3 < imgW / imgH) {
							setWidth();
						} else {
							if (imgW < imgH) {
								setHeight();
							} else {
								setWidth();
							}
						}
					}
				} else { // 纵向
					scale = imgW / imgH; // 宽高比
					if (1600 < imgW || 820 < imgH) {
						if ((960 < imgH && 1200 < 960 * scale) || 3 < imgH / imgW) {
							setWidth();
						} else {
							if (imgW < imgH) {
								setHeight();
							} else {
								setWidth();
							}
						}
					}
				}
				Rectangle2D screen = Screen.getPrimary().getVisualBounds();
				setX(screen.getMaxX() / 2 - getWidth() / 2);
				setY(screen.getMaxY() / 2 - getHeight() / 2);
				getMain().getChildren().remove(getPB());
				getImgBox().setBackground(Background.EMPTY);
				getImgBox().setBorder(BorderX.EMPTY);
				getImgBox().setCenter(img);
				getImgBox().setEffect(dropshadow);
			}
		});
		request.start();
	}
	
	private void setWidth() {
		setWidth(1200 + 10);
		setHeight(1200 * scale + 10);
		getImg().setFitWidth(getWidth() - 10);
		getImg().setFitHeight(getHeight() - 10);
	}
	
	private void setHeight() {
		setWidth(960 * scale + 10);
		setHeight(960 + 10);
		getImg().setFitWidth(getWidth() - 10);
		getImg().setFitHeight(getHeight() - 10);
	}
}