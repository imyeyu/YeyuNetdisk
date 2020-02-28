package net.imyeyu.netdisk.ctrl;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.NetSpeed;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.dialog.Confirm;
import net.imyeyu.netdisk.dialog.DeleteFile;
import net.imyeyu.netdisk.dialog.FolderSelector;
import net.imyeyu.netdisk.dialog.Rename;
import net.imyeyu.netdisk.dialog.UnZip;
import net.imyeyu.netdisk.dialog.Zip;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.request.StateRequest;
import net.imyeyu.netdisk.ui.FileListTable;
import net.imyeyu.netdisk.util.FileFormat;
import net.imyeyu.netdisk.util.FileTPService;
import net.imyeyu.netdisk.util.IOHistoryLoader;
import net.imyeyu.netdisk.view.ViewMain;
import net.imyeyu.netdisk.view.ViewProperties;
import net.imyeyu.utils.Configer;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.TipsX;

public class Main extends Application {
	
	private ResourceBundle rb = Entrance.getRb();
	
	private Upload upload;
	private Download download;
	private IOHistoryLoader ioHistoryLoader = new IOHistoryLoader();
	private static List<IOHistory> ioHistories;
	private StateRequest state;
	private NetSpeed speed;
	
	private int searchIndex = 0;
	private Stage stage;
	private Label tips;
	private ViewMain view;
	private FileListTable fileList;
	private Stack<String> prevStack, nextStack;
	private Map<String, Object> config = Entrance.getConfig();
	private Map<String, String> server;
	
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		view = new ViewMain();
		tips = view.getTipsPane().getTips();
		fileList = view.getFileList();
		
		prevStack = new Stack<String>(); // 返回栈
		nextStack = new Stack<String>(); // 前进栈
		prevStack.push(view.getPath().getText());
		
		getFileList(prevStack.peek());
		
		// 获取服务器配置
		getServerConfig();
		
		// 服务器状态
		setStatus();
		// 上传核心
		upload = new Upload();
		upload.getFinish().addListener((obs, oldValue, newValue) ->  getFileList(view.getPath().getText()));
		upload.start();
		// 下载核心
		download = new Download();
		download.start();
		// 传输速度监听
		netSpeed();
		
		// 预览
		view.getOpen().setOnAction(event -> enter());
		// 属性
		view.getPropertie().setOnAction(event -> {
			FileCell fileCell = fileList.getSelectionModel().getSelectedItem();
			if (fileCell != null) {
				new ViewProperties(view.getPath().getText(), fileCell);
			}
		});
		// 同步
		view.getSync().setOnAction(event -> YeyuUtils.gui().tips(tips, "当前不可用", 2000, TipsX.ERROR));
		// 上传文件
		view.getUpload().setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(config.get("defaultUploadFolder").toString()));
			fileChooser.setTitle(rb.getString("fileSelectTypeFile"));
			fileChooser.getExtensionFilters().add(new ExtensionFilter(rb.getString("fileSelectFilterAll"), "*.*"));
			List<File> list = fileChooser.showOpenMultipleDialog(null);
			if (list != null && 0 < list.size()) {
				upload.add(list, view.getPath().getText());
				config.put("defaultUploadFolder", list.get(0).getParent());
			}
		});
		
		// 下载文件
		view.getDownload().setOnAction(event -> {
			ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
			download.add(selected, view.getPath().getText(), "");
		});
		
		// 压缩
		view.getZip().setOnAction(event -> {
			ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
			Zip zip = new Zip(view.getPath().getText(), selected);
			zip.isFinish().addListener((obs, oldValue, newValue) -> {
				if (newValue) getFileList(view.getPath().getText());
			});
		});
		
		// 解压
		view.getUnZip().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				if (list.size() == 1) {
					String fileName = list.get(0).getName();
					fileName = fileName.substring(fileName.indexOf(".") + 1);
					UnZip selector = new UnZip("选择解压文件夹", view.getPath().getText() + fileName, "unzip");
					selector.isFinish().addListener((obs, oldValue, newValue) -> {
						getFileList(view.getPath().getText());
					});
				} else {
					YeyuUtils.gui().tips(tips, "只能选择一个对象");
				}
			} else {
				YeyuUtils.gui().tips(tips, "请选择一个对象");
			}
		});
		
		// 新建文件夹
		view.getNewFolder().setOnAction(event -> {
			PublicRequest request = new PublicRequest("newFolder", view.getPath().getText());
			request.valueProperty().addListener((tmp, oldValue, newValue) -> {
				if (newValue != null && newValue.equals("finish")) getFileList(view.getPath().getText());
			});
			request.start();
		});
		
		// 重命名
		view.getRename().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				if (list.size() == 1) {
					Rename rename = new Rename(view.getPath().getText(), list.get(0).getName(), fileList.getItems());
					rename.isFinish().addListener((tmp, oldValue, newValue) -> {
						if (newValue != null && newValue) getFileList(view.getPath().getText());
					});
				} else {
					YeyuUtils.gui().tips(tips, "只能选择一个对象");
				}
			} else {
				YeyuUtils.gui().tips(tips, "请选择一个对象");
			}
		});
		
		// 移动
		view.getMove().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				List<String> fileList = new ArrayList<>();
				String fileName;
				for (int i = 0; i < list.size(); i++) {
					fileName = list.get(i).getName();
					fileList.add(view.getPath().getText() + fileName.substring(fileName.indexOf(".") + 1));
				}
				FolderSelector selector = new FolderSelector("移动到", fileList, "move");
				selector.isFinish().addListener((obs, oldValue, newValue) -> {
					if (newValue) getFileList(view.getPath().getText());
				});
			} else {
				YeyuUtils.gui().tips(tips, "请选择对象");
			}
		});
		
		// 复制
		view.getCopy().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				List<String> fileList = new ArrayList<>();
				String fileName;
				for (int i = 0; i < list.size(); i++) {
					fileName = list.get(i).getName();
					fileList.add(view.getPath().getText() + fileName.substring(fileName.indexOf(".") + 1));
				}
				new FolderSelector("复制到", fileList, "copy");
			} else {
				YeyuUtils.gui().tips(tips, "请选择对象");
			}
		});
		
		// 删除
		view.getDelete().setOnAction(event -> {
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
				YeyuUtils.gui().tips(tips, "请选择对象");
			}
		});
		
		// 传输列表
		ioHistories = ioHistoryLoader.get();
		view.getIoList().setOnAction(event -> {
			IO io = new IO(upload, download);
			io.getShow().addListener((obs, oldValue, newValue) -> {
				if (!newValue.equals("")) {
					prevStack.push(view.getPath().getText());
					getFileList(newValue);
					io.close();
				}
			});
		});
		
		// 设置
		view.getSetting().setOnAction(event -> new Setting());
		
		// 选择文件对象
		fileList.getSelectionModel().selectedItemProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null) view.getTipsPane().setSelected(fileList.getSelectionModel().getSelectedItems().size());
		});
		// 双击文件列表
		fileList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) enter();
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
		// 根目录
		view.getRoot().setOnAction(event -> {
			view.getPath().setText("\\");
			getFileList(view.getPath().getText());
		});
		// 创建外链到公开文件夹
		view.getToPublic().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				Confirm confirm = new Confirm("提示", "确定复制文件到外链空间吗？", false);
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
				YeyuUtils.gui().tips(tips, "请选择对象");
			}
		});
		// 路径监听
		view.getPath().textProperty().addListener((obs, oldValue, newValue) -> {
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
	        if (event.getCode() == KeyCode.ENTER) enter();
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
			new PhotoManager(upload, download, server.get("photo"), Boolean.valueOf(server.get("compressImg")));
		});
		// 文档
		view.getDocument().setOnAction(event -> {
			prevStack.push(view.getPath().getText());
			view.getPath().setText(server.get("document") + File.separator);
			getFileList(view.getPath().getText());
		});
		// 其他备份
		view.getBackup().setOnAction(event -> {
			prevStack.push(view.getPath().getText());
			view.getPath().setText(server.get("otherBackup") + File.separator);
			getFileList(view.getPath().getText());
		});
		// 外部链接
		view.getPublicFile().setOnAction(event -> {
			prevStack.push(view.getPath().getText());
			if (server.get("publicFile") != null) {
				view.getPath().setText(server.get("publicFile") + File.separator);
				getFileList(view.getPath().getText());
			} else {
				YeyuUtils.gui().tips(tips, "服务器未设置公开外链路径", 4000, TipsX.ERROR);
			}
		});
		
		/**
		 * 注册热键
		 * 
		 */
		// 文件列表返回
		view.getScene().setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.BACK_SPACE) {
				if (prevStack.size() != 1) {
					nextStack.push(view.getPath().getText());
					getFileList(prevStack.pop());
				}
	        }
		});
	}
	
	// 关闭程序
	public void stop() throws Exception {
		new Configer("Netdisk").set(Entrance.getConfig());
		ioHistoryLoader.set();
		// 关闭后台线程
		speed.shutdown();
		upload.shutdown();
		download.shutdown();
		state.shutdown();
		
		stage.close();
		super.stop();
	}
	
	/**
	 * 打开选中项目
	 * 
	 */
	private void enter() {
		FileCell selected = fileList.getSelectionModel().getSelectedItem();
		if (selected != null) {
			String fileName = selected.getName();
			String format = fileName.substring(0, fileName.indexOf("."));
			fileName = fileName.substring(fileName.indexOf(".") + 1);
			if (format.equals("folder")) {
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
			}
		}
	}
	
	/**
	 * 搜索当前文件列表
	 * 
	 */
	private void search() {
    	ObservableList<FileCell> list = fileList.getItems();
    	String searchKey = view.getSearchField().getText().toLowerCase();
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
	
	/**
	 * 获取服务器状态
	 * 
	 */
	private void setStatus() {
		state = new StateRequest();
		JsonParser jp = new JsonParser();
		state.valueProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null && !newValue.equals("")) {
				JsonObject jo = (JsonObject) jp.parse(newValue);
				view.getCpu().getPB().setProgress(jo.get("cpuUse").getAsDouble());
				view.getMemory().getPB().setProgress(jo.get("memUse").getAsDouble() / jo.get("memMax").getAsDouble());
				view.getDisk().getPB().setProgress(jo.get("diskUse").getAsDouble() / jo.get("diskMax").getAsDouble());
			}
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
				JsonParser jp = new JsonParser();
				JsonObject jo = (JsonObject) jp.parse(newValue);
				server = new HashMap<>();
				server.put("compressImg", jo.get("compressImg").getAsString());
				server.put("photo", jo.get("photo").getAsString());
				server.put("document", jo.get("document").getAsString());
				server.put("otherBackup", jo.get("otherBackup").getAsString());
				if (jo.get("publicFile") != null) {
					server.put("publicFile", jo.get("publicFile").getAsString());
				}
			}
		});
		request.start();
	}
	
	/**
	 * 网络传输速度
	 * 
	 */
	private void netSpeed() {
		speed = new NetSpeed(upload, download);
		view.getTipsPane().getSpeed().textProperty().bind(speed.messageProperty());
		speed.start();
	}
	
	/**
	 * 获取文件列表
	 * 
	 * @param path 路径
	 */
	private void getFileList(String path) {
		view.getTipsPane().getTips().setText("正在获取文件列表...");
		PublicRequest request = new PublicRequest("list", path);
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

	public static List<IOHistory> getIoHistories() {
		return ioHistories;
	}

	public static void setIoHistories(List<IOHistory> ioHistories) {
		Main.ioHistories = ioHistories;
	}
}