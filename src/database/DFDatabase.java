package database;

import database.DFSQL.DFSQL;
import database.WebServer.DFWebServerDispatch;
import database.WebServer.DispatchDirection;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static database.DFError.kExpandedDescription;
import static database.DFError.kMethodName;

/**
 * The main database communicator class.  All communication with the database will run through this class
 */
@SuppressWarnings("unused")
public class DFDatabase
{
	/**
	 * A queue for running blocks on the main thread.
	 */
	public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

	/**
	 * The singleton instance of DFDatabase
	 */
	public static final DFDatabase defaultDatabase = new DFDatabase();

	private final String websiteUserName	= "tokanone_abcapp";
	private final String websiteUserPass	= "RAD-88Q-gDx-pEZ";

	private final char[] hexArray			= "0123456789ABCDEF".toCharArray();
	private boolean      useEncryption      = true;

	private SecretKeySpec secretKeySpec;
	private byte[] iv;

	/**
	 * Wanna debug DFDatabase and related components? Set this flag to 1.
	 */

	private int _debug = 0;
	private Cipher encryptor, decryptor;

	public int debug()
	{
		return _debug;
	}

	public void enableDebug()
	{
		_debug = 1;
	}

	private DFDatabase()
	{
		Authenticator.setDefault (new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication (websiteUserName, websiteUserPass.toCharArray());
			}
		});
		try
		{
			Security.addProvider(new BouncyCastleProvider());

			encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
			decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");

			String encryptionKey = "A97525E2C26F8B2DDFDF8212F1D62";
			byte[] key = encryptionKey.getBytes();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);

			secretKeySpec = new SecretKeySpec(key, "AES");

			iv = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			encryptor.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
			print("Encryption failed to initialize.  Falling back to NO encryption.");
			useEncryption = false;
		}
	}

	/**
	 * @param SQLStatement the SQL statement to execute backend side
	 * @param delegate the delegate object that will respond to data changes.  This object must conform to the DFDatabaseCallbackDelegate interface
	 */
	public void execute( DFSQL SQLStatement,  DFDatabaseCallbackDelegate delegate)
	{
		if (Objects.equals(SQLStatement.formattedStatement(), ""))
		{
			Map<String, String> errorInfo = new HashMap<>();
			errorInfo.put(kMethodName, getMethodName());
			errorInfo.put(kExpandedDescription, "DFDatabase cannot work with an empty DFSQL Object.");
			if (delegate != null)
			{
				delegate.returnedData(null, new DFError(-3, "Empty DFSQL object delivered", errorInfo));
			}
			return;
		}

		DFWebServerDispatch.current.add(SQLStatement.formattedStatement().contains("UPDATE") || SQLStatement.formattedStatement().contains("INSERT") ? DispatchDirection.upload : DispatchDirection.download, SQLStatement, delegate);
	}

	public  String hashString( String decryptedString)
	{
		byte[] key = decryptedString.getBytes();
		MessageDigest sha;
		try
		{
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			return bytesToHex(key);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public  String encryptString( String decryptedString)
    {
	    if (useEncryption)
	    {
		    byte[] byteText = decryptedString.getBytes();
		    try
		    {
			    byte[] byteCipherText = encryptor.doFinal(byteText);
			    return bytesToHex(iv) + bytesToHex(byteCipherText);
		    }
		    catch (IllegalBlockSizeException | BadPaddingException e)
		    {
			    e.printStackTrace();
			    return "";
		    }
	    }
	    else
	    {
		    return decryptedString;
	    }
    }

	public  String decryptString( String encryptedString)
    {
	    if (useEncryption)
	    {
		    byte[] byteText = hexToBytes(encryptedString);
		    byte[] iv = new byte[16];
		    int length = byteText.length - 16;

		    byte[] encryptedBytes = new byte[length];
		    System.arraycopy(byteText, 0, iv, 0, iv.length);
		    System.arraycopy(byteText, iv.length, encryptedBytes, 0, (byteText.length - iv.length));
		    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		    try
		    {
			    decryptor.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			    byte[] byteCipherText = decryptor.doFinal(encryptedBytes);
			    return new String(byteCipherText);
		    }
		    catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e)
		    {
			    e.printStackTrace();
			    return "";
		    }
	    }
	    else
	    {
		    return encryptedString;
	    }
	}

    private  String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private  byte[] hexToBytes(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

	public  static String getMethodName()
	{
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		return "`" +
			       ste[Integer.min(ste.length - 1, Integer.max(2, 0))].getMethodName() +
			       "(_:" +
			       ", _:" +
			       ")`";
	}

	public  static String getMethodNameOfSuperMethod()
	{
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		return "`" +
			       ste[Integer.min(ste.length - 1, Integer.max(0, 0))].getMethodName() +
			       "(_:" +
			       ")`";
	}

	public static void print(Object object)
	{
		System.out.println(object);
	}

	public static void debugLog(Object object)
	{
		if (DFDatabase.defaultDatabase._debug == 1) print(object);
	}
}
