package com.excelente.geek_soccer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.excelente.geek_soccer.R;
import com.excelente.geek_soccer.SessionManager;

public class FixturesGroupLists {
	
	private List<FixturesGroupList> fixturesGroupLists;
	Activity act; 
	JSONArray dataJson;
	private List<FixturesModel> fixturesList;
	private int indexNextMatch;
	private int indexNextMatchGroup;
	private String fixturesSeason;
	
	public FixturesGroupLists(Activity act, JSONArray response) {
		this.act = act;
		this.dataJson = response;
		this.fixturesGroupLists = new ArrayList<FixturesGroupList>();
		this.fixturesList = new ArrayList<FixturesModel>();
		this.indexNextMatch = -1;
		this.setFixturesSeason("");
	}
	
	@SuppressWarnings("deprecation")
	public FixturesGroupLists build() {
		
		if(dataJson == null || dataJson.length() == 0){
			return this;
		}
		
		try {
			
			String lang = SessionManager.getLang(act);
			SimpleDateFormat sdfSeason = new SimpleDateFormat("yyyy", new Locale(lang));
			SimpleDateFormat sdfMatchDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale(lang));
			SimpleDateFormat sdfMatchDisplay = new SimpleDateFormat("EEE dd MMM yy, HH:mm", new Locale(lang));
			SimpleDateFormat sdfMatchDisplayFull = new SimpleDateFormat("EEEEE dd MMMMM yy, HH:mm", new Locale(lang));
			SimpleDateFormat sdfMatchTimeDisplay = new SimpleDateFormat("HH:mm", new Locale(lang));
			SimpleDateFormat sdfHead = new SimpleDateFormat("MMMMM yyyy", new Locale(lang));
			
			sortFixtures(lang, sdfMatchDateTime);
			Date now = new Date();

			int i = 0;
			int lenght = fixturesList.size();
			boolean hasNextMatch = false; 
			for (FixturesModel fixtures : fixturesList) {
				Date matchDateTime = sdfMatchDateTime.parse(fixtures.getMatchDate() + " " + fixtures.getMatchTime());
				String headTitle = sdfHead.format(matchDateTime);  
	
		    	fixtures.setMatchDateDisplay(sdfMatchDisplay.format(matchDateTime));
		    	fixtures.setMatchTime(sdfMatchTimeDisplay.format(matchDateTime));

		    	if(i==0){
		    		FixturesModel fixturesLst = fixturesList.get(lenght-1);
		    		Date dLastMatch = sdfMatchDateTime.parse(fixturesLst.getMatchDate() + " " + fixturesLst.getMatchTime());
		    		String seasonResource = act.getResources().getString(R.string.fixtures_season);
		    		setFixturesSeason(seasonResource + " " + sdfSeason.format(matchDateTime) + "-" + sdfSeason.format(dLastMatch));
		    	}
		    	
		    	int indexGroup = hasFixturesGroup(headTitle, fixturesGroupLists);
		    	if(indexGroup == -1){
		    		FixturesGroupList fixturesGroupList = new FixturesGroupList(headTitle);
		    		fixturesGroupLists.add(fixturesGroupList);
		    		
		    		indexGroup = fixturesGroupLists.size()-1;
		    		
		    		if(now.getMonth() == matchDateTime.getMonth()){
		    			setIndexNextMatchGroup(indexGroup);
		    		}
		    		
				}
		    	
		    	FixturesGroupList fixturesGroupList = fixturesGroupLists.get(indexGroup);
				
				if(!hasNextMatch && i < fixturesList.size()-1 && fixturesList.get(i).getScore().contains("v") && !fixturesList.get(i-1).getScore().contains("v")){
		    		setIndexNextMatch(fixturesGroupLists.get(indexGroup).children.size());
		    		fixtures.setNextMatch(true);
		    		fixtures.setMatchDateDisplay(sdfMatchDisplayFull.format(matchDateTime));
		    		hasNextMatch = true;
		    	}
				
				fixturesGroupList.children.add(fixtures);

		    	i++;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	private void sortFixtures(String lang, SimpleDateFormat sdfMatchDateTime) throws JSONException, ParseException {

		for (int i = 0; i < dataJson.length(); i++){
			JSONObject dt = dataJson.getJSONObject(i);

			FixturesModel fixtures = new FixturesModel();
	    	fixtures.setAwayImg(dt.getString(FixturesModel.FIXTURES_AWAY_IMG));
	    	fixtures.setAwayName(dt.getString(FixturesModel.FIXTURES_AWAY_NAME));
	    	fixtures.setHomeImg(dt.getString(FixturesModel.FIXTURES_HOME_IMG));
	    	fixtures.setHomeName(dt.getString(FixturesModel.FIXTURES_HOME_NAME));
	    	fixtures.setId(dt.getString(FixturesModel.FIXTURES_ID)); 
	    	fixtures.setLink(dt.getString(FixturesModel.FIXTURES_LINK));
	    	fixtures.setMatchTime(dt.getString(FixturesModel.FIXTURES_MATCH_TIME));
	    	fixtures.setMatchDate(dt.getString(FixturesModel.FIXTURES_MATCH_DATE));
	    	fixtures.setMatchType(dt.getString(FixturesModel.FIXTURES_MATCH_TYPE));
	    	fixtures.setScore(dt.getString(FixturesModel.FIXTURES_SCORE));
	    	fixturesList.add(fixtures);
		}
		
		if(fixturesList.isEmpty()){
			return;
		}
		
		for (int i = 0; i < fixturesList.size(); i++){
			for (int j = 0; j < fixturesList.size()-1; j++) {
				FixturesModel fixturesI = fixturesList.get(j+1);
				FixturesModel fixturesJ = fixturesList.get(j);
				
				Date matchDateTimeI = sdfMatchDateTime.parse(fixturesI.getMatchDate() + " " + fixturesI.getMatchTime());
				Date matchDateTimeJ = sdfMatchDateTime.parse(fixturesJ.getMatchDate() + " " + fixturesJ.getMatchTime());
				
				if(matchDateTimeI.before(matchDateTimeJ)){
					fixturesList.set(j+1, fixturesJ);
					fixturesList.set(j, fixturesI);
				}
				
			}
		}
		
	}

	public List<FixturesGroupList> getFixturesGroupLists() {
		return fixturesGroupLists;
	} 
	
	private int hasFixturesGroup(String headTitle, List<FixturesGroupList> fixturesGroupLists) {
		for (int i=0; i< fixturesGroupLists.size(); i++) {
			FixturesGroupList fixturesGroupList = fixturesGroupLists.get(i);
			if(fixturesGroupList.headerTitle.equals(headTitle)){
				return i;
    		}
		}
		return -1;
	}

	public int getIndexNextMatch() {
		return indexNextMatch;
	}

	public void setIndexNextMatch(int indexNextMatch) {
		this.indexNextMatch = indexNextMatch;
	}

	public int getIndexNextMatchGroup() {
		return indexNextMatchGroup;
	}

	public void setIndexNextMatchGroup(int indexNextMatchGroup) {
		this.indexNextMatchGroup = indexNextMatchGroup;
	}

	public String getFixturesSeason() {
		return fixturesSeason;
	}

	public void setFixturesSeason(String fixturesSeason) {
		this.fixturesSeason = fixturesSeason;
	}
	
}
