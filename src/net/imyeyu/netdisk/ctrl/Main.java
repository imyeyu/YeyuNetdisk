package net.imyeyu.netdisk.ctrl;

import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.bean.VideoInfo;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.NetSpeed;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.dialog.Alert;
import net.imyeyu.netdisk.dialog.Confirm;
import net.imyeyu.netdisk.dialog.DeleteFile;
import net.imyeyu.netdisk.dialog.FolderSelector;
import net.imyeyu.netdisk.dialog.Properties;
import net.imyeyu.netdisk.dialog.Rename;
import net.imyeyu.netdisk.dialog.UnZip;
import net.imyeyu.netdisk.dialog.Zip;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.request.StateRequest;
import net.imyeyu.netdisk.ui.FileListTable;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.netdisk.ui.SystemTrayX;
import net.imyeyu.netdisk.util.FileFormat;
import net.imyeyu.netdisk.util.FileTPService;
import net.imyeyu.netdisk.util.IOHistoryLoader;
import net.imyeyu.netdisk.view.ViewMain;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.TipsX;

public class Main extends Application {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private Upload upload;
	private NetSpeed speed;
	private Download download;
	private StateRequest state;
	private IOHistoryLoader ioHistoryLoader = new IOHistoryLoader();
	private static List<IOHistory> ioHistories;
	
	private int searchIndex = 0;
	private Stage stage;
	private Label tips;
	private boolean isReload = false;
	private ViewMain view;
	private SystemTrayX tray;
	private FileListTable fileList;
	private Stack<String> prevStack, nextStack;
	private Map<String, Object> config = Entrance.getConfig();
	private Map<String, String> server;
	
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		view = new ViewMain();
		if (!config.get("width").toString().equals("default")) view.setWidth(Double.valueOf(config.get("width").toString()));
		if (!config.get("height").toString().equals("default")) view.setHeight(Double.valueOf(config.get("height").toString()));
		
		tips = view.getTipsPane().getTips();
		fileList = view.getFileList();
		
		prevStack = new Stack<String>(); // 返回栈
		nextStack = new Stack<String>(); // 前进栈
		prevStack.push(view.getPath().getText());
		getFileList(prevStack.peek());
		
		// 获取服务器配置
		getServerConfig();
		// 系统托盘
		tray = new SystemTrayX(this);
		// 服务器状态
		setStatus();
		// 上传核心
		upload = new Upload();
		upload.getFinish().addListener((obs, oldValue, newValue) -> getFileList(view.getPath().getText()));
		upload.exceptionProperty().addListener((obs, oldValue, newValue) -> newValue.printStackTrace());
		upload.start();
		// 下载核心
		download = new Download();
		download.exceptionProperty().addListener((obs, oldValue, newValue) -> newValue.printStackTrace());
		download.start();
		// 读取已完成传输列表
		ioHistories = ioHistoryLoader.get();
		// 传输速度监听
		netSpeed();
		
		
		// 预览
		view.getOpen().setOnAction(event -> open());
		view.getmOpen().setOnAction(event -> open());
		// 属性
		view.getPropertie().setOnAction(event -> properties());
		view.getmProperties().setOnAction(event -> properties());
		// 同步
		view.getSync().setOnAction(event -> YeyuUtils.gui().tips(tips, rbx.def("currentlyUnavailable"), 2000, TipsX.ERROR));
		// 上传文件
		view.getUpload().setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(config.get("defaultUploadFolder").toString()));
			fileChooser.setTitle(rbx.def("fileSelectTypeFile"));
			fileChooser.getExtensionFilters().add(new ExtensionFilter(rbx.def("fileSelectFilterAll"), "*.*"));
			List<File> list = fileChooser.showOpenMultipleDialog(null);
			if (list != null && 0 < list.size()) {
				upload.add(list, view.getPath().getText());
				YeyuUtils.gui().tips(tips, rbx.r("add") + list.size() + rbx.l("addToUpload"), 3000, TipsX.WARNING);
				config.put("defaultUploadFolder", list.get(0).getParent());
				if (Boolean.valueOf(config.get("openIOList").toString())) openIOList();
			}
		});
		// 下载文件
		view.getDownload().setOnAction(event -> download());
		view.getmDownload().setOnAction(event -> download());
		// 压缩
		view.getZip().setOnAction(event -> zip());
		view.getmZip().setOnAction(event -> zip());
		// 解压
		view.getUnZip().setOnAction(event -> unZip());
		view.getmUnZip().setOnAction(event -> unZip());
		// 新建文件夹
		view.getNewFolder().setOnAction(event -> newObject("newFolder"));
		view.getmNewFolder().setOnAction(event -> newObject("newFolder"));
		// 新建文本文档
		view.getmNewText().setOnAction(event -> newObject("newText"));
		// 重命名
		view.getRename().setOnAction(event -> rename());
		view.getmRename().setOnAction(event -> rename());
		// 移动
		view.getMove().setOnAction(event -> moveTo());
		view.getmMove().setOnAction(event -> moveTo());
		// 复制
		view.getCopy().setOnAction(event -> copyTo());
		view.getmCopy().setOnAction(event -> copyTo());
		// 删除
		view.getDelete().setOnAction(event -> delete());
		view.getmDelete().setOnAction(event -> delete());
		// 传输列表
		view.getIoList().setOnAction(event -> openIOList());
		// 设置
		view.getSetting().setOnAction(event -> {
			Setting setting = new Setting();
			setting.isUpdateRestart().addListener((obs, oldValue, newValue) -> {
				if (!isReload) {
					Confirm confirm = new Confirm(rbx.def("settingRestartConfirm"), rbx.def("restart"));
					confirm.initConfirm(confirm, new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							restart(); // 重新启动
						}
					});
					isReload = true;
				}
			});
		});
		// 选择文件对象
		fileList.getSelectionModel().selectedItemProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null) view.getTipsPane().setSelected(fileList.getSelectionModel().getSelectedItems().size());
		});
		// 双击文件列表
		fileList.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) open();
		});
		// 拖拽 - 到列表
		fileList.setOnDragOver(event -> {
			Dragboard dragboard = event.getDragboard(); 
			if (dragboard.hasFiles()) event.acceptTransferModes(TransferMode.COPY);
		});
		// 拖拽 - 释放
		fileList.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			if (dragboard.hasFiles()) {
				List<File> files = dragboard.getFiles();
				if (0 < files.size()) { // 上传
					upload.add(files, view.getPath().getText());
					int folders = 0;
					for (int i = 0; i < files.size(); i++) {
						folders = files.get(i).isDirectory() ? folders + 1 : folders;
					}
					if (folders != 0) {
						YeyuUtils.gui().tips(tips, rbx.r("cannotUploadFolder") + rbx.r("add") + (files.size() - folders) + rbx.l("addToUpload"), 5000, TipsX.ERROR);
					} else {
						YeyuUtils.gui().tips(tips, rbx.r("add") + files.size() + rbx.l("addToUpload"), 3000, TipsX.WARNING);
					}
					config.put("defaultUploadFolder", files.get(0).getParent());
					if (Boolean.valueOf(config.get("openIOList").toString())) openIOList();
					event.acceptTransferModes(TransferMode.COPY);
				}
			}
		});
		// 返回
		view.getPrev().setOnAction(event -> {
			if (prevStack.size() != 1) {
				nextStack.push(view.getPath().getText());
				getFileList(prevStack.pop());
			}
		});
		// 前进
		view.getNext().setOnAction(event -> {
			if (nextStack.size() != 0) {
				prevStack.push(view.getPath().getText());
				getFileList(nextStack.pop());
			}
		});
		// 父级
		view.getParent().setOnAction(event -> {
			String path = view.getPath().getText();
			if (path.lastIndexOf("\\", path.length() - 2) != -1) {
				prevStack.push(path);
				path = path.substring(0, path.lastIndexOf("\\", path.length() - 2) + 1);
				getFileList(path);
			}
		});
		// 刷新
		view.getRefresh().setOnAction(event -> getFileList(view.getPath().getText()));
		view.getmRefresh().setOnAction(event -> getFileList(view.getPath().getText()));
		// 根目录
		view.getRoot().setOnAction(event -> {
			view.getPath().setText("\\");
			getFileList(view.getPath().getText());
		});
		// 创建外链到公开文件夹
		view.getToPublic().setOnAction(event -> {
			if (isNullToServerConfig(event, "publicFile")) return;
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				Confirm confirm = new Confirm(rbx.def("tips"), rbx.def("copyToPublic"), false);
				confirm.initConfirm(confirm, new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						List<String> fileList = new ArrayList<>();
						String fileName;
						for (int i = 0; i < list.size(); i++) {
							fileName = list.get(i).getName();
							fileList.add(view.getPath().getText() + fileName.substring(fileName.indexOf(".") + 1));
						}
						(new FileTPService(server.get("publicFile"), fileList, "copy")).start();
						confirm.close();
					}
				});
			} else {
				YeyuUtils.gui().tips(tips, rbx.def("selectItems"));
			}
		});
		// 路径监听
		view.getPath().textProperty().addListener((obs, oldValue, newValue) -> {
			if (isNullToServerConfig("publicFile")) return;
			if (newValue.indexOf(server.get("publicFile")) != -1) { // 公开外链，限制权限
				view.getOpen().setDisable(true);
				view.getFileCtrlBtns().setEnable(false, view.getFileCtrlBtns().length() - 1);
			} else {
				view.getOpen().setDisable(false);
				view.getFileCtrlBtns().setEnable(true);
			}
		});
		// 路径文本域回车
		view.getPath().setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				getFileList(view.getPath().getText());
			}
		});
		// 搜索按钮
		view.getSearch().setOnAction(event -> search());
		// 进入选定文件夹
		view.getFileList().setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ENTER) open();
		});
		// 搜索回车
		view.getSearchField().setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ENTER) search();
		});
		// 搜索失去焦点
		view.getSearchField().focusedProperty().addListener(event -> {
			if (!view.getSearchField().isFocused()) searchIndex = 0;
		});
		// 照片管理器
		view.getPhoto().setOnAction(event -> {
			if (isNullToServerConfig(event, "photo")) return;
			new PhotoManager(upload, download, server.get("photo"), Boolean.valueOf(server.get("compressImg")));
		});
		// 外部链接
		view.getPublicFile().setOnAction(event -> {
			prevStack.push(view.getPath().getText());
			if (server != null && server.get("publicFile") != null) {
				view.getPath().setText(server.get("publicFile") + File.separator);
				getFileList(view.getPath().getText());
			} else {
				YeyuUtils.gui().tips(tips, rbx.def("unknownPublic"), 4000, TipsX.ERROR);
			}
		});
		// 调整文件名列的宽度
		view.widthProperty().addListener((obs, oldValue, newValue) -> {
			fileList.getColName().setPrefWidth(fileList.getColName().getPrefWidth() + (newValue.doubleValue() - oldValue.doubleValue()));
		});
		// 文件列表右键菜单
		fileList.setOnContextMenuRequested(event -> {
			if (isNullToServerConfig(event, "publicFile")) return;
			if (view.getPath().getText().indexOf(server.get("publicFile")) == -1) { // 非公开外链
				ObservableList<MenuItem> items = view.getMenu().getItems();
				for (int i = 0; i < items.size(); i++) items.get(i).setDisable(false);
				ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
				if (selected.size() != 1) {
					view.getmOpen().setDisable(true);
					view.getmUnZip().setDisable(true);
					view.getmRename().setDisable(true);
					view.getmProperties().setDisable(true);
				} else {
					String name = selected.get(0).getName();
					if (name.startsWith("folder")) {
						view.getmOpen().setText(rbx.def("open"));
					} else {
						view.getmOpen().setText(rbx.def("preview"));
					}
					view.getmOpen().setDisable(false);
					view.getmUnZip().setDisable(false);
					if (!name.startsWith("zip")) view.getmUnZip().setDisable(true);
					view.getmRename().setDisable(false);
					view.getmProperties().setDisable(false);
				}
			} else {
				ObservableList<MenuItem> items = view.getMenu().getItems();
				for (int i = 0; i < items.size() - 3; i++) items.get(i).setDisable(true);
			}
		});
		// 快捷键
		view.getScene().setOnKeyPressed(event -> {
			// 退格返回
	        if (event.getCode() == KeyCode.BACK_SPACE) {
				if (prevStack.size() != 1) {
					nextStack.push(view.getPath().getText());
					getFileList(prevStack.pop());
				}
				return;
	        }
	        // F5 刷新
	        if (event.getCode() == KeyCode.F5) {
	        	getFileList(view.getPath().getText());
	        	return;
	        }
		});
		// 关闭事件
		Platform.setImplicitExit(Boolean.valueOf(config.get("exitOnClose").toString()));
		view.setOnCloseRequest(event -> {
			if (Boolean.valueOf(config.get("exitOnClose").toString())) {
				if (Download.getListProperty().size() != 0 || Upload.getListProperty().size() != 0) {
					Confirm confirm = new Confirm(rbx.def("warn"), rbx.def("exitWhenIOListRunning"), rbx.def("confirm"), "ewarn");
					confirm.initConfirm(confirm, new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							Platform.exit();
						}
					});
					event.consume();
				}
			}
		});
		// 关闭启动页
		if (SplashScreen.getSplashScreen() != null) SplashScreen.getSplashScreen().close();
	}
	
	/**
	 * 关闭程序
	 * 
	 */
	public void stop() throws Exception {
		stage.close();
		// 保存设置
		if (view.getWidth() != view.getMinWidth()) {
			config.put("width", view.getWidth());
		} else {
			config.put("width", "default");
		}
		if (view.getHeight() != view.getMinHeight()) {
			config.put("height", view.getHeight());
		} else {
			config.put("height", "default");
		}
		Entrance.setConfig(config);
		new Configer("iNetdisk").set(Entrance.getConfig());
		ioHistoryLoader.set();
		// 关闭后台线程
		Upload.getListProperty().clear();
		Download.getListProperty().clear();
		tray.removeIcon();
		speed.shutdown();
		upload.shutdown();
		download.shutdown();
		state.shutdown();
		
		super.stop();
	}
	
	/**
	 * 重启程序
	 * 
	 */
	private void restart() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
					Runtime.getRuntime().exec("java -jar " + System.getProperty("java.class.path"));
				} catch (IOException e) {
					e.printStackTrace();
				}
            }    
        });
		try {
			stop();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查服务器配置
	 * 
	 */
	private boolean isNullToServerConfig(String k) {
		return server == null || server.get(k) == null;
	}
	private boolean isNullToServerConfig(Event event, String k) {
		event.consume();
		return server == null || server.get(k) == null;
	}
	
	/**
	 * 属性
	 * 
	 */
	private void properties() {
		FileCell fileCell = fileList.getSelectionModel().getSelectedItem();
		if (fileCell != null) {
			new Properties(view.getPath().getText(), fileCell);
		}
	}
	
	/**
	 * 下载文件
	 * 
	 */
	private void download() {
		ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
		if (0 < selected.size()) {
			for (int i = 0; i < selected.size(); i++) {
				if (selected.get(i).getName().startsWith("folder")) {
					YeyuUtils.gui().tips(tips, rbx.def("selectFolderWhenDownload"), 3000, TipsX.ERROR);
					return;
				}
			}
			download.add(selected, view.getPath().getText(), "");
			YeyuUtils.gui().tips(tips, rbx.r("add") + selected.size() + rbx.l("addToDownload"), 3000, TipsX.WARNING);
			if (Boolean.valueOf(config.get("openIOList").toString())) openIOList();
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectOnly"));
		}
	}
	
	/**
	 * 压缩
	 * 
	 */
	private void zip() {
		ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
		if (0 < selected.size()) {
			Zip zip = new Zip(view.getPath().getText(), selected);
			zip.isFinish().addListener((obs, oldValue, newValue) -> {
				if (newValue) getFileList(view.getPath().getText());
			});
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectOnly"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 解压
	 * 
	 */
	private void unZip() {
		ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
		if (0 < list.size()) {
			if (list.size() == 1) {
				String fileName = list.get(0).getName();
				if (fileName.startsWith("zip")) {
					fileName = fileName.substring(fileName.indexOf(".") + 1);
					UnZip selector = new UnZip(rbx.def("selectUnZipFolder"), view.getPath().getText() + fileName, "unzip");
					selector.isFinish().addListener((obs, oldValue, newValue) -> {
						getFileList(view.getPath().getText());
					});
				} else {
					YeyuUtils.gui().tips(tips, rbx.def("onlyZipFormat"), 3000, TipsX.ERROR);
				}
			} else {
				YeyuUtils.gui().tips(tips, rbx.def("selectOnly"), 3000, TipsX.WARNING);
			}
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectItems"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 新建对象
	 * 
	 */
	private void newObject(String flag) {
		PublicRequest request = new PublicRequest(flag, view.getPath().getText());
		request.valueProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) getFileList(view.getPath().getText());
		});
		request.start();
	}
	
	/**
	 * 重命名
	 * 
	 */
	private void rename() {
		ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
		if (0 < list.size()) {
			if (list.size() == 1) {
				Rename rename = new Rename(view.getPath().getText(), list.get(0).getName(), fileList.getItems());
				rename.isFinish().addListener((tmp, oldValue, newValue) -> {
					if (newValue != null && newValue) getFileList(view.getPath().getText());
				});
			} else {
				YeyuUtils.gui().tips(tips, rbx.def("selectOnly"), 3000, TipsX.WARNING);
			}
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectItems"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 移动到
	 * 
	 */
	private void moveTo() {
		ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
		if (0 < list.size()) {
			List<String> fileList = new ArrayList<>();
			String fileName;
			for (int i = 0; i < list.size(); i++) {
				fileName = list.get(i).getName();
				fileList.add(view.getPath().getText() + fileName.substring(fileName.indexOf(".") + 1));
			}
			FolderSelector selector = new FolderSelector(rbx.def("mainFileMoveTo"), fileList, "move");
			selector.isFinish().addListener((obs, oldValue, newValue) -> {
				if (newValue) getFileList(view.getPath().getText());
			});
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectItems"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 复制到
	 * 
	 */
	private void copyTo() {
		ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
		if (0 < list.size()) {
			List<String> fileList = new ArrayList<>();
			String fileName;
			for (int i = 0; i < list.size(); i++) {
				fileName = list.get(i).getName();
				fileList.add(view.getPath().getText() + fileName.substring(fileName.indexOf(".") + 1));
			}
			new FolderSelector(rbx.def("mainFileCopyTo"), fileList, "copy");
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectItems"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 删除
	 * 
	 */
	private void delete() {
		ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
		if (list != null && 0 < list.size()) {
			List<String> fileList = new ArrayList<String>();
			String name;
			for (int i = 0; i < list.size(); i++) {
				name = list.get(i).getName();
				fileList.add(view.getPath().getText() + name.substring(name.indexOf(".") + 1, name.length()));
			}
			DeleteFile deleteFile = new DeleteFile(fileList);
			deleteFile.isFinish().addListener((tmp, oldValue, newValue) -> {
				if (newValue) getFileList(view.getPath().getText());
			});
		} else {
			YeyuUtils.gui().tips(tips, rbx.def("selectItems"), 3000, TipsX.WARNING);
		}
	}
	
	/**
	 * 打开选中项目
	 * 
	 */
	private void open() {
		FileCell selected = fileList.getSelectionModel().getSelectedItem();
		if (selected != null) {
			String selectedItem = selected.getName();
			String format = selectedItem.substring(0, selectedItem.indexOf("."));
			final String fileName = selectedItem.substring(selectedItem.indexOf(".") + 1);
			if (format.equals("folder")) { // 文件夹
				String path = view.getPath().getText() + fileName + File.separator;
				prevStack.push(view.getPath().getText());
				getFileList(path);
				return;
			}
			if (view.getPath().getText().indexOf(server.get("publicFile")) == -1) { // 非公开外链
				if (FileFormat.isTextFile(format)) { // 文本预览
					TextEditor editor = new TextEditor(view.getPath().getText() + fileName);
					editor.getIsSave().addListener(event -> getFileList(view.getPath().getText()));
					return;
				}
				if (FileFormat.isImg(format)) { // 图片预览
					new Img(view.getPath().getText() + fileName);
					return;
				}
				if (FileFormat.isMP4(format)) { // 视频播放
					PublicRequest request = new PublicRequest("getMP4Info", view.getPath().getText() + fileName);
					YeyuUtils.gui().tips(tips, "正在获取视频信息...", 3000, TipsX.WARNING);
					request.messageProperty().addListener((tmp, o, n) -> {
						JsonObject jo = (new JsonParser()).parse(n).getAsJsonObject();
						VideoInfo video = new VideoInfo();
						video.setName(fileName);
						video.setUrl(view.getPath().getText() + fileName, config.get("ip").toString(), config.get("portHTTP").toString());
						video.setWidth(jo.get("width").getAsDouble());
						video.setHeight(jo.get("height").getAsDouble());
						video.setDeg(jo.get("deg").getAsInt());
						new Video(video, video.filter(fileList.getItems()), view.getPath().getText());
					});
					request.start();
					return;
				}
				YeyuUtils.gui().tips(tips, rbx.r("notSupportOpen") + format + rbx.l("file"), 3000, TipsX.WARNING);
			}
		}
	}
	
	/**
	 * 传输列表
	 * 
	 */
	private void openIOList() {
		IO io = new IO(upload, download);
		io.getShow().addListener((obs, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				prevStack.push(view.getPath().getText());
				getFileList(newValue);
				io.close();
			}
		});
	}
	
	/**
	 * 搜索当前文件列表
	 * 
	 */
	private void search() {
		String searchKey = view.getSearchField().getText();
		if (!searchKey.equals("")) {
	    	ObservableList<FileCell> list = fileList.getItems();
	    	searchKey = searchKey.toLowerCase();
	    	searchIndex = searchIndex == list.size() ? 0 : searchIndex;
	    	for (int i = searchIndex; i < list.size(); i++) {
				if (list.get(i).getName().toLowerCase().indexOf(searchKey) != -1) {
					searchIndex = i + 1;
					fileList.getSelectionModel().clearAndSelect(i);
					fileList.scrollTo(fileList.getSelectionModel().getSelectedItem());
					return;
				} else {
					searchIndex = 0;
				}
			}
		}
	}
	
	/**
	 * 获取服务器状态
	 * 
	 */
	private String diskUse, diskTotal;
	private void setStatus() {
		if (state != null) state.shutdown();
		state = new StateRequest();
		JsonParser jp = new JsonParser();
		DecimalFormat formatUse = new DecimalFormat("####");
		DecimalFormat formatTotal = new DecimalFormat("####.##");
		state.valueProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null && !newValue.equals("")) {
				JsonObject jo = (JsonObject) jp.parse(newValue);
				view.getCpu().setValue(jo.get("cpuUse").getAsDouble());
				view.getMemory().setValue(jo.get("memUse").getAsDouble() / jo.get("memMax").getAsDouble());
				diskUse = YeyuUtils.tools().storageFormat(jo.get("diskUse").getAsDouble(), formatUse);
				diskTotal = YeyuUtils.tools().storageFormat(jo.get("diskMax").getAsDouble(), formatTotal);
				view.getDisk().setValue(diskUse + " / " + diskTotal);
			}
		});
		state.exceptionProperty().addListener((l, o, n) -> {
			new Alert(rbx.def("error"), rbx.def("connectFaile"), Alert.ERROR);
			n.printStackTrace();
		});
		state.start();
	}
	
	/**
	 * 获取服务器配置
	 * 
	 */
	private void getServerConfig() {
		PublicRequest request = new PublicRequest("getConfig", "");
		request.messageProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonObject jo = (new JsonParser()).parse(newValue).getAsJsonObject();
				server = new HashMap<>();
				server.put("compressImg", jo.get("compressImg").getAsString());
				server.put("photo", jo.get("photo").getAsString());
				setNavList(jo.get("navList").getAsString());
				if (jo.get("publicFile") != null) server.put("publicFile", jo.get("publicFile").getAsString());
			}
		});
		request.start();
		isReload = false;
	}
	
	/**
	 * 设置侧边导航，导航内容从服务端获取
	 * 
	 */
	private void setNavList(String navList) {
		Button btn;
		JsonArray ja = (new JsonParser()).parse(navList).getAsJsonArray();
		JsonObject jo;
		for (int i = 0; i < ja.size(); i++) {
			jo = ja.get(i).getAsJsonObject();
			for (Map.Entry<String, JsonElement> item : jo.entrySet()) {
				btn = new NavButton(item.getKey());
				btn.setOnAction(event -> {
					getFileList(File.separator + item.getValue().getAsString() + File.separator);
					prevStack.push(view.getPath().getText());
				});
				view.getNavBtns().getChildren().add(i + 1, btn);
			}
		}
	}
	
	/**
	 * 网络传输速度
	 * 
	 */
	private void netSpeed() {
		speed = new NetSpeed(upload, download, view.getNetSpeed());
		view.getTipsPane().getSpeed().textProperty().bind(speed.messageProperty());
		speed.start();
	}
	
	/**
	 * 获取文件列表
	 * 
	 * @param path 路径
	 */
	private void getFileList(String path) {
		PublicRequest request = new PublicRequest("list", path);
		request.setOnRunning(event -> {
			view.getTipsPane().getTips().setText(rbx.def("gettingFileList"));
		});
		request.messageProperty().addListener((tmp, oldValue, newValue) -> {
			if (!newValue.equals("") && !newValue.equals("null")) {
				SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				fileList.getItems().clear();
				fileList.refresh();
				JsonParser jp = new JsonParser();
				JsonElement jo = (JsonElement) jp.parse(newValue);
				JsonArray ja = jo.getAsJsonArray();
				JsonObject data;
				DecimalFormat format = new DecimalFormat("#,###");
				long fileSize = -1;
				for (int i = 0, l = ja.size(); i < l; i++) {
					data = ja.get(i).getAsJsonObject();
					fileSize = data.get("size").getAsLong();
					fileList.getItems().add(new FileCell(
						data.get("name").getAsString(),
						dataFormat.format(new Date(data.get("date").getAsLong())),
						YeyuUtils.tools().storageFormat(fileSize, format),
						fileSize
					));
				}
				view.getPath().setText(path);
				view.getTipsPane().setItems(ja.size());
				view.getTipsPane().setSelected(0);
				view.getTipsPane().getTips().setText("");
			}
		});
		request.setOnSucceeded(event -> view.getTipsPane().getTips().setText(""));
		request.setOnFailed(event -> view.getTipsPane().getTips().setText(""));
		request.start();
	}
	
	public ViewMain getView() {
		return view;
	}
	
	public static List<IOHistory> getIoHistories() {
		return ioHistories;
	}

	public static void setIoHistories(List<IOHistory> ioHistories) {
		Main.ioHistories = ioHistories;
	}
}