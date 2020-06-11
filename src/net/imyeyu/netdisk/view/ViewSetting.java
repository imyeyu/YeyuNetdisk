package net.imyeyu.netdisk.view;

import java.util.Map;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
import net.imyeyu.utils.Lang;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.LabelTextField;

public class ViewSetting extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	private Map<String, Object> config = Entrance.getConfig();
	
	private Label blog, version;
	private String versionValue = "1.0.3";
	private Button setDLLocation, openDLLocation, setCache, openCache, save, close;
	private GridPane generalPane, serverPane, aboutPane;
	private CheckBox sound, ioList, eToken;
	private ComboBox<String> lang;
	private NavButton general, server, about;
	private TextField token, dlLocation, cache, ip;
	private BorderPane main;
	private RadioButton exit;
	private LabelTextField portPublic, portState, portUpload, portDownload, portHTTP;
	
	public ViewSetting() {
		main = new BorderPane();
		
		// 初始化面板
		generalPane = new GridPane();
		initGeneralPane(generalPane);
		serverPane = new GridPane();
		initServerPane(serverPane);
		aboutPane = new GridPane();
		initAboutPane(aboutPane);

		// 导航
		general = new NavButton(rbx.def("settingGeneral"), 80, 26);
		general.setOnAction(event -> main.setCenter(generalPane));
		server = new NavButton(rbx.def("settingServer"), 80, 26);
		server.setOnAction(event -> main.setCenter(serverPane));
		about = new NavButton(rbx.def("settingAbout"), 80, 26);
		VBox nav = new VBox();
		nav.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		nav.getChildren().addAll(general, server, about);
		
		// 按钮
		HBox btns = new HBox();
		save = new Button(rbx.def("save"));
		close = new Button(rbx.def("close"));
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
		setTitle(rbx.def("settingTitle"));
		setMinWidth(520);
		setMinHeight(340);
		setWidth(520);
		setHeight(340);
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		setScene(scene);
		show();
	}
	
	// 通用
	private void initGeneralPane(GridPane pane) {
		Label langLabel = new Label(rbx.def("settingLang"));
		Label dlLocationLabel = new Label(rbx.def("settingDLLocation"));
		Label textCacheLabel = new Label(rbx.def("settingCache"));
		Label ioLabel = new Label(rbx.def("settingIOList"));
		Label soundLabel = new Label(rbx.def("settingSound"));
		Label closeLabel = new Label(rbx.def("settingClose"));
		
		lang = new ComboBox<String>();
		ObservableList<String> langList = FXCollections.observableArrayList();
		langList.add("English");
		langList.add("简体中文");
		langList.add("繁體中文");
		lang.setItems(langList);
		lang.getSelectionModel().select(Lang.toString(config.get("language").toString()));
		
		HBox dlBox = new HBox();
		dlLocation = new TextField(config.get("dlLocation").toString());
		setDLLocation = new Button(rbx.def("select"));
		openDLLocation = new Button(rbx.def("open"));
		dlBox.setSpacing(8);
		dlBox.setAlignment(Pos.CENTER_LEFT);
		dlBox.getChildren().addAll(dlLocation, setDLLocation, openDLLocation);
		
		HBox cacheBox = new HBox();
		cache = new TextField(config.get("cache").toString());
		setCache = new Button(rbx.def("select"));
		openCache = new Button(rbx.def("open"));
		cacheBox.setSpacing(8);
		cacheBox.setAlignment(Pos.CENTER_LEFT);
		cacheBox.getChildren().addAll(cache, setCache, openCache);
		
		ioList = new CheckBox(rbx.def("settingOpenIOList"));
		ioList.setSelected(Boolean.valueOf(config.get("openIOList").toString()));
		
		sound = new CheckBox(rbx.def("settingPlaySound"));
		sound.setSelected(Boolean.valueOf(config.get("sound").toString()));
		
		HBox closeBox = new HBox();
		ToggleGroup group = new ToggleGroup();
		RadioButton hide = new RadioButton(rbx.def("settingHideOnClose"));
		hide.setSelected(!Boolean.valueOf(config.get("exitOnClose").toString()));
		hide.setToggleGroup(group);
		exit = new RadioButton(rbx.def("settingExitOnClose"));
		exit.setSelected(Boolean.valueOf(config.get("exitOnClose").toString()));
		exit.setToggleGroup(group);
		closeBox.setSpacing(8);
		closeBox.setAlignment(Pos.CENTER_LEFT);
		closeBox.getChildren().addAll(hide, exit);

		ColumnConstraints col = new ColumnConstraints(90);
		col.setHalignment(HPos.RIGHT);
		RowConstraints row = new RowConstraints(32);
		
		pane.getColumnConstraints().add(col);
		pane.getRowConstraints().addAll(row, row, row, row, row, row);
		pane.setPadding(new Insets(12));
		pane.addColumn(0, langLabel, dlLocationLabel, textCacheLabel, ioLabel, soundLabel, closeLabel);
		pane.addColumn(1, lang, dlBox, cacheBox, ioList, sound, closeBox);
	}
	
	// 服务器
	private void initServerPane(GridPane pane) {
		Label ipLabel = new Label(rbx.def("settingIP"));
		Label tokenLabel = new Label(rbx.def("settingToken"));
		Label portLabel = new Label(rbx.def("settingPort"));
		
		ip = new TextField(config.get("ip").toString());
		
		HBox tokenBox = new HBox();
		token = new TextField(config.get("token").toString());
		eToken = new CheckBox(rbx.def("settingEToken"));
		eToken.setSelected(Boolean.valueOf(config.get("eToken").toString()));
		eToken.selectedProperty().addListener(event -> {
			if (eToken.isSelected()) new Alert(rbx.def("tips"), rbx.def("settingETokenTips"));
		});
		tokenBox.setSpacing(8);
		tokenBox.setAlignment(Pos.CENTER_LEFT);
		tokenBox.getChildren().addAll(token, eToken);
		
		HBox portPane0 = new HBox();
		portPublic = new LabelTextField(rbx.def("settingPortPublic"), 64);
		portPublic.setText(config.get("portPublic").toString());
		portState = new LabelTextField(rbx.def("settingPortState"), 64);
		portState.setText(config.get("portState").toString());
		portPane0.setSpacing(6);
		portPane0.getChildren().addAll(portPublic, portState);

		HBox portPane1 = new HBox();
		portUpload = new LabelTextField(rbx.def("upload"), 64);
		portUpload.setText(config.get("portUpload").toString());
		portDownload = new LabelTextField(rbx.def("download"), 64);
		portDownload.setText(config.get("portDownload").toString());
		portPane1.setSpacing(6);
		portPane1.getChildren().addAll(portUpload, portDownload);
		
		HBox portPane2 = new HBox();
		portHTTP = new LabelTextField("HTTP", 60);
		portHTTP.setText(config.get("portHTTP").toString());
		portPane2.setSpacing(6);
		portPane2.getChildren().addAll(portHTTP);
		
		Label restartTips = new Label(rbx.def("settingRestartTips"));
		restartTips.setTextFill(Paint.valueOf("#B5B5B5"));

		ColumnConstraints col = new ColumnConstraints(90);
		col.setHalignment(HPos.RIGHT);
		RowConstraints row = new RowConstraints(32);
		row.setVgrow(Priority.NEVER);
		
		pane.getColumnConstraints().add(col);
		pane.getRowConstraints().addAll(row, row, row, row, row, row);
		pane.setPadding(new Insets(12));
		pane.addColumn(0, ipLabel, tokenLabel, portLabel);
		pane.addColumn(1, ip, tokenBox, portPane0, portPane1, portPane2, restartTips);
	}

	// 关于
	private void initAboutPane(GridPane pane) {
		BorderPane main = new BorderPane();
		VBox center = new VBox();
		Label name = new Label("iNetdisk");
		name.setFont(Font.font("System", FontWeight.BOLD, 24));
		Label cname = new Label(rbx.def("title"));
		cname.setFont(Font.font(14));
		version = new Label(versionValue);
		center.setAlignment(Pos.TOP_CENTER);
		center.setSpacing(4);
		center.getChildren().addAll(name, cname, version);
		
		VBox bottom = new VBox();
		blog = new Label(rbx.def("settingMyBlog") + "https://www.imyeyu.net");
		blog.setCursor(Cursor.HAND);
		Label cr = new Label("Copyright © 夜雨 2020 All Rights Reserved 版权所有");
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
	
	public Button getOpenDLLocation() {
		return openDLLocation;
	}

	public GridPane getDevPane() {
		return serverPane;
	}

	public GridPane getAboutPane() {
		return aboutPane;
	}

	public CheckBox geteToken() {
		return eToken;
	}
	
	public CheckBox getIOList() {
		return ioList;
	}
	
	public CheckBox getSound() {
		return sound;
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

	public NavButton getServer() {
		return server;
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
	
	public LabelTextField getPortHTTP() {
		return portHTTP;
	}

	public Label getBlog() {
		return blog;
	}
	
	public Label getVersion() {
		return version;
	}
	
	public String getVersionValue() {
		return versionValue;
	}

	public Button getSetCache() {
		return setCache;
	}

	public Button getOpenCache() {
		return openCache;
	}

	public TextField getCache() {
		return cache;
	}
	
	public RadioButton getExit() {
		return exit;
	}
}