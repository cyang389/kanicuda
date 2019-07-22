package Expression;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import java.io.*;
import java.util.*;


public class PearsonCorrelation {
	private File profile; 
	private List<String> vars;
	private int[] arr;
	private int[][] avail_arr;
	private Map<String, double[]> arrMap;
	private int smidIndex;
	int count = 0;
	private int avail_line_count;
	
	public PearsonCorrelation(File profile, List<String> vars, int[][] avail, int avail_line_count) {
		this.profile = profile;
		this.vars = vars;
		this.arrMap = new HashMap<>();
		this.smidIndex = -1;
		this.avail_arr = avail;
		this.avail_line_count = avail_line_count;
		arr = new int[vars.size()];
	}
	
	public void process() {
		// hard coded this size
		int size = 800;
		for (int i = 0; i < vars.size(); i++) {
			if (vars.get(i).equals("smid")) {
				this.smidIndex = i;
			}
		}
		if (smidIndex == -1) {
			return;
		}
		for (int i = smidIndex; i < vars.size(); i++) {
			arrMap.put(vars.get(i), new double[size]);
		}
		/*
		try {
			if(profile.exists()) {
				FileReader fr = new FileReader(profile);
				BufferedReader br = new BufferedReader(fr);
				String read;
				int index = 0;
				while ((read = br.readLine()) != null && index < size) {
					List<String> sList = Arrays.asList(read.split(" "));
					if (sList.contains("N")) { continue; }
					for (int i = smidIndex; i < vars.size(); i++) {
						arrMap.get(vars.get(i))[index] = Integer.parseInt(sList.get(i));
					}
					index++;
				}
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		for (int i = 0; i < avail_line_count; i++) {
			int index = 0;
			for (int j = smidIndex; j < vars.size(); j++) {
				arrMap.get(vars.get(j))[index] = avail_arr[i][j];
			}
			index++;
		}
	}
	
	public List<String> getRelVars() {
		List<String> result = new ArrayList<String>();
		double[] smidList = arrMap.get(vars.get(smidIndex));
		for (int i = smidIndex + 1; i < vars.size(); i++) {
			double corr = new PearsonsCorrelation().correlation(smidList, arrMap.get(vars.get(i)));
			if (corr > 0.5 || corr < -0.5) {
				result.add(vars.get(i));
				arr[count++] = i;
			}
		}
		return result;
	}
	
	public List<String> getConstants() {
		List<String> result = new ArrayList<String>();
		Variance v = new Variance();
		for (int i = smidIndex + 1; i < vars.size(); i++) {
			if (v.evaluate(arrMap.get(vars.get(i))) == 0) {
				//System.out.println(vars.get(i));
				result.add(vars.get(i));
				arr[count++] = i;
			}
		}
		return result;
	}
	
	public int[] getVarIndex() {
		int[] varIndex = new int[count];
		System.arraycopy(arr, 0, varIndex, 0, count);
		return varIndex;
	}
	
	public int getSmid() {
		return smidIndex;
	}
}