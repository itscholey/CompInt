package cw;

import java.util.Comparator;
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
	
  public boolean equals(Object obj) {
	    if (!(obj instanceof Expression)) {
	      return false;
	    }
	    Expression emp = (Expression) obj;
	    return fitness.equals(emp.getFitness());
	  }
	  
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
	
	public String toString() {
		String s = "Values: ";
		
		for (int i = 0; i < expression.length; i++) {
			s += expression[i] + " ";
		}
		s += fitness;
		
		return s;
	}
}
