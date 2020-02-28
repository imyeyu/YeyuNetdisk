package net.imyeyu.netdisk.view;

import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.bean.FileCell;
import net.imyeyu.netdisk.util.FileFormat;
import net.imyeyu.utils.gui.BorderX;

public class ViewProperties extends Stage {
	
	private Label type, path, size, date;

	public ViewProperties(String pos, FileCell fileCell) {
		ResourceBundle rb = Entrance.getRb();
		
		String fileName = fileCell.getName();
		String fileType = fileName.substring(0, fileName.indexOf("."));
		// 窗体图标
		getIcons().add(FileFormat.getImage(fileType));
		fileType = fileType.equals("folder") ? "文件夹" : fileType;
		fileName = fileName.substring(fileName.indexOf(".") + 1);
		DecimalFormat formatSize = new DecimalFormat("#,###");

		Label title = new Label(fileName);
		title.setPrefWidth(296);
		title.setPrefHeight(26);
		title.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).bottom());
		title.setAlignment(Pos.CENTER);
		title.setFont(Font.font("System", FontWeight.BOLD, 20));
		
		GridPane content = new GridPane();
		Label labelFormat = new Label("类型：");
		labelFormat.setAlignment(Pos.CENTER_RIGHT);
		Label labelPath = new Label("位置：");
		Label labelSize = new Label("大小：");
		Label labelDate = new Label("日期：");
		
		Insets padding = new Insets(4, 0, 4, 0);
		type = new Label(fileType);
		type.setPrefWidth(232);
		type.setPadding(padding);
		path = new Label(pos + fileName);
		path.setPadding(padding);
		size = new Label(fileCell.getSize() + " (" + formatSize.format(fileCell.getSizeLong()) + " 字节)");
		size.setPadding(padding);
		date = new Label(fileCell.getDate());
		date.setPadding(padding);

		ColumnConstraints col = new ColumnConstraints(72);
		col.setHalignment(HPos.RIGHT);
		content.setPrefWidth(296);
		content.setPadding(new Insets(8, 0, 0, 0));
		content.getColumnConstraints().add(col);
		if (fileType.equals(rb.getString("propertieFolder"))) {
			content.addColumn(0, labelFormat, labelPath, new Separator(), labelDate);
			content.addColumn(1, type, path, new Separator(), date);
		} else {
			content.addColumn(0, labelFormat, labelPath, new Separator(), labelSize, labelDate);
			content.addColumn(1, type, path, new Separator(), size, date);
		}
		
		BorderPane main = new BorderPane();
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		main.setPadding(new Insets(12));
		main.setTop(title);
		main.setCenter(content);
		
		Scene scene = new Scene(main);
		setScene(scene);
		setTitle(fileName + " 属性");
		setWidth(320);
		setHeight(200);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		show();
	}
}