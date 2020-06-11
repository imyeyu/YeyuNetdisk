package net.imyeyu.netdisk.ctrl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import javafx.beans.property.SimpleIntegerProperty;
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
import net.imyeyu.utils.ResourceBundleX;

/**
 * 照片管理器
 * 
 * @author Yeyu
 *
 */
public class PhotoManager extends ViewPhotoManager {
	
	private ResourceBundleX rbx = Entrance.getRb();
	private Map<String, Object> config = Entrance.getConfig();
	
	private int count = 0;
	private double defaultWidth = -1;
	private String root, nowYear, sep = File.separator;
	private SimpleIntegerProperty selected = new SimpleIntegerProperty(0);
	
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
				getPhotoList(nowYear);
			}
		};
		// 新增年份
		getAddYear().setOnAction(event -> {
			List<String> list = new ArrayList<>();
			JsonObject jo;
			for (int i = 0; i < date.size(); i++) {
				jo = date.get(i).getAsJsonObject();
				list.add(jo.get("year").getAsString());
			}
			AddYear addYear = new AddYear(list);
			addYear.isFinish().addListener((obs, oldValue, newValue) -> {
				if (newValue) getYearList();
			});
		});
		// 上传
		getUpload().setOnAction(event -> {
			boolean isAutoDate = getAutoDate().getSelectionModel().isSelected(0);
			if (nowYear == null && !isAutoDate) {
				new Alert(rbx.def("error"), rbx.def("photoNotAutoArchiving"));
				return;
			}
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Images File", "*.jpg;*.gif;*.bmp;*.png"));
			fileChooser.setInitialDirectory(new File(config.get("defaultUploadFolder").toString()));
			fileChooser.setTitle(rbx.def("fileSelectTypeFile"));
			List<File> list = fileChooser.showOpenMultipleDialog(null);
			boolean isEn = rbx.def("language").toString().equals("English");
			if (list != null && 0 < list.size()) {
				config.put("defaultUploadFolder", list.get(0).getParent());
				String year, month, toPath;
				year = month = rbx.def("photoNotAutomatic");
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
										if (isEn) {
											String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dev"};
											month = months[Integer.valueOf(dateFormat.format(date)) - 1];
										} else {
											month = dateFormat.format(date);
										}
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
				new Alert(rbx.def("tips"), rbx.r("add") + list.size() + rbx.l("photoUpload"));
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
					if (0 < count) new Alert(rbx.def("tips"), rbx.r("add") + count + rbx.l("photoDownload"));
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
						PublicRequest request = getYearList();
						request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
							if (newValuex.equals("finish") && !nowYear.equals("")) {
								getPhotoList(nowYear);
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
				selected.set(map.size());
			}
		});
		// 反选
		getDeSelect().setOnAction(event -> {
			Map<Photo, String> map = list.getRequestList();
			if (map != null) {
				for (Map.Entry<Photo, String> item : map.entrySet()) {
					item.getKey().toggleSelect();
				}
				selected.set(map.size() - selected.get());
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
				selected.set(0);
			}
		});
		// 刷新
		getRefresh().setOnAction(event -> {
			PublicRequest request = getYearList();
			request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
				if (nowYear != null) {
					if (newValuex.equals("finish")) {
						getPhotoList(nowYear);
					}
				} else {
					getDateList();
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
				FolderSelector selector = new FolderSelector(rbx.def("mainFileMoveTo"), fileList, "move");
				selector.isFinish().addListener((obs, oldValue, newValue) -> {
					if (newValue) {
						PublicRequest request = getYearList();
						request.valueProperty().addListener((obsx, oldValuex, newValuex) -> {
							if (newValuex.equals("finish") && !nowYear.equals("")) {
								getPhotoList(nowYear);
							}
						});
					}
				});
			}
		});
		// 列表选择状态
		selected.addListener((obs, oldValue, newValue) -> {
			if (newValue.intValue() != 0) {
				getSelected().setText(newValue + rbx.l("photoSelected"));
			} else {
				getSelected().setText("");
			}
		});
		// 关闭事件
		setOnCloseRequest(event -> {
			if (list != null) list.shutdown();
		});
		
		getYearList();
	}
	
	// 获取日期列表
	private PublicRequest getYearList() {
		PublicRequest request = new PublicRequest("getPhotoDateList", "");
		request.messageProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonParser jp = new JsonParser();
				date = (JsonArray) jp.parse(newValue);
				NavButton dateBtn;
				JsonObject jo;
				if (0 < date.size()) {
					getDateList().getChildren().clear();
					for (int i = 0; i < date.size(); i++) {
						jo = date.get(i).getAsJsonObject();
						dateBtn = new NavButton(jo.get("year").getAsString());
						dateBtn.setOnAction(clickDateEvent);
						getDateList().getChildren().add(dateBtn);
					}
				} else {
					((NavButton) getDateList().getChildren().get(0)).setText(rbx.def("nullData"));
				}
			}
		});
		request.start();
		return request;
	}
	// 获取照片列表
	private void getPhotoList(String year) {
		if (list != null) list.shutdown();
		JsonObject jo;
		for (int i = 0; i < date.size(); i++) {
			jo = date.get(i).getAsJsonObject();
			if (jo.get("year").getAsString().equals(year)) {
				JsonArray ja = jo.get("months").getAsJsonArray();
				list = new PhotoList(jo.get("year").getAsString(), ja, isCompressImg);
				count = list.getRequestList().size();
				if (count != 0) {
					getCount().setText(rbx.r("photoAll") + count + rbx.l("photos"));
				} else {
					getCount().setText(rbx.def("photoNone", year));
				}
				getSpList().setContent(list);
				// 绑定加载进度条宽度
				list.getPb().prefWidthProperty().bind(getSpList().widthProperty());
				// 选择照片事件
				selectPhotoEvent();
				// 出现滚动条事件
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
					selected.set(selected.get() - 1);
				} else {
					imgList.getKey().toggleSelect();
					if (imgList.getKey().isSelect()) {
						getPhotoInfo(imgList.getValue());
						selected.set(selected.get() + 1);
					} else {
						selected.set(selected.get() - 1);
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