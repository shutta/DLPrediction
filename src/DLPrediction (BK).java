import java.io.*;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DLPrediction 
{

	
	// ********* Connect Function ******************************************
	public void connect(String host, String db, String user, String pass) throws SQLException
	{
		try 
		{
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} 
		
		catch (Exception e) 
		{
			System.out.println("Database driver \"jdbc not\" loaded");
		    System.out.println(e.toString());
		}
		
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?" + "user=" + user +"&password=" + pass);
	}
	
	// ********* Disconnect Function ***************************************
	public void disconnect() throws SQLException
	{
		connection.close();
	}
	
	// ********* Execute Function ******************************************
	public void execSentence(String sql) throws SQLException
	{
		cmd = connection.createStatement();

		rs = cmd.executeQuery(sql);
	}
	
	// ********* Execute Function ******************************************
	public void execAction(String sql) throws SQLException
	{
		cmd = connection.createStatement();

		cmd.executeUpdate(sql);
	}
	
	// ********* getResult Function ****************************************
	public ResultSet getResult()
	{
		return rs;
	}
	
	
	// ********* parseData Function ****************************************
    public List<String> parseData()  
    {
    	// esta funcion es la que lee el json y saca todos sus datos
    	String host, dataDBase, configDBase, logsDBase, user, pass;
    	List<String> parameters = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		try 
		{
            Object obj = parser.parse(new FileReader("/opt/sdc/etc/env.json"));  
 
            JSONObject jsonObject = (JSONObject) obj;
            
            JSONObject mariaObject;
			
            mariaObject = (JSONObject) jsonObject.get("mariadb");
            
            host = (String) mariaObject.get("host");
            parameters.add(host);
            configDBase = (String) mariaObject.get("configDB");
            parameters.add(configDBase);
            dataDBase = (String) mariaObject.get("dataDB");
            parameters.add(dataDBase);
            user = (String) mariaObject.get("user");
            parameters.add(user);
            pass = (String) mariaObject.get("password");
            parameters.add(pass);
            logsDBase = (String) mariaObject.get("logsDB");
            parameters.add(logsDBase);
	    } 
	    catch (Exception e) 
	    {
            e.printStackTrace();
        }
		
		return parameters;
    }
    
    
    // ********* checkStatusChange Function *****************************
    public boolean checkStatusChange() throws SQLException
    {
		execSentence("select * from rt_control");
		ResultSet results;
		results = getResult();

		boolean status = false;

		while (results.next()) 
        {
            status = results.getBoolean("reloadMlConfig");
        }
		
    	return status;
    }
	
	
	
    // ********* Member Variables ******************************************
	private static String modelClassName = "deeplearning_d5c35043_8929_441a_9a23_dc44b06b519f";
	private Connection connection = null;
	private Statement cmd;
	private ResultSet rs = null;
	
	// ********* Main Function *********************************************
	@SuppressWarnings("null")
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, PredictException 
	{
		hex.genmodel.GenModel rawModel;
	    rawModel = (hex.genmodel.GenModel) Class.forName(modelClassName).newInstance();
	    EasyPredictModelWrapper model = new EasyPredictModelWrapper(rawModel);
	    RowData row = new RowData();

	    row.put("Input-1","14.5");
	    row.put("Input-2", "42.8");


	    MultinomialModelPrediction mmp = model.predictMultinomial(row);
	    
	    //AbstractPrediction ap = model.predict(row);
	    
	    //BinomialModelPrediction p = model.predictBinomial(row);
	    
	    //AutoEncoderModelPrediction aemp = model.predictAutoEncoder(row);
	    
	    //ClusteringModelPrediction cmp = model.predictClustering(row);
	    
	    //RegressionModelPrediction rmp = model.predictRegression(row);
	    
    
	    
	    System.out.println("Label (aka prediction) is flight departure delayed: " + mmp.label);
	    System.out.print("Class probabilities: ");
	    for (int i = 0; i < mmp.classProbabilities.length; i++) 
	    {
	      if (i > 0) 
	      {
	        System.out.print(", ");
	      }
	    
	      System.out.print(mmp.classProbabilities[i]);
	    }
	    
	    System.out.println("");

	}

}
