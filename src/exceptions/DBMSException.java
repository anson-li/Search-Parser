package exceptions;

@SuppressWarnings("serial")
public class DBMSException extends Exception {
  
  public DBMSException() { 
      super();
  }

  public DBMSException(String message) {
      super(message);
  }

  public DBMSException(String message, Throwable cause) {
      super(message, cause);
  }

  public DBMSException(Throwable cause) {
      super(cause);
  }
}
