package dm.sime.com.kharetati.util;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;

import dm.sime.com.kharetati.pojo.BuildingViolationResponse;
import dm.sime.com.kharetati.pojo.EmailParam;
import dm.sime.com.kharetati.pojo.ExportParam;

/**
 * Created by Imran on 8/21/2017.
 */

public class PlotDetails {
    public static BuildingViolationResponse buildingViolationResponse;
    public static String parcelNo="";
    public static String communityEn;
    public static String communityAr;
    public static ExportParam exportParam;
    public static EmailParam emailParam;
    public static double lat;
    public static double lon;
    public static double area;
    public static boolean isUserPlot=false;
    public static String pdfUrl="";
    public static Geometry plotGeometry;
    public static boolean isOwner = false;

    public static CurrentState currentState=new CurrentState();
    //public static byte

    public static String getCommunity() {
        if(Constant.CURRENT_LOCALE=="en")
            return PlotDetails.communityEn;
        else
            return PlotDetails.communityAr;
    }

    static public class CurrentState{
        public Graphic graphic;
        public Point parcelCenter;
        public int zoomScale;
        public Graphic textLabel2;
        public Graphic textLabel3;
        public Graphic textLabel;
    }

    public static void clearCommunity(){
        PlotDetails.communityEn=null;
        PlotDetails.communityAr=null;
    }
}
