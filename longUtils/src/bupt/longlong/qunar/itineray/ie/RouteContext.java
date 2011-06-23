package bupt.longlong.qunar.itineray.ie;

import java.util.HashSet;
import java.util.Set;

public class RouteContext {

	//线路类别
	private boolean domestic;

	//判断线路的区域，国内游范围限制在区（华中，华北），出境游限制在洲
	private Set<Sight> region = new HashSet<Sight>();

	private Set<Sight> optionRegion = new HashSet<Sight>();

	public Set<Sight> getOptionRegion() {
		return optionRegion;
	}

	public void setOptionRegion(Set<Sight> optionRegion) {
		this.optionRegion = optionRegion;
	}

	public boolean isDomestic() {
		return domestic;
	}

	public void setDomestic(boolean domestic) {
		this.domestic = domestic;
	}

	public Set<Sight> getRegion() {
		return region;
	}

	public void setRegion(Set<Sight> region) {
		this.region = region;
	}

	public void switchOptionSight() {
		Set<Sight> tSet = new HashSet<Sight>();
		tSet.addAll(region);
		region.clear();
		region.addAll(optionRegion);
		optionRegion.clear();
		optionRegion.addAll(tSet);
	}

}
