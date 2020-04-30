package dm.sime.com.kharetati.pojo;

public class BasicResponse {
    private String is_exception;

    private String message;

    //private String service_response;

    public String getIsException ()
    {
        return is_exception;
    }

    public void setIsException (String is_exception)
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

    /*//public String getServiceResponse ()
    {
        return service_response;
    }*/

    /*public void setServiceResponse (String service_response)
    {
        this.service_response = service_response;
    }*/
}
