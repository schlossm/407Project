package ui;

import json.util.JSONQueryError;
import ui.util.Alert;
import ui.util.ButtonType;

public class ErrorMessage
{
	public static final ErrorMessage defaultMessageManager = new ErrorMessage();

	public boolean checkIfNeedToShowErrorForLoadingAnnouncements(JSONQueryError error)
	{
		if (error != null)
		{
			Alert errorAlert = new Alert("Error", "ABC could not add the announcement.  Please try again.");
			errorAlert.addButton("OK", ButtonType.defaultType, null, false);
			errorAlert.show(Window.current.mainScreen);
			return true;
		}
		return false;
	}
}
