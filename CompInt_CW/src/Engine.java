import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import adt.DataSet;

public class Engine {

	private DataSet<Double> data;
	private Scanner scanner;
	// operations
	private DataSet<String> population;
	private static final String[] OPERATERS = { "+", "-", "*" };
	private static final int POPULATION_SIZE = 50;
	private ScriptEngine scriptEngine;
	
	public static void main(String[] args) {
		new Engine();
	}
		
	public Engine() {
		ScriptEngineManager sem = new ScriptEngineManager();
	    scriptEngine = sem.getEngineByName("JavaScript");
		setupData(new File("C:/Users/Chloe/Downloads/cwk_test.csv"));
		method();
	}
	
	private void setupData(File file) {
		data = new DataSet<>();
		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int countNodes = 0;

		String line = scanner.nextLine();
		
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length];
			for (int pos = 0; pos < tokens.length; pos++) {
				components[pos] = Double.parseDouble(tokens[pos]);	
			}
			countNodes++;
			data.add(components);
		}
		System.out.println("Total nodes = " + countNodes);
		//System.out.println(data.toString());
	}
	
	public void method()
	{
		
		
		/*
		 * BEGIN */
		initialise();
		
		buildExpression(population.getData(0), 0);
		
		
		/* 	EVALUATE candidates
		 * 	REPEAT UNTIL ( termination condition IS satisfied ) DO
		 * 		1. SELECT parents
		 *  	2. RECOMBINE pairs of parents
		 *  	3. MUTATE offspring
		 *  	4. EVALUATE new candidates
		 *  	5. SELECT individuals for next gen
		 * 	OD
		 * END
		*/		
	}	
	
	private void initialise() {
		population = new DataSet<>();
		
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(randomExpression());
		}
		
		System.out.println("Size = " + population.size() + "\n" + population.toString());		
		
	}
	
	private String[] randomExpression() {
		
		Random r = new Random(362);
		String[] result = new String[data.getDataLength()-2];
		
		for (int i = 0; i < data.getDataLength()-2; i++) {
			result[i] = OPERATERS[r.nextInt(OPERATERS.length)];
		}
		
		return result;
	}
	
	private String[] buildExpression(String[] ops, int index) {
		
		String[] expr = new String[ops.length + data.getDataLength() - 1];
		Double[] values = data.getData(index);
		System.out.println("expr " + expr.length + " values " + values.length + " ops " + ops.length);
		
		int op = 0;
		int num = 1;
		
		for (int i = 0; i < ((data.getDataLength()-1)*2)-1; i++) {
			if (i%2 == 0) {
				expr[i] = String.valueOf(values[num]);
				num++;
			} else {
				if (i < ((data.getDataLength()-1)*2)-1) {
					expr[i] = ops[op];
					op++;
				}
			}
		}
		
		evaluateExpression(expr);
		return expr;
	}

	private Double evaluateExpression(String[] expr) {
		ArrayList<String> expression = new ArrayList<String>();
		
		for (int i = 0; i < expr.length; i++) {
			expression.add(expr[i]);
		}
		
		String strExpr = "";
		for (int i = 0; i < expression.size(); i++) {
			strExpr += expression.get(i);
			if (i < expression.size()-1) {
				strExpr += " ";
			}
		}
		System.out.println(strExpr);
		
		try {
			Object result = scriptEngine.eval(strExpr);
			System.out.println("Result = " + result);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}


}
