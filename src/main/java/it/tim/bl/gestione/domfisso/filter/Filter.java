package it.tim.bl.gestione.domfisso.filter;

import jakarta.servlet.annotation.WebFilter;
import org.springframework.stereotype.Component;

import it.tim.gup.common.filter.GupFilter;

@Component
@WebFilter("bl/domfisso/*")
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
