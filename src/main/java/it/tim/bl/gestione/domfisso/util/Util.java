package it.tim.bl.gestione.domfisso.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class Util {

	private static final String GIURIDICA = "G";
	private static final String FISICA = "F";
	private static Pattern p = Pattern.compile("^[0-9]*$");

	public ObjectMapper getMapper() {
		ObjectMapper modelMapper = new ObjectMapper();
		JavaTimeModule module = new JavaTimeModule();
		LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
		LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
		modelMapper.registerModule(module);
		modelMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return modelMapper;
	}

	public String getTipoIdentificativoUnivoco(String codiceFiscaleIva) {

		if (StringUtils.isEmpty(codiceFiscaleIva))
			return null;

		if (p.matcher(codiceFiscaleIva).find())
			return GIURIDICA;
		else
			return FISICA;

	}

	public static String fillImporto(String importo) {

		String[] imStr = importo.split("\\.");
		if (imStr.length == 1) {
			return imStr[0] + ".00";
		} else if (imStr[1].length() == 1) {
			return imStr[0] + "." + imStr[1] + "0";
		} else
			return importo;
	}

	public static boolean isBusiness(String iuv) {
		if (!StringUtils.isEmpty(iuv)) {
			char character = iuv.charAt(iuv.length() - 1);
			if (character == '0')
				return true;
			else
				return false;
		}
		return false;
	}

	public static String getUTCDateAsString(Date d) {
		String date = "yyyy-MM-dd";
		String time = "HH:mm:ss.SSS";
		SimpleDateFormat sdfDate = new SimpleDateFormat(date);
		sdfDate.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat sdfTime = new SimpleDateFormat(time);
		sdfTime.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdfDate.format(d) + "T" + sdfTime.format(d) + "Z";
	}

	public static Timestamp stringToTimestamp(String pattern, String timestampAsString) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// parsing string to date using parse() method
		Date parsedDate = dateFormat.parse(timestampAsString);
		// finally creating a timestamp
		Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

		return timestamp;
	}

	public static XMLGregorianCalendar getGregorianCalendarFromString(String pattern, String timestampAsString) {

		Timestamp ts;
		try {
			ts = stringToTimestamp(pattern, timestampAsString);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ts.getTime());

			XMLGregorianCalendar xmlGregorianCalendar = null;

			try {
				xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xmlGregorianCalendar.setDay(cal.get(Calendar.DATE));
			xmlGregorianCalendar.setMonth(cal.get(Calendar.MONTH) + 1);

			xmlGregorianCalendar.setYear(cal.get(Calendar.YEAR));

			return xmlGregorianCalendar;

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;

	}

	public static String getIUVSenzaPrimoCarattere(String iuv) {

		if (StringUtils.isEmpty(iuv)) {
			return null;
		}
		if (iuv.length() == 18)
			return iuv.substring(1, iuv.length());
		return iuv;
	}

}
