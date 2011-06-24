package com.qunar.deals.parse;

import java.util.List;

import com.qunar.deals.ParseInterface;
import com.qunar.deals.util.RouteDetail;

public interface DealsParseInterface extends ParseInterface {

	public List<String> extractSightSpot(String string);
	
	public List<String> extractCities(String dayTitle);
	
	public RouteDetail parseGroupRoutePage(String html, String url, String type, String subject, String otherInfo);
	
	public RouteDetail parseFreeRoutePage(String html, String url, String type, String subject, String otherInfo);
}
