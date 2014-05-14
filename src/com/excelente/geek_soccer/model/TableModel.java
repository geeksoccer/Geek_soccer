package com.excelente.geek_soccer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_ID = "table_id";
	public static final String TABLE_LEAGUE = "table_league";
	public static final String TABLE_TYPE = "table_type";
	public static final String TABLE_CREATE_TIME = "table_create_time";
	public static final String TABLE_UPDATE_TIME = "table_update_time";
	public static final String TABLE_SEQ = "table_seq";
	public static final String TABLE_TEAM = "table_team";
	public static final String TABLE_TEAM_IAMGE = "table_team_image";
	public static final String TABLE_MATCH = "table_match";
	public static final String TABLE_WIN = "table_win";
	public static final String TABLE_DRAW = "table_draw";
	public static final String TABLE_LOSE = "table_lose";
	public static final String TABLE_GET_GOAL = "table_get_goal";
	public static final String TABLE_LOSE_GOAL = "table_lose_goal";
	public static final String TABLE_RESULT_GOAL = "table_result_goal";
	public static final String TABLE_MARK = "table_mark";
	public static final String TABLE_STATUS = "table_status";
	
	int tableId;
	String tableLeague;
	String tableType;
	String tableCreateTime;
	String tableUpdateTime;
	int tableSeq;
	String tableTeam;
	String tableTeamImage;
	int tableMatch;
	int tableWin;
	int tableDraw;
	int tableLose;
	int tableGetGoal;
	int tableLoseGoal;
	int tableResultGoal;
	int tableMark;
	String tableStatus;
	
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getTableLeague() {
		return tableLeague;
	}
	public void setTableLeague(String tableLeague) {
		this.tableLeague = tableLeague;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getTableCreateTime() {
		return tableCreateTime;
	}
	public void setTableCreateTime(String tableCreateTime) {
		this.tableCreateTime = tableCreateTime;
	}
	public String getTableUpdateTime() {
		return tableUpdateTime;
	}
	public void setTableUpdateTime(String tableUpdateTime) {
		this.tableUpdateTime = tableUpdateTime;
	}
	public int getTableSeq() {
		return tableSeq;
	}
	public void setTableSeq(int tableSeq) {
		this.tableSeq = tableSeq;
	}
	public String getTableTeam() {
		return tableTeam;
	}
	public void setTableTeam(String tableTeam) {
		this.tableTeam = tableTeam;
	}
	public String getTableTeamImage() {
		return tableTeamImage;
	}
	public void setTableTeamImage(String tableTeamImage) {
		this.tableTeamImage = tableTeamImage;
	}
	public int getTableMatch() {
		return tableMatch;
	}
	public void setTableMatch(int tableMatch) {
		this.tableMatch = tableMatch;
	}
	public int getTableWin() {
		return tableWin;
	}
	public void setTableWin(int tableWin) {
		this.tableWin = tableWin;
	}
	public int getTableDraw() {
		return tableDraw;
	}
	public void setTableDraw(int tableDraw) {
		this.tableDraw = tableDraw;
	}
	public int getTableLose() {
		return tableLose;
	}
	public void setTableLose(int tableLose) {
		this.tableLose = tableLose;
	}
	public int getTableGetGoal() {
		return tableGetGoal;
	}
	public void setTableGetGoal(int tableGetGoal) {
		this.tableGetGoal = tableGetGoal;
	}
	public int getTableLoseGoal() {
		return tableLoseGoal;
	}
	public void setTableLoseGoal(int tableLoseGoal) {
		this.tableLoseGoal = tableLoseGoal;
	}
	public int getTableResultGoal() {
		return tableResultGoal;
	}
	public void setTableResultGoal(int tableResultGoal) {
		this.tableResultGoal = tableResultGoal;
	}
	public int getTableMark() {
		return tableMark;
	}
	public void setTableMark(int tableMark) {
		this.tableMark = tableMark;
	}
	
	public String getTableStatus() {
		return tableStatus;
	}
	public void setTableStatus(String tableStatus) {
		this.tableStatus = tableStatus;
	}
	public static List<TableModel> convertTableStrToList(String result) {  
		
		List<TableModel> tableList = new ArrayList<TableModel>();
		 
		try {
			
			JSONArray tableJsonArr = new JSONArray(result); 
			for(int i=0; i<tableJsonArr.length(); i++){
				JSONObject tableObj = (JSONObject) tableJsonArr.get(i); 
				
				TableModel table = new TableModel(); 
				table.setTableId(tableObj.getInt(TABLE_ID)); 
				table.setTableLeague(tableObj.getString(TABLE_LEAGUE));
				table.setTableType(tableObj.getString(TABLE_TYPE));
				table.setTableCreateTime(tableObj.getString(TABLE_CREATE_TIME));
				table.setTableUpdateTime(tableObj.getString(TABLE_UPDATE_TIME));
				table.setTableSeq(tableObj.getInt(TABLE_SEQ));
				table.setTableTeam(tableObj.getString(TABLE_TEAM).replace("&nbsp;", ""));
				table.setTableTeamImage(tableObj.getString(TABLE_TEAM_IAMGE));
				table.setTableMatch(tableObj.getInt(TABLE_MATCH));
				table.setTableWin(tableObj.getInt(TABLE_WIN));
				table.setTableDraw(tableObj.getInt(TABLE_DRAW));
				table.setTableLose(tableObj.getInt(TABLE_LOSE));
				table.setTableGetGoal(tableObj.getInt(TABLE_GET_GOAL));
				table.setTableLoseGoal(tableObj.getInt(TABLE_LOSE_GOAL));
				table.setTableResultGoal(tableObj.getInt(TABLE_RESULT_GOAL));
				table.setTableMark(tableObj.getInt(TABLE_MARK));
				table.setTableStatus(tableObj.getString(TABLE_STATUS));
				
				tableList.add(table);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return tableList;
		}
		
		return tableList;
	}

}
