package parser;

import java.util.EmptyStackException;
import java.util.LinkedList;
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
	
	private Queue<Token> compiledExpression;
	
	private StackParser() {
		//creating a private constructor, so the Singleton pattern can be used
	}
	
	/**
	 * Returns the StackParser's instance, since this is a Singleton class.
	 * @return
	 */
	public StackParser getInstance() {
		if (instance == null) 
			instance = new StackParser();
		
		return instance;
	}
		
	/**
	 * Set the values for which the variables in the form x[i].
	 * After invoking this method, all the expressions to be parsed are allowed
	 * to use indexed variables in the range [0-n[. To change this configuration,
	 * invoke one of the forms of this method or <code>setConstantExpression()</code>,
	 * to forbid the use of variables on future expressions.
	 * If a scalar variable or an index out of bounds is found while parsing an 
	 * expression that is expected to have indexed variables, a <code>LexerException</code> 
	 * is thrown by the parsing method. To change this behavior, invoke either overload 
	 * form of this method or <code>setConstantExpression</code>. 
	 * 
	 * @param variablesVaue The values for which x[0], x[1], ... x[n-1] are going
	 * to be replaced in future expressions.
	 */
	public void setVariable(double[] variablesVaue) {
		this.variablesVaue = variablesVaue;
		Lexer.getInstance().setAcceptVariables(true);
		Lexer.getInstance().setMaxDimension(variablesVaue.length);
	}
	
	/**
	 * Set the value for which the scalar variable x will be replaced on evaluation time.
	 * After invoking this method, all the future parsed expressions will be forbit to
	 * have indexed variables, causing the parser to throw a <code>LexerException</code>.
	 * To change this behavior, invoke either overload form of this method or 
	 * <code>setConstantExpression</code>. 
	 * 
	 * @param variableValue The value for which x will be replaced in future expressions.
	 */
	public void setVariable(double variableValue) {
		this.variablesVaue = new double[] {variableValue};
		Lexer.getInstance().setAcceptVariables(true);
	}
	
	/**
	 * Forbids future expressions to accept any kind of variables. If this happens, then
	 * a <code>LexerException will be thrown</code>. To change this behavior, invoke either
	 * overload form of this method or <code>setConstantExpression</code>.   
	 */
	public void setConstantExpression() {
		Lexer.getInstance().setAcceptVariables(false);
	}

	/**
	 * PRE: Call this method BEFORE setting the variable's configuration through the <code>
	 * setVariable()</code> and <code>setConstantExpression()</code> methods.
	 * 
	 * Parses and caches <code>expression</code> so if it has to be evaluated multiple times,
	 * this can be done just changing the variables's values through <code>setVariable()</code>
	 * and invoking <code>parse()</code>.
	 * 
	 * @param expression The expression to be evaluated. It can contain traditional operations 
	 * (+,-,*,/,^ - power), functions (sin, cos, tan, asin, acos, atan, sinh, cosh, tanh, ln).
	 * Parenthesed expressions will be properly evaluated.
	 *  
	 * @throws LexerException if the expression contains invalid function names, characters, 
	 * or invalid variables, which are set through the <code>setVariable()</code> and 
	 * <code>setConstantExpression()</code>, methods. 
	 * 
	 * @throws ParserException If the expression is malformed.
	 */
	public void compileExpression(String expression) throws LexerException, ParserException {
		ShuntingYard s = ShuntingYard.getInstance();

		s.convertFromInfixToPosfix(expression);
		this.compiledExpression = s.getExpression();
	}
	
	/**
	 * PRE: Invoke <code>compileExpression</code> before calling this method.
	 * Given a compiled expression, parse its value based on the variable's value (if this
	 * is the case).
	 * 
	 * @return The value which this expression corresponds to.
	 * @throws ParserException if the expression is malformed or a illegal mathematical operation
	 * is performed (such as 0^0, division by zero or log(x), x < 0).
	 */
	public double evaluate() throws ParserException {
		if (compiledExpression == null)
			throw new ParserException("There is no compiled expression to evaluate.");
		
		Queue<Token> workingStack = new LinkedList<Token>(compiledExpression);
		
		operators = new Stack<>();
		numbers = new Stack<>();
		
		double op1, op2;
		
		while (!workingStack.isEmpty()) {
			Token element = workingStack.poll();
			
			switch (element.getType()) {
				case NUM: 
					if (element instanceof DecimalToken)
						numbers.push(((DecimalToken)element).getValue());
					else // can only be a IntegerToken
						numbers.push((double) ((IntegerToken)element).getValue());
						
				break;
				
				case VAR:
					int index = ((VariableToken)element).getIndex();
					try {
						numbers.push(variablesVaue[index]);
					}
					catch(Exception e) {
						throw new ParserException("Variable x["+index+"] is not set. Did you change the variables"
								+ "array to a smaller one?");
					}
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
				
				case POS:
					//do nothing and avoid an exception 
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
	
	/* Tries to pop an element from the stack (because it may be empty). In this case
	 * swap the EmptyStackException by a ParserException */
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
			sp.compileExpression("x[0]^2 - x[1]");
			System.out.println(sp.evaluate());
			
			sp.setVariable(-2);
			sp.compileExpression("x^2");
			System.out.println(sp.evaluate());
			
			sp.setVariable(new double[]{3, 4});
			System.out.println(sp.evaluate());
			
			sp.setVariable(2);
			sp.compileExpression("ln(e-1+cos(2^2-4))*2^-1");
			System.out.println(sp.evaluate());
			
			sp.setVariable(-2);
			sp.compileExpression("-x");
			System.out.println(sp.evaluate());
			
		} catch (ParserException e) {
			System.out.println(e.getMessage());
		} catch (LexerException e) {
			System.out.println(e.getMessage());
		}
	}
}
