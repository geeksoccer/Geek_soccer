package com.excelente.geek_soccer.model;

import java.io.Serializable;

public class TeamModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String TEAM_ID = "team_id";
	public static final String TEAM_NAME = "team_name";
	public static final String TEAM_NAME_TH = "team_name_th";
	public static final String TEAM_LEAGUE = "team_league";
	public static final String TEAM_IMAGE = "team_logo";
	public static final String TEAM_COLOR = "team_color";
	public static final String TEAM_TEXT_COLOR = "team_text_color";
	public static final String TEAM_NAME_FIND = "team_name_find"; 
	public static final String TEAM_SHORT_NAME = "team_short_name"; 
	
	private int teamId;
	private String teamName;
	private String teamLeague;
	private String teamNameTH;
	private String teamNameFind;
	private String teamShortName;
	private String teamImage;
	private String teamTextColor;
	private String teamColor;
	
	public TeamModel(int teamId, String teamName, String teamImage, String teamColor) {
		this.teamId = teamId;
		this.teamName = teamName;
		this.teamImage = teamImage;
		this.teamColor = teamColor;
	}

	public TeamModel() {
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamImage() {
		return teamImage;
	}

	public void setTeamImage(String teamImage) {
		this.teamImage = teamImage;
	}

	public String getTeamColor() {
		return teamColor;
	}

	public void setTeamColor(String teamColor) {
		this.teamColor = teamColor;
	}

	public String getTeamNameTH() {
		return teamNameTH;
	}

	public void setTeamNameTH(String teamNameTH) {
		this.teamNameTH = teamNameTH;
	}

	public String getTeamNameFind() {
		return teamNameFind;
	}

	public void setTeamNameFind(String teamNameFind) {
		this.teamNameFind = teamNameFind;
	}

	public String getTeamShortName() {
		return teamShortName;
	}

	public void setTeamShortName(String teamShortName) {
		this.teamShortName = teamShortName;
	}

	public String getTeamLeague() {
		return teamLeague;
	}

	public void setTeamLeague(String teamLeague) {
		this.teamLeague = teamLeague;
	}

	public String getTeamTextColor() {
		return teamTextColor;
	}

	public void setTeamTextColor(String teamTextColor) {
		this.teamTextColor = teamTextColor;
	}
}
