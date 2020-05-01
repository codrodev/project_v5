package dm.sime.com.kharetati.pojo;

public class SessionResponse {
    private String is_exception;

    private String message;
    private String message_ar;

    public String getMessage_ar() {
        return message_ar;
    }

    public void setMessage_ar(String message_ar) {
        this.message_ar = message_ar;
    }

    private Session service_response;

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

    public Session getSession ()
    {
        return service_response;
    }

    public void setSession (Session service_response)
    {
        this.service_response = service_response;
    }

}