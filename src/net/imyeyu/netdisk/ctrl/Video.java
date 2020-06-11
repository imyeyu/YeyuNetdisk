package net.imyeyu.netdisk.ctrl;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.util.Duration;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.VideoInfo;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.util.ZoomUtil;
import net.imyeyu.netdisk.view.ViewVideo;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.AnchorPaneX;

/**
 * 视频播放器
 * 
 * @author Yeyu
 *
 */
public class Video extends ViewVideo {
	
	private Map<String, Object> config = Entrance.getConfig();
	
	private int playingID = -1;
	private double ox = 0, oy = 0, cx = 0, cy = 0;
	private boolean isSeeking = false;
	private MediaPlayer player = null;
	private SimpleBooleanProperty setShowCtrl = new SimpleBooleanProperty(true);

	/**
	 * 在预览动作中执行。构造视频播放器并立即播放一个视频<br />
	 * 注意：list 云盘视频列表必须经过格式过滤，只保留播放器所支持的文件<br />
	 * 
	 * @param video 视频播放对象信息
	 * @param list  当前云盘文件视频列表
	 * @param path  当前云盘访问目录
	 */
	public Video(VideoInfo video, List<FileCell> list, String path) {
		// 正在播放的视频在列表的位置
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(video.getFileCellName())) {
				playingID = i;
				break;
			}
		}
		// 准备播放器
		preparePlayer(video);
		
		// 显示界面
		show();
		
		// 控件面板控制
		setShowCtrl.addListener((tmp, o, n) -> {
			getCtrl().setOpacity(n ? 1 : 0);
			getScene().getRoot().setCursor(n ? Cursor.DEFAULT : Cursor.NONE);
		});
		// 场景 - 点击
		getScene().setOnMousePressed(event -> {
			cx = event.getScreenX();
			cy = event.getScreenY();
		});
		// 场景 - 释放
		getScene().setOnMouseReleased(event -> {
			// 点击至释放过程没有拖动
			if (cx == event.getScreenX() && cy == event.getScreenY()) {
				setShowCtrl.set(!setShowCtrl.get());
			}
		});
		// 场景 - 移入
		getScene().setOnMouseEntered(event -> setShowCtrl.set(true));
		// 场景 - 移出
		getScene().setOnMouseExited(event -> setShowCtrl.set(false));
		// 快捷键
		getScene().setOnKeyReleased(event -> {
			switch (event.getCode()) {
				case SPACE:
					togglePlay();
					break;
				case UP:
					getVolume().setValue(getVolume().getValue() + .1);
					break;
				case DOWN:
					getVolume().setValue(getVolume().getValue() - .1);
					break;
				case LEFT:
					player.seek(Duration.seconds(getPb().getValue() - 10));
					break;
				case RIGHT:
					player.seek(Duration.seconds(getPb().getValue() + 10));
					break;
				default:
					break;
			}
		});
		// 头部 点击
		getHeader().setOnMousePressed(event -> {
			getHeader().setCursor(Cursor.CLOSED_HAND);
			ox = event.getX();
			oy = event.getY();
		});
		// 头部 - 拖动
		getHeader().setOnMouseDragged(event -> {
			setX(event.getScreenX() - ox - 10); // 10 像素阴影
			setY(event.getScreenY() - oy - 10);
			if (isFullScreen()) setFullScreen(false);
		});
		// 头部 - 释放
		getHeader().setOnMouseReleased(event -> getHeader().setCursor(Cursor.DEFAULT));
		// 上一个视频
		getPrev().setOnAction(event -> {
			playingID = playingID == 0 ? list.size() - 1 : playingID - 1;
			changeMedia(path, list.get(playingID));
		});
		// 播放状态切换
		getToggle().setOnAction(event -> togglePlay());
		// 下一个视频
		getNext().setOnAction(event -> {
			playingID = playingID == list.size() - 1 ? 0 : playingID + 1;
			changeMedia(path, list.get(playingID));
		});
		// 音量
		getVolume().valueProperty().addListener((tmp, o, n) -> player.setVolume(n.doubleValue()));
		getVolume().setOnMouseReleased(e -> config.put("volume", getVolume().getValue()));
		// 全屏
		getFull().setOnAction(event -> setFullScreen(!isFullScreen()));
		// 全屏监听
		fullScreenProperty().addListener((tmp, o, isFull) -> {
			if (isFull) { // 全屏
				Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
				if (video.getWidth() < bounds.getMaxX()) { // 横向无法全屏
					double scale = bounds.getMaxY() / getHeight(); // 计算高度缩放比
					getMediaView().setFitHeight(bounds.getMaxY());
					getMediaView().setTranslateX((bounds.getMaxX() / 2 - scale * getMediaView().getFitWidth() / 2)); // 偏移
				} else { // 横向超越或等于屏幕
					getMediaView().setFitWidth(bounds.getMaxX());
					double scale = bounds.getMaxX() / video.getWidth(); // 计算宽度缩放比
					if (scale * video.getHeight() < bounds.getMaxY()) { // 预期高度小于屏幕高度
						getMediaView().setTranslateY(64);
					}
				}
				getMainBox().setPadding(Insets.EMPTY);
				YeyuUtils.gui().setBgTp(getFull(), "net/imyeyu/netdisk/res/exFull.png", 32, 0, -2);
			} else {
				getMediaView().setFitWidth(getMediaView().getFitWidth() - 20);
				getMediaView().setFitHeight(getMediaView().getFitHeight() - 20);
				getMediaView().setTranslateX(0);
				getMediaView().setTranslateY(0);
				getMainBox().setPadding(new Insets(10));
				YeyuUtils.gui().setBgTp(getFull(), "net/imyeyu/netdisk/res/full.png", 32, 0, -2);
			}
		});
		// 关闭事件
		getClose().setOnAction(event -> {
			if (player != null) player.dispose();
			this.close();
		});
	}
	
	/**
	 * 播放状态切换
	 * 
	 */
	private void togglePlay() {
		if (player.getStatus().equals(Status.PAUSED) || player.getStatus().equals(Status.READY) || player.getStatus().equals(Status.STOPPED)) {
			player.play();
		} else {
			player.pause();
		}
	}
	
	/**
	 * 初始化播放器，在启动和切换视频时执行
	 * 
	 * @param video 视频播放对象
	 */
	private void preparePlayer(VideoInfo video) {
		// 计算窗体尺寸
		if (video.getDeg() % 180 == 0 && video.getHeight() < video.getWidth()) { // 横向
			getMediaView().setFitWidth(860 - 20);
			setWidth(860);
			setHeight(video.getHeight() * (860 / video.getWidth()) + 8);
		} else { // 纵向
			Rotate rotate = new Rotate(video.getDeg(), 260, 260);
			getMediaView().getTransforms().add(rotate);
			getMediaView().setFitHeight(960);
			setWidth(video.getWidth() * (980 / video.getHeight()));
			setHeight(966);
		}
		// 窗体缩放
		ZoomUtil.addDragEvent(this, getScene().getRoot(), getWidth(), getHeight());
		// 提示区尺寸
		AnchorPaneX.def(getTips(), null, getWidth() / 2 + 60, 6, 6);
		YeyuUtils.gui().tips(getTips(), "正在加载：" + video.getUrl());
		
		// 开始加载视频
		if (player != null) player.dispose();
		setTitle(video.getName());
		try {
			setVideoTitle(video.getName());
			Media media = new Media(video.getUrl());
			player = new MediaPlayer(media);
			player.setAutoPlay(true);
			getMediaView().setMediaPlayer(player);
		} catch (Exception e) {
			getTips().setText("加载失败：" + video.getName());
		}
		// 播放器 - 绑定播放区尺寸
		widthProperty().addListener((tmp, o, n) -> {
			getMediaView().setFitWidth(n.doubleValue() - (isFullScreen() ? 0 : 20));
		});
		// 播放器 - 准备就绪
		player.setOnReady(() -> {
			double second = player.getTotalDuration().toSeconds();
			getPb().setMax(second);
			getTimeMax().setText(second2time((int) second));
			double volume = Double.valueOf(config.get("volume").toString());
			player.setVolume(volume);
			getVolume().setValue(volume);
		});
		// 播放器 - 绑定播放进度条
		player.currentTimeProperty().addListener((tmp, o, n) -> {
			if (!isSeeking) getPb().setValue(n.toSeconds());
		});
		// 播放器 - 进度值监听
		getPb().valueProperty().addListener((tmp, o, n) -> {
			getTimeNow().setText(second2time(n.intValue()));	
		});
		// 播放器 - 进度条按下
		getPb().setOnMousePressed(event -> {
			isSeeking = true;
			player.seek(Duration.seconds(getPb().getValue()));
			isSeeking = false;
		});
		// 播放器 - 进度条拖拽
		getPb().setOnMouseDragged((event) -> isSeeking = true);
		// 播放器 - 进度条释放
		getPb().setOnMouseReleased(event -> {
			player.seek(Duration.seconds(getPb().getValue()));
			isSeeking = false;
		});
		// 播放器 - 播放完成
		player.setOnEndOfMedia(() -> {
			player.stop();
			setShowCtrl.set(true);
		});
		// 播放器 - 播放状态切换
		player.statusProperty().addListener((tmp, o, status) -> {
			if (status.equals(Status.PLAYING)) {
				YeyuUtils.gui().setBgTp(getToggle(), "net/imyeyu/netdisk/res/pause.png", 32, 0, 0);
			} else {
				YeyuUtils.gui().setBgTp(getToggle(), "net/imyeyu/netdisk/res/play.png", 32, 0, 0);
			}
		});
	}
	
	/**
	 * 改变播放媒体
	 * 
	 * @param path 路径
	 * @param file 云盘文件对象
	 */
	private void changeMedia(String path, FileCell file) {
		PublicRequest request = new PublicRequest("getMP4Info", path + file.getDisplayName());
		request.messageProperty().addListener((tmp, o, n) -> {
			JsonObject jo = (new JsonParser()).parse(n).getAsJsonObject();
			VideoInfo video = new VideoInfo();
			video.setName(file.getDisplayName());
			video.setUrl(path + file.getDisplayName(), config.get("ip").toString(), config.get("portHTTP").toString());
			video.setWidth(jo.get("width").getAsDouble());
			video.setHeight(jo.get("height").getAsDouble());
			video.setDeg(jo.get("deg").getAsInt());
			preparePlayer(video);
		});
		request.start();
	}
	
	private String second2time(int second) {
		return String.format("%02d", Math.abs(second / 60)) + ":" + String.format("%02d", second % 60);
	}
}