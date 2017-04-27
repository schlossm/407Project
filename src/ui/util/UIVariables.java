package ui.util;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.bin.BinaryParser;
import net.sf.plist.io.domxml.DOMXMLWriter;
import objects.User;
import objects.userType;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.LocalStorage;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class UIVariables implements DFNotificationCenterDelegate
{
	public static final UIVariables current = new UIVariables();

	public CurrentOS currentOS = CurrentOS.unknown;

	public User currentUser;

	public Directory applicationDirectories;
	public Map<String, Object> globalUserData = new HashMap<>();
	private Map<String, NSObject> map = new HashMap<>();

	private UIVariables()
	{
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac"))
		{
			currentOS = CurrentOS.macOS;
		}
		else if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("windows"))
		{
			currentOS = CurrentOS.windows;
		}
		else if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("linux"))
		{
			currentOS = CurrentOS.linux;
		}
		linkDirectories();
		loadPrefs();

		DFNotificationCenter.defaultCenter.register(this, UIStrings.fiveMinutesHavePassedNotification);
	}

	private void linkDirectories()
	{
		Directory directory = new Directory();

		switch (currentOS)
		{
			case linux:
			{
				if (!new File(System.getProperty("user.home") + "/" + "ABC/").exists())
				{
					if (!new File(System.getProperty("user.home") + "/" + "ABC/Library/").mkdirs())
					{
						System.err.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}
				directory.library = System.getProperty("user.home") + "/" + "ABC/Library/";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/" + "ABC/";
				break;
			}

			case macOS:
			{
				if (!new File(System.getProperty("user.home") + "/Library/" + "ABC/").exists())
				{
					if (!new File(System.getProperty("user.home") + "/Library/" + "ABC/").mkdirs())
					{
						System.err.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}

				directory.library = System.getProperty("user.home") + "/Library/" + "ABC/";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/Documents/" + "ABC/";
				break;
			}

			case unknown:
			{
				System.out.println("Could not determine OS.  Exiting application as specific features are needed.");
				System.exit(-1);
				break;
			}

			case windows:
			{
				if (!new File(System.getenv("LocalAppData") + File.pathSeparator + "ABC/").exists())
				{
					if (!new File(System.getenv("LocalAppData") + File.pathSeparator + "ABC/").mkdirs())
					{
						System.err.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}
				directory.library = System.getenv("LocalAppData") + File.pathSeparator + "ABC/";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/Documents/" + "ABC/";
				break;
			}
		}
		applicationDirectories = directory;

		try
		{
			currentUser = (User) LocalStorage.defaultManager.loadObjectFromFile(applicationDirectories.library + ".user.abc");
		}
		catch (Exception ignored) { }
	}

	private void loadPrefs()
	{
		NSDictionary dictionary = null;
		try
		{
			dictionary = (NSDictionary) BinaryParser.parse(new File(UIVariables.current.applicationDirectories.library + "ABCPrefs.plist"));
		}
		catch (Exception ignored) { }

		if (dictionary != null)
		{
			map = dictionary.toMap();
		}
	}

	public NSObject valueFor(String key)
	{
		if (map.isEmpty())
		{
			loadPrefs();
		}
		return map.get(key);
	}

	public void writeValue(String key, NSObject object)
	{
		System.out.print(object);
		map.put(key, object);
	}

	public void synchronize()
	{
		NSDictionary dictionary = new NSDictionary(map);

		try
		{
			DOMXMLWriter.write(dictionary, new File(UIVariables.current.applicationDirectories.library + "ABCPrefs.plist"));
		}
		catch (PropertyListException | IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public boolean isInstructor()
	{
		return EnumSet.of(userType.TA, userType.TEACHER).contains(currentUser.getUserType());
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.fiveMinutesHavePassedNotification))
		{
			synchronize();
		}
	}

	public static String delimitSQLDATETIME(String t)
	{
		String timestamp = t;
		if (timestamp.contains("-"))    //We are working with SQL DATETIME
		{
			String[] components = timestamp.split("\\s+");
			String date = components[0];
			String time = components[1];

			//Process Date
			String[] dateComponents = date.split("-");
			date = dateComponents[1] + "/" + dateComponents[2] + "/" + dateComponents[0].substring(2);

			String[] timeComponents = time.split(":");
			String amPM;
			if (Integer.parseInt(timeComponents[0]) - 12 >= 0)
			{
				timeComponents[0] = String.valueOf(Integer.parseInt(timeComponents[0]) - 12);
				amPM = "PM";
			}
			else if (Objects.equals(timeComponents[0], "00") || Objects.equals(timeComponents[0], "0"))
			{
				timeComponents[0] = "12";
				amPM = "AM";
			}
			else
			{
				amPM = "AM";
			}

			time = timeComponents[0] + ":" + timeComponents[1] + " " + amPM;

			timestamp = date + " " + time;
		}
		return timestamp;
	}

	public static String dateToSQLDATETIME(String t)
	{
		String[] tComponents = t.split("\\s+");
		if (tComponents.length > 1) //We have date AND time
		{
			String date = tComponents[0];
			String time = tComponents[1];

			//Process Date
			String[] dateComponents = date.split("/");
			if (dateComponents[2].length() == 2)    //Make it a 4 digit year for SQL
			{
				dateComponents[2] = "20" + dateComponents[2];
			}

			date = dateComponents[2] + "-" + dateComponents[0] + "-" + dateComponents[1];

			//Process Time
			String[] timeAMPM = time.split("\\s+");

			String[] timeComponents = timeAMPM[0].split(":");

			if (Objects.equals(timeAMPM[1], "PM"))
			{
				timeComponents[0] = "" + (Integer.valueOf(timeComponents[0]) + 12);
			}

			time = timeComponents[0] + ":" + timeComponents[1] + ":00";

			return date + " " + time;
		}
		else    //Just Date
		{
			String date = tComponents[0];

			//Process Date
			String[] dateComponents = date.split("/");
			if (dateComponents[2].length() == 2)    //Make it a 4 digit year for SQL
			{
				dateComponents[2] = "20" + dateComponents[2];
			}

			date = dateComponents[2] + "-" + dateComponents[0] + "-" + dateComponents[1];

			return date + " 00:00:00";
		}
	}
}
