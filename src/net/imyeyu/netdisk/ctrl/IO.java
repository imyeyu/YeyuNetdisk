package net.imyeyu.netdisk.ctrl;

import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.DownloadFile;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.bean.UploadFile;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.view.ViewIO;

public class IO extends ViewIO {
	
	private Upload upload;
	private Download download;
	
	private String isUpload, isDownload;
	private Map<String, Object> config = Entrance.getConfig();
	private SimpleStringProperty show;
	
	private ObservableList<IOCell> uploadList, downloadList;
	private ObservableList<IOHistory> finishList;

	public IO(Upload upload, Download download) {
		this.upload = upload;
		this.download = download;
		initUploadList();
		initDownloadList();
		initFinishList();
		List<IOHistory> ioHistories = Main.getIoHistories();
		for (int i = 0; i < ioHistories.size(); i++) {
			finishList.add(ioHistories.get(i));
		}
		
		// 上传
		getUpload().setOnAction(event -> upload());
		
		// 下载
		getDownload().setOnAction(event -> download());
		
		// 已完成
		getFinish().setOnAction(event -> finish());
		
		// 清空记录
		getClear().setOnAction(event -> {
			getFinishList().getItems().clear();
		});
		
		// 关闭
		setOnCloseRequest(event -> close());
	}
	
	public void close() {
		Main.setIoHistories(finishList);
		super.close();
	}
	
	// 获取核心上传队列
	private void initUploadList() {
		IOCell cell;
		uploadList = getUploadList().getItems();
		SimpleListProperty<UploadFile> files = Upload.getListProperty();
		for (int i = Upload.getIndex(); i < files.size(); i++) {
			cell = new IOCell(files.get(i).getName(), files.get(i).getSize(), -1);
			cell.setPath(files.get(i).getToPath());
			cell.setLocal(false);
			cell.setMaxSize(files.get(i).getSize());
			uploadList.add(cell);
		}
		if (0 < uploadList.size()) bindUploadCore(uploadList.get(0));
	}
	
	// 获取核心下载队列
	private void initDownloadList() {
		IOCell cell;
		downloadList = getDownloadList().getItems();
		SimpleListProperty<DownloadFile> files = Download.getListProperty();
		for (int i = Download.getIndex(); i < files.size(); i++) {
			cell = new IOCell(files.get(i).getName(), files.get(i).getSize(), -1);
			cell.setPath(config.get("dlLocation").toString());
			cell.setLocal(true);
			cell.setMaxSize(files.get(i).getSize());
			downloadList.add(cell);
		}
		if (0 < downloadList.size()) bindDownloadCore(downloadList.get(0));
	}
	
	private void initFinishList() {
		finishList = getFinishList().getItems();
		show = getFinishList().getShow();
	}
	
	// 绑定上传核心
	private void bindUploadCore(IOCell cell) {
		upload.messageProperty().addListener((list, oldValue, newValue) -> {
			if (!newValue.equals(isUpload)) { // 不明原因会被多次执行
				if (uploadList.size() != 0) {
					isUpload = newValue;
					uploadList.remove(0);
					getUploadList().refresh();
					if (0 < uploadList.size()) bindUploadCore(uploadList.get(0));
				}
			}
		});
		upload.valueProperty().addListener((list, oldValue, newValue) -> {
			cell.setSize(newValue.doubleValue());
		});
		upload.progressProperty().addListener((list, oldValue, newValue) -> {
			cell.setPercent(newValue.doubleValue());
		});
	}
	
	// 绑定下载核心
	private void bindDownloadCore(IOCell cell) {
		download.messageProperty().addListener((list, oldValue, newValue) -> {
			if (!newValue.equals(isDownload)) {
				if (downloadList.size() != 0) {
					finishList.add(0, new IOHistory(downloadList.get(0).getName(), newValue.substring(newValue.indexOf("#") + 1), true));
					isDownload = newValue;
					downloadList.remove(0);
					getDownloadList().refresh();
					if (0 < downloadList.size()) bindDownloadCore(downloadList.get(0));
				}
			}
		});
		download.valueProperty().addListener((list, oldValue, newValue) -> {
			cell.setSize(newValue.doubleValue());
		});
		download.progressProperty().addListener((list, oldValue, newValue) -> {
			cell.setPercent(newValue.doubleValue());
		});
	}

	public SimpleStringProperty getShow() {
		return show;
	}
}