package net.imyeyu.netdisk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.AnchorPaneX;
import net.imyeyu.utils.gui.BorderX;

public class ViewVideo extends Stage {
	
	private Label videoTitle, timeNow, timeMax, tips;
	private Slider pb, volume;
	private Button close, prev, toggle, next, full;
	private MediaView mediaView;
	private BorderPane ctrl;
	private AnchorPane mainBox, header;

	public ViewVideo() {
		mainBox = new AnchorPane();
		
		AnchorPane main = new AnchorPane();
		// 播放区域
        mediaView = new MediaView();
        AnchorPaneX.def(mediaView);
        // 控制面板
        ctrl = new BorderPane();
        // 顶部
        videoTitle = new Label();
        videoTitle.setTextFill(Paint.valueOf("#F4F4F4"));
        videoTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
        videoTitle.setTextAlignment(TextAlignment.CENTER);
        videoTitle.setAlignment(Pos.CENTER);
        AnchorPaneX.def(videoTitle, 0, 25);
        
        header = new AnchorPane();
        close = new Button();
        close.setPrefSize(22, 22);
        close.setFocusTraversable(false);
        AnchorPaneX.exLeft(close);
		YeyuUtils.gui().setBgTp(close, "net/imyeyu/netdisk/res/close.png", 22, 0, 2);
        header.setPrefWidth(860);
		header.setStyle("-fx-background-color: #AAAA");
		header.setPadding(new Insets(2, 6, 2, 6));
		header.setBorder(new BorderX("#CCCC", BorderX.SOLID, 1).bottom());
		header.getChildren().addAll(videoTitle, close);
		// 中空区域
		Label nu11 = new Label();
		// 底部
		AnchorPane bottom = new AnchorPane();
		pb = new Slider();
		AnchorPaneX.def(pb, -6, -4, null, -4);
		
		BorderPane timeBox = new BorderPane();
		timeNow = new Label("00:00");
		timeNow.setTextFill(Color.WHITE);
		timeNow.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
		timeMax = new Label("00:00");
		timeMax.setTextFill(Color.WHITE);
		timeMax.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
		timeBox.setLeft(timeNow);
		timeBox.setRight(timeMax);
		timeBox.setPadding(new Insets(4));
		AnchorPaneX.def(timeBox, 6, 8, null, 8);
		
		tips = new Label();
		tips.setTextFill(Color.WHITE);
		
		HBox playCtrl = new HBox();
		prev = new Button();
		prev.setPrefSize(22, 22);
		prev.setFocusTraversable(false);
		YeyuUtils.gui().setBgTp(prev, "net/imyeyu/netdisk/res/playPrev.png", 22, 0, 2);
		toggle = new Button();
		toggle.setPrefSize(32, 32);
		toggle.setFocusTraversable(false);
		YeyuUtils.gui().setBgTp(toggle, "net/imyeyu/netdisk/res/play.png", 32, 0, 0);
		next = new Button();
		next.setPrefSize(22, 22);
		next.setFocusTraversable(false);
		YeyuUtils.gui().setBgTp(next, "net/imyeyu/netdisk/res/playNext.png", 22, 0, 2);
		
		playCtrl.setSpacing(16);
		playCtrl.setAlignment(Pos.CENTER);
		playCtrl.getChildren().addAll(prev, toggle, next);
		AnchorPaneX.def(playCtrl, null, 0, 6, 0);
		
		HBox otherCtrl = new HBox();
		ImageView volumeIcon = new ImageView(new Image("net/imyeyu/netdisk/res/volume.png"));
		volume = new Slider(0, 1, .5);
		volume.setPrefWidth(64);
		volume.setFocusTraversable(false);
		full = new Button();
		full.setPrefSize(32, 18);
		full.setFocusTraversable(false);
		HBox.setMargin(volume, new Insets(0, 16, 0, 4));
		YeyuUtils.gui().setBgTp(full, "net/imyeyu/netdisk/res/full.png", 32, 0, -2);
		otherCtrl.setAlignment(Pos.CENTER_RIGHT);
		otherCtrl.setPadding(new Insets(0, 16, 0, 0));
		otherCtrl.getChildren().addAll(volumeIcon, volume, full);
		AnchorPaneX.def(otherCtrl, null, 0, 8, null);

		bottom.setStyle("-fx-background-color: #AAAA");
		bottom.setBorder(new BorderX("#CCCC", BorderX.SOLID, 1).top());
		bottom.setPrefHeight(64);
		bottom.getChildren().addAll(timeBox, tips, playCtrl, otherCtrl, pb);
		
		ctrl.setTop(header);
		ctrl.setCenter(nu11);
		ctrl.setBottom(bottom);
		AnchorPaneX.def(ctrl);
        
		AnchorPaneX.def(main);
		DropShadow dropshadow = new DropShadow();
		dropshadow.setRadius(6);
		dropshadow.setOffsetX(0);
		dropshadow.setOffsetY(0);
		dropshadow.setSpread(.05);
		dropshadow.setColor(Color.valueOf("#000E"));
		main.setStyle("-fx-background-color: #000");
		main.setEffect(dropshadow);
		main.getChildren().addAll(mediaView, ctrl);
		
		mainBox.setPadding(new Insets(10));
		mainBox.setBackground(Background.EMPTY);
		mainBox.getChildren().add(main);
		
		Scene scene = new Scene(mainBox);
		scene.setFill(null);
		getIcons().add(new Image("net/imyeyu/netdisk/res/video.png"));
		setScene(scene);
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.APPLICATION_MODAL);
	}
	
	public void setVideoTitle(String value) {
		this.videoTitle.setText(value);
	}

	public AnchorPane getHeader() {
		return header;
	}

	public Slider getPb() {
		return pb;
	}
	
	public Label getTimeNow() {
		return timeNow;
	}

	public Label getTimeMax() {
		return timeMax;
	}
	
	public Label getTips() {
		return tips;
	}

	public Slider getVolume() {
		return volume;
	}

	public Button getClose() {
		return close;
	}

	public Button getPrev() {
		return prev;
	}

	public Button getToggle() {
		return toggle;
	}

	public Button getNext() {
		return next;
	}

	public Button getFull() {
		return full;
	}

	public MediaView getMediaView() {
		return mediaView;
	}

	public BorderPane getCtrl() {
		return ctrl;
	}
	
	public AnchorPane getMainBox() {
		return mainBox;
	}
}