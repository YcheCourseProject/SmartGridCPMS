<%@ WebHandler Language="C#" Class="HandlerTableData" %>

using System;
using System.Web;
using System.Data;
using System.IO;
using System.Text;
public class HandlerTableData : IHttpHandler {
    
    public void ProcessRequest (HttpContext context) {
        PublicSQL publicSQL = new PublicSQL("snort");
        string endtimeStr = context.Request.Params["endtime"];
        string durationStr= context.Request.Params["durationtime"];
        string tablestyle = context.Request.Params["select"];
        int order ;
        if (tablestyle.Equals("physical_system_table"))
        {
            order = 0;
        }
        else if (tablestyle.Equals("cyber_system_table"))
        {
            order = 1;
        }
        else
        {
            order = 2;
        }           
        int duration=int.Parse(durationStr.Substring(0,1))*3600;
        DataTable []dts= publicSQL.getViews(endtimeStr,duration);            
        for (int i = 0; i < dts.Length; i++)
        {
            string filename = "data" + i+".json";
            String path = System.Web.HttpContext.Current.Server.MapPath(filename);   
            String text = JsonDataTable.ToJson(dts[i]);
            Write(path, text, true);
            if(i==order)
                context.Response.Write(text);
        }
    }
    public bool IsReusable {
        get {
            return false;
        }
    }
    public void Write(string path,string text,bool isfirst)
    {
        FileStream fs;
        if (isfirst == true)
            fs = new FileStream(path, FileMode.Create);
        else
            fs = new FileStream(path, FileMode.Append);
        StreamWriter sw = new StreamWriter(fs, Encoding.Default);
        sw.Write(text);
        sw.Close();
        fs.Close();
    }
}