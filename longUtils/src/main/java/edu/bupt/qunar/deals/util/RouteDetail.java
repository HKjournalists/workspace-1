package edu.bupt.qunar.deals.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.bupt.qunar.itineray.ie.Itinerary;


public class RouteDetail {
	
	public static final String FUNCTION_GROUP = "跟团游";
	public static final String FUNCTION_FREE = "自由行";
	
	public static final String TYPE_DOMESTIC = "国内游";
	public static final String TYPE_OUTBOUND = "出境游";
	public static final String TYPE_HK = "港澳游";
	public static final String TYPE_ARROUND = "周边游";
	
	private String wrapperId; 
	private String wrapperName;
	
	private String url;//链接地址
	private String title;//线路名称
	private String price;//报价
	private String currency; //币种
	private String route_snapShot; //线路图片
	private List<String> feature; //线路描述
	private String function; //线路方式
	
	private String departure;//出发城市
	private String arrive;//到达城市
	private String type;//目的地
	private List<String> subject; //线路主题
	
	private String dateOfDeparture;//出发时间
	private String dateOfExpire;//结束时间
	private String dateOfBookingExpire; //报名截止日期
	private String itineraryDay;//行程天数
	private List<Itinerary> miscellaneous; //每日行程
	private Set<String> sightSpot; //景点
	private Set<String> sightSpot_B; 
	private Set<String> sightSpot_C;
	private String traffic;//交通方式
	private String starGrade;//住宿标准	
	private String route_snapShotSavePath; //线路图片存储路径
	private String route_snapShot_Small;
	private String route_snapShot_mid;
	
	private ShipDetail ship;//邮轮数据
	
	private String toFlight; //去时航班
	private String backFlight; //返回航班
	private String feeInfo;  //费用相关
	private String others;  //其他信息
	private String raw_date; //日期原始描述，如天天发团
	
	private String bookingFlag = ""; //记录该条记录是否是因为booking校验而产生,如果是，这个值为"booking"
	
	private String feeInclude; //费用包括
	
	private String feeExclude; //费用不包括
	
	private String ownExpense; //自费项目
	
	private String bookingTerms; //预订条款
	
	private String visaInfos; //签证信息
	
	private String shopInfos; //购物信息
	
	private String flightInfos; //航班详情
	
	private String spotOfDeparture;  //出发集合地点
	
	private String spotOfBack; //返回地点
	
	private String tips; //小贴士
	
	private String contact; //联系方式
	
	private String raw_price; //价格原始描述，如电询
	
	private String raw_bookexpiredate; //原始报名截止时间 ，如提前x天报名
	
	
	
	public String getRaw_bookexpiredate() {
		return raw_bookexpiredate;
	}



	public void setRaw_bookexpiredate(String rawBookexpiredate) {
		raw_bookexpiredate = rawBookexpiredate;
	}



	public String getRaw_price() {
		return raw_price;
	}



	public void setRaw_price(String rawPrice) {
		raw_price = rawPrice;
	}



	public String getFeeInclude() {
		return feeInclude;
	}



	public void setFeeInclude(String feeInclude) {
		this.feeInclude = feeInclude;
	}



	public String getFeeExclude() {
		return feeExclude;
	}



	public void setFeeExclude(String feeExclude) {
		this.feeExclude = feeExclude;
	}



	public String getOwnExpense() {
		return ownExpense;
	}



	public void setOwnExpense(String ownExpense) {
		this.ownExpense = ownExpense;
	}



	public String getBookingTerms() {
		return bookingTerms;
	}



	public void setBookingTerms(String bookingTerms) {
		this.bookingTerms = bookingTerms;
	}



	public String getVisaInfos() {
		return visaInfos;
	}



	public void setVisaInfos(String visaInfos) {
		this.visaInfos = visaInfos;
	}



	public String getShopInfos() {
		return shopInfos;
	}



	public void setShopInfos(String shopInfos) {
		this.shopInfos = shopInfos;
	}



	public String getFlightInfos() {
		return flightInfos;
	}



	public void setFlightInfos(String flightInfos) {
		this.flightInfos = flightInfos;
	}



	public String getSpotOfDeparture() {
		return spotOfDeparture;
	}



	public void setSpotOfDeparture(String spotOfDeparture) {
		this.spotOfDeparture = spotOfDeparture;
	}



	public String getSpotOfBack() {
		return spotOfBack;
	}



	public void setSpotOfBack(String spotOfBack) {
		this.spotOfBack = spotOfBack;
	}



	public String getTips() {
		return tips;
	}



	public void setTips(String tips) {
		this.tips = tips;
	}



	public String getContact() {
		return contact;
	}



	public void setContact(String contact) {
		this.contact = contact;
	}



	public String getBookingFlag() {
		return bookingFlag;
	}



	public void setBookingFlag(String bookingFlag) {
		this.bookingFlag = bookingFlag;
	}

	private List<RouteDate> routeDates = new ArrayList<RouteDate>();
	
	
	
	public List<RouteDate> getRouteDates() {
		return routeDates;
	}



	public void setRouteDates(List<RouteDate> routeDates) {
		this.routeDates = routeDates;
	}



	public String getRaw_date() {
		return raw_date;
	}



	public void setRaw_date(String rawDate) {
		raw_date = rawDate;
	}



	public String getToFlight() {
		return toFlight;
	}



	public void setToFlight(String toFlight) {
		this.toFlight = toFlight;
	}



	public String getBackFlight() {
		return backFlight;
	}



	public void setBackFlight(String backFlight) {
		this.backFlight = backFlight;
	}



	public String getFeeInfo() {
		return feeInfo;
	}



	public void setFeeInfo(String feeInfo) {
		this.feeInfo = feeInfo;
	}



	public String getOthers() {
		return others;
	}



	public void setOthers(String others) {
		this.others = others;
	}



	public ShipDetail getShip() {
		return ship;
	}



	public void setShip(ShipDetail ship) {
		this.ship = ship;
	}

	private String listInfo = ""; //记录从list中过来的原始信息
	
	public String getCurrency() {
		return currency;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}



	public String getListInfo() {
		return listInfo;
	}



	public void setListInfo(String listInfo) {
		this.listInfo = listInfo;
	}



	public Set<String> getSightSpot_B() {
		return sightSpot_B;
	}



	public void setSightSpot_B(Set<String> sightSpotB) {
		sightSpot_B = sightSpotB;
	}



	public Set<String> getSightSpot_C() {
		return sightSpot_C;
	}



	public void setSightSpot_C(Set<String> sightSpotC) {
		sightSpot_C = sightSpotC;
	}



	public String getRoute_snapShot_Small() {
		return route_snapShot_Small;
	}



	public void setRoute_snapShot_Small(String routeSnapShotSmall) {
		route_snapShot_Small = routeSnapShotSmall;
	}



	public String getRoute_snapShot_mid() {
		return route_snapShot_mid;
	}



	public void setRoute_snapShot_mid(String routeSnapShotMid) {
		route_snapShot_mid = routeSnapShotMid;
	}



	public String getRoute_snapShotSavePath() {
		return route_snapShotSavePath;
	}



	public void setRoute_snapShotSavePath(String routeSnapShotSavePath) {
		route_snapShotSavePath = routeSnapShotSavePath;
	}



	@Override
	public String toString() {
		return "RouteDetail [arrive=" + arrive + ", dateOfBookingExpire="
				+ dateOfBookingExpire + ", dateOfDeparture=" + dateOfDeparture
				+ ", dateOfExpire=" + dateOfExpire + ", departure=" + departure
				+ ", feature=" + feature + ", function=" + function
				+ ", itineraryDay=" + itineraryDay + ", miscellaneous="
				+ miscellaneous + ", price=" + price + ", route_snapShot="
				+ route_snapShot + ", sightSpot=" + sightSpot + ", starGrade="
				+ starGrade + ", subject=" + subject + ", title=" + title
				+ ", traffic=" + traffic + ", type=" + type + ", url=" + url
				+ ", wrapperId=" + wrapperId + ", wrapperName=" + wrapperName
				+ "]";
	}



	public String getDateOfBookingExpire() {
		return dateOfBookingExpire;
	}



	public void setDateOfBookingExpire(String dateOfBookingExpire) {
		this.dateOfBookingExpire = dateOfBookingExpire;
	}



	public List<Itinerary> getMiscellaneous() {
		return miscellaneous;
	}



	public void setMiscellaneous(List<Itinerary> miscellaneous) {
		this.miscellaneous = miscellaneous;
	}



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<String> getFeature() {
		return feature;
	}

	public void setFeature(List<String> feature) {
		this.feature = feature;
	}

	
	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	
	
	public String getArrive() {
		return arrive;
	}



	public void setArrive(String arrive) {
		this.arrive = arrive;
	}



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(List<String> subject) {
		this.subject = subject;
	}

	
	public String getDateOfDeparture() {
		return dateOfDeparture;
	}

	public void setDateOfDeparture(String dateOfDeparture) {
		this.dateOfDeparture = dateOfDeparture;
	}

	
	public String getDateOfExpire() {
		return dateOfExpire;
	}

	public void setDateOfExpire(String dateOfExpire) {
		this.dateOfExpire = dateOfExpire;
	}

	
	public String getItineraryDay() {
		return itineraryDay;
	}

	public void setItineraryDay(String itineraryDay) {
		this.itineraryDay = itineraryDay;
	}


	

	public Set<String> getSightSpot() {
		return sightSpot;
	}

	public void setSightSpot(Set<String> sightSpot) {
		this.sightSpot = sightSpot;
	}

	
	public String getTraffic() {
		return traffic;
	}



	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}



	
	public String getStarGrade() {
		return starGrade;
	}



	public void setStarGrade(String starGrade) {
		this.starGrade = starGrade;
	}	
	public String getWrapperId() {
		return wrapperId;
	}

	public void setWrapperId(String wrapperId) {
		this.wrapperId = wrapperId;
	}

	public String getWrapperName() {
		return wrapperName;
	}

	public void setWrapperName(String wrapperName) {
		this.wrapperName = wrapperName;
	}
	public String getRoute_snapShot() {
		return route_snapShot;
	}

	public void setRoute_snapShot(String routeSnapShot) {
		route_snapShot = routeSnapShot;
	}
	
	public static void main(String[] args) {
		List<String> ss = new ArrayList();
		ss.add("fef");
		ss.add("ee");
		System.out.println(ss);
	}
}
