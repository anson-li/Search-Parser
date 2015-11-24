package exceptions;

@SuppressWarnings("serial")
public class DBMSExitException extends Exception {
  
  public DBMSExitException() { 
      super();
  }

  public DBMSExitException(String message) {
      super(message);
  }

  public DBMSExitException(String message, Throwable cause) {
      super(message, cause);
  }

  public DBMSExitException(Throwable cause) {
      super(cause);
  }
}
