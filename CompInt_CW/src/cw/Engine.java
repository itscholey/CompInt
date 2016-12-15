package cw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A Class to model evolutionary and genetic algorithms to tackle a Logistics
 * problem with Computational Intelligence methods.
 * 
 * Please change the Variation Operators to get varied results.
 * 
 * 
 * An example analysis of the algorithm is as follows (actual results):
 * Algorithm used was: Local Search
 * Population size: 10   Generations: 150
 * Best solution was: Values: + - ^ + + - / % ^ + + / 35.194736842105264
 * TRAIN AVERAGE = 35.194736842105264
 * TEST AVERAGE  = 17.835526315789473

 * 
 * @author Chloe Barnes, 139006412
 * @version CS3910 Computational Intelligence Logistics Coursework
 * @version 15th December 2016
 */
public class Engine {

	// The data set to train the algorithm on
	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	// The data set to test the algorithm on
	private DataSet<Double> testData;
	private ArrayList<Double> testActual;
	// The Expression population to evolve
	private ArrayList<Expression> population;	
	
	/* Variation Operators */
	private static final Double  	RECOMBINATION_PROBABILITY = 0.7;
	private static final Double  	MUTATION_PROBABILITY = 0.7;
	private static final int 		NUM_PARENTS = 4;
	// Change the method used by passing an int to *evolve(i)* in Engine()
	private static final String[]	EVOLUTIONARY_METHOD = {"Two Opt Generation", "Simple Step", "Local Search", "Random Search"};
	private static final Boolean 	ELITISM = true;
	private static final int 	 	POPULATION_SIZE = 10;
	private static final int 	 	GENERATIONS = 150;
	private static final int 	 	CROSSOVER_POINT = 3;
	
	// The files to read in the data from
	private static final String	 	TRAIN_FILE = "C:/Users/Chloe/Downloads/cwk_train.csv";
	private static final String  	TEST_FILE = "C:/Users/Chloe/Downloads/cwk_test.csv";
	private Random r;
	private Scanner scanner;

	/**
	 * Run the main loop for the Engine class.
	 * 
	 * @param args Array of arguments to pass into the main loop.
	 */
	public static void main(String[] args) {
		new Engine(); 
	}

	/**
	 * Construct and initialise a new Engine instance.
	 * Set up the data sets and start the evolutionary algorithm.
	 */
	public Engine() {
		// Set up the train data
		Object[] train = setupData(new File(TRAIN_FILE), data, actualValues);
		data = (DataSet<Double>) train[0]; 
		actualValues = (ArrayList<Double>) train[1];
		
		// Set up the test data
		Object[] test = setupData(new File(TEST_FILE), testData, testActual);
		testData = (DataSet<Double>) test[0];
		testActual = (ArrayList<Double>) test[1];
		
		// Instantiate variables
		r = new Random();
		
		// Start the evolutionary algorithm
		evolve(2);
	}

	/**
	 * Run an evolutionary algorithm for the given number of generations, and for
	 * the specified evolutionary algorithm method.
	 * 
	 * @param method The algorithm to use.
	 * @see EVOLUTIONARY_METHOD
	 */
	public void evolve(int method) {
		System.out.println("------------- Initialising ------------");
		initialise();

		// The Generation Loop
		for (int i = 0; i < GENERATIONS; i++) {
			System.out.println("------------- Generation " + i + " -----------------");
			
			// Choose which evolutionary step to use
			switch(method) 
			{
			case 0: twoOptGeneration(tournament()); break;
			case 1: simpleStep(); break;
			case 2: localSearch(); break;
			case 3: randomSearch(); break;
			default: randomSearch(); break;
			}

			// Print the best five results
			Collections.sort(population);
			for (int j = 0; j < 5; j++) {
				System.out.println("Best " + j + ": " + population.get(j).toString());
			}
		}
		
		System.out.println("\n ********** END OF ALGORITHM **********\n");
		System.out.println(getAnalysis(method));
	}

	/**
	 * Two Opt Generation algorithm step; generates a two-opt neighbourhood for each of
	 * the parents chosen, and chooses the best set of generated children to go forward
	 * as the next generation.
	 * 
	 * @param parents The set of parents that a two opt neighbourhood will be generated
	 * 		 		  from, and subsequently the new generation will be taken from.
	 */
	public void twoOptGeneration(ArrayList<Expression> parents) {
		ArrayList<Expression> pool = new ArrayList<>();

		// For each parent, add the two opt neighbourhood to the pool to choose from
		for (int i = 0; i < parents.size(); i++) {
			pool.addAll(twoOptNeighbourhood(parents.get(i)));
		}
		Collections.sort(pool);
		population.clear();
		int index = 0;
		if (ELITISM) {
			population.add(pool.get(index).clone());
			index++;
		}
		
		// Get the best solutions from the neighbourhood pool
		for (int i = index; i < POPULATION_SIZE; i++) {
			population.add(pool.get(r.nextInt(pool.size())).clone());
		}
	}

	/**
	 * Simple Step evolutionary algorithm step; incorporates mutation with a probability
	 * as well as novel parent selection: a child is generated from a parent and mutated 
	 * with a probability - the parent and child then enter a tournament to select which
	 * of the two will go into the next generation (Deterministic Replacement/Crowding).
	 */
	public void simpleStep() {
		ArrayList<Expression> newPop = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			// Create a clone of the parent
			Expression child = population.get(i).clone();
			
			// Mutate child with a probability of mutation
			if (r.nextDouble() <= MUTATION_PROBABILITY) {
				child.mutateByChange();
				child.setFitness(getExpressionAverage(child, data, actualValues));
			}
			
			// Tournament of child and parent to decide survivor
			ArrayList<Expression> pool = new ArrayList<>();
			pool.add(child);
			pool.add(population.get(i));
			newPop.add(bestNeighbour(pool));
		}

		population.clear();
		population.addAll(newPop);
	}

	/**
	 * Local Search genetic algorithm step; incorporates mutation and crossover with a
	 * probability. A Generational approach (all elements last exactly one generation).
	 * When <ELITISM> is true, the best solution from the previous generation persists. 
	 */
	public void localSearch() {
		Collections.sort(population);

		ArrayList<Expression> parents = (ArrayList<Expression>) tournament().clone();
		ArrayList<Expression> children = new ArrayList<>();

		// Parents should an even number
		int pars = NUM_PARENTS;
		if (pars%2 != 0) { 
			pars--;
		} else if (pars < 2) {
			pars = 2;
		}
			
		// Recombination with one-point crossover
		for (int i = 0; i < pars-1; i += 2) {
			if (r.nextDouble() < RECOMBINATION_PROBABILITY) {
				children.addAll(crossover(CROSSOVER_POINT, parents.get(i), parents.get(i+1)));
			}
		}

		// Random mutation
		for (int i = 0; i < children.size(); i++) {
			if (r.nextDouble() < MUTATION_PROBABILITY) {
				children.set(i, children.get(i).mutateByChange().clone());
			}
			children.get(i).setFitness(getExpressionAverage(children.get(i), data, actualValues));
		}
		
		// Add the best element to the next generation if elitism is chosen
		if (ELITISM) {
			children.add(population.get(0).clone());
		}
		
		for (int i = children.size(); i < POPULATION_SIZE; i++) {
			children.add(children.get(i%children.size()).clone().mutateByChange());
			children.get(i).setFitness(getExpressionAverage(children.get(i), data, actualValues));
		}

		population = (ArrayList<Expression>) children.clone();
	}

	/**
	 * Random Search algorithm step; generates random solutions and keeps the best one.
	 */
	public void randomSearch() {

		Collections.sort(population);
		
		ArrayList<Expression> newPop = new ArrayList<>();
		newPop.add(population.get(0).clone());
		
		for (int i = 1; i < POPULATION_SIZE; i++) {
			newPop.add(new Expression(population.get(0).size()));
			newPop.get(i).setFitness(getExpressionAverage(newPop.get(i), data, actualValues));
		}
		
		population.clear();
		population.addAll(newPop);
	}
	
	/**
	 * Perform one-point crossover, beginning at the crossover point crossPoint
	 * for parents parentA and parentB.
	 * 
	 * @param crossPoint The point in which to cross the two parents over.
	 * @param parentA The first parent Expression.
	 * @param parentB The second parent Expression.
	 * @return ArrayList<Expression> containing the combined children.
	 */
	private ArrayList<Expression> crossover(int crossPoint, 
			Expression parentA, Expression parentB) {

		// Set up the crossover elements
		String[] first = parentA.getExpression().clone();
		String[] second = parentB.getExpression().clone();
		String[] childOne = first.clone();
		String[] childTwo = second.clone();
		ArrayList<Expression> result = new ArrayList<>();

		// After the crossPoint, swap elements from parentsA and B
		for (int i = crossPoint; i < parentA.getExpression().length; i++) {
			childOne[i] = second[i];
			childTwo[i] = first[i];
		}

		result.add(new Expression(childOne));
		result.add(new Expression(childTwo));
		result.get(0).setFitness(getExpressionAverage(result.get(0), data, actualValues));
		result.get(1).setFitness(getExpressionAverage(result.get(1), data, actualValues));

		return result;
	}

	/**
	 * Tournament the population for the number of parents NUM_PARENTS.
	 * 
	 * @return ArrayList<Expression> containing the winning parents.
	 */
	private ArrayList<Expression> tournament() {
		ArrayList<Expression> parents = new ArrayList<>();

		// Increment index if Elitist
		int index = 0;
		if (ELITISM) {
			parents.add(bestNeighbour(population).clone());
			index++;
		}
		for (int i = index; i < NUM_PARENTS; i++) {
			parents.add(population.get(r.nextInt(population.size())).clone());
		}
		return parents;
	}

	/**
	 * Return the best neighbour in the neighbourhood.
	 * 
	 * @param nbhd The Expressions to find the best in.
	 * @return The best Expression.
	 */
	private Expression bestNeighbour(ArrayList<Expression> nbhd) {
		Collections.sort(nbhd);
		return nbhd.get(0);
	}

	/**
	 * A helper method to return the two opt neighbourhood for a given Expression. This
	 * is every element in the Expression swapped, i.e. for [^+-/], the output would be
	 * as follows:
	 * 
	 * <[+^-/], [-+^/], [/+-^], [^-+/], [^/-+], [^+/-]>
	 * 
	 * @param expr The Expression to find the two opt neighbourhood for.
	 * @return The two opt neighbourhood.
	 */
	private ArrayList<Expression> twoOptNeighbourhood(final Expression expr) {
		ArrayList<Expression> result = new ArrayList<>();
		// A temporary array to store the expression being built
		String[] tmpExpr = new String[expr.getExpression().length];
		// A temporary variable to store during swap
		String tmpPart = "";

		// 1, 2, 3, ...
		for (int i = 0; i < expr.getExpression().length; i++) {
			// 1-2, 1-3, 1-4, ...
			for (int gap = 1; gap+i < expr.getExpression().length; gap++) {
				tmpExpr = expr.getExpression().clone();
				tmpPart = expr.getExpression()[i];
				tmpExpr[i] = expr.getExpression()[i+gap];
				tmpExpr[i+gap] = tmpPart;
				Expression e = new Expression(tmpExpr);
				e.setFitness(getExpressionAverage(e, data, actualValues));
				result.add(e);
			}
		}
		return result;
	}

	/**
	 * Return the average evaluated solution for the Expression, based on actual values
	 * passed in and a data set.
	 * 
	 * @param expr The Expression to evaluate and return an answer for.
	 * @param d The data set to apply the Expression to.
	 * @param actual The actual values to compare the average with.
	 * @return The Double value of the average of the Expression.
	 */
	private Double getExpressionAverage(Expression expr, DataSet<Double> d, ArrayList<Double> actual)
	{
		Double fitness = 0.0;
		String[] expressions = new String[d.size()]; 
		for (int j = 0; j < d.size(); j++) {
			expressions[j] = buildExpression(expr, d.getData(j));
			fitness += Math.abs(getExpressionResult(expressions[j]) - actual.get(j));
		}
		fitness = (fitness / data.size());
		return fitness;
	}

	/**
	 * Build a String representation of an Expression based on a set of data.
	 * 
	 * @param ops The Expression operators to use.
	 * @param data The data to use in the expression.
	 * @return A String representation of the Expression combined with data.
	 */
	private String buildExpression(Expression ops, Double[] data) {

		String expr = "";
		Double[] values = data.clone();		
		int op = 0;
		int num = 0;

		// Create expression with operands and operators
		for (int i = 0; i < (data.length*2)-1; i++) {
			if (i%2 == 0) {
				expr += String.valueOf(values[num]) + " ";
				num++;
			} else if (i < (data.length*2)-1) {
				expr += ops.getExpressionPart(op) + " ";
				op++;
			}
		}
		return expr;
	}

	/**
	 * Return the result a String representation of an arithmetic expression evaluates
	 * to.
	 * 
	 * @param expr The String representation of an arithmetic expression to evaluate.
	 * @return The Double value that the String evaluates to.
	 */
	private Double getExpressionResult(String expr) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine scriptEngine = sem.getEngineByName("JavaScript");

		Object result = null;
		try {
			result = scriptEngine.eval(expr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.valueOf(result.toString());
	}

	/**
	 * Initialise the population with random solutions.
	 */
	private void initialise() {
		population = new ArrayList<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new Expression(data.getDataLength()-1));	
			population.get(i).setFitness(getExpressionAverage(population.get(i), data, actualValues));
		}
	}
	
	/**
	 * Read in and set up the data sets that the algorithms are based on.
	 * 
	 * @param file The file to read in.
	 * @param d The data set to write to.
	 * @param actual The set of actual data to compare to.
	 * @return Object[] of both the populated data set and actual data set.
	 */
	private Object[] setupData(File file, DataSet<Double> d, ArrayList<Double> actual) {
		d = new DataSet<>();
		actual = new ArrayList<>();

		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line = "";

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// Set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			actual.add(Double.parseDouble(tokens[0]));

			for (int pos = 1; pos < tokens.length; pos++) {
				components[pos-1] = Double.parseDouble(tokens[pos]);	
			}
			d.add(components);
		}

		Object[] rtn = new Object[2];
		rtn[0] = d;
		rtn[1] = actual;
		return rtn;
	}
	
	/**
	 * Return a String representation of detail on the evolutionary algorithm.
	 * 
	 * @param evolutionMethod The index of the EVOLUTIONARY_METHOD array to use.
	 * @return The String representation of analysis.
	 */
	private String getAnalysis(int evolutionMethod) {
		String analysis = "";

		for (int i = 0; i < data.size(); i++) {
			String exp = buildExpression(population.get(0), data.getData(i));
			analysis += "Row " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + actualValues.get(i) + "\n";
		}

		for (int i = 0; i < testData.size(); i++) {
			String exp = buildExpression(population.get(0), testData.getData(i));
			analysis += "Test Row: " + i + " is: " + exp + "\n";
			analysis += "      Estimated: " + getExpressionResult(exp) + "\n";
			analysis += "      Actual:    " + testActual.get(i) + "\n";
		}
		analysis += "Algorithm used was: " + EVOLUTIONARY_METHOD[evolutionMethod] + "\n";
		analysis += "Population size: " + POPULATION_SIZE + "   Generations: " + GENERATIONS + "\n";
		analysis += "Best solution was: " + population.get(0) + "\n";
		analysis += " TRAIN AVERAGE = " + getExpressionAverage(population.get(0), data, actualValues) + "\n";
		analysis += " TEST AVERAGE  = " + getExpressionAverage(population.get(0), testData, testActual) + "\n";
		return analysis;
	}
}
