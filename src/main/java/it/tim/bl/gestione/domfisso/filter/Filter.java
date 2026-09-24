package it.tim.bl.gestione.domfisso.filter;

import javax.servlet.annotation.WebFilter;

import org.springframework.stereotype.Component;

import it.tim.gup.common.filter.GupFilter;

@Component
@WebFilter("gup/bl/visualizza/richiesta/domfisso/*")
public class Filter extends GupFilter {

	@Override
	public String getGupApplicationComponent() {
		return "GUP";
	}

	@Override
	public String getGupEventType() {
		return "BlVisualizzaRichiestaDomFisso";
	}

}
