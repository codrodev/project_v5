package dm.sime.com.kharetati.pojo;

public class LandActivitiesResponse {
    private String is_exception;

    private String message;

    private LandActivities service_response;

    public String getIs_exception ()
    {
        return is_exception;
    }

    public void setIs_exception (String is_exception)
    {
        this.is_exception = is_exception;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public LandActivities getLandActivities ()
    {
        return service_response;
    }

    public void setLandActivities (LandActivities service_response)
    {
        this.service_response = service_response;
    }


}
