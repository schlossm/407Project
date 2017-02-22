package ui.util;

import objects.User;

import java.io.File;

public class UIVariables
{
	public static UIVariables current = new UIVariables();

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
				if(!new File(System.getProperty("user.home") + "/" + "ABC").exists())
				{
					if (!new File(System.getProperty("user.home") + "/" + "ABC").mkdirs())
					{
						System.out.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}
				directory.library = System.getProperty("user.home") + "/" + "ABC/Library";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/" + "ABC";
				break;
			}

			case macOS:
			{
				if(!new File(System.getProperty("user.home") + "/Library/" + "ABC").exists())
				{
					if (!new File(System.getProperty("user.home") + "/Library/" + "ABC").mkdirs())
					{
						System.out.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}

				directory.library = System.getProperty("user.home") + "/Library/" + "ABC";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/Documents/" + "ABC";
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
				if(!new File(System.getenv("LocalAppData") + File.pathSeparator + "ABC").exists())
				{
					if (!new File(System.getenv("LocalAppData") + File.pathSeparator + "ABC").mkdirs())
					{
						System.out.println("Could not create folders needed.  Exiting application as specific features are needed.");
						System.exit(-1);
					}
				}
				directory.library = System.getenv("LocalAppData") + File.pathSeparator + "ABC";
				directory.temp = System.getProperty("java.io.tmpdir");
				directory.documents = System.getProperty("user.home") + "/Documents/" + "ABC";
				break;
			}
		}
		applicationDirectories = directory;
	}
}
