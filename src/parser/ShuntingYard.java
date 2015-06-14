package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import lexer.FunctionToken;
import lexer.IntegerToken;
import lexer.Lexer;
import lexer.DecimalToken;
import lexer.Token;
import lexer.VariableToken;
import exceptions.LexerException;
import exceptions.ParserException;

public class ShuntingYard {
	private Lexer lexer;
	private Queue<Token> output;
	private static ShuntingYard instance;

	private ShuntingYard() {
		lexer = Lexer.getInstance();
	}
	
	/**
	 * Returns the ShuntingYard instance, since this is a Singleton class.
	 * @return
	 */
	public static ShuntingYard getInstance() {
		if (instance == null)
			instance = new ShuntingYard();
		
		return instance;
	}
	
	public void convertFromInfixToPosfix(String expression) throws ParserException, LexerException {
		lexer = Lexer.getInstance();
		lexer.parseToTokens(expression);
		
		List<Token> tokens = lexer.getList();
		Stack<Token> operatorsStack = new Stack<>();
		output = new LinkedList<Token>();
		
		for (int i = 0; i < tokens.size(); i++) {
			Token current = tokens.get(i);
			
			if (current instanceof DecimalToken || current instanceof IntegerToken || current instanceof VariableToken)
				output.add(current);
			
			else if (current instanceof FunctionToken)
				operatorsStack.add(current);
			
			else if (current.getType() == Token.Type.LPAR)
				operatorsStack.push(current);
			
			else if (current.getType() == Token.Type.RPAR) {
				
				while (operatorsStack.peek().getType() != Token.Type.LPAR) {
					transfer(operatorsStack);
					
					if (operatorsStack.size() == 0)
						throw new ParserException("Mismatched parenthesis.1");
				}
				
				//get rid of )
				operatorsStack.pop();
				transfer(operatorsStack);
			}
			
			else {
				//can only be now an operator or function (since all functions have  
				//just one argument, for now)
				
				while (operatorsStack.size() > 0) {
					if ((operatorsStack.peek().getPriority() > current.getPriority() && (current.getType() == Token.Type.POW
							|| current.getType() == Token.Type.NEG)) ||
						(operatorsStack.peek().getPriority() >= current.getPriority() && current.getType() != Token.Type.POW
							&& current.getType() != Token.Type.NEG)) {
						transfer(operatorsStack);
					}
					else break;
				}
				
				operatorsStack.push(current);
			}
			
//			System.out.println("O = " + operatorsStack.toString());
//			System.out.println("N = " + output.toString());
		}
		
		while (operatorsStack.size() > 0) {
			if (operatorsStack.peek().getType() == Token.Type.LPAR)
				throw new ParserException("Mismatched parenthesis.2");
			
			transfer(operatorsStack);
		}
	}
	
	private void transfer(Stack<Token> operands) {
		Token transfered = operands.pop();
		
		if (transfered.getType() != Token.Type.LPAR && transfered.getType() != Token.Type.RPAR)
			output.add(transfered);
		
	}
	
	public Queue<Token> getExpression() {
		return this.output;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		for (Token t : output)
			buffer.append(t.toString() + " ");
		
		return buffer.toString().substring(0, buffer.length() - 1);
	}
}
