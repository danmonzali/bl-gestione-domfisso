package it.tim.bl.gestione.domfisso.util;

/**
 * Mascheramento dei dati sensibili (cf, prefisso, numero) prima della scrittura nei log.
 */
public final class MaskingUtil {

	private static final int CARATTERI_VISIBILI = 3;

	private MaskingUtil() {
	}

	public static String mask(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() <= CARATTERI_VISIBILI) {
			return "*".repeat(value.length());
		}
		return "*".repeat(value.length() - CARATTERI_VISIBILI) + value.substring(value.length() - CARATTERI_VISIBILI);
	}

}
