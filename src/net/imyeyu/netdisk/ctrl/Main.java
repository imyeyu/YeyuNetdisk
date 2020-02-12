package net.imyeyu.netdisk.ctrl;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.dialog.DeleteFile;
import net.imyeyu.netdisk.dialog.FolderSelector;
import net.imyeyu.netdisk.dialog.Rename;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.request.StateRequest;
import net.imyeyu.netdisk.ui.FileList;
import net.imyeyu.netdisk.util.IOHistoryLoader;
import net.imyeyu.netdisk.view.ViewMain;
import net.imyeyu.netdisk.view.ViewProperties;
import net.imyeyu.utils.YeyuUtils;

public class Main extends Application {
	
	private ResourceBundle rb;
	
	private Upload upload;
	private Download download;
	private IOHistoryLoader ioHistoryLoader = new IOHistoryLoader();
	private static List<IOHistory> ioHistories;
	private StateRequest state;
	
	private Stage stage;
	private ViewMain view;
	private FileList fileList;
	private Stack<String> prevStack, nextStack;

	public void start(Stage stage) throws Exception {
		this.stage = stage;
		this.rb = Entrance.getRb();
		
		view = new ViewMain();
		fileList = view.getFileList();
		
		prevStack = new Stack<String>(); // 返回栈
		nextStack = new Stack<String>(); // 前进栈
		prevStack.push(view.getPath().getText());
		
		getFileList(prevStack.peek());
		
		// 服务器状态
		setStatus();
		// 上传核心
		upload = new Upload();
		upload.start();
		// 下载核心
		download = new Download();
		download.start();
		
		// 属性
		view.getPropertie().setOnAction(event -> {
			FileCell fileCell = fileList.getSelectionModel().getSelectedItem();
			if (fileCell != null) {
				new ViewProperties(view.getPath().getText(), fileCell);
			}
		});
		
		// 上传文件
		view.getUpload().setOnAction(event -> {
			ExtensionFilter all = new ExtensionFilter(rb.getString("fileSelectFilterAll"), "*.*");
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(rb.getString("fileSelectTypeFile"));
			fileChooser.getExtensionFilters().add(all);
			List<File> list = fileChooser.showOpenMultipleDialog(null);
			if (list != null && 0 < list.size()) {
				upload.add(list, view.getPath().getText());
			}
		});
		
		// 下载文件
		view.getDownload().setOnAction(event -> {
			ObservableList<FileCell> selected = fileList.getSelectionModel().getSelectedItems();
			download.add(selected, view.getPath().getText());
		});
		
		// 新建文件夹
		view.getNewFolder().setOnAction(event -> {
			PublicRequest request = new PublicRequest("newFolder", view.getPath().getText());
			request.valueProperty().addListener((tmp, oldValue, newValue) -> {
				if (newValue != null && newValue.equals("true")) getFileList(view.getPath().getText());
			});
			request.start();
		});
		
		// 重命名
		view.getRename().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (0 < list.size()) {
				if (list.size() == 1) {
					Rename rename = new Rename(view.getPath().getText(), list.get(0).getName(), list);
					rename.isFinish().addListener((tmp, oldValue, newValue) -> {
						if (newValue != null && newValue) getFileList(view.getPath().getText());
					});
				} else {
					view.getTipsPane().getTips().setText("只能选择一个对象");
				}
			} else {
				view.getTipsPane().getTips().setText("请选择一个对象");
			}
		});
		
		// 移动
		view.getMove().setOnAction(event -> {
			new FolderSelector();
		});
		
		// 复制
		view.getCopy().setOnAction(event -> {
			new FolderSelector();
		});
		
		// 删除
		view.getDelete().setOnAction(event -> {
			ObservableList<FileCell> list = fileList.getSelectionModel().getSelectedItems();
			if (list != null && 0 < list.size()) {
				DeleteFile deleteFile = new DeleteFile(rb, view.getPath().getText(), list);
				deleteFile.isFinish().addListener((tmp, oldValue, newValue) -> {
					if (newValue) getFileList(view.getPath().getText());
				});
			} else {
				view.getTipsPane().getTips().setText("请选择对象");
			}
		});
		
		// 传输列表
		ioHistories = ioHistoryLoader.get();
		view.getIoList().setOnAction(event -> new IO(upload, download));
		
		// 设置
		view.getSetting().setOnAction(event -> new Setting(rb));
		
		// 选择文件对象
		fileList.getSelectionModel().selectedItemProperty().addListener((tmp, oldValue, newValue) -> {
			if (newValue != null) view.getTipsPane().setSelected(fileList.getSelectionModel().getSelectedItems().size());
		});
		// 双击文件列表
		fileList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				FileCell selected = fileList.getSelectionModel().getSelectedItem();
				if (selected != null && !selected.getName().equals("")) {
					String folder = selected.getName();
					String path = view.getPath().getText() + folder.substring(folder.indexOf(".") + 1) + File.separator;
					prevStack.push(view.getPath().getText());
					getFileList(path);
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
		// 路径文本域回车
		view.getPath().setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				getFileList(view.getPath().getText());
			}
		});
	}
	
	// 关闭程序
	public void stop() throws Exception {
		ioHistoryLoader.set();
		// 关闭后台线程
		upload.shutdown();
		download.shutdown();
		state.shutdown();
		
		stage.close();
		super.stop();
	}

	/**
	 * 获取服务器状态
	 * 
	 */
	private void setStatus() {
		state = new StateRequest();
		JsonParser jp = new JsonParser();
		state.valueProperty().addListener((tmp, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonObject jo = (JsonObject) jp.parse(newValue);
				view.getCpu().getPB().setProgress(jo.get("cpuUse").getAsDouble());
				view.getMemory().getPB().setProgress(jo.get("memUse").getAsDouble() / jo.get("memMax").getAsDouble());
				view.getDisk().getPB().setProgress(jo.get("diskUse").getAsDouble() / jo.get("diskMax").getAsDouble());
			}
		});
		state.start();
	}
	
	/**
	 * 获取文件列表
	 * 
	 * @param path 路径
	 */
	private void getFileList(String path) {
		PublicRequest request = new PublicRequest("path", path);
		request.valueProperty().addListener((tmp, oldValue, newValue) -> {
			if (!newValue.equals("")) {
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
			}
		});
		request.start();
	}

	public static List<IOHistory> getIoHistories() {
		return ioHistories;
	}

	public static void setIoHistories(List<IOHistory> ioHistories) {
		Main.ioHistories = ioHistories;
	}
}