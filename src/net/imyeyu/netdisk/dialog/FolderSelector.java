package net.imyeyu.netdisk.dialog;

import java.io.File;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.imyeyu.netdisk.Entrance;
import net.imyeyu.netdisk.request.PublicRequest;
import net.imyeyu.netdisk.util.FileTPService;
import net.imyeyu.utils.ResourceBundleX;
import net.imyeyu.utils.gui.BorderX;

public class FolderSelector extends Stage {
	
	private ResourceBundleX rbx = Entrance.getRb();
	private SimpleBooleanProperty isFinish = new SimpleBooleanProperty(false);
	
	private Label header;
	private Button confirm, cancel;
	private TreeView<String> treeView;
	private TreeItem<String> root;
	
	private String flag;
	private List<String> list;

	public FolderSelector(String title, List<String> list, String flag) {
		this.list = list;
		this.flag = flag;
		
		BorderPane main = new BorderPane();
		header = new Label(rbx.def("fileSelectTypeFolder"));
		header.setPadding(new Insets(0, 0, 8, 0));
		
		treeView = new TreeView<>();
		treeView.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).exBottom());
		treeView.setPadding(Insets.EMPTY);
		treeView.setStyle("-fx-background-insets: 0");
		root = new TreeItem<>(rbx.def("root"));
		root.setExpanded(true);
		treeView.setRoot(root);
		
		HBox btnBox = new HBox();
		confirm = new Button(rbx.def("confirm"));
		cancel = new Button(rbx.def("cancel"));
		btnBox.setPadding(new Insets(8, 0, 0, 0));
		btnBox.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		btnBox.setSpacing(8);
		btnBox.setAlignment(Pos.CENTER_RIGHT);
		btnBox.getChildren().addAll(confirm, cancel);
		
		main.setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		main.setPadding(new Insets(12, 12, 8, 12));
		main.setTop(header);
		main.setCenter(treeView);
		main.setBottom(btnBox);
		
		Scene scene = new Scene(main);
		getIcons().add(new Image("net/imyeyu/netdisk/res/" + flag + ".png"));
		setScene(scene);
		setTitle(title);
		setMinWidth(280);
		setMinHeight(340);
		setWidth(280);
		setHeight(340);
		initModality(Modality.APPLICATION_MODAL);
		show();

		getFolderList("\\", root);
		
		// 展开树结构
		root.addEventHandler(TreeItem.<String>branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<String>>() {
			public void handle(TreeModificationEvent<String> event) {
				TreeItem<String> item = event.getTreeItem();
				StringBuffer path = new StringBuffer();
				while (item.getParent() != null) {
					path.insert(0, item.getValue() + File.separator);
					item = item.getParent();
				}
				path.insert(0, "\\");
				getFolderList(path.toString(), event.getTreeItem());
			}
		});
		// 确定
		confirm.setOnAction(event -> confirm());
		treeView.setOnKeyReleased(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) confirm();
		});
		
		cancel.setOnAction(event -> close());
	}
	
	private void confirm() {
		treeView.setDisable(true);
		confirm.setDisable(true);
		cancel.setDisable(true);
		
		TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
		StringBuffer toPath = new StringBuffer();
		while (item.getParent() != null) {
			toPath.insert(0, item.getValue() + File.separator);
			item = item.getParent();
		}
		toPath.insert(0, File.separator);
		FileTPService request = new FileTPService(toPath.substring(0, toPath.length() - 1), list, flag);
		header.textProperty().bind(request.messageProperty());
		request.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.equals("finish")) {
				isFinish.set(true);
				close();
			}
		});
		request.start();
	}
	
	// 获取文件夹
	private void getFolderList(String path, TreeItem<String> parent) {
		PublicRequest request = new PublicRequest("folder", path);
		request.messageProperty().addListener((tmp, oldValue, newValue) -> {
			if (!newValue.equals("")) {
				JsonParser jp = new JsonParser();
				JsonArray ja = (JsonArray) jp.parse(newValue), subJa;
				JsonObject jo, subJo;
				TreeItem<String> item, subItem;
				parent.getChildren().clear();
				for (int i = 0; i < ja.size(); i++) {
					jo = ja.get(i).getAsJsonObject();
					item = new TreeItem<>(jo.get("name").getAsString());
					subJa = jo.get("sub").getAsJsonArray();
					for (int j = 0; j < subJa.size(); j++) {
						subJo = subJa.get(j).getAsJsonObject();
						subItem = new TreeItem<>(subJo.get("name").getAsString());
						item.getChildren().add(subItem);
					}
					parent.getChildren().add(item);
				}
			}
		});
		request.start();
	}
	
	public SimpleBooleanProperty isFinish() {
		return isFinish;
	}
}