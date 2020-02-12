package net.imyeyu.netdisk.util;

import javafx.scene.image.Image;

public class FileIcon {

	public static Image getImage(String format) {
		String icon;
		switch (format.toLowerCase()) {
			case "folder":
				icon = "folder";
				break;
			case "log":
			case "txt":
				icon = "txt";
				break;
			case "jpg":
			case "jpeg":
			case "png":
			case "ico":
			case "psd":
				icon = "photo";
				break;
			case "js":
			case "py":
			case "ini":
			case "cfg":
			case "css":
			case "sql":
			case "yml":
			case "php":
			case "java":
			case "json":
			case "lang":
			case "config":
			case "properties":
				icon = "dev";
				break;
			case "bat":
			case "cmd":
				icon = "script";
				break;
			case "7z":
			case "zip":
			case "rar":
			case "iso":
				icon = "7z";
				break;
			case "exe":
				icon = "exe";
				break;
			case "jar":
				icon = "jar";
				break;
			case "mp4":
			case "avi":
			case "mov":
				icon = "video";
				break;
			case "mp3":
			case "ape":
			case "wav":
			case "m4a":
			case "flac":
				icon = "voice";
				break;
			case "pdf":
				icon = "pdf";
			case "doc":
			case "docx":
				icon = "word";
				break;
			case "ppt":
			case "pptx":
				icon = "ppt";
				break;
			case "xls":
			case "xlsx":
				icon = "excel";
				break;
			case "url":
			case "htm":
			case "html":
				icon = "web";
				break;
			case "db":
			case "dll":
				icon = "system";
				break;
			default:
				icon = "unknown";
				break;
		}
		return new Image("net/imyeyu/netdisk/res/" + icon + ".png");
	}
}
