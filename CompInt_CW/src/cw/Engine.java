package cw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;

public class Engine {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private Scanner scanner;
	// operations
	private ArrayList<Expression> population;
	private static final int POPULATION_SIZE = 10;
	private static final int TOURNAMENT_SIZE = 5;
	private ScriptEngine scriptEngine;
	private static final int GENERATIONS = 10;
	private static final int NUM_PARENTS = 2;
	private static final String[] MUTATIONS = { "two-opt", "change-value" };
	
	public static void main(String[] args) {
		new Engine();
	}
		
	public Engine() {
		ScriptEngineManager sem = new ScriptEngineManager();
	    scriptEngine = sem.getEngineByName("JavaScript");
		setupData(new File("C:/Users/Chloe/Downloads/cwk_test.csv"));
		evolutionaryAlgorithm();		
	}
	

	public void evolutionaryAlgorithm()
	{
		// initialise
		initialise();

		
		// REPEAT UNTIL ( termination condition IS satisfied ) DO
		for (int i = 0; i < GENERATIONS; i++) {
			
			System.out.println("------------------- Generation " + i + " --------------------\n");	
			
			System.out.println("Population:");
			Collections.sort(population);
			for (int j = 0; j < population.size(); j++) {
				System.out.println(population.get(j).toString());
			}
			
			// evaluate candidates & find parents
			System.out.println("Tournament");
			ArrayList<Expression> parents = tournament();
			//Collections.sort(parents);
			System.out.println("\nParents Ordered size: " + parents.size());
			for (int j = 0; j < parents.size(); j++) {
				System.out.println(parents.get(j).toString());
			}
			// recombine pairs of parents
			System.out.println();
			
			// mutate offspring
			System.out.println("New Generation");
			newGeneration(parents);
			
			// evaluate new candidates
			
			
			// select individuals for next gen
			System.out.println("-------------- Generation Over -------------------");
			
		}
	}	

	private ArrayList<Expression> tournament() {
		System.out.println("---------- Starting Tournament ------------");
		ArrayList<Expression> winners = new ArrayList<>();
		ArrayList<Expression> tournamentPopulation = new ArrayList<>();
		HashSet<Integer> toChoose = new HashSet<>();
		Random r = new Random();
		
		Collections.sort(population);
		tournamentPopulation.add(new Expression(population.get(0).getExpression()));
		tournamentPopulation.get(0).setFitness(getFitnessOfExpression(0));
		boolean unique = false;
		
		for (int i = 0; i < TOURNAMENT_SIZE-1; i++) {
			while (!unique) {
				if (toChoose.add(r.nextInt(population.size()))) {
					unique = true;
				}
			}
			tournamentPopulation.add(population.get(r.nextInt(population.size())));
		}
		// elitism
		//winners.add(findBest(population, 1).get(0));
		winners.addAll(findBest(tournamentPopulation, NUM_PARENTS));
		System.out.println("Winners : " + winners.size());
		System.out.println("-------------- Done Tournament ------------- ");
		return winners;
	}
	
	private void newGeneration(final ArrayList<Expression> parents) {
		System.out.println("-------- Create New Generation ----------");
		Collections.sort(parents);
		
		ArrayList<Expression> newGen = new ArrayList<>();

		for (int i = 0; i < POPULATION_SIZE; i++)
		{
			if (i < parents.size()) {
				newGen.add(parents.get(i)); // add best parent
			}
			else {
				String[] expr = parents.get(0).getExpression();
				
				newGen.add(new Expression(expr));
				newGen.get(i).mutateByChange();
			}
		}
		
		population.clear();
		population.addAll(newGen);
		for (int i = 0; i < population.size(); i++) {
			population.get(i).setFitness(getFitnessOfExpression(i));
		}
		Collections.sort(population);
		for (int i = 0; i < population.size(); i++) {
			System.out.println(population.get(i).toString());
		}
		
		System.out.println("----------- Done New Generation ----------");
	}
	
	
	/**
	 * Initialise the population.
	 */
	private void initialise() {
		System.out.println("----------------Initialising----------------");
		population = new ArrayList<>();
		
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new Expression(data.getDataLength()-1));
			population.get(i).setFitness(Math.abs(getFitnessOfExpression(i)));
		}
		String result = "";
		for (int i = 0; i < population.size(); i++)
		{
			result += "[ ";
			for (int j = 0; j < population.get(i).size(); j++)
			{
				result += population.get(i).getExpressionPart(j) + "  ";
			}
			result += "]\n";
		}
		
		System.out.println("---------- Done Initialising -----------");
		//System.out.println("Size = " + population.size() + "\n" + result);	
	}
	
	/**
	 * Take an array of operators and the index for the set of operands held in the data set, and 
	 * create an expression from it in String[] form.
	 * 
	 * @param ops The array of operators.
	 * @param index	The index in which the operands can be found in the data set.
	 * @return The String[] representation of the combined expression.
	 */
	private String[] buildExpression(Expression ops, int index) {
		
		String[] expr = new String[ops.size() + data.getDataLength()];
		Double[] values = data.getData(index);		
		int op = 0;
		int num = 0;
		
		// create expression with operands and operators
		for (int i = 0; i < (data.getDataLength()*2)-1; i++) {
			if (i%2 == 0) {
				expr[i] = String.valueOf(values[num]);
				num++;
			} else if (i < (data.getDataLength()*2)-1) {
					expr[i] = ops.getExpressionPart(op);
					op++;
			}
		}
		
		/*
		// ensure no x/0 or x*0
		for (int i = 0; i < expr.length; i++)
		{
			if ((expr[i] == "*" || expr[i] == "/")
					&& ((Double.valueOf(expr[i-1]) == 0.0 && Double.valueOf(expr[i+1]) == 0.0) 
							|| (Double.valueOf(expr[i-1]) == 0.0) 
							|| (Double.valueOf(expr[i+1]) == 0.0))) {
				expr[i] = "+";
			}
		}*/
		
		return expr;
	}

	/** 
	 * Evaluate an expression in String[] form, giving an answer as a Double.
	 * 
	 * @param expr The String[] representation of an expression, with each term as one index of the array.
	 * @return The evaluated Double answer to the inputed expression.
	 */
	private Double getExpressionResult(String[] expr) {
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
		//System.out.println(strExpr);
		
		Object result = null;
		try {
			result = scriptEngine.eval(strExpr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.valueOf(result.toString());
	}

	private Double getFitnessOfExpression(int operatorSet)
	{
		Double fitness = 0.0;
		String fitnesses = "";
		String[][] expressions = new String[data.size()][]; 
		for (int j = 0; j < data.size(); j++) {
			expressions[j] = buildExpression(population.get(operatorSet), j);
			//System.out.println("Operators " + operatorSet + " and values " + j + "\n" + evaluateExpression(expressions[j]));
			fitness += (getExpressionResult(expressions[j]) - actualValues.get(j));
			fitnesses += "Estimate: " + getExpressionResult(expressions[j]) + " Actual: " + actualValues.get(j) + " Difference: " +
					(getExpressionResult(expressions[j]) - actualValues.get(j) + "\n");
		}
		
		fitness = (fitness / data.size());
		
		//System.out.println("Fitness for operator set " + operatorSet + " is " + fitness);
		//System.out.println(fitnesses);
		return fitness;
	}
	
	private ArrayList<Expression> findBest(ArrayList<Expression> pop, int num) {		
		System.out.println("----------- Find Best --------------");
		System.out.println("Find the best out of:");
		Collections.sort(pop);
		
		for (int i = 0; i < pop.size(); i++) {
			System.out.println(pop.get(i).toString());
		}
 		
		ArrayList<Expression> best = new ArrayList<>();
		String s = "The best are: \n";
		for (int i = 0; i < num; i++) {
			best.add(pop.get(i));
			s += "Best " + i + " = " + best.get(i).getFitness() + "\n";
		}
		System.out.println(s);
		System.out.println("------------- Done Find Best ----------------");
		return best;
	}
	
	/** 
	 * Set up both the data set and actual values to compare to with values from the input file. 
	 * 
	 * @param file The file containing the test data.
	 */
	private void setupData(File file) {
		data = new DataSet<>();
		actualValues = new ArrayList<>();
		
		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int countNodes = 0;
		String line = "";
		
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			actualValues.add(Double.parseDouble(tokens[0]));
			
			for (int pos = 1; pos < tokens.length; pos++) {
				components[pos-1] = Double.parseDouble(tokens[pos]);	
			}
			
			countNodes++;
			data.add(components);
		}
		//System.out.println(data.toString());
	}
}
