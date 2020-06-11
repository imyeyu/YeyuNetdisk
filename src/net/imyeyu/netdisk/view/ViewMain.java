package net.imyeyu.netdisk.view;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.ui.ButtonBar;
import net.imyeyu.netdisk.ui.FileListTable;
import net.imyeyu.netdisk.ui.NavButton;
import net.imyeyu.netdisk.ui.ServerStateChart;
import net.imyeyu.netdisk.ui.ServerStateText;
import net.imyeyu.netdisk.ui.TipsPane;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.YeyuUtils;
import net.imyeyu.utils.gui.BorderX;
import net.imyeyu.utils.gui.SeparatorX;

public class ViewMain extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	
	private VBox navBtns;
	private Button open, propertie, sync, upload, download, zip, unzip, newFolder, rename, move, copy, delete, ioList, setting, photo, publicFile, prev, next, parent, refresh, root, toPublic, search;
	private TipsPane tipsPane;
	private MenuItem mOpen, mRefresh, mNewFolder, mNewText, mDownload, mZip, mUnzip, mMove, mCopy, mRename, mDelete, mProperties;
	private TextField path, searchField;
	private ButtonBar fileCtrlBtns;
	private ContextMenu menu;
	private FileListTable fileList;
	private ServerStateText disk;
	private ServerStateChart cpu, memory, netSpeed;
	
	public ViewMain() {
		BorderPane topPane = new BorderPane();
		// 文件控制面板
		FlowPane ctrlLeft = new FlowPane();
		// 文件读取
		ButtonBar fileLoadBtns = new ButtonBar();
		open = new Button(rbx.def("preview"));
		propertie = new Button(rbx.def("mainFileProperties"));
		fileLoadBtns.addAll(open, propertie);
		ctrlLeft.setPrefWidth(128);
		ctrlLeft.setAlignment(Pos.CENTER);
		ctrlLeft.getChildren().add(fileLoadBtns);
		
		// 文件操作
		FlowPane ctrlCenter = new FlowPane();
		fileCtrlBtns = new ButtonBar();
		sync = new Button(rbx.def("mainFileSync"));
		YeyuUtils.gui().setBg(sync, "net/imyeyu/netdisk/res/sync.png", 16, 5, 5);
		upload = new Button(rbx.def("mainFileUpload"));
		YeyuUtils.gui().setBg(upload, "net/imyeyu/netdisk/res/upload.png", 16, 5, 5);
		download = new Button(rbx.def("mainFileDownload"));
		YeyuUtils.gui().setBg(download, "net/imyeyu/netdisk/res/download.png", 16, 5, 5);
		zip = new Button(rbx.def("zip"));
		unzip = new Button(rbx.def("unZip"));
		newFolder = new Button(rbx.def("mainFileNewFolder"));
		rename = new Button(rbx.def("mainFileRename"));
		move = new Button(rbx.def("move"));
		copy = new Button(rbx.def("copy"));
		delete = new Button(rbx.def("mainFileDelete"));
		fileCtrlBtns.addAll(sync, upload, download, zip, unzip, newFolder, rename, move, copy, delete);
		ctrlCenter.getChildren().addAll(new SeparatorX(true, 10, 48), fileCtrlBtns);
		
		// 设置
		FlowPane ctrlRight = new FlowPane();
		ButtonBar settingBtn = new ButtonBar();
		ioList = new Button(rbx.def("mainIOList"));
		YeyuUtils.gui().setBg(ioList, "net/imyeyu/netdisk/res/ioList.png", 16, 5, 5);
		setting = new Button(rbx.def("mainSetting"));
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
		navBtns = new VBox();
		photo = new NavButton(rbx.def("mainPhoto"));
		publicFile = new NavButton(rbx.def("mainShare"));
		navBtns.getChildren().addAll(photo, publicFile);
		// 服务器状态面板
		VBox states = new VBox();
		Label server = new Label(rbx.def("mainServerState"), new ImageView("net/imyeyu/netdisk/res/state.png"));
		server.setPrefWidth(155);
		server.setPadding(new Insets(0, 0, 4, 6));
		cpu = new ServerStateChart(rbx.def("mainStateCPU"), 1.1, 90, 32);
		memory = new ServerStateChart(rbx.def("mainStateMemory"), 1.1, 90, 32);
		disk = new ServerStateText(rbx.def("mainStateDisk"), 96, 32);
		netSpeed = new ServerStateChart(rbx.def("mainStateNetSpeed"), 90, 32);
		states.setPadding(new Insets(4, 0, 0, 0));
		states.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		states.getChildren().addAll(server, cpu, memory, disk, netSpeed);
		
		leftPane.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		leftPane.setCenter(navBtns);
		leftPane.setBottom(states);
		
		// 文件列表面板
		BorderPane filePane = new BorderPane();
		
		// 路径操作面板
		BorderPane pathCtrlPane = new BorderPane();
		ButtonBar pathCtrlBtns = new ButtonBar();
		prev = new Button();
		prev.setPrefWidth(26);
		YeyuUtils.gui().setBg(prev, "net/imyeyu/netdisk/res/prev.png", 16, 5, 5);
		next = new Button();
		next.setPrefWidth(26);
		YeyuUtils.gui().setBg(next, "net/imyeyu/netdisk/res/next.png", 16, 5, 5);
		parent = new Button();
		parent.setPrefWidth(26);
		YeyuUtils.gui().setBg(parent, "net/imyeyu/netdisk/res/parent.png", 16, 5, 5);
		refresh = new Button();
		refresh.setPrefWidth(26);
		YeyuUtils.gui().setBg(refresh, "net/imyeyu/netdisk/res/refresh.png", 16, 5, 5);
		root = new Button();
		root.setPrefWidth(26);
		YeyuUtils.gui().setBg(root, "net/imyeyu/netdisk/res/home.png", 16, 5, 5);
		toPublic = new Button();
		toPublic.setPrefWidth(26);
		YeyuUtils.gui().setBg(toPublic, "net/imyeyu/netdisk/res/share.png", 16, 5, 5);
		pathCtrlBtns.setBorder(false);
		pathCtrlBtns.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).right());
		pathCtrlBtns.addAll(prev, next, parent, refresh, root, toPublic);
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
		YeyuUtils.gui().setBg(search, "net/imyeyu/netdisk/res/search.png", 16, 5, 5);
		searchPane.getChildren().addAll(searchField, search);
		
		pathCtrlPane.setLeft(pathCtrlBtns);
		pathCtrlPane.setCenter(path);
		pathCtrlPane.setRight(searchPane);
		
		// 文件列表
		fileList = new FileListTable(rbx);
		fileList.setPadding(Insets.EMPTY);
		fileList.setStyle("-fx-background-insets: 0");
		
		// 右键菜单
		String empty = "              ";
		menu = new ContextMenu();
		mOpen = new MenuItem(rbx.def("open"));
		mRefresh = new MenuItem(rbx.def("refresh"), new ImageView("net/imyeyu/netdisk/res/refresh.png"));
		mRefresh.setAccelerator(new KeyCodeCombination(KeyCode.F5));
		Menu newFile = new Menu(rbx.def("new"));
		mNewFolder = new MenuItem(rbx.def("folder"), new ImageView("net/imyeyu/netdisk/res/folder.png"));
		mNewFolder.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN, KeyCodeCombination.SHIFT_DOWN));
		mNewText = new MenuItem(rbx.def("txt"), new ImageView("net/imyeyu/netdisk/res/txt.png"));
		newFile.getItems().addAll(mNewFolder, mNewText);
		mDownload = new MenuItem(rbx.def("mainFileDownload"), new ImageView("net/imyeyu/netdisk/res/download.png"));
		mZip = new MenuItem(rbx.def("zip"), new ImageView("net/imyeyu/netdisk/res/7z.png"));
		mUnzip = new MenuItem(rbx.def("unZip"));
		mMove = new MenuItem(rbx.def("move"));
		mMove.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		mCopy = new MenuItem(rbx.def("copy"));
		mCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
		mRename = new MenuItem(rbx.def("mainFileRename") + empty);
		mRename.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
		mDelete = new MenuItem(rbx.def("mainFileDelete"), new ImageView("net/imyeyu/netdisk/res/delete.png"));
		mDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
		mProperties = new MenuItem(rbx.def("mainFileProperties"));
		mProperties.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN));

		menu.getItems().addAll(mOpen, mRefresh, newFile, new SeparatorMenuItem(), mDownload, mZip, mUnzip, new SeparatorMenuItem(), mMove, mCopy, mRename, mDelete, new SeparatorMenuItem(), mProperties);
		fileList.setContextMenu(menu);
		
		// 提示信息栏
		tipsPane = new TipsPane();
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
		scene.getStylesheets().add(this.getClass().getResource("/net/imyeyu/netdisk/res/serverState.css").toExternalForm());
		getIcons().add(new Image("net/imyeyu/netdisk/res/icon.png"));
		setTitle(rbx.def("title"));
		setMinWidth(880);
		setMinHeight(520);
		setWidth(880);
		setHeight(520);
		setScene(scene);
		show();
		
		fileList.requestFocus();
	}

	public Button getOpen() {
		return open;
	}

	public Button getPropertie() {
		return propertie;
	}
	
	public ButtonBar getFileCtrlBtns() {
		return fileCtrlBtns;
	}
	
	public Button getSync() {
		return sync;
	}

	public Button getUpload() {
		return upload;
	}

	public Button getDownload() {
		return download;
	}
	
	public Button getZip() {
		return zip;
	}
	
	public Button getUnZip() {
		return unzip;
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

	public VBox getNavBtns() {
		return navBtns;
	}

	public Button getPhoto() {
		return photo;
	}

	public Button getPublicFile() {
		return publicFile;
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
	
	public Button getRoot() {
		return root;
	}
	
	public Button getToPublic() {
		return toPublic;
	}

	public Button getSearch() {
		return search;
	}

	public FileListTable getFileList() {
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

	public ServerStateChart getCpu() {
		return cpu;
	}

	public ServerStateChart getMemory() {
		return memory;
	}

	public ServerStateText getDisk() {
		return disk;
	}
	
	public ServerStateChart getNetSpeed() {
		return netSpeed;
	}
	
	public ContextMenu getMenu() {
		return menu;
	}

	public MenuItem getmOpen() {
		return mOpen;
	}
	
	public MenuItem getmRefresh() {
		return mRefresh;
	}
	
	public MenuItem getmNewFolder() {
		return mNewFolder;
	}
	
	public MenuItem getmNewText() {
		return mNewText;
	}

	public MenuItem getmDownload() {
		return mDownload;
	}

	public MenuItem getmZip() {
		return mZip;
	}

	public MenuItem getmUnZip() {
		return mUnzip;
	}

	public MenuItem getmMove() {
		return mMove;
	}

	public MenuItem getmCopy() {
		return mCopy;
	}

	public MenuItem getmRename() {
		return mRename;
	}

	public MenuItem getmDelete() {
		return mDelete;
	}

	public MenuItem getmProperties() {
		return mProperties;
	}
}