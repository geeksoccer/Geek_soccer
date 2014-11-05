package com.excelente.geek_soccer.model;

import java.util.ArrayList;
import java.util.List;

public class FixturesGroupList {
	
		public String headerTitle;
		public final List<FixturesModel> children = new ArrayList<FixturesModel>();
		
		public FixturesGroupList(String string) {
			this.headerTitle = string;
		}
		
}
