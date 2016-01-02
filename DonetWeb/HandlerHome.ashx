<%@ WebHandler Language="C#" Class="HandlerHome" %>

using System;
using System.Web;
using System.Data;

public class HandlerHome : IHttpHandler {
    
    public void ProcessRequest (HttpContext context) {
        PublicSQL publicSQL = new PublicSQL("snort");
        DataTable dt=publicSQL.getPhyscialData();
        String jsonstr=JsonDataTable.ToJson(dt);
        Console.Write(AppDomain.CurrentDomain.SetupInformation.ApplicationBase);   
        context.Response.Write(jsonstr);
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}