import java.io.*;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.*;

public class DLPrediction 
{

	private static String modelClassName = "deeplearning_d5c35043_8929_441a_9a23_dc44b06b519f";
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, PredictException 
	{
		hex.genmodel.GenModel rawModel;
	    rawModel = (hex.genmodel.GenModel) Class.forName(modelClassName).newInstance();
	    EasyPredictModelWrapper model = new EasyPredictModelWrapper(rawModel);

	    RowData row = new RowData();
	    row.put("Year", "1987");
	    row.put("Month", "10");
	    row.put("DayofMonth", "14");
	    row.put("DayOfWeek", "3");
	    row.put("CRSDepTime", "730");
	    row.put("UniqueCarrier", "PS");
	    row.put("Origin", "SAN");
	    row.put("Dest", "SFO");

	    //AbstractPrediction m = model.predict(row);
	    BinomialModelPrediction p = model.predictBinomial(row);
	    System.out.println("Label (aka prediction) is flight departure delayed: " + p.label);
	    System.out.print("Class probabilities: ");
	    for (int i = 0; i < p.classProbabilities.length; i++) {
	      if (i > 0) {
	        System.out.print(",");
	      }
	      System.out.print(p.classProbabilities[i]);
	    }
	    System.out.println("");

	}

}
