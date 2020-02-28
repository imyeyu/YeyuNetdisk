package net.imyeyu.netdisk.ctrl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.DownloadFile;
import net.imyeyu.netdisk.bean.PhotoInfo;
import net.imyeyu.netdisk.bean.UploadFile;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.dialog.AddYear;
import net.imyeyu.netdisk.dialog.Alert;
import net.imyeyu.netdisk.dialog.DeleteFile;
import net.imyeyu.netdisk.dialog.FolderSelector;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.netdisk.ui.Photo;
import net.imyeyu.netdisk.ui.PhotoList;
import net.imyeyu.netdisk.view.ViewPhotoManager;

/**
 * 照片管理器
 * 
 * @author Yeyu
 *
 */
public class PhotoManager extends ViewPhotoManager {
	
	private ResourceBundle rb = Entrance.getRb();
	private Map<String, Object> config = Entrance.getConfig();
	
	private double defaultWidth = -1;
	private String root, nowYear, sep = File.separator;
	
	private boolean isCompressImg = false;
	private JsonArray date;
	private PhotoList list;
	private SimpleBooleanProperty isShowListScroll = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty isShowDateScroll = new SimpleBooleanProperty(false);
	private EventHandler<ActionEvent> clickDateEvent;
	
	public PhotoManager(Upload upload, Download download, String root, boolean isCompressImg) {
		this.root = root;
		this.isCompressImg = isCompressImg;
		defaultWidth = getWidth();

		// 日期列表出现滚动条时调整窗体尺寸
		isShowDateScroll.addListener((obs, oldValue, isShowDateScroll) -> {
			if (isShowDateScroll) {
				setWidth(getWidth() + 13);
				getSpDate().setPrefWidth(getSpDate().getWidth() + 13);
				defaultWidth += 13;
			}
		});
		// 照片列表出现滚动条时调整窗体尺寸
		isShowListScroll.addListener((obs, oldValue, isShowListScroll) -> {
			if (isShowListScroll) {
				setWidth(getWidth() + 16);
			} else {
				setWidth(getWidth() - 16);
			}
		});
		getDateList().heightProperty().addListener((obs, oldValue, newValue) -> {
			isShowDateScroll.set(getSpDate().getHeight() < newValue.doubleValue());
		});
		// 点击日期
		clickDateEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Button btn = (Button) event.getSource();
				nowYear = btn.getText();
				requestPhotoList(nowYear);
			}
		};
		// 新增年份
		getAddYear().setOnAction(event -> {
			AddYear addYear = new AddYear();
			addYear.isFinish().addListener((obs, oldValue, newValue) -> {
				if (newValue) requestDateList();
			});
		});
		// 上传
		getUpload().setOnAction(event -> {
			boolean isAutoDate = getAutoDate().getSelectionModel().isSelected(0);
			if (nowYear == null && !isAutoDate) {
				new Alert("错误", "非自动归档需要选择年份再上传");
				return;
			}
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Images File", "*.jpg;*.gif;*.bmp;*.png"));
			fileChooser.setInitialDirectory(new File(config.get("defaultUploadFolder").toString()));
			fileChooser.setTitle(rb.getString("fileSelectTypeFile"));
			List<File> list = fileChooser.showOpenMultipleDialog(null);
			if (list != null && 0 < list.size()) {
				config.put("defaultUploadFolder", list.get(0).getParent());
				String year = "未归档", month = "未归档", toPath;
				File file;
				if (isAutoDate) { // 自动归档
					for (int i = 0; i < list.size(); i++) {
						file = list.get(i);
						try {
							Metadata metadata = ImageMetadataReader.readMetadata(file);
							out:for (Directory dir : metadata.getDirectories()) {
								if (dir == null) continue;
								for (Tag tag : dir.getTags()) {
									if (tag.getTagName().equals("Date/Time Original")) { // 获取拍摄时间
										SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
										Date date = dateFormat.parse(tag.getDescription());
										dateFormat = new SimpleDateFormat("yyyy");
										year = dateFormat.format(date);
										dateFormat = new SimpleDateFormat("M");
										month = dateFormat.format(date);
										break out;
									}
								}
							}
						} catch (ImageProcessingException | IOException | ParseException e) {
							e.printStackTrace();
						}
						toPath = root + sep + year + sep + month + sep;
						// 添加到上传队列
						upload.add(new UploadFile(file.getName(), file.getAbsolutePath(), toPath, file.length()));
					}
				} else { // 非自动归档
					for (int i = 0; i < list.size(); i++) {
						file = list.get(i);
						month = getAutoDate().getSelectionModel().getSelectedItem();
						month = month.substring(0, month.indexOf(" "));
						toPath = root + sep + nowYear + sep + month + sep;
						upload.add(new UploadFile(file.getName(), file.getAbsolutePath(), toPath, file.length()));
					}
				}
				new Alert("提示", "已添加 " + list.size() + " 张照片到上传列表");
			}
		});
		// 下载
		getDownload().setOnAction(event -> {
			if (list != null) {
				Map<Photo, String> map = list.getRequestList();
				if (map != null) {
					int count = 0;
					for (Map.Entry<Photo, String> item : map.entrySet()) {
						if (item.getKey().isSelect()) {
							count++;
							// 获取照片路径
							PublicRequest request = new PublicRequest("getPhotoInfo", item.getValue());
							request.messageProperty().addListener((obs, oldValue, newValue) -> {
								if (!newValue.equals("")) {
									JsonParser jp = new JsonParser();
									JsonObject jo = (JsonObject) jp.parse(newValue);
									PhotoInfo info = new Gson().fromJson(jo, PhotoInfo.class);
									String value = item.getValue();
									String path = root + sep + value.substring(0, value.lastIndexOf(sep));
									(new File(config.get("dlLocation").toString() + sep + path)).mkdirs();
									// 添加到下载队列
									DownloadFile dlFile = new DownloadFile(
										value.substring(value.lastIndexOf(sep) + 1),
										sep + path + sep,
										path,
										Long.valueOf(info.getSize())
									);
									download.add(dlFile);
								}
							});
							request.start();
							item.getKey().toggleSelect();
						}
					}
					if (0 < count) new Alert("提示", "已添加 " + count + " 张照片到下载列表");
				}
			}
		});
		// 删除
		getDel().setOnAction(event -> {
			if (list != null) {
				Map<Photo, String> map = list.getRequestList();
				if (map != null) {
					List<String> fileList = new ArrayList<>();
					for (Map.Entry<Photo, String> item : map.entrySet()) {
						if (item.getKey().isSelect()) {
							fileList.add(root + sep + item.getValue());
							item.getKey().toggleSelect();
						}
					}
					DeleteFile deleteFile = new DeleteFile(fileList);
					deleteFile.isFinish().addListener((tmp, oldValue, newValue) -> {
						PublicRequest request = requestDateList();
						request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
							if (newValuex.equals("finish") && !nowYear.equals("")) {
								requestPhotoList(nowYear);
							}
						});
					});
				}
			}
		});
		// 全选
		getSelectAll().setOnAction(event -> {
			Map<Photo, String> map = list.getRequestList();
			if (map != null) {
				for (Map.Entry<Photo, String> item : map.entrySet()) {
					if (!item.getKey().isSelect()) item.getKey().toggleSelect();
				}
			}
		});
		// 反选
		getDeSelect().setOnAction(event -> {
			Map<Photo, String> map = list.getRequestList();
			if (map != null) {
				for (Map.Entry<Photo, String> item : map.entrySet()) {
					item.getKey().toggleSelect();
				}
			}
		});
		// 取消选择
		getUnSelect().setOnAction(event -> {
			Map<Photo, String> map = list.getRequestList();
			if (map != null) {
				for (Map.Entry<Photo, String> item : map.entrySet()) {
					if (item.getKey().isSelect()) item.getKey().toggleSelect();
				}
				getInfoTable().clear();
			}
		});
		// 刷新
		getRefresh().setOnAction(event -> {
			PublicRequest request = requestDateList();
			request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
				if (newValuex.equals("finish") && !nowYear.equals("")) {
					requestPhotoList(nowYear);
				}
			});
		});
		// 移动
		getMove().setOnAction(event -> {
			if (list != null) {
				List<String> fileList = new ArrayList<>();
				for (Map.Entry<Photo, String> imgList : list.getRequestList().entrySet()) {
					if (imgList.getKey().isSelect()) {
						fileList.add(root + sep + imgList.getValue());
					}
				}
				FolderSelector selector = new FolderSelector("移动到", fileList, "move");
				selector.isFinish().addListener((obs, oldValue, newValue) -> {
					if (newValue) {
						PublicRequest request = requestDateList();
						request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
							if (newValuex.equals("finish") && !nowYear.equals("")) {
								requestPhotoList(nowYear);
							}
						});
					}
				});
			}
		});
		// 关闭事件
		setOnCloseRequest(event -> {
			if (list != null) list.shutdown();
		});
		
		requestDateList();
	}
	
	// 获取日期列表
	private PublicRequest requestDateList() {
		PublicRequest request = new PublicRequest("getPhotoDateList", "");
		request.messageProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonParser jp = new JsonParser();
				date = (JsonArray) jp.parse(newValue);
				NavButton dateBtn;
				JsonObject jo;
				getDateList().getChildren().clear();
				for (int i = 0; i < date.size(); i++) {
					jo = date.get(i).getAsJsonObject();
					dateBtn = new NavButton(jo.get("year").getAsString());
					dateBtn.setOnAction(clickDateEvent);
					getDateList().getChildren().add(dateBtn);
				}
			}
		});
		request.start();
		return request;
	}
	// 获取照片列表
	private void requestPhotoList(String year) {
		if (list != null) list.shutdown();
		JsonObject jo;
		for (int i = 0; i < date.size(); i++) {
			jo = date.get(i).getAsJsonObject();
			if (jo.get("year").getAsString().equals(year)) {
				list = new PhotoList(jo.get("year").getAsString(), jo.get("months").getAsJsonArray(), isCompressImg);
				getSpList().setContent(list);
				// 绑定加载进度条宽度
				list.getPb().prefWidthProperty().bind(getSpList().widthProperty());
				// 选择照片事件
				selectPhotoEvent();
				list.heightProperty().addListener((obs, oldValue, newValue) -> {
					isShowListScroll.set(getSpList().getHeight() < newValue.doubleValue());
					if (isShowListScroll.get()) {
						list.setPrefWidth(getSpList().getWidth() - 16);
						getSpList().widthProperty().addListener((obsx, oldValuex, newValuex) -> list.setPrefWidth(newValuex.doubleValue() - 16));
					} else {
						list.setPrefWidth(getSpList().getWidth());
						getSpList().widthProperty().addListener((obsx, oldValuex, newValuex) -> list.setPrefWidth(newValuex.doubleValue()));
					}
				});
				return;
			}
		}
	}
	// 注册选择照片事件
	private void selectPhotoEvent() {
		for (Map.Entry<Photo, String> imgList : list.getRequestList().entrySet()) {
			imgList.getKey().setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					new Img(root + sep + imgList.getValue());
					imgList.getKey().toggleSelect();
				} else {
					imgList.getKey().toggleSelect();
					if (imgList.getKey().isSelect()) {
						getPhotoInfo(imgList.getValue());
					}
				}
			});
		}
	}
	// 获取照片信息
	private void getPhotoInfo(String path) {
		PublicRequest request = new PublicRequest("getPhotoInfo", path);
		request.messageProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonParser jp = new JsonParser();
				JsonObject jo = (JsonObject) jp.parse(newValue);
				PhotoInfo info = new Gson().fromJson(jo, PhotoInfo.class);
				getInfoTable().set(info);
			}
		});
		request.start();
	}
	// 照片信息键值对
	public class InfoKV {
		
		private String k;
		private String v;
		
		public InfoKV(String k, String v) {
			this.k = k;
			this.v = v;
		}

		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
	}
}