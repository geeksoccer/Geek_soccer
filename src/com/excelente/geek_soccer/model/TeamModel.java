package com.excelente.geek_soccer.model;

public class TeamModel {
	
	String teamName;
	int teamLogo;
	int teamColor;
	
	public TeamModel(String teamName, int teamLogo, int teamColor) {
		this.teamName = teamName;
		this.teamLogo = teamLogo;
		this.teamColor = teamColor;
	}
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getTeamLogo() {
		return teamLogo;
	}
	public void setTeamLogo(int teamLogo) {
		this.teamLogo = teamLogo;
	}
	public int getTeamColor() {
		return teamColor;
	}
	public void setTeamColor(int teamColor) {
		this.teamColor = teamColor;
	}
}
