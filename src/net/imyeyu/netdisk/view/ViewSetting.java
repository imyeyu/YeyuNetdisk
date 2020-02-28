package net.imyeyu.netdisk.view;

import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.dialog.Alert;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.LabelTextField;

public class ViewSetting extends Stage {
	
	private ResourceBundle rb = Entrance.getRb();
	private Map<String, Object> config;
	
	private Label blog;
	private Button setDLLocation, save, close;
	private GridPane generalPane, devPane, aboutPane;
	private CheckBox eToken;
	private ComboBox<String> lang;
	private NavButton general, dev, about;
	private TextField token, dlLocation, ip;
	private BorderPane main;
	private LabelTextField portPublic, portState, portUpload, portDownload;
	
	public ViewSetting() {
		this.config = Entrance.getConfig();
		
		main = new BorderPane();
		
		// 初始化面板
		generalPane = new GridPane();
		initGeneralPane(generalPane);
		devPane = new GridPane();
		initDevPane(devPane);
		aboutPane = new GridPane();
		initAboutPane(aboutPane);

		// 导航
		general = new NavButton(rb.getString("settingGeneral"), 80, 26);
		general.setOnAction(event -> main.setCenter(generalPane));
		dev = new NavButton(rb.getString("settingDev"), 80, 26);
		dev.setOnAction(event -> main.setCenter(devPane));
		about = new NavButton(rb.getString("settingAbout"), 80, 26);
		VBox nav = new VBox();
		nav.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		nav.getChildren().addAll(general, dev, about);
		
		// 按钮
		HBox btns = new HBox();
		save = new Button(rb.getString("settingSave"));
		close = new Button(rb.getString("settingClose"));
		btns.setAlignment(Pos.CENTER_RIGHT);
		btns.setSpacing(6);
		btns.setPadding(new Insets(6));
		btns.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btns.getChildren().addAll(save, close);
		
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		main.setLeft(nav);
		main.setCenter(generalPane);
		main.setBottom(btns);

		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/setting.png"));
		setTitle(rb.getString("settingTitle"));
		setMinWidth(460);
		setMinHeight(320);
		setWidth(460);
		setHeight(320);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		setScene(scene);
		show();
		
		lang.getSelectionModel().select(1);
	}
	
	// 通用
	private void initGeneralPane(GridPane pane) {
		Label langLabel = new Label(rb.getString("settingLang"));
		Label tokenLabel = new Label(rb.getString("settingToken"));
		Label dlLocationLabel = new Label(rb.getString("settingDLLocation"));
		
		lang = new ComboBox<String>();
		ObservableList<String> langList = FXCollections.observableArrayList();
		langList.add("English");
		langList.add("简体中文");
		langList.add("繁體中文");
		lang.setItems(langList);
		
		HBox tokenBox = new HBox();
		token = new TextField(config.get("token").toString());
		eToken = new CheckBox("加密储存");
		eToken.setSelected(Boolean.valueOf(config.get("eToken").toString()));
		eToken.selectedProperty().addListener(event -> {
			if (eToken.isSelected()) new Alert("提示", "加密储存只可重置，不可找回");
		});
		tokenBox.setSpacing(8);
		tokenBox.setAlignment(Pos.CENTER_LEFT);
		tokenBox.getChildren().addAll(token, eToken);
		
		HBox dlBox = new HBox();
		dlLocation = new TextField(config.get("dlLocation").toString());
		setDLLocation = new Button(rb.getString("fileSelectTypeFolder"));
		dlBox.setSpacing(8);
		dlBox.setAlignment(Pos.CENTER_LEFT);
		dlBox.getChildren().addAll(dlLocation, setDLLocation);

		ColumnConstraints col = new ColumnConstraints(90);
		col.setHalignment(HPos.RIGHT);
		RowConstraints row = new RowConstraints(32);
		
		pane.getColumnConstraints().add(col);
		pane.getRowConstraints().addAll(row, row, row);
		pane.setPadding(new Insets(12));
		pane.addColumn(0, langLabel, tokenLabel, dlLocationLabel);
		pane.addColumn(1, lang, tokenBox, dlBox);
	}
	
	// 高级
	private void initDevPane(GridPane pane) {
		Label ipLabel = new Label(rb.getString("settingIP"));
		Label portLabel = new Label(rb.getString("settingPort"));
		
		ip = new TextField(config.get("ip").toString());
		HBox portPane0 = new HBox();
		portPublic = new LabelTextField(rb.getString("settingPortPublic"), 64);
		portPublic.setText(config.get("portPublic").toString());
		portState = new LabelTextField(rb.getString("settingPortState"), 64);
		portState.setText(config.get("portState").toString());
		portPane0.setSpacing(6);
		portPane0.getChildren().addAll(portPublic, portState);

		HBox portPane1 = new HBox();
		portUpload = new LabelTextField(rb.getString("settingPortUpload"), 64);
		portUpload.setText(config.get("portUpload").toString());
		portDownload = new LabelTextField(rb.getString("settingPortDownload"), 64);
		portDownload.setText(config.get("portDownload").toString());
		portPane1.setSpacing(6);
		portPane1.getChildren().addAll(portUpload, portDownload);
		
		Label restartTips = new Label(rb.getString("settingRestartTips"));
		restartTips.setTextFill(Paint.valueOf("#B5B5B5"));

		ColumnConstraints col = new ColumnConstraints(90);
		col.setHalignment(HPos.RIGHT);
		RowConstraints row = new RowConstraints(32);
		row.setVgrow(Priority.NEVER);
		
		pane.getColumnConstraints().add(col);
		pane.getRowConstraints().addAll(row, row, row, row);
		pane.setPadding(new Insets(12));
		pane.addColumn(0, ipLabel, portLabel);
		pane.addColumn(1, ip, portPane0, portPane1, restartTips);
	}

	// 关于
	private void initAboutPane(GridPane pane) {
		BorderPane main = new BorderPane();
		VBox center = new VBox();
		Label name = new Label("Yeyu Netdisk");
		name.setFont(Font.font("System", FontWeight.BOLD, 24));
		Label cname = new Label("夜雨云盘");
		cname.setFont(Font.font(14));
		Label version = new Label("v1.0.0 BETA");
		center.setAlignment(Pos.TOP_CENTER);
		center.setSpacing(4);
		center.getChildren().addAll(name, cname, version);
		
		VBox bottom = new VBox();
		blog = new Label("个人博客：https://www.imyeyu.net");
		blog.setCursor(Cursor.HAND);
		Label cr = new Label("CopyRight © 夜雨 2020 All Rights Reserved 版权所有");
		bottom.setSpacing(4);
		bottom.setAlignment(Pos.CENTER);
		bottom.getChildren().addAll(blog, cr);
		
		main.prefWidthProperty().bind(pane.widthProperty());
		main.prefHeightProperty().bind(pane.heightProperty());
		main.setPadding(new Insets(16));
		main.setCenter(center);
		main.setBottom(bottom);
		
		pane.addColumn(0, main);
	}

	public Button getSave() {
		return save;
	}

	public Button getClose() {
		return close;
	}

	public GridPane getGeneralPane() {
		return generalPane;
	}
	
	public Button getSetDLLocation() {
		return setDLLocation;
	}

	public GridPane getDevPane() {
		return devPane;
	}

	public GridPane getAboutPane() {
		return aboutPane;
	}

	public CheckBox geteToken() {
		return eToken;
	}
	
	public TextField getDlLocation() {
		return dlLocation;
	}

	public ComboBox<String> getLang() {
		return lang;
	}

	public NavButton getGeneral() {
		return general;
	}

	public NavButton getDev() {
		return dev;
	}

	public NavButton getAbout() {
		return about;
	}

	public TextField getToken() {
		return token;
	}

	public TextField getIp() {
		return ip;
	}

	public BorderPane getMain() {
		return main;
	}

	public LabelTextField getPortPublic() {
		return portPublic;
	}

	public LabelTextField getPortState() {
		return portState;
	}

	public LabelTextField getPortUpload() {
		return portUpload;
	}

	public LabelTextField getPortDownload() {
		return portDownload;
	}
	
	public Label getBlog() {
		return blog;
	}
}