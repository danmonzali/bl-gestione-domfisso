package it.tim.bl.gestione.domfisso.filter;

import org.springframework.stereotype.Component;

import it.tim.gup.common.filter.GupFilter;

@Component
public class Filter extends GupFilter {

	@Override
	public String getGupApplicationComponent() {
		return "BL";
	}

	@Override
	public String getGupEventType() {
		return "BlGestioneDomfisso";
	}

}
