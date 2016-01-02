<%@ WebHandler Language="C#" Class="HandlerHisCurveSelect" %>

using System;
using System.Web;
using System.Data;
public class HandlerHisCurveSelect : IHttpHandler {
    
    public void ProcessRequest (HttpContext context) {
        PublicSQL publicSQL = new PublicSQL("snort");
        string endtimeStr = context.Request.Params["endtime"];
        string durationStr = context.Request.Params["durationtime"];   
        int duration = int.Parse(durationStr.Substring(0, 2).Trim()) * 60;
        DataTable hisdatatable=publicSQL.gethisPhysicalData(endtimeStr, duration);
        String text = JsonDataTable.ToJson(hisdatatable);
        context.Response.Write(text);
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}