using System;
using System.Data;
using System.Data.SqlClient;
using System.Configuration;
using System.Linq;
 
using System.Xml.Linq;
using System.Reflection;


/// <summary>
/// PublicSQL 的摘要说明
/// </summary>
/// 
public class PublicSQL
{
    protected SqlConnection cnn = new SqlConnection();
    protected SqlCommand cmd = new SqlCommand();

    public PublicSQL() { }
    public void fusion()
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandType = CommandType.StoredProcedure;
        cmd.CommandText = "fusion";
        cmd.ExecuteNonQuery();
        return;
    }
    public PublicSQL(string uid)
    {
        cnn.ConnectionString = "server=localhost;uid=" + uid + ";pwd=" + uid + ";database=snortsnort";
        cmd.Connection = cnn;
        //System.Web.HttpContext.Current.Response.Write(uid);
    }
    public DataTable[] getViews(string endTime, int durationTime)
    {
        DataTable[] views=new DataTable[3];
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandType = CommandType.Text;
        cmd.CommandText = "declare @startTime datetime " +
            "set @startTime=Dateadd(s,-@durationTime,@endTime) " +
            "select num,time,powersum,power1,power2,ratio,phywarn " +
            "from phydect_table "+
            "where DATEDIFF(second,@endTime,phydect_table.time)<0 " +
            "and DATEDIFF(second,@startTime,phydect_table.time)>0 "+
            "select * from cyber_view "+
            "where DATEDIFF(second,@endTime,cyber_view.timestp)<0 "+
            "and DATEDIFF(second,@startTime,cyber_view.timestp)>0 "+
            "select * from threat_fusion "+
            "where DATEDIFF(second,@endTime,threat_fusion.timestp)<0 "+
            "  and DATEDIFF(second,@startTime,threat_fusion.timestp)>0 ";
        cmd.Parameters.Clear();
        cmd.Parameters.AddWithValue("@endTime",endTime);
        cmd.Parameters.AddWithValue("@durationTime", durationTime);
    

        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        views[0] = dataset.Tables[0];
        views[1] = dataset.Tables[1];
        views[2] = dataset.Tables[2];
        return views;
    }
    public DataTable gethisPhysicalData(string endTime, int durationTime)
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandType = CommandType.Text;
        cmd.CommandText = "declare @startTime datetime " +
            "set @startTime=Dateadd(s,-@durationTime,@endTime) " +
            "select time,powersum,power1,power2,ratio " +
            "from phydect_table " +
            "where DATEDIFF(second,@endTime,phydect_table.time)<0 " +
            "and DATEDIFF(second,@startTime,phydect_table.time)>0 ";
        cmd.Parameters.Clear();
        cmd.Parameters.AddWithValue("@endTime", endTime);
        cmd.Parameters.AddWithValue("@durationTime", durationTime);
        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        return dataset.Tables[0];
    }
    public DataTable getPhyscialData()
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandText = "select top 1  Usum,Isum,PowerSum,RPowerSum,U1,I1,Power1,RPower1,U2,I2,Power2,RPower2" + " from phydect_table" + " order by time desc";
        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        cnn.Close();
        return dataset.Tables[0];
    }
    public DataTable getSinglePowerData()
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandText = "select top 1 time,powersum,power1,power2,ratio" + " from phydect_table" + " order by time desc";
        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        cnn.Close();
        return dataset.Tables[0];
    }
    public DataTable getInitPowerData()
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandText = "select * " + " from top20" + " order by time asc";
        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        cnn.Close();
        return dataset.Tables[0];
    }
    public DataSet getStatistics()
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();
        cmd.CommandType = CommandType.Text;
        cmd.CommandText = "select count(*) pthreatcount " +
            "from phydect_table where phywarn=1 " +
            "select count(*) cthreatcount " +
            "from cyber_view " +
            "select count(*) fusionthreatcount " +
            "from threat_fusion "+
            "select count(*) as count, sig_name "+
            "from cyber_view group by sig_name";
        cmd.Parameters.Clear();       
        SqlDataAdapter adapter = new SqlDataAdapter(cmd);
        DataSet dataset = new DataSet();
        adapter.Fill(dataset);
        return dataset;
    }
    public void DataOperate(object instance, string mode)
    {
        if (cnn.State == ConnectionState.Closed)
            cnn.Open();

        Type type = instance.GetType();
        PropertyInfo[] properties = type.GetProperties();
        string identity = GetIdentity(type.Name);
        //Console.WriteLine("pKey=" + pKey);

        cmd.Parameters.Clear();
        foreach (PropertyInfo p in properties)
        {
            //Console.WriteLine(p.GetValue(product, null));
            if (p.Name.ToLower() == identity.ToLower() && identity != "No identity column defined.")
                continue;

            cmd.Parameters.AddWithValue("@" + p.Name, p.GetValue(instance, null));
        }
        string cmdString = "insert into " + type.Name + "(";

        if (mode == "update")
            cmdString = "update " + type.Name + " set ";

        foreach (SqlParameter p in cmd.Parameters)
        {
            if (mode == "update")
                cmdString += p.ParameterName.Substring(1) + "=" + p.ParameterName + ",";
            else
                cmdString += p.ParameterName.Substring(1) + ",";
        }


        if (mode == "update")
        {
            cmdString = cmdString.Substring(0, cmdString.Length - 1) + " where " + GetPkey(type.Name) + "=@" + GetPkey(type.Name);

            foreach (PropertyInfo p in properties)
            {
                if (p.Name.ToLower() == identity.ToLower())
                    cmd.Parameters.AddWithValue("@" + p.Name, Convert.ToInt16(p.GetValue(instance, null)));
            }
        }
        else
            cmdString = cmdString.Substring(0, cmdString.Length - 1) + ") values(";

        if (mode == "insert")
        {
            foreach (SqlParameter p in cmd.Parameters)
            {
                if (p.ParameterName.ToLower() == identity.ToLower() && identity != "No identity column defined.")
                    continue;

                cmdString += p.ParameterName + ",";
            }

            cmdString = cmdString.Substring(0, cmdString.Length - 1) + ")";
        }

        cmd.CommandText = cmdString;
        cmd.CommandType = CommandType.Text;

        cmd.ExecuteNonQuery();

        cnn.Close();

        //System.Web.HttpContext.Current.Response.Write(cmdString);
    }
    /// <summary>
    /// 根据指定的表查找主键列
    /// </summary>
    /// <param name="table">表名</param>
    /// <returns>主键列名称</returns>
    private string GetPkey(string table)
    {
        //cmd.Parameters.Clear();
        SqlCommand command = new SqlCommand("select index_col(@table ,1,1)", cnn);

        command.CommandType = CommandType.Text;

        command.Parameters.AddWithValue("@table", table);

        object obj = command.ExecuteScalar();


        if (obj != null)
            return obj.ToString();

        return "";
    }
    private string GetIdentity(string table)
    {
        //cmd.Parameters.Clear();

        cmd.CommandText = "sp_help";
        cmd.CommandType = CommandType.StoredProcedure;
        cmd.Parameters.AddWithValue("@objname", table);

        SqlDataReader reader = cmd.ExecuteReader();
        string identity = "";
        if (reader.NextResult())
        {
            if (reader.NextResult())
            {
                if (reader.Read())
                {
                    identity = reader["identity"].ToString();
                }
            }
        }
        reader.Close();
        //No identity column defined.
        return identity;
    }

}
