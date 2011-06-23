/*
 * @(#)OptionSight.java 0.01 2010/3/18
 * 
 * Copyright (c) 2010-2012 Qunar, Inc. All rights reserved.
 */
package bupt.longlong.qunar.itineray.ie;

import java.util.List;

public class OptionSight {

	private int startPos;
	private int endPos;

	private String aliasName;
	private List<Sight> optionSights;

	private boolean isToRegion;

	public OptionSight() {

	}

	public OptionSight(int startPos, int endPos, String aliasName, List<Sight> optionSights, boolean isToRegion) {
		super();
		this.startPos = startPos;
		this.endPos = endPos;
		this.aliasName = aliasName;
		this.optionSights = optionSights;
		this.isToRegion = isToRegion;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public List<Sight> getOptionSights() {
		return optionSights;
	}

	public void setOptionSights(List<Sight> optionSights) {
		this.optionSights = optionSights;
	}

	public boolean isToRegion() {
		return isToRegion;
	}

	public void setToRegion(boolean isToRegion) {
		this.isToRegion = isToRegion;
	}
}
