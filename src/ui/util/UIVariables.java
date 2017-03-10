package ui.util;

import net.sf.plist.NSDictionary;
import net.sf.plist.NSObject;
import net.sf.plist.NSString;
import net.sf.plist.io.PropertyListException;
import net.sf.plist.io.bin.BinaryParser;
import net.sf.plist.io.domxml.DOMXMLWriter;
import objects.User;
import ui.Window;
import uikit.LocalStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class UIVariables
{
	public static final UIVariables current = new UIVariables();

	public CurrentOS currentOS = CurrentOS.unknown;

	public User currentUser;

	public Directory applicationDirectories;

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
	}

	private void linkDirectories()
	{
		Directory directory = new Directory();

		switch (currentOS)
		{
			case linux:
			{
				if(!new File(System.getProperty("user.home") + "/" + "ABC/").exists())
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
				if(!new File(System.getProperty("user.home") + "/Library/" + "ABC/").exists())
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
				if(!new File(System.getenv("LocalAppData") + File.pathSeparator + "ABC/").exists())
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

	public void writeFrame()
	{
		NSDictionary dictionary = null;
		try
		{
			dictionary = (NSDictionary) BinaryParser.parse(new File(UIVariables.current.applicationDirectories.library + "ABCPrefs.plist"));
		}
		catch (Exception ignored) { }

		Map<String, NSObject> map;

		if (dictionary != null)
		{
			map = dictionary.toMap();
			map.put("X", new NSString(String.valueOf(Window.current.mainScreen.getX())));
			map.put("Y", new NSString(String.valueOf(Window.current.mainScreen.getY())));
			map.put("W", new NSString(String.valueOf(Window.current.mainScreen.getWidth())));
			map.put("H", new NSString(String.valueOf(Window.current.mainScreen.getHeight())));
		}
		else
		{
			map = new HashMap<>();
			map.put("X", new NSString(String.valueOf(Window.current.mainScreen.getX())));
			map.put("Y", new NSString(String.valueOf(Window.current.mainScreen.getY())));
			map.put("W", new NSString(String.valueOf(Window.current.mainScreen.getWidth())));
			map.put("H", new NSString(String.valueOf(Window.current.mainScreen.getHeight())));
		}

		dictionary = new NSDictionary(map);

		try
		{
			DOMXMLWriter.write(dictionary, new File(UIVariables.current.applicationDirectories.library + "ABCPrefs.plist"));
		}
		catch (PropertyListException | IOException e1)
		{
			e1.printStackTrace();
		}
	}
}
