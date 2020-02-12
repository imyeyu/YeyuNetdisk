package net.imyeyu.netdisk.ctrl;

import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import net.imyeyu.netdisk.bean.IOCell;
import net.imyeyu.netdisk.bean.IOHistory;
import net.imyeyu.netdisk.core.Download;
import net.imyeyu.netdisk.core.Download.DownloadFile;
import net.imyeyu.netdisk.core.Upload;
import net.imyeyu.netdisk.core.Upload.UploadFile;
import net.imyeyu.netdisk.view.ViewIO;

public class IO extends ViewIO {
	
	private Upload upload;
	private Download download;
	private String isUpload, isDownload;
	
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
	
	private void initUploadList() {
		IOCell cell;
		uploadList = getUploadList().getItems();
//		for (int i = 0; i < 5; i++) {
//			cell = new IOCell("test" + i + ".jar", 2000, -1);
//			uploadList.add(cell);
//		}
		SimpleListProperty<UploadFile> files = Upload.getListProperty();
		for (int i = 0; i < files.size(); i++) {
			cell = new IOCell(files.get(i).getName(), files.get(i).getSize(), -1);
			cell.setPath(files.get(i).getToPath());
			cell.setLocal(false);
			cell.setMaxSize(files.get(i).getSize());
			uploadList.add(cell);
		}
		if (0 < uploadList.size()) bindUploadCore(uploadList.get(0));
//		uploadList.addListener(new ListChangeListener<IOCell>() {
//			public void onChanged(Change<? extends IOCell> c) {
//				System.out.println("change");
////				c.next();
////				upload.remove(c.getRemoved().get(0));
//			}
//		});
//		uploadList.removeListener(new ListChangeListener<IOCell>() {
//			public void onChanged(Change<? extends IOCell> c) {
//				System.out.println("remove");
//			}
//		});
	}
	private void initDownloadList() {
		IOCell cell;
		downloadList = getDownloadList().getItems();
		SimpleListProperty<DownloadFile> files = download.getListProperty();
		for (int i = 0; i < files.size(); i++) {
			cell = new IOCell(files.get(i).getName(), files.get(i).getSize(), -1);
			cell.setPath(Download.DL_PATH);
			cell.setLocal(true);
			cell.setMaxSize(files.get(i).getSize());
			downloadList.add(cell);
		}
		if (0 < downloadList.size()) bindDownloadCore(downloadList.get(0));
	}
	
	private void initFinishList() {
		finishList = getFinishList().getItems();
	}
	
	private void bindUploadCore(IOCell cell) {
		upload.messageProperty().addListener((list, oldValue, newValue) -> {
			if (!newValue.equals(isUpload)) { // 不明原因会被多次执行
				if (uploadList.size() != 0) {
					finishList.add(0, new IOHistory(uploadList.get(0).getName(), uploadList.get(0).getPath(), false));
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
	
	private void bindDownloadCore(IOCell cell) {
		download.messageProperty().addListener((list, oldValue, newValue) -> {
			if (!newValue.equals(isDownload)) {
				if (downloadList.size() != 0) {
					finishList.add(0, new IOHistory(downloadList.get(0).getName(), downloadList.get(0).getPath(), true));
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
}