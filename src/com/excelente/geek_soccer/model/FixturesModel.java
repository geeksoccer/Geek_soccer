package com.excelente.geek_soccer.model;

public class FixturesModel {
	
	public static final String FIXTURES_ID = "id";
	public static final String FIXTURES_MATCH_TYPE = "ln";
	public static final String FIXTURES_MATCH_TIME = "tp";
	public static final String FIXTURES_HOME_IMG = "hl";
	public static final String FIXTURES_HOME_NAME = "ht";
	public static final String FIXTURES_AWAY_IMG = "al";
	public static final String FIXTURES_AWAY_NAME = "at";
	public static final String FIXTURES_SCORE = "sc"; 
	public static final String FIXTURES_LINK = "lk";
	public static final String FIXTURES_MATCH_DATE = "dt";
	public static final String FIXTURES_CREDIT = "mlk";
	
	private String id;
	private String matchType;
	private String matchDate;
	private String matchTime;
	private String homeImg;
	private String homeName;
	private String awayImg;
	private String awayName;
	private String score;
	private String link;
	private String credit;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMatchType() {
		return matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}
	public String getMatchDate() {
		return matchDate;
	}
	public void setMatchDate(String matchDate) {
		this.matchDate = matchDate;
	}
	public String getMatchTime() {
		return matchTime;
	}
	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}
	public String getHomeImg() {
		return homeImg;
	}
	public void setHomeImg(String homeImg) {
		this.homeImg = homeImg;
	}
	public String getHomeName() {
		return homeName;
	}
	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}
	public String getAwayImg() {
		return awayImg;
	}
	public void setAwayImg(String awayImg) {
		this.awayImg = awayImg;
	}
	public String getAwayName() {
		return awayName;
	}
	public void setAwayName(String awayName) {
		this.awayName = awayName;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}

}
