package net.imyeyu.netdisk.util;

import javafx.scene.image.Image;

public class FileFormat {
	
	public static boolean isTextFile(String format) {
		switch (format.toLowerCase()) {
			case "js":
			case "py":
			case "md":
			case "log":
			case "txt":
			case "ini":
			case "cfg":
			case "css":
			case "sql":
			case "xml":
			case "yml":
			case "php":
			case "bat":
			case "cmd":
			case "url":
			case "htm":
			case "java":
			case "json":
			case "lang":
			case "html":
			case "config":
			case "properties":
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isImg(String format) {
		switch (format.toLowerCase()) {
			case "jpg":
			case "jpeg":
			case "png":
			case "gif":
			case "bmp":
			case "ico":
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isMP4(String format) {
		return format.toLowerCase().equals("mp4");
	}

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
			case "gif":
			case "bmp":
			case "psd":
				icon = "photo";
				break;
			case "js":
			case "py":
			case "md":
			case "ini":
			case "cfg":
			case "css":
			case "sql":
			case "xml":
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
