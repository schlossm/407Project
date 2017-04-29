package uikit;

import ui.util.UIStrings;

import java.awt.*;
import java.util.Calendar;
import java.util.TimeZone;

import static database.DFDatabase.debugLog;

@SuppressWarnings("InfiniteLoopStatement")
public class TimeManager implements Runnable
{
	private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Indianapolis"));
	private long startMillisecond = calendar.getTimeInMillis();
	private int oldDay = calendar.get(Calendar.DAY_OF_YEAR);
	private boolean justFiredOffNotification = false;

	private void listenForTimeChanges()
	{
		while (true)
		{
			Calendar tempCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Indianapolis"));
			int thisDay = tempCalendar.get(Calendar.DAY_OF_YEAR);
			long thisTime = tempCalendar.getTimeInMillis();

			if (thisDay != oldDay)
			{
				if (!justFiredOffNotification)
				{
					justFiredOffNotification = true;
					debugLog("A new day has begun.  Firing notification and resetting time.");
					oldDay = thisDay;
					startMillisecond = thisTime;
					EventQueue.invokeLater(() -> DFNotificationCenter.defaultCenter.post(UIStrings.newDayNotification, null));
				}
			}
			else if ((thisTime - startMillisecond) % 3600000 == 0)
			{
				if (!justFiredOffNotification)
				{
					justFiredOffNotification = true;
					debugLog("One hour has passed!");
					EventQueue.invokeLater(() -> DFNotificationCenter.defaultCenter.post(UIStrings.oneHourHasPassedNotification, null));
				}
			}
			else if ((thisTime - startMillisecond) % 1500000 == 0)
			{
				if (!justFiredOffNotification)
				{
					justFiredOffNotification = true;
					debugLog("25 minutes have passed!");
					EventQueue.invokeLater(() -> DFNotificationCenter.defaultCenter.post(UIStrings.twentyFiveMinutesHavePassedNotification, null));
				}
			}
			else if ((thisTime - startMillisecond) % 600000 == 0)
			{
				if (!justFiredOffNotification)
				{
					justFiredOffNotification = true;
					debugLog("10 minutes has passed!");
					EventQueue.invokeLater(() -> DFNotificationCenter.defaultCenter.post(UIStrings.tenMinutesHavePassedNotification, null));
				}
			}
			else if ((thisTime - startMillisecond) % 300000 == 0)
			{
				if (!justFiredOffNotification)
				{
					justFiredOffNotification = true;
					debugLog("5 minutes has passed!");
					EventQueue.invokeLater(() -> DFNotificationCenter.defaultCenter.post(UIStrings.fiveMinutesHavePassedNotification, null));
				}
			}
			else
			{
				justFiredOffNotification = false;
			}
		}
	}

	@Override
	public void run()
	{
		listenForTimeChanges();
	}
}
