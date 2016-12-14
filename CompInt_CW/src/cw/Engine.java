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

public class Engine {

	private DataSet<Double> data;
	private ArrayList<Double> actualValues;
	private ArrayList<Expression> population;

	private static final Double  	RECOMBINATION_PROBABILITY = 1.00;
	private static final Double  	MUTATION_PROBABILITY = 0.7;
	private static final int 		NUM_PARENTS = 4;						// MIN parents = 2
	private static final String[]	EVOLUTIONARY_METHOD = {"twoOptGeneration", "simpleStep", "crowdingStep", "localSearch", "randomSearch"};
	private static final Boolean 	ELITISM = true;
	private static final String  	SURVIVOR_SELECTION = "Generational";
	private static final int 	 	POPULATION_SIZE = 100;
	private static final int 	 	GENERATIONS = 100;
	private static final int 	 	CROSSOVER_POINT = 5;
	private static final String	 	TRAIN_FILE = "C:/Users/Chloe/Downloads/cwk_train.csv";
	private static final String  	TEST_FILE = "C:/Users/Chloe/Downloads/cwk_test.csv";
	private static final int		SECONDS = 10;
	private Random r;
	private Scanner scanner;

	public static void main(String[] args) {
		new Engine();
	}

	/**
	 * Construct a new Engine object to initialise the program.
	 */
	public Engine() {
		setupData(new File(TRAIN_FILE));
		r = new Random();
		evolve(1); // IN evolutionary_method
	}

	/**
	 * 
	 * 
	 * @param method The evolutionary/genetic method to use, @see
	 */
	public void evolve(int method) {
		System.out.println("------------- Initialise ------------");
		initialise();
		System.out.println("--------- Done Initialising ---------");
		Collections.sort(population);
		System.out.println("Best in generation: " + getBestExpression(population));


		// evaluate each candidate

		// repeat until ( termination condition is satisfied ) do
		
		for (int i = 0; i < GENERATIONS; i++) {
			System.out.println("------------- Generation " + i + " -----------------");
			Collections.sort(population);
			for (int j = 0; j < population.size(); j++) {
				System.out.println("Best " + j + ": " + population.get(j).toString());
			}

			switch(method) 
			{
			case 0: twoOptGeneration(tournament());
			case 1: simpleStep();
			//case 2: crowdingStep();
			case 3: localSearch();
			case 4: randomSearch();
			default: randomSearch();
			}
			
			Collections.sort(population);
			for (int j = 0; j < 5; j++) {
				System.out.println("Best " + j + ": " + population.get(j).toString());
			}
			
		}


	}

	public void crowdingStep() {
		ArrayList<Expression> parentPool = new ArrayList<>();
		ArrayList<Expression> children = new ArrayList<>();
		Collections.shuffle(population);
		int parents = NUM_PARENTS;
		if (parents%2 != 0) { 
			parents++;
		}

		/* ****** PHASE 1 ****** */
		for (int i = 0; i < parents; i++) {
			parentPool.add(population.get(i).clone());			
		}

		/* ****** PHASE 2 ****** */

		for (int i = 0; i < parentPool.size(); i+=2) {
			// Crossover
			if (r.nextDouble() < RECOMBINATION_PROBABILITY) {
				// one-point crossover
				children.addAll(crossover(CROSSOVER_POINT, parentPool.get(i), parentPool.get(i+1)));
			}
			// Mutation
			else {
				children.add(parentPool.get(i).clone());
				children.add(parentPool.get(i+1).clone());
				if (r.nextDouble() < MUTATION_PROBABILITY)	children.get(i).mutateByChange();
				if (r.nextDouble() < MUTATION_PROBABILITY)	children.get(i+1).mutateByChange();
			}
		}

		/* ****** PHASE 3 ****** */
		for (int parent = 0; parent < parentPool.size(); parent++) {
			for (int child = 0; child < children.size(); child++) {

			}
		}

		/* ****** PHASE 4 ****** */

		/* ****** PHASE 5 ****** */

	}

	public void twoOptGeneration(ArrayList<Expression> parents) {
		HashSet<Expression> pool = new HashSet<>();
		ArrayList<Expression> newGenPool = new ArrayList<>();

		for (int i = 0; i < parents.size(); i++) {
			pool.addAll(twoOptNeighbourhood(parents.get(i)));
		}
		newGenPool.addAll(pool);
		Collections.sort(newGenPool);
		population.clear();
		int index = 0;
		if (ELITISM) {
			population.add(newGenPool.get(index).clone());
			index++;
		}
		for (int i = index; i < POPULATION_SIZE; i++) {
			population.add(newGenPool.get(r.nextInt(newGenPool.size())).clone());
		}

		Collections.sort(population);
		for (int i = 0; i < population.size(); i++) {
			System.out.println(population.get(i).toString());
		}
	}

	public void simpleStep() {
		ArrayList<Expression> newPop = new ArrayList<>();
		for (int i = 0; i < population.size(); i++) {
			// child = oldPop[i]
			Expression child = population.get(i).clone();
			// mutate with prob of mutation
			if (r.nextDouble() <= MUTATION_PROBABILITY) {
				child.mutateByChange();
				child.setFitness(getExpressionAverage(child));
			}
			// tournament of child and parent
			ArrayList<Expression> pool = new ArrayList<>();
			pool.add(child);
			pool.add(population.get(i));
			newPop.add(bestNeighbour(pool));
		}

		population.clear();
		population.addAll(newPop);
	}

	public ArrayList<Expression> crossover(int crossPoint, 
			Expression parentA, Expression parentB) {

		String[] first = parentA.getExpression().clone();
		String[] second = parentB.getExpression().clone();
		String[] childOne = first.clone();
		String[] childTwo = second.clone();
		ArrayList<Expression> result = new ArrayList<>();

		for (int i = crossPoint; i < parentA.getExpression().length; i++) {
			childOne[i] = second[i];
			childTwo[i] = first[i];
		}

		result.add(new Expression(childOne));
		result.add(new Expression(childTwo));
		result.get(0).setFitness(getExpressionAverage(result.get(0)));
		result.get(1).setFitness(getExpressionAverage(result.get(1)));

		System.out.println("\n\n" + parentA.toString() + "\n" + parentB.toString() + "\n" + result.get(0).toString() + "\n" + result.get(1).toString());
		return result;
	}

	private ArrayList<Expression> tournament() {
		System.out.println("---------- Tournament ------------");
		ArrayList<Expression> parents = new ArrayList<>();

		int index = 0;
		if (ELITISM) {
			parents.add(bestNeighbour(population).clone());
			index++;
		}
		for (int i = index; i < NUM_PARENTS; i++) {
			parents.add(population.get(r.nextInt(population.size())).clone());
		}
		Collections.sort(parents);
		for (int i = 0; i < parents.size(); i++) {
			System.out.println(parents.get(i).toString());
		}

		System.out.println("-------------- End of Tournament --------------");
		return parents;
	}

	private Expression localSearch() {
		int iterations = 0;
		int seconds = 50;

		Collections.sort(population);
		Expression expr = new Expression(population.get(0).getExpression().clone());
		expr.setFitness(getExpressionAverage(expr));
		Expression best = new Expression(expr.getExpression().clone());
		best.setFitness(getExpressionAverage(expr));
		long time = System.currentTimeMillis() + (seconds*1000);

		while(System.currentTimeMillis() < time) {
			System.out.println("\nGeneration: " + iterations + "\nTwo Opt of : " + expr.toString());
			ArrayList<Expression> nbhd = twoOptNeighbourhood(expr);
			expr = bestNeighbour(nbhd);
			System.out.println("Best neighbour: " + expr.toString());

			if (Math.abs(expr.getFitness()) < Math.abs(best.getFitness())) {
				best.setExpression(expr.getExpression().clone());
				best.setFitness(getExpressionAverage(best));
				System.out.println("*** New Best *** " + best.toString());
			}
			iterations++;
		}
		System.out.println("Best is " + best.toString());
		return best;		
	}

	public void initialise() {
		population = new ArrayList<>();
		HashSet<Expression> pop = new HashSet<>();

		for (int i = 0; i < POPULATION_SIZE; i++) {
			boolean unique = false;
			while (!unique) {
				if (pop.add(new Expression(data.getDataLength()-1))) {
					unique = true;
				}
			}
		}

		population.addAll(pop);

		for (int i = 0; i < population.size(); i++) {
			population.get(i).setFitness(getExpressionAverage(population.get(i)));
			System.out.println(population.get(i).toString());
		}

	}

	private Expression bestNeighbour(ArrayList<Expression> nbhd) {
		Collections.sort(nbhd);
		return nbhd.get(0);
	}


	private ArrayList<Expression> twoOptNeighbourhood(final Expression expr) {
		HashSet<Expression> nbhd = new HashSet<>();
		String[] tmpExpr = new String[expr.getExpression().length];
		String tmpPart = "";

		// 1, 2, 3, ...
		for (int i = 0; i < expr.getExpression().length; i++) {
			//System.out.println(i);
			// 1-2, 1-3, 1-4, ...
			for (int gap = 1; gap+i < expr.getExpression().length; gap++) {
				tmpExpr = expr.getExpression().clone();
				tmpPart = expr.getExpression()[i];
				tmpExpr[i] = expr.getExpression()[i+gap];
				tmpExpr[i+gap] = tmpPart;
				Expression e = new Expression(tmpExpr);
				e.setFitness(getExpressionAverage(e));
				nbhd.add(e);
				//System.out.println(e.toString());
			}
		}

		//System.out.println("Neighbourhood");
		ArrayList<Expression> result = new ArrayList<>();
		result.addAll(nbhd);
		Collections.sort(result);

		/*for (Expression e : result) {
			System.out.println(e.toString());
		}	*/	
		return result;
	}

	private String getBestExpression(ArrayList<Expression> area) {
		Collections.sort(area);
		return area.get(0).toString();
	}

	private Double getExpressionAverage(Expression expr)
	{
		Double fitness = 0.0;
		String fitnesses = "";
		String[][] expressions = new String[data.size()][]; 
		for (int j = 0; j < data.size(); j++) {
			expressions[j] = buildExpression(expr, j);
			fitness += (getExpressionResult(expressions[j]) - actualValues.get(j));
			fitnesses += "Estimate: " + getExpressionResult(expressions[j]) + " Actual: " + actualValues.get(j) + " Difference: " +
					(getExpressionResult(expressions[j]) - actualValues.get(j) + "\n");
		}
		fitness = (fitness / data.size());

		//System.out.println("Fitness is " + fitness + "\n" + fitnesses);
		return fitness;
	}

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
		return expr;
	}

	private Double getExpressionResult(String[] expr) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine scriptEngine = sem.getEngineByName("JavaScript");

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

		Object result = null;
		try {
			result = scriptEngine.eval(strExpr);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.valueOf(result.toString());
	}

	private void setupData(File file) {
		data = new DataSet<>();
		actualValues = new ArrayList<>();

		try {
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line = "";

		while (scanner.hasNextLine()) {
			//while (count < 3) {
			line = scanner.nextLine();
			// set up line as double values
			String[] tokens = line.split(",");
			Double[] components = new Double[tokens.length-1];
			actualValues.add(Double.parseDouble(tokens[0]));

			for (int pos = 1; pos < tokens.length; pos++) {
				components[pos-1] = Double.parseDouble(tokens[pos]);	
			}
			data.add(components);
		}
		System.out.println(data.toString());
	}

	private Expression randomSearch() {

		Expression random = new Expression(12);
		random.setFitness(getExpressionAverage(random));
		Expression best = new Expression(random.getExpression().clone());
		best.setFitness(getExpressionAverage(random));
		long time = System.currentTimeMillis() + (SECONDS*1000);
		int iterations = 0;

		while(System.currentTimeMillis() < time) {
			random.randomExpression(12);
			random.setFitness(getExpressionAverage(random));
			System.out.println(random.toString());

			if (Math.abs(random.getFitness()) < Math.abs(best.getFitness())) {
				best.setExpression(random.getExpression().clone());
				best.setFitness(getExpressionAverage(best));
				System.out.println("*** New Best *** " + best.toString());
			}
			iterations++;
		}
		System.out.println("Best is " + best.toString());
		return best;
	}
}
