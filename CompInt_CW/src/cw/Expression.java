package cw;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.lang.Math;

public class Expression implements Comparable<Expression> {
	private static final String[] OPERATORS = { "+", "-", "*" };
	private String[] expression;
	private Double fitness;
	
	public Expression(String[] expr) {
		this.expression = expr;
	}
	
	public Expression(int length) {
		randomExpression(length);
	}
	
	public Expression() {
	}
	
	public void setExpression(String[] expr) {
		this.expression = expr;
	}
	public String[] getExpression() {
		return expression;
	}
	
	public int size() {
		return expression.length;
	}
	
	public void setFitness(Double f) {
		this.fitness = f;
	}
	
	public Double getFitness() {
		return fitness;
	}
	
	public String getExpressionPart(int part) {
		return expression[part];
	}
	
	public int compareTo(Expression otherExpr) {
		Double ftns = Math.abs(fitness);
		Double otrftns = Math.abs(otherExpr.getFitness());
		return ftns.compareTo(otrftns);
    }
	
	public static Comparator<Expression> ExpressionComparator
    = new Comparator<Expression>() {

		public int compare(Expression expr1, Expression expr2) {

			Double exprFit1 = Math.abs(expr1.getFitness());
			Double exprFit2 = Math.abs(expr2.getFitness());

			//ascending
			return exprFit1.compareTo(exprFit2);

			//descending
			//return exprFit2.compareTo(exprFit1);
		}
	
	};
	  
	public Expression mutateByChange() {
		Random r = new Random();
		String[] swapped = getExpression();
		
		for (int i = 0; i < 3; i++) {
			int toSwap = r.nextInt(expression.length);
			swapped[toSwap] = OPERATORS[r.nextInt(OPERATORS.length)];
		}
		
		setExpression(swapped);
		
		return this;
	}
	
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

	public String toString() {
		String s = "Values: ";
		
		for (int i = 0; i < expression.length; i++) {
			s += expression[i] + " ";
		}
		s += fitness;
		
		return s;
	}
}
