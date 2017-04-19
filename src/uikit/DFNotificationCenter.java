package uikit;

import ui.util.UIStrings;

import java.util.ArrayList;
import java.util.Objects;

import static database.DFDatabase.print;

class DFNotificationCenterObject
{
	final DFNotificationCenterDelegate observer;
	final String notificationName;

	DFNotificationCenterObject(DFNotificationCenterDelegate object, String notificationName)
	{
		observer = object;
		this.notificationName = notificationName;
	}
}

/**
 * DFNotificationCenter - A Notification parser
 * Objects can place calls to register(_:, _:) to register themselves to listen for notifications
 * Objects can place calls to post(_:, _:) to post notifications with optional userData
 * <p>
 * This class is meant to be used for passing information among different packages(modules)
 */
@SuppressWarnings({"unchecked", "unused"})
public class DFNotificationCenter
{
	public static final DFNotificationCenter defaultCenter = new DFNotificationCenter();

	private final ArrayList<DFNotificationCenterObject> observers = new ArrayList<>();

	private DFNotificationCenter() { }

	/**
	 * @param observer         The object that will be receiving the notification.  This object must conform to the DFNotificationCenterDelegate interface
	 * @param notificationName The notification name the object wishes to listen for.  All other notifications will be ignored
	 */
	public synchronized void register(DFNotificationCenterDelegate observer, String notificationName)
	{
		for (DFNotificationCenterObject object : observers)
		{
			if (object.observer == observer && Objects.equals(object.notificationName, notificationName))
			{
				return;
			}
		}
		observers.add(new DFNotificationCenterObject(observer, notificationName));
	}

	/**
	 * A convenience method for removing all instances of an Object
	 *
	 * @param observer The object that wishes to be removed
	 */
	public synchronized void remove(DFNotificationCenterDelegate observer)
	{
		((ArrayList<DFNotificationCenterObject>) observers.clone()).stream().filter(object -> object.observer == observer).forEach(observers::remove);
	}

	/**
	 * @param notificationName The notification name the object wishes to post for
	 * @param userData         Optional data the object calling this function wishes to pass on to any observers
	 */
	public synchronized void post(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.oneHourHasPassedNotification) || Objects.equals(notificationName, UIStrings.tenMinutesHavePassedNotification) || Objects.equals(notificationName, UIStrings.twentyFiveMinutesHavePassedNotification) || Objects.equals(notificationName, UIStrings.fiveMinutesHavePassedNotification) || Objects.equals(notificationName, UIStrings.newDayNotification))
		{
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			boolean foundTimeManager = false;
			for (StackTraceElement element : stackTraceElements)
			{
				if (element.getClassName().contains("TimeManager"))
				{ foundTimeManager = true; }
			}
			if (!foundTimeManager)
			{
				print("Warning! " + notificationName + " is reserved for the TimeManager class.  Any other use of this notification will be disregarded in order to prevent incorrect timing.");
				return;
			}
		}

		((ArrayList<DFNotificationCenterObject>) observers.clone()).stream().filter(observer -> Objects.equals(observer.notificationName, notificationName)).forEach(observer -> observer.observer.performActionFor(notificationName, userData));
	}
}
