<%@ WebHandler Language="C#" Class="HandlerGraph" %>

using System;
using System.Web;
using System.Data;
public class HandlerGraph : IHttpHandler {
    
    public void ProcessRequest (HttpContext context) {
        string temp = context.Request.Params["content"];
        if (temp.Equals("realtimepower"))
        {
            PublicSQL publicSQL = new PublicSQL("snort");
            DataTable dt = publicSQL.getSinglePowerData();
            String jsonstr = JsonDataTable.ToJson(dt);
            context.Response.Write(jsonstr);
        }
        else if (temp.Equals("initrealpower"))
        {
            PublicSQL publicSQL = new PublicSQL("snort");
            DataTable dt = publicSQL.getInitPowerData();
            String jsonstr = JsonDataTable.ToJson(dt);
            context.Response.Write(jsonstr);
        }
        else if (temp.Equals("getSinglePowerData"))
        {
            PublicSQL publicSQL = new PublicSQL("snort");
            DataTable dt = publicSQL.getSinglePowerData();
            String jsonstr = JsonDataTable.ToJson(dt);
            context.Response.Write(jsonstr);
        }
        else
        context.Response.Write("Hello World");
    }
 
    public bool IsReusable {
        get {
            return false;
        }
    }

}