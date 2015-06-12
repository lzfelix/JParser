package lexer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
	private Lexer lexer;
	private Queue<Token> output;
	
	public Parser(String expression) throws ParserException {
		lexer = new Lexer(expression);
		
		Queue<Token> tokens = lexer.getList();
		Stack<Token> operatorsStack = new Stack<>();
		output = new LinkedList<Token>();
		
		while (!tokens.isEmpty()) {
			Token current = tokens.poll();
			
			//TODO: interpret variables
			
			if (current instanceof NumericToken || current instanceof FunctionToken)
				output.add(current);
			
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
			}
			
			else {
				//can only be now an operator or function (since all functions have  
				//just one argument, for now)
				
				while (operatorsStack.size() > 0) {
					if ((operatorsStack.peek().getPriority() > current.getPriority() && current.getType() == Token.Type.POW) ||
						(operatorsStack.peek().getPriority() >= current.getPriority() && current.getType() != Token.Type.POW)) {
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
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		for (Token t : output)
			buffer.append(t.toString() + " ");
		
		return buffer.toString().substring(0, buffer.length() - 1);
	}
	
	public static void main(String args[]) {
		Parser p;
		
		try {
			p = new Parser("1/(1+2)");
			System.out.println(p.toString());
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
}
