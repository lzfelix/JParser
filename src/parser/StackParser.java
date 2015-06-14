package parser;

import java.util.EmptyStackException;
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

public class StackParser {
	Stack<Double> numbers;
	Stack<Token> operators;
	
	private static StackParser instance = null;
	private double[] variablesVaue;
	
	
	private StackParser() {
		//creating a private constructor, so the Singleton pattern can be used
	}
	
	/**
	 * Retuns the StackParser's instance, since this is a Singleton class.
	 * @return
	 */
	public StackParser getInstance() {
		if (instance == null) 
			instance = new StackParser();
		
		return instance;
	}
		
	public void setVariable(double[] variablesVaue) {
		this.variablesVaue = variablesVaue;
		Lexer.getInstance().setAcceptVariables(true);
		Lexer.getInstance().setMaxDimension(variablesVaue.length);
	}
	
	public void setVariable(double value) {
		this.variablesVaue = new double[] {value};
		Lexer.getInstance().setAcceptVariables(true);
	}
	
	public double parse(String expression) throws ParserException, LexerException {
		ShuntingYard s = ShuntingYard.getInstance();
		
		s.convertFromInfixToPosfix(expression);
		Queue<Token> queue = s.getExpression();
		
		operators = new Stack<>();
		numbers = new Stack<>();
		
		double op1, op2;
		System.out.println(Lexer.getInstance());
		
		while (!queue.isEmpty()) {
			Token element = queue.poll();
			
			switch (element.getType()) {
				case NUM: 
					if (element instanceof DecimalToken)
						numbers.push(((DecimalToken)element).getValue());
					else // can only be a IntegerToken
						numbers.push((double) ((IntegerToken)element).getValue());
						
				break;
				
				case VAR:
					int index = ((VariableToken)element).getIndex();
					numbers.push(variablesVaue[index]);
				break;
					
				
				case ADD:
					numbers.push(tryPop() + tryPop());
				break;
					
				//subtraction it is not commutative
				case SUB:
					op1 = tryPop();
					op2 = tryPop();
					numbers.push(op2 - op1);
				break;
					
				case MUL:
					numbers.push(tryPop() * tryPop());
				break;
					
				case DIV:
					op1 = tryPop();
					op2 = tryPop();
					
					try {
						numbers.push(op2 / op1);
					}
					catch (Exception e) { //Division by zero
						throw new ParserException("Division by zero.");
					}
				break;
					
				case NEG: 
					numbers.push(-tryPop()); 
				break;
				
				case POW:
					op1 = tryPop();
					op2 = tryPop();
					numbers.push(Math.pow(op2, op1));
				break;
	
				case FUN:
					FunctionToken token = (FunctionToken)element;
					
					switch (token.getFunctionID()) {
						case sin: numbers.push(Math.sin(tryPop())); break;
						case cos: numbers.push(Math.cos(tryPop())); break;
						case tan: numbers.push(Math.tan(tryPop())); break;
						
						case sec: numbers.push(1 / Math.cos(tryPop())); break;
						case csc: numbers.push(1 / Math.sin(tryPop())); break;
						case ctg: numbers.push(1 / Math.tan(tryPop())); break;
						
						case sinh: numbers.push(Math.sinh(tryPop())); break;
						case cosh: numbers.push(Math.cosh(tryPop())); break;
						case tanh: numbers.push(Math.tanh(tryPop())); break;
						
						case asin: numbers.push(Math.asin(tryPop())); break;
						case acos: numbers.push(Math.acos(tryPop())); break;
						case atan: numbers.push(Math.atan(tryPop())); break;
						
						case ln: numbers.push(Math.log(tryPop())); break;
					}
				break;
					
				case COM: 
					throw new ParserException("Commas are not allowed. Use '.' as decimal separator."); 
					
				default: 
					throw new ParserException("Unknown error.");
			}
		}
		
		return tryPop();
	}
	
	private double tryPop() throws ParserException {
		Double toReturn = null;
		
		try {
			toReturn = numbers.pop();
		}
		catch (EmptyStackException e) {
			throw new ParserException("Malformed expression.");
		}

		return toReturn;
	}
	
	public static void main(String argsp[]) {
		StackParser sp = new StackParser();
		
		try {
			sp.setVariable(new double[]{2, 4});
			System.out.println(sp.parse("x[0]^2 - x[1]"));
//			System.out.println(sp.parse("ln(e-1+cos(2^2-4))*2^-1"));
		} catch (ParserException e) {
			System.out.println(e.getMessage());
		} catch (LexerException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
