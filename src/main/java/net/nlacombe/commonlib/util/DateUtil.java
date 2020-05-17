package net.nlacombe.commonlib.util;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.TimeZone;

public class DateUtil
{
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

	public static String toHumanFormatDuration(Duration duration) {
		return duration.toString()
				.substring(2)
				.replaceAll("(\\d[HMS])(?!$)", "$1 ")
				.toLowerCase();
	}
}
