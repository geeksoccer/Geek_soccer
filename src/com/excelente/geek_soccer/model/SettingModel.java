package com.excelente.geek_soccer.model;

public class SettingModel {

	String tag;
	String topic;
	String detail;
	int showCheckBox;
	String val;
	String detail2;

	public SettingModel(String tag, String topic, String detail, String val, int showCheckBox, String detail2) {
		this.tag = tag;
		this.topic = topic;
		this.detail = detail;
		this.detail2 = detail2;
		this.val = val;
		this.showCheckBox = showCheckBox;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public int getShowCheckBox() {
		return showCheckBox;
	}

	public void setShowCheckBox(int showCheckBox) {
		this.showCheckBox = showCheckBox;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
}
