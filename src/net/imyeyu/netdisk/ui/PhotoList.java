package net.imyeyu.netdisk.ui;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.imyeyu.netdisk.request.ImgRequest;
import net.imyeyu.utils.YeyuUtils;

public class PhotoList extends VBox {
	
	private boolean isShutdown = false;
	
	private Map<Photo, String> requestList = new LinkedHashMap<>();
	private Iterator<Map.Entry<Photo, String>> entries;
	
	private double pbStep = -1;
	private boolean isCompressImg = false;
	private ProgressBar pb;
	
	public PhotoList(String year, JsonArray list, boolean isCompressImg) {
		this.isCompressImg = isCompressImg;
		
		year = year + File.separator;
		// 数据
		JsonArray items;
		JsonObject month;
		// UI
		Label monthLabel;
		Photo item;
		FlowPane itemBox;
		BorderPane monthBox;
		Rectangle2D rec = new Rectangle2D(0, 0, 128, 128);
		
		// 进度条
		double pbSize = 0;
		pb = new ProgressBar();
		getChildren().add(pb);
		
        // 渲染
		for (int i = 0; i < list.size(); i++) {
			monthBox = new BorderPane();
			month = list.get(i).getAsJsonObject();
			
			// 月份标签
			if (YeyuUtils.encode().isNumber(month.get("month").getAsString())) {
				monthLabel = new Label(month.get("month").getAsString() + " 月");
			} else {
				monthLabel = new Label(month.get("month").getAsString());
			}
			monthLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
			monthLabel.setPadding(new Insets(12, 0, 12, 20));
			
			// 照片
			itemBox = new FlowPane();
			items = month.get("items").getAsJsonArray();
			for (int j = 0; j < items.size(); j++) {
				item = new Photo(items.get(j).getAsString());
				item.setViewport(rec);
				itemBox.getChildren().add(item);
				requestList.put(item, year + month.get("month").getAsString() + File.separator + items.get(j).getAsString());
				
				pbSize++;
			}
			itemBox.setHgap(4);
			itemBox.setVgap(4);
			
			monthBox.setTop(monthLabel);
			monthBox.setCenter(itemBox);
			
			getChildren().add(monthBox);
		}
		
		// 初始化获取照片
		entries = requestList.entrySet().iterator();
		if (entries.hasNext()) {
			setCompressImg();
			pbStep = 1d / pbSize;
			pb.setProgress(0);
		}
	}
	
	// 获取照片
	private void setCompressImg() {
	    Map.Entry<Photo, String> entry = entries.next();
	    ImgRequest request = new ImgRequest("getImgPM", entry.getValue(), isCompressImg);
	    request.valueProperty().addListener((obs, old, img) -> {
	    	if (img != null) {
	    		entry.getKey().setImage(img);
				pb.setProgress(pb.getProgress() + pbStep);
	    		if (!isShutdown && entries.hasNext()) {
	    			setCompressImg();
	    		} else {
	    			getChildren().remove(pb);
	    		}
			}
	    });
	    request.start();
	}
	
	public void shutdown() {
		this.isShutdown = true;
	}
	
	public ProgressBar getPb() {
		return pb;
	}

	public Map<Photo, String> getRequestList() {
		return requestList;
	}
}