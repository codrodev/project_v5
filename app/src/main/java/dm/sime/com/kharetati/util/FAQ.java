package dm.sime.com.kharetati.util;

/**
 * Created by Hasham on 11/16/2017.
 */

public class FAQ {
  private String key;
  private String value;
  public Boolean isExpanded=false;

  public FAQ(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
