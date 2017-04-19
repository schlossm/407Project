package database.Utilities;


import java.util.Formatter;
import java.util.Locale;

import static database.DFDatabase.print;
import static java.lang.Double.valueOf;

/**
 * Prints human readable data sizes nicely formatted by byte size
 */
public class DFDataSizePrinter
{
	/**
	 * The singleton instance of the printer
	 */
	public static final DFDataSizePrinter current = new DFDataSizePrinter();

	private DFDataSizePrinter() { }

	/**
	 * Prints the data size
	 *
	 * @param dataSize An integer equaling the number of bytes desired to print
	 */
	public void printDataSize(Integer dataSize)
	{
		Integer byteSize = 0;

		Double length = valueOf(dataSize);

		while (length >= 1024.0)
		{
			byteSize += 1;
			length /= 1024.0;
		}

		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format("%.2f", length);
		formatter.close();

		String fileSize = "";

		switch (byteSize)
		{
			case 0:
				fileSize = "B";
				break;
			case 1:
				fileSize = "KB";
				break;
			case 2:
				fileSize = "MB";
				break;
			case 3:
				fileSize = "GB";
				break;
			case 4:
				fileSize = "TB";
				break;
			case 5:
				fileSize = "PB";
				break;
		}

		String size = new String(sb);
		size = size.replace(".00", "");

		print("Downloaded Data Size: " + size + " " + fileSize);
	}
}
