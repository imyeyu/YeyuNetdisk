package net.imyeyu.netdisk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.ui.ButtonBar;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.netdisk.ui.PhotoInfoTable;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.ToolTipsX;
import net.imyeyu.utils.interfaces.GUIX;

public class ViewPhotoManager extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private VBox dateList;
	private Label count, selected;
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
		spDate.setStyle("-fx-background-insets: 0;-fx-padding: 0");
		spDate.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		dateList = new VBox();
		dateList.getChildren().add(new NavButton(rbx.def("loading")));
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
		addYear = new Button(rbx.def("photoNewYear"));
		addYear.setPrefWidth(83);
		autoDate = new ChoiceBox<String>();
		autoDate.setPrefWidth(80);
		autoDate.getItems().add(rbx.def("photoAutoArchiving"));
		if (rbx.def("language").equals("English")) {
			String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dev"};
			for (int i = 0; i < months.length; i++) {
				autoDate.getItems().add(months[i] + " ");
			}
		} else {
			for (int i = 1; i <= 12; i++) {
				autoDate.getItems().add(i + rbx.l("month"));
			}
		}
		autoDate.getSelectionModel().select(0);
		autoDate.setTooltip(new ToolTipsX(rbx.def("photoAutoArchivingTips")));
		uploadSettingBox.setSpacing(8);
		uploadSettingBox.setAlignment(Pos.CENTER_LEFT);
		uploadSettingBox.getChildren().addAll(addYear, autoDate);
		
		ButtonBar ioBtnBar = new ButtonBar();
		upload = new Button(rbx.def("upload"));
		upload.setPrefSize(86, 28);
		download = new Button(rbx.def("download"));
		download.setPrefSize(85, 28);
		ioBtnBar.addAll(upload, download);
		
		ButtonBar selectBtnBar = new ButtonBar();
		selectAll = new Button(rbx.def("all"));
		selectAll.setPrefSize(43, 20);
		deSelect = new Button(rbx.def("rall"));
		deSelect.setPrefSize(43, 20);
		unSelect = new Button(rbx.def("selectNone"));
		unSelect.setPrefSize(85, 20);
		selectBtnBar.addAll(selectAll, deSelect, unSelect);
		
		ButtonBar changeBtnBar = new ButtonBar();
		refresh = new Button(rbx.def("refresh"));
		refresh.setPrefSize(71, 20);
		move = new Button(rbx.def("move"));
		move.setPrefSize(50, 20);
		del = new Button(rbx.def("del"));
		del.setPrefSize(50, 20);
		changeBtnBar.addAll(refresh, move, del);
		
		HBox selectTips = new HBox();
		count = new Label();
		count.setTextFill(GUIX.LIGHT_GRAY);
		selected = new Label();
		selected.setTextFill(GUIX.LIGHT_GRAY);
		selectTips.setSpacing(8);
		selectTips.setAlignment(Pos.CENTER);
		selectTips.getChildren().addAll(count, selected);
		
		btns.setSpacing(10);
		btns.setPadding(new Insets(16, 16, 4, 16));
		btns.setBorder(spLine.bottom());
		btns.getChildren().addAll(uploadSettingBox, ioBtnBar, selectBtnBar, changeBtnBar, selectTips);
		
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
		setTitle(rbx.def("mainPhoto"));
		setMinWidth(897);
		setMinHeight(555);
		setWidth(897);
		setHeight(555);
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
	
	public Label getCount() {
		return count;
	}
	
	public Label getSelected() {
		return selected;
	}
}