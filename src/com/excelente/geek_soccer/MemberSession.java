package com.excelente.geek_soccer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.excelente.geek_soccer.model.MemberModel;

public class MemberSession{
	
	final public static String MEMBER_SHAREPREFERENCE = "MEMBER_SHAREPREFERENCE";
	
	static MemberModel member;
	static boolean globalNews = false;
	
	public static MemberModel getMember() {
		return member;
	}
	
	public static void setMember(Context context, MemberModel member) {
		if(member != null){
			SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		
			Editor editMember = memberFile.edit();
			editMember.putInt(MemberModel.MEMBER_UID, member.getUid());
			editMember.putString(MemberModel.MEMBER_USER, member.getUser());
			editMember.putInt(MemberModel.MEMBER_TEAM_ID, member.getTeamId());
			editMember.putString(MemberModel.MEMBER_TOKEN, member.getToken());
			editMember.putString(MemberModel.MEMBER_BIRTHDAY, member.getBirthday());
			editMember.putInt(MemberModel.MEMBER_GENDER, member.getGender());
			editMember.putString(MemberModel.MEMBER_NICKNAME, member.getNickname());
			editMember.putString(MemberModel.MEMBER_PHOTO, member.getPhoto());
			editMember.putString(MemberModel.MEMBER_EMAIL, member.getEmail());
			editMember.putString(MemberModel.MEMBER_TYPE_LOGIN, member.getTypeLogin());
			editMember.putInt(MemberModel.MEMBER_ROLE, member.getRole());
			editMember.commit();
		}
		
		MemberSession.member = member;
	}
	
	public static void clearMember(Context context) {
		SharedPreferences memberFile = context.getSharedPreferences(MEMBER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		
		Editor editMember = memberFile.edit();
		editMember.clear();
		editMember.commit();
		
		MemberSession.member = null;
	}
	
	public static boolean hasMember() {
		if(MemberSession.getMember() != null && MemberSession.getMember().getUid()>0){
			return true;
		}
		return false;
	}
	
}
