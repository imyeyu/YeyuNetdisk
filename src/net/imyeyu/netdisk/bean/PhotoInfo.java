package net.imyeyu.netdisk.bean;

public class PhotoInfo {

	private String name;
	private String pos;
	private String size;
	private String width;       // 宽
	private String height;      // 高
	private String date;        // 拍摄日期
	private String make;        // 相机厂商
	private String camera;      // 相机型号
	private String os;          // 系统
	private String aperture;    // 光圈
	private String toe;         // 曝光时间
	private String iso;         // ISO 速度
	private String focalLength; // 焦距
	private String lng;         // 经度
	private String lat;         // 纬度
	private String alt;         // 海拔

	public PhotoInfo() {
		this.name = "";
		this.pos = "";
		this.size = "";
		this.width = "";
		this.height = "";
		this.date = "";
		this.make = "";
		this.camera = "";
		this.os = "";
		this.aperture = "";
		this.toe = "";
		this.iso = "";
		this.focalLength = "";
		this.lng = "";
		this.lat = "";
		this.alt = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getAperture() {
		return aperture;
	}

	public void setAperture(String aperture) {
		this.aperture = aperture;
	}

	public String getToe() {
		return toe;
	}

	public void setToe(String toe) {
		this.toe = toe;
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
}