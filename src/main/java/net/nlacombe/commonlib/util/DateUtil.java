package net.nlacombe.commonlib.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{
	public static String toIsoDate(Date date)
	{
		return toIsoDate(date, TimeZone.getTimeZone("America/Montreal"));
	}

	public static String toIsoDate(Date date, TimeZone timeZone)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		dateFormat.setTimeZone(timeZone);

		return dateFormat.format(date);
	}
}
