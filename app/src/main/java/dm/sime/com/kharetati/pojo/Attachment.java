package dm.sime.com.kharetati.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hasham on 8/24/2017.
 */

public class Attachment {
  public String FileType;
  public String FileContent;
  public int FileSize;
  public String FileName;
  public String PlaceHolderType;

  public String getFileType() {
    return FileType;
  }

  public void setFileType(String fileType) {
    FileType = fileType;
  }

  public String getFileContent() {
    return FileContent;
  }

  public void setFileContent(String fileContent) {
    FileContent = fileContent;
  }

  public int getFileSize() {
    return FileSize;
  }

  public void setFileSize(int fileSize) {
    FileSize = fileSize;
  }

  public String getFileName() {
    return FileName;
  }

  public void setFileName(String fileName) {
    FileName = fileName;
  }

  public String getPlaceHolderType() {
    return PlaceHolderType;
  }

  public void setPlaceHolderType(String placeHolderType) {
    PlaceHolderType = placeHolderType;
  }

  public JSONObject getJSONObject() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("FileType", this.getFileType());
      obj.put("FileContent", this.getFileContent());
      obj.put("FileSize",this.getFileSize());
      obj.put("FileName", this.getFileName());
      obj.put("PlaceHolderType", this.getPlaceHolderType());
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
    return obj;
  }
}
