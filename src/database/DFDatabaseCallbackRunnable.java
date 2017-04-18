package database;

@FunctionalInterface
public interface DFDatabaseCallbackRunnable
{
	public abstract void run(Object response, DFError error);
}
