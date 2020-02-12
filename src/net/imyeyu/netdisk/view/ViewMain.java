package net.imyeyu.netdisk.view;

import java.io.File;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.ui.ButtonBar;
import net.imyeyu.netdisk.ui.FileList;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.netdisk.ui.ServerState;
import net.imyeyu.netdisk.ui.TipsPane;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.SeparatorX;

public class ViewMain extends Stage {
	
	private ResourceBundle rb;
	private Button open, propertie, sync, upload, download, newFolder, rename, move, copy, delete, ioList, setting, photo, document, backup, tomcatFile, prev, next, parent, refresh, share, search;
	private FileList fileList;
	private TipsPane tipsPane;
	private TextField path, searchField;
	private ServerState cpu, memory, disk;
	
	public ViewMain() {
		this.rb = Entrance.getRb();
		
		BorderPane topPane = new BorderPane();
		// 文件控制栏
		FlowPane ctrlLeft = new FlowPane();
		// 文件读取
		ButtonBar fileLoadBtns = new ButtonBar();
		open = new Button(rb.getString("mainFileOpen"));
		propertie = new Button(rb.getString("mainFileProperties"));
		fileLoadBtns.addAll(open, propertie);
		ctrlLeft.setPrefWidth(123);
		ctrlLeft.setAlignment(Pos.CENTER);
		ctrlLeft.getChildren().add(fileLoadBtns);
		
		// 文件操作
		FlowPane ctrlCenter = new FlowPane();
		ButtonBar fileCtrlBtns = new ButtonBar();
		sync = new Button(rb.getString("mainFileSync"));
		YeyuUtils.gui().setBackground(sync, "net/imyeyu/netdisk/res/sync.png", 16, 5, 5);
		upload = new Button(rb.getString("mainFileUpload"));
		YeyuUtils.gui().setBackground(upload, "net/imyeyu/netdisk/res/upload.png", 16, 5, 5);
		download = new Button(rb.getString("mainFileDownload"));
		YeyuUtils.gui().setBackground(download, "net/imyeyu/netdisk/res/download.png", 16, 5, 5);
		newFolder = new Button(rb.getString("mainFileNewFolder"));
		rename = new Button(rb.getString("mainFileRename"));
		move = new Button(rb.getString("mainFileMove"));
		copy = new Button(rb.getString("mainFileCopy"));
		delete = new Button(rb.getString("mainFileDelete"));
		fileCtrlBtns.addAll(sync, upload, download, newFolder, rename, move, copy, delete);
		ctrlCenter.getChildren().addAll(new SeparatorX(true, 10, 48), fileCtrlBtns);
		
		// 设置
		FlowPane ctrlRight = new FlowPane();
		ButtonBar settingBtn = new ButtonBar();
		ioList = new Button(rb.getString("mainIOList"));
		YeyuUtils.gui().setBackground(ioList, "net/imyeyu/netdisk/res/ioList.png", 16, 5, 5);
		setting = new Button(rb.getString("mainSetting"));
		settingBtn.addAll(ioList, setting);
		ctrlRight.setPrefWidth(120);
		ctrlRight.setAlignment(Pos.CENTER);
		ctrlRight.getChildren().add(settingBtn);

		topPane.setPadding(new Insets(0, 16, 0, 16));
		topPane.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).vertical());
		topPane.setLeft(ctrlLeft);
		topPane.setCenter(ctrlCenter);
		topPane.setRight(ctrlRight);
		
		BorderPane leftPane = new BorderPane();
		// 左侧导航
		VBox navBtns = new VBox();
		
		photo = new NavButton(rb.getString("mainPhoto"));
		document = new NavButton(rb.getString("mainDocument"));
		backup = new NavButton(rb.getString("mainBackup"));
		tomcatFile = new NavButton(rb.getString("mainShare"));
		navBtns.getChildren().addAll(photo, document, backup, tomcatFile);
		// 服务器状态面板
		VBox states = new VBox();
		cpu = new ServerState(rb.getString("mainStateCPU"), 80);
		memory = new ServerState(rb.getString("mainStateMemory"), 80);
		disk = new ServerState(rb.getString("mainStateDisk"), 80);
		states.setPadding(new Insets(6, 0, 6, 0));
		states.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		states.getChildren().addAll(cpu, memory, disk);
		leftPane.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		leftPane.setCenter(navBtns);
		leftPane.setBottom(states);
		
		// 文件列表栏
		BorderPane filePane = new BorderPane();
		
		// 路径操作栏
		BorderPane pathCtrlPane = new BorderPane();
		ButtonBar pathCtrlBtns = new ButtonBar();
		prev = new Button();
		prev.setPrefWidth(26);
		YeyuUtils.gui().setBackground(prev, "net/imyeyu/netdisk/res/prev.png", 16, 5, 5);
		next = new Button();
		next.setPrefWidth(26);
		YeyuUtils.gui().setBackground(next, "net/imyeyu/netdisk/res/next.png", 16, 5, 5);
		parent = new Button();
		parent.setPrefWidth(26);
		YeyuUtils.gui().setBackground(parent, "net/imyeyu/netdisk/res/parent.png", 16, 5, 5);
		refresh = new Button();
		refresh.setPrefWidth(26);
		YeyuUtils.gui().setBackground(refresh, "net/imyeyu/netdisk/res/refresh.png", 16, 5, 5);
		share = new Button();
		share.setPrefWidth(26);
		YeyuUtils.gui().setBackground(share, "net/imyeyu/netdisk/res/share.png", 16, 5, 5);
		pathCtrlBtns.setBorder(false);
		pathCtrlBtns.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		pathCtrlBtns.addAll(prev, next, parent, refresh, share);
		pathCtrlPane.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).bottom());
		// 路径
		path = new TextField(File.separator);
		path.setStyle("-fx-background-insets: 0");
		// 搜索
		HBox searchPane = new HBox();
		searchField = new TextField();
		searchField.setStyle("-fx-background-insets: 0");
		searchField.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).horizontal());
		search = new Button();
		search.setPrefWidth(26);
		YeyuUtils.gui().setBackground(search, "net/imyeyu/netdisk/res/search.png", 16, 5, 5);
		searchPane.getChildren().addAll(searchField, search);
		
		pathCtrlPane.setLeft(pathCtrlBtns);
		pathCtrlPane.setCenter(path);
		pathCtrlPane.setRight(searchPane);
		
		// 文件列表
		fileList = new FileList(rb);
		fileList.setPadding(Insets.EMPTY);
		fileList.setStyle("-fx-background-insets: 0");
		
		// 提示信息栏
		tipsPane = new TipsPane(rb);
		tipsPane.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		
		filePane.setTop(pathCtrlPane);
		filePane.setCenter(fileList);
		filePane.setBottom(tipsPane);
		
		// 主面板
		BorderPane main = new BorderPane();
		main.setTop(topPane);
		main.setLeft(leftPane);
		main.setCenter(filePane);
		// 主窗体
		Scene scene = new Scene(main);
		setTitle("夜雨云盘");
		setMinWidth(780);
		setMinHeight(460);
		setWidth(780);
		setHeight(460);
		setScene(scene);
		show();
	}

	public Button getOpen() {
		return open;
	}

	public Button getPropertie() {
		return propertie;
	}

	public Button getUpload() {
		return upload;
	}

	public Button getDownload() {
		return download;
	}

	public Button getNewFolder() {
		return newFolder;
	}

	public Button getRename() {
		return rename;
	}

	public Button getMove() {
		return move;
	}

	public Button getCopy() {
		return copy;
	}

	public Button getDelete() {
		return delete;
	}

	public Button getIoList() {
		return ioList;
	}

	public Button getSetting() {
		return setting;
	}

	public Button getPhoto() {
		return photo;
	}

	public Button getDocument() {
		return document;
	}

	public Button getBackup() {
		return backup;
	}

	public Button getTomcatFile() {
		return tomcatFile;
	}

	public Button getPrev() {
		return prev;
	}

	public Button getNext() {
		return next;
	}

	public Button getParent() {
		return parent;
	}

	public Button getRefresh() {
		return refresh;
	}

	public Button getShare() {
		return share;
	}

	public Button getSearch() {
		return search;
	}

	public FileList getFileList() {
		return fileList;
	}

	public TipsPane getTipsPane() {
		return tipsPane;
	}

	public TextField getPath() {
		return path;
	}

	public TextField getSearchField() {
		return searchField;
	}

	public ServerState getCpu() {
		return cpu;
	}

	public ServerState getMemory() {
		return memory;
	}

	public ServerState getDisk() {
		return disk;
	}
}