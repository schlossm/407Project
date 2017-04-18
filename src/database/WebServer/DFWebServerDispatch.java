package database.WebServer;


import database.DFDatabaseCallbackRunnable;
import database.DFSQL.DFSQL;
import database.Utilities.DFDataSizePrinter;

import java.util.ArrayList;

import static database.DFDatabase.debugLog;

/**
 * Manages a Queue of WebServer Dispatch objects to retain atomicity of delegates and sql statements.  prevents overloading DFDataDownloader and DFDataUploader
 */
public class DFWebServerDispatch
{
	static final String website			    = "https://www.mascomputech.com/abc";
	static final String readFile			= "ReadFile.php";
	static final String writeFile			= "WriteFile.php";
	static final String websiteUserName	    = "tokanone_abcapp";
	static final String databaseUserPass    = "RAD-88Q-gDx-pEZ";

	private final DFDataDownloader dataDownloader	= new DFDataDownloader();
	private final DFDataUploader dataUploader		= new DFDataUploader();

	final DFDataSizePrinter dataSizePrinter = DFDataSizePrinter.current;

	public static final DFWebServerDispatch current = new DFWebServerDispatch();

	private final ArrayList<PrivateDFDispatchObject> queue = new ArrayList<>();
	private PrivateDFDispatchObject nextObject;
	private boolean isProcessing = false;

	public void add(DispatchDirection direction, DFSQL statement, DFDatabaseCallbackRunnable runnable)
	{
		queue.add(new PrivateDFDispatchObject(direction, statement, runnable));
		debugLog("Added new entry!");
		debugLog("Queue size: " + queue.size());
		if (!isProcessing)
		{
			isProcessing = true;
			processQueue();
		}
	}

	private void processQueue()
	{
		if (queue.size() == 0)
		{
			isProcessing = false;
			return;
		}
		debugLog("Processing Next Entry in Dispatch Queue");

		nextObject = queue.remove(0);
		if (nextObject.fork == DispatchDirection.download)
		{
			dataDownloader.downloadDataWith(nextObject.SQLStatement, nextObject.runnable);
		}
		else
		{
			dataUploader.uploadDataWith(nextObject.SQLStatement, nextObject.runnable);
		}
	}
}

class PrivateDFDispatchObject
{
	final DispatchDirection fork;
	final DFSQL SQLStatement;
	final DFDatabaseCallbackRunnable runnable;

	PrivateDFDispatchObject(DispatchDirection fork, DFSQL SQLStatement, DFDatabaseCallbackRunnable runnable)
	{
		this.fork = fork;
		this.SQLStatement = SQLStatement;
		this.runnable = runnable;
	}
}

