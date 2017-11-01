package net.nlacombe.commonlib.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.YearMonth;
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

	public static Instant getStartOfMonth(YearMonth yearMonth, TimeZone timeZone)
	{
		return yearMonth.atDay(1).atStartOfDay().atZone(timeZone.toZoneId()).toInstant();
	}

	public static Instant getLastSecondBeforeNextMonth(YearMonth yearMonth, TimeZone timeZone)
	{
		YearMonth nextMonth = yearMonth.plusMonths(1);
		Instant startOfNextMonthUtc = getStartOfMonth(nextMonth, timeZone);

		return startOfNextMonthUtc.minusSeconds(1);
	}
}
