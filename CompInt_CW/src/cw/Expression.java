package cw;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.lang.Math;

/**
 * A Class to model an Expression, which is a String[] of operators.
 * 
 * @author Chloe Barnes, 139006412
 * @version CS3910 Computational Intelligence Logistics Coursework
 * @version 15th December 2016
 */
public class Expression implements Comparable<Expression>, Cloneable {
	private static final String[] OPERATORS = { "+", "-", "*", "/", "%", "^"};
	private String[] expression;
	private Double fitness;
	
	/**
	 * Construct a new Expression object, with a provided String[] of operators.
	 * 
	 * @param expr String[] of the operators.
	 */
	public Expression(String[] expr) {
		this.expression = expr;
	}
	
	/**
	 * Construct a new Expression object, with a random String[] of operators on
	 * a given length.
	 * 
	 * @param length How long the Expression is.
	 */
	public Expression(int length) {
		randomExpression(length);
	}
	
	/**
	 * Construct an empty Expression.
	 */
	public Expression() {
	}
	
	/**
	 * Set the value of the expression field.
	 * 
	 * @param expr The expression to set as.
	 */
	public void setExpression(String[] expr) {
		this.expression = expr;
	}
	
	/**
	 * Get the expression representation.
	 * 
	 * @return The String[] representation.
	 */
	public String[] getExpression() {
		return expression;
	}
	
	/**
	 * Return the size of the expression.
	 * 
	 * @return The size of the expression.
	 */
	public int size() {
		return expression.length;
	}
	
	/**
	 * Set the fitness of the expression.
	 * 
	 * @param f The fitness to set as.
	 */
	public void setFitness(Double f) {
		this.fitness = f;
	}
	
	/**
	 * Return the fitness of the expression.
	 * 
	 * @return The Double value of the expression.
	 */
	public Double getFitness() {
		return fitness;
	}
	
	/**
	 * Return a single element of the expression.
	 * 
	 * @param part The index to get.
	 * @return The String at the index.
	 */
	public String getExpressionPart(int part) {
		return expression[part];
	}
	  
	/**
	 * Mutate the expression by a random amount.
	 * 
	 * @return This Expression object.
	 */
	public Expression mutateByChange() {
		Random r = new Random();
		String[] swapped = getExpression();
		int toMutate = r.nextInt(expression.length);
		
		for (int i = 0; i < toMutate; i++) {
			int toSwap = r.nextInt(expression.length);
			swapped[toSwap] = OPERATORS[r.nextInt(OPERATORS.length)];
		}
		
		setExpression(swapped);
		
		return this;
	}
	
	/**
	 * Return a random operator from the valid operators list.
	 * 
	 * @return A random operator.
	 */
	public static String getRandomOperator() {
		Random r = new Random();
		String result = OPERATORS[r.nextInt(OPERATORS.length)];
		return result;
	}
	
	/**
	 * Generate a random string of operators.
	 * 
	 * @return A String[] containing the random operators.
	 */
	public void randomExpression(int length) {
		expression = new String[length];
		
		for (int i = 0; i < length; i++) {
			expression[i] = Expression.getRandomOperator();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(expression);
		result = prime * result + ((fitness == null) ? 0 : fitness.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expression other = (Expression) obj;
		if (!Arrays.equals(expression, other.expression))
			return false;
		if (fitness == null) {
			if (other.fitness != null)
				return false;
		} else if (!fitness.equals(other.fitness))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String s = "Values: ";
		
		for (int i = 0; i < expression.length; i++) {
			s += expression[i] + " ";
		}
		s += fitness;
		
		return s;
	}
	
	@Override
	public int compareTo(Expression otherExpr) {
		Double ftns = Math.abs(fitness);
		Double otrftns = Math.abs(otherExpr.getFitness());
		return ftns.compareTo(otrftns);
    }

	@Override
	public Expression clone() {
		final Expression clone;
		try {
			clone = (Expression) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException("Cannot clone.", e);
		}
		clone.setExpression(this.expression.clone());
		clone.setFitness(this.fitness);
		return clone;
	}
}
