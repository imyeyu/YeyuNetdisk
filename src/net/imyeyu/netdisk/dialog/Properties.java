package net.imyeyu.netdisk.dialog;

import java.text.DecimalFormat;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
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
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.ToolTipsX;

public class Properties extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private Label type, path, size, date, isFocusLabel;

	public Properties(String pos, FileCell fileCell) {
		String fileName = fileCell.getName();
		String fileType = fileName.substring(0, fileName.indexOf("."));
		//右键菜单
		ContextMenu menu = new ContextMenu();
		MenuItem copy = new MenuItem(rbx.def("copy"));
		menu.getItems().add(copy);
		menu.setAnchorX(-20);
		// 窗体图标
		getIcons().add(FileFormat.getImage(fileType));
		fileType = fileType.equals("folder") ? rbx.def("folder") : fileType;
		fileName = fileName.substring(fileName.indexOf(".") + 1);
		DecimalFormat formatSize = new DecimalFormat("#,###");

		Label title = new Label(fileName);
		title.setPrefWidth(296);
		title.setPrefHeight(26);
		title.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).bottom());
		title.setAlignment(Pos.CENTER);
		title.setFont(Font.font("System", FontWeight.BOLD, 20));
		title.setTooltip(new ToolTipsX(fileName));
		title.setContextMenu(menu);
		title.setOnContextMenuRequested(event -> isFocusLabel = (Label) event.getSource());
		
		GridPane content = new GridPane();
		Label labelFormat = new Label(rbx.def("propertiesType"));
		labelFormat.setAlignment(Pos.CENTER_RIGHT);
		Label labelPath = new Label(rbx.def("propertiesLocation"));
		Label labelSize = new Label(rbx.def("propertiesSize"));
		Label labelDate = new Label(rbx.def("propertiesDate"));
		
		Insets padding = new Insets(4, 0, 4, 0);
		type = new Label(fileType);
		type.setPrefWidth(232);
		type.setPadding(padding);
		path = new Label(pos + fileName);
		path.setPadding(padding);
		path.setTooltip(new ToolTipsX(pos + fileName));
		path.setContextMenu(menu);
		path.setOnContextMenuRequested(event -> isFocusLabel = (Label) event.getSource());
		size = new Label(fileCell.getSize() + " (" + formatSize.format(fileCell.getSizeLong()) + rbx.l("byte") + ")");
		size.setPadding(padding);
		date = new Label(fileCell.getDate());
		date.setPadding(padding);

		ColumnConstraints col = new ColumnConstraints(72);
		col.setHalignment(HPos.RIGHT);
		content.setPrefWidth(296);
		content.setPadding(new Insets(8, 0, 0, 0));
		content.getColumnConstraints().add(col);
		if (fileType.equals(rbx.def("propertieFolder"))) {
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
		setTitle(fileName + rbx.l("properties"));
		setWidth(320);
		setHeight(200);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		show();

		copy.setOnAction(event -> {
			YeyuUtils.tools().setIntoClipboard(isFocusLabel.getText());
		});
		scene.setOnKeyReleased(event -> {
			if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.ESCAPE)) {
				close();
			}
		});
	}
}