package database;

@FunctionalInterface
public interface DFDatabaseCallbackRunnable
{
	void run(Object response, DFError error);
}
