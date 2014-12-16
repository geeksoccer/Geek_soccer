package com.excelente.geek_soccer.model;

import java.util.List;

public class LeagueModel {
	
	private String id;
	private String name;
	private String nameTH;
	private List<TeamModel> teams;
	private String image;
	
	public LeagueModel(String id, String name, List<TeamModel> teams, String image ) {
		this.id = id;
		this.name = name;
		this.teams = teams;
		this.image = image;
	}
	
	public LeagueModel() {
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<TeamModel> getTeams() {
		return teams;
	}
	public void setTeams(List<TeamModel> teams) {
		this.teams = teams;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public String getNameTH() {
		return nameTH;
	}

	public void setNameTH(String nameTH) {
		this.nameTH = nameTH;
	}

}
