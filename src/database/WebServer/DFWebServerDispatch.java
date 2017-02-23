package database.WebServer;


import com.google.gson.JsonObject;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;
import database.Utilities.DFDataSizePrinter;

import java.util.ArrayList;

import static database.DFDatabase.debugLog;

/**
 * Manages a Queue of WebServer Dispatch objects to retain atomicity of delegates and sql statements.  prevents overloading DFDataDownloader and DFDataUploader
 */
public class DFWebServerDispatch implements DFDatabaseCallbackDelegate
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

	public void add(DispatchDirection direction, DFSQL statement, DFDatabaseCallbackDelegate delegate)
	{
		queue.add(new PrivateDFDispatchObject(direction, statement, delegate));
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
			dataDownloader.downloadDataWith(nextObject.SQLStatement, this);
		}
		else
		{
			dataUploader.uploadDataWith(nextObject.SQLStatement, this);
		}
	}

	@Override
	public void returnedData(JsonObject jsonObject, DFError error)
	{
		nextObject.delegate.returnedData(jsonObject, error);
		processQueue();
	}

	@Override
	public void uploadStatus(DFDataUploaderReturnStatus success, DFError error)
	{
		nextObject.delegate.uploadStatus(success, error);
		processQueue();
	}
}

class PrivateDFDispatchObject
{
	final DispatchDirection fork;
	final DFSQL SQLStatement;
	final DFDatabaseCallbackDelegate delegate;

	PrivateDFDispatchObject(DispatchDirection fork, DFSQL SQLStatement, DFDatabaseCallbackDelegate delegate)
	{
		this.fork = fork;
		this.SQLStatement = SQLStatement;
		this.delegate = delegate;
	}
}

