package net.imyeyu.netdisk.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.imyeyu.utils.gui.BorderX;

public class ViewTextEditor extends Stage {
	
//	private ResourceBundle rb = Entrance.getRb();
	
	private Label tips;
	private String path;
	private TextArea line, textArea;
	
	private Menu fontSizeItems, fontFamilyItems;
	private MenuItem[] fontSize = new MenuItem[6];
	private MenuItem[] fontFamily = new MenuItem[5];
	private MenuItem reload, saveCloud, saveCache, saveSync, saveAs, close, cancel, redo, selectAll, copy, paste, cut;

	public ViewTextEditor(String path) {
		this.path = path;
		
		BorderPane main = new BorderPane();
		
		// 菜单
		MenuBar menu = new MenuBar();
		
		Menu file = new Menu("文件");
		reload = new MenuItem("重载云端文件", new ImageView("net/imyeyu/netdisk/res/reload.png"));
		saveCloud = new MenuItem("保存到云端", new ImageView("net/imyeyu/netdisk/res/save.png"));
		saveCloud.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
		saveCache = new MenuItem("缓存到本地");
		saveSync = new MenuItem("同步保存");
		saveSync.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		saveAs = new MenuItem("另存为..");
		close = new MenuItem("关闭", new ImageView("net/imyeyu/netdisk/res/exit.png"));
		close.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
		file.getItems().addAll(reload, new SeparatorMenuItem(), saveCloud, saveCache, saveSync, saveAs, new SeparatorMenuItem(), close);
		
		Menu edit = new Menu("编辑");
		cancel = new MenuItem("撤销", new ImageView("net/imyeyu/netdisk/res/undo.png"));
		cancel.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
		redo = new MenuItem("重做", new ImageView("net/imyeyu/netdisk/res/redo.png"));
		redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
		selectAll = new MenuItem("全选", new ImageView("net/imyeyu/netdisk/res/selectAll.png"));
		selectAll.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
		copy = new MenuItem("复制", new ImageView("net/imyeyu/netdisk/res/copyText.png"));
		copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
		paste = new MenuItem("粘贴", new ImageView("net/imyeyu/netdisk/res/paste.png"));
		paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
		cut = new MenuItem("剪切", new ImageView("net/imyeyu/netdisk/res/cut.png"));
		cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		edit.getItems().addAll(cancel, redo, selectAll, new SeparatorMenuItem(), copy, paste, cut);
		
		Menu font = new Menu("字体");
		
		String[] fontSizeText = {"12", "14", "16", "18", "20", "22"};
		fontSizeItems = new Menu("字号", new ImageView("net/imyeyu/netdisk/res/fontSize.png"));
		for (int i = 0; i < fontSizeText.length; i++) {
			fontSize[i] = new MenuItem(fontSizeText[i]);
			fontSize[i].setStyle("-fx-font-size: " + fontSizeText[i]);
			fontSizeItems.getItems().add(fontSize[i]);
		}
		
		String[] fontFamilyText = {"System", "Arial", "Calibri", "Consolas", "Microsoft YaHei"};
		fontFamilyItems = new Menu("字体", new ImageView("net/imyeyu/netdisk/res/fontFamily.png"));
		for (int i = 0; i < fontFamilyText.length; i++) {
			fontFamily[i] = new MenuItem(fontFamilyText[i]);
			fontFamily[i].setStyle("-fx-font-family: " + fontFamilyText[i]);
			fontFamilyItems.getItems().add(fontFamily[i]);
		}
		
		font.getItems().addAll(fontSizeItems, fontFamilyItems);
		menu.getMenus().addAll(file, edit, font);
		
		// 文本编辑区
		BorderPane textBox = new BorderPane();
		AnchorPane lineBox = new AnchorPane();
		line = new TextArea();
		line.setEditable(false);
		line.setId("line");
		lineBox.setPrefWidth(52);
		AnchorPane.setTopAnchor(line, 0d);
		AnchorPane.setLeftAnchor(line, 0d);
		AnchorPane.setRightAnchor(line, -16d);
		AnchorPane.setBottomAnchor(line, 0d);
		lineBox.getChildren().add(line);
		textArea = new TextArea();
		textArea.setBackground(Background.EMPTY);
		textArea.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).left());
		
		textBox.setLeft(lineBox);
		textBox.setCenter(textArea);
		// 提示
		HBox tipsBox = new HBox();
		tips = new Label("正在获取文本数据..");
		tipsBox.setPadding(new Insets(4, 6, 4, 6));
		tipsBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		tipsBox.getChildren().add(tips);
		
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		main.setTop(menu);
		main.setCenter(textBox);
		main.setBottom(tipsBox);
		
		Scene scene = new Scene(main);
		scene.getStylesheets().add(this.getClass().getResource("/net/imyeyu/netdisk/res/textEditor.css").toExternalForm());
		setScene(scene);
		getIcons().add(new Image("net/imyeyu/netdisk/res/textEditor.png"));
		setTitle("文本文件编辑器 - " + path);
		setMinWidth(320);
		setMinHeight(220);
		setWidth(620);
		setHeight(460);
		show();
	}

	public Label getTips() {
		return tips;
	}

	public String getPath() {
		return path;
	}

	public TextArea getLine() {
		return line;
	}

	public TextArea getTextArea() {
		return textArea;
	}

	public Menu getFontSizeItems() {
		return fontSizeItems;
	}

	public Menu getFontFamilyItems() {
		return fontFamilyItems;
	}

	public MenuItem[] getFontSize() {
		return fontSize;
	}

	public MenuItem[] getFontFamily() {
		return fontFamily;
	}
	
	public MenuItem getReload() {
		return reload;
	}

	public MenuItem getSaveCloud() {
		return saveCloud;
	}

	public MenuItem getSaveCache() {
		return saveCache;
	}

	public MenuItem getSaveSync() {
		return saveSync;
	}

	public MenuItem getSaveAs() {
		return saveAs;
	}

	public MenuItem getClose() {
		return close;
	}
	
	public MenuItem getCancel() {
		return cancel;
	}

	public MenuItem getRedo() {
		return redo;
	}

	public MenuItem getSelectAll() {
		return selectAll;
	}

	public MenuItem getCopy() {
		return copy;
	}

	public MenuItem getPaste() {
		return paste;
	}

	public MenuItem getCut() {
		return cut;
	}
}