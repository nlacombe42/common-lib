package net.nlacombe.commonlib.util;

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
}
