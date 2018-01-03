package tr.com.hive.smm.mapping;

/**
 * Created by ozgur on 4/7/17.
 */
public class MappingException extends RuntimeException {

  public MappingException(final String message) {
    super(message);
  }

  public MappingException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
