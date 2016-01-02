<%@ WebHandler Language="C#" Class="HandlerStatistics" %>

using System;
using System.Web;
using System.Data;
using System.Web.Script.Serialization;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;
public class HandlerStatistics : IHttpHandler
{

    public void ProcessRequest(HttpContext context)
    {
        PublicSQL publicSQL = new PublicSQL("snort");
        DataSet ds = publicSQL.getStatistics();
        int[] countarr = new int[3];
        DataTable dt;
        for (int i = 0; i < 3; i++)
        {
            dt = ds.Tables[i];
            countarr[i] = (int)dt.Rows[0][0];
        }
        string[] name = new string[3];
        name[0] = "physical";
        name[1] = "cyber";
        name[2] = "fusion";
        string text = ToJson(countarr, name);
        string filename = "categorize_count" + ".json";
        String path = System.Web.HttpContext.Current.Server.MapPath(filename);
        Write(path, text, true);
     
        string text2 = JsonDataTable.ToJson(ds.Tables[3]);
        filename = "division" + ".json";
        path = System.Web.HttpContext.Current.Server.MapPath(filename);
        Write(path, text2, true);

        string result = "{" +"\""+"categorize_count"+"\":"+
            text + "," + "\"" + "division" + "\":" + 
             text2 + "}";
        filename = "test" + ".json";
        path = System.Web.HttpContext.Current.Server.MapPath(filename);
        Write(path, result, true);
        context.Response.Write(result); 
    }
    public static string ToJson(int[] count, string[] name)
    {
        JavaScriptSerializer javaScriptSerializer = new JavaScriptSerializer();
        javaScriptSerializer.MaxJsonLength = Int32.MaxValue; //取得最大数值
        ArrayList arrayList = new ArrayList();
        Dictionary<string, object> dictionary = new Dictionary<string, object>();  //实例化一个参数集合   
        for (int i = 0; i < count.Length; i++)
            dictionary.Add(name[i], count[i].ToString());
        arrayList.Add(dictionary); //ArrayList集合中添加键值
        return javaScriptSerializer.Serialize(arrayList);  //返回一个json字符串
    }
    public void Write(string path, string text, bool isfirst)
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
    public bool IsReusable
    {
        get
        {
            return false;
        }
    }

}