package json;

import json.util.JSONQueryError;

@FunctionalInterface
public interface QueryCallbackRunnable
{
	public abstract void run(Object returnedData, JSONQueryError error);
}
