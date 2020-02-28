package net.imyeyu.netdisk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.ui.ButtonBar;
import net.imyeyu.netdisk.ui.PhotoInfoTable;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.ToolTipsX;

public class ViewPhotoManager extends Stage {
	
	private VBox dateList;
	private Button addYear, upload, download, selectAll, deSelect, unSelect, refresh, move, del;
	private ChoiceBox<String> autoDate;
	private BorderPane main;
	private AnchorPane listBox;
	private ScrollPane spList, spDate;
	private PhotoInfoTable info;

	public ViewPhotoManager() {
		main = new BorderPane();
		
		BorderX spLine = new BorderX("#B5B5B5", BorderX.SOLID, 1);
		
		// 日期
		spDate = new ScrollPane();
		dateList = new VBox();
		dateList.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		spDate.setContent(dateList);
		
		// 照片列表
		listBox = new AnchorPane();
		spList = new ScrollPane();
		spList.setHvalue(100);
		spList.setPadding(Insets.EMPTY);
		spList.setStyle("-fx-background-insets: 0");
		AnchorPane.setTopAnchor(spList, 0d);
		AnchorPane.setLeftAnchor(spList, 0d);
		AnchorPane.setRightAnchor(spList, 0d);
		AnchorPane.setBottomAnchor(spList, 0d);
		listBox.getChildren().add(spList);
		
		// 控制
		BorderPane right = new BorderPane();
		right.setBorder(spLine.left());
		
		VBox btns = new VBox();
		
		HBox uploadSettingBox = new HBox();
		addYear = new Button("新增年份");
		addYear.setPrefWidth(83);
		autoDate = new ChoiceBox<String>();
		autoDate.getItems().add("自动归档");
		for (int i = 1; i <= 12; i++) {
			autoDate.getItems().add(i + " 月");
		}
		autoDate.getSelectionModel().select(0);
		autoDate.setTooltip(new ToolTipsX("自动保存到拍照日期目录"));
		uploadSettingBox.setSpacing(8);
		uploadSettingBox.setAlignment(Pos.CENTER_LEFT);
		uploadSettingBox.getChildren().addAll(addYear, autoDate);
		
		ButtonBar ioBtnBar = new ButtonBar();
		upload = new Button("上传");
		upload.setPrefSize(86, 28);
		download = new Button("下载");
		download.setPrefSize(85, 28);
		ioBtnBar.addAll(upload, download);
		
		ButtonBar selectBtnBar = new ButtonBar();
		selectAll = new Button("全选");
		selectAll.setPrefSize(43, 20);
		deSelect = new Button("反选");
		deSelect.setPrefSize(43, 20);
		unSelect = new Button("取消选择");
		unSelect.setPrefSize(85, 20);
		selectBtnBar.addAll(selectAll, deSelect, unSelect);
		
		ButtonBar changeBtnBar = new ButtonBar();
		refresh = new Button("刷新");
		refresh.setPrefSize(71, 20);
		move = new Button("移动");
		move.setPrefSize(50, 20);
		del = new Button("删除");
		del.setPrefSize(50, 20);
		changeBtnBar.addAll(refresh, move, del);
		
		btns.setSpacing(12);
		btns.setPadding(new Insets(16));
		btns.setBorder(spLine.bottom());
		btns.getChildren().addAll(uploadSettingBox, ioBtnBar, selectBtnBar, changeBtnBar);
		
		VBox ctrl = new VBox();
		ctrl.getChildren().add(btns);
		
		// 照片信息
		info = new PhotoInfoTable();
		info.setPrefWidth(right.getWidth());
		
		right.setTop(ctrl);
		right.setCenter(info);
		
		main.setBorder(spLine.top());
		main.setLeft(spDate);
		main.setCenter(listBox);
		main.setRight(right);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/photo.png"));
		setScene(scene);
		setTitle("照片管理器");
		setMinWidth(900);
		setMinHeight(545);
		setWidth(900);
		setHeight(545);
		initModality(Modality.APPLICATION_MODAL);
		show();
		
		upload.requestFocus();
	}
	
	public VBox getDateList() {
		return dateList;
	}
	
	public AnchorPane getListBox() {
		return listBox;
	}
	
	public Button getAddYear() {
		return addYear;
	}

	public Button getUpload() {
		return upload;
	}

	public Button getDownload() {
		return download;
	}
	
	public Button getSelectAll() {
		return selectAll;
	}
	
	public Button getDeSelect() {
		return deSelect;
	}
	
	public Button getUnSelect() {
		return unSelect;
	}
	
	public Button getRefresh() {
		return refresh;
	}

	public Button getMove() {
		return move;
	}

	public Button getDel() {
		return del;
	}
	
	public ChoiceBox<String> getAutoDate() {
		return autoDate;
	}
	
	public BorderPane getMain() {
		return main;
	}
	
	public ScrollPane getSpList() {
		return spList;
	}
	
	public ScrollPane getSpDate() {
		return spDate;
	}
	
	public PhotoInfoTable getInfoTable() {
		return info;
	}
}