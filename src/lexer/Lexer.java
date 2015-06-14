package lexer;

import java.util.LinkedList;
import java.util.List;

import lexer.FunctionToken.FunctionID;
import exceptions.LexerException;

public class Lexer {
	private static final char ADD = '+';
	private static final char SUB = '-';
	private static final char MUL = '*';
	private static final char DIV = '/';
	private static final char POW = '^';
	private static final char LPAR = '(';
	private static final char COM = ',';
	private static final char RPAR = ')';
	
	private static String INDEXED_VARIABLE_PATTERN = "^x\\[\\d+\\]";
	
	private static Lexer instance = null;
	
	private List<Token> tokens;
	private boolean acceptsVariables;
	private int maxDimension;
	
	
	private Lexer() {
		tokens = new LinkedList<>();
		this.acceptsVariables = false;
		this.maxDimension = 0;
	}
	
	/**
	 * Retuns the Lexer's instance, since this is a Singleton class.
	 * @return a Lexer's instance.
	 */
	public static Lexer getInstance() {
		if (instance == null)
			instance = new Lexer();
		
		return instance;
	}
	
	/**
	 * Sets if the Lexer should accept variables in the form x or x[i]. By 
	 * default this value is <code>false</code>.If <code>acceptVariables</code> 
	 * is set to false, then the parsing of any variable (which has either the 
	 * form 'x' or 'x[i]' triggers a <code>LexerException</code>. Making this 
	 * variable <code>true</code>, by default will configure the Lexer to accept 
	 * only scalar variables (in the form x, for example 3*x). To set that a vector
	 * can be used as variable, then call <code>setVariableDimension</code>.
	 * NOTE: The only allowed name to variables is x.
	 * @param acceptVariables <code>true</code> if this expression is allowed to contain
	 * variables.
	 */
	public void setAcceptVariables(boolean acceptsVariables) {
		this.acceptsVariables = acceptsVariables;	
		this.maxDimension = (acceptsVariables) ? 1 : 0;
	}
	
	public boolean getAcceptVariables() {
		return this.acceptsVariables;
	}
	
	/**
	 * Sets the dimension of the vector used as variable. So ranges from x[0] .. x[i-1]
	 * will be correctly parsed. If no variables are allowed, invoking this method
	 * won't affect Lexer's internal state. After invoking <code>setAcceptVariables</code>
	 * the maximum dimension is set as 1 by default.
	 * @param dim the maximum allowed dimension
	 * @throws RuntimeException if <code>dim</code> lesser than 1.
	 */
	public void setMaxDimension(int maxDimension) throws RuntimeException {
		if (maxDimension <= 0)
			throw new RuntimeException("Max dimension has to be a positive non-zero value.");
		
		if (acceptsVariables)
			this.maxDimension = maxDimension;
	}
	
	public int getMaxDimension() {
		return this.maxDimension;
	}
	
	public void parseToTokens(String expression) throws LexerException {
		//resets from previous lexing
		int pos = -1;
		tokens.clear();
		
		//strips spaces and transform the expression into lower case
		expression = expression.replaceAll("\\s", "").toLowerCase();
		
		while (++pos < expression.length()) {
			char digit = expression.charAt(pos);
			int newPos = 0;
			
			//If a text was parsed, update the seeking position and try to parse anything else
			newPos = tryParseText(expression, pos, tokens); 
			if (newPos != pos) {
				pos = newPos - 1;
				continue;
			}
			
			switch (digit) {
				//treating operators case
				case ADD: tokens.add(new Token(Token.Type.ADD)); continue;
				
				//this can be either a negative sign or subtraction operator
				case SUB:
					if (tokens.size() == 0 || Token.willNegate(tokens.get(tokens.size() - 1)))
						tokens.add(new Token(Token.Type.NEG));
					else
						tokens.add(new Token(Token.Type.SUB)); 
				continue;
					
				case MUL: tokens.add(new Token(Token.Type.MUL)); continue;
				case DIV: tokens.add(new Token(Token.Type.DIV)); continue;
				case POW: tokens.add(new Token(Token.Type.POW)); continue;
				
				//parenthesis and comma
				case LPAR: tokens.add(new Token(Token.Type.LPAR)); continue;
				case COM: tokens.add(new Token(Token.Type.COM)); continue;
				case RPAR: tokens.add(new Token(Token.Type.RPAR)); continue;
			}
			
			//Same for a number
			newPos = tryParseNumber(expression, pos, tokens);
			if (newPos != pos) {
				pos = newPos - 1;
				continue;
			}
			
			//if we got here, then there is something wrong with this expression
			throw new LexerException("Expression malformed arround position " + pos + ".");
		}
	}
	
	/**
	 * Tries to parse a function or variable name from <code>expression</code> starting on position <code>pos</code> 
	 * @param expression The expression being parsed
	 * @param pos The current seek position on the expression
	 * @param tokens The list of already parsed tokens
	 * @return the new seek position. If the returned value is equals to the
	 * passed as <code>pos</code> then no text was parsed.
	 * @throws LexerException if a invalid function name or variable is found, if no variables are allowed
	 * on this expression and one is found or if indexed variables are permitted (x[i]), but a scalar one
	 * (x) is found.
	 */
	private int tryParseText(String expression, int pos, List<Token> tokens) throws LexerException {
		StringBuilder buffer = new StringBuilder();
		boolean isText = false;
		char digit = expression.charAt(pos);
		
		while (Character.isLetter(digit)) {
			isText = true;
			buffer.append(digit);
			
			if (++pos >= expression.length()) break;
			digit = expression.charAt(pos);
		}
		if (digit == '[') {
			buffer.append(digit);
			
			if (++pos >= expression.length())
				throw new LexerException("Unexpected end of expresion before setting variable's index.");
						
			//now try to parse the variable's index, which shall be a number -- no, you can't index
			//a variable with another variable
			int new_pos = tryParseNumber(expression, pos, tokens);
			
			if (new_pos == pos)
				throw new LexerException("The variable's index is not a valid integer.");
			
			//remove the index that was inserted into the tokens list and add it to the variable buffer
			Token t = tokens.remove(tokens.size() - 1);
			
			if (!(t instanceof IntegerToken))
				throw new LexerException("The variable's index must be a integer value.");
				
			buffer.append(((IntegerToken)t).getValue());
			pos = new_pos;
			
			if (pos >= expression.length())
				throw new LexerException("Unexpected end of expresion before enclosing indexing bracket.");
			
			digit = expression.charAt(pos);
			if (digit != ']')
				throw new LexerException("Expected ], but ." + digit + " on position " + pos + " found.");
			
			buffer.append(digit);
			pos++;
		}
		
		//if a text was succesfully parsed, try to classify it
		if (isText) {
			String text = buffer.toString();
						
			//treat constants
			if (text.equals("e"))
				tokens.add(new DecimalToken(Math.E));
			
			else if (text.equals("pi"))
				tokens.add(new DecimalToken(Math.PI));
			
			else {
				//treat valid function names
				
				FunctionID fID = FunctionToken.isFunction(text);
				
				if (fID != null) {
					tokens.add(new FunctionToken(fID));
				}
				else if (text.equals("x")) {
					//a scalar variable was found, is it allowed?
					
					if (maxDimension == 0)
						throw new LexerException("No variables are allowed on this expression.");
					
					if (maxDimension == 1)
						tokens.add(new VariableToken(0));
					else
						throw new LexerException("Just scalar variable, in the form 'x' is allowed on this expression.");
				}
				else if (text.matches(INDEXED_VARIABLE_PATTERN)) {
					//a indexed variable was found, it is allowed?
					
					if (maxDimension == 0)
						throw new LexerException("No variables are allowed on this expression.");
					
					if (maxDimension == 1)
						throw new LexerException("Just scalar variable, in the form 'x' is not allowed on this expression.");
					
					//discover the variable's index
					int index = Integer.valueOf(text.substring(text.indexOf('[') + 1, text.indexOf(']')));
					if (index < 0 || index >= maxDimension)
						throw new LexerException("Variable index outside of valid range [0-." + maxDimension +"[.");
					
					tokens.add(new VariableToken(index));	
				}
				else 
					throw new LexerException("This is not either a valid variable name nor a valid function name.");
			}
		}
		
		return pos;
	}
	
	/**
	 * Tries to parse a number from <code>expression</code> starting on position <code>pos</code>
	 * @param expression The expression being parsed
	 * @param pos The current seek position on the expression
	 * @param tokens The list of already parsed tokens
	 * @return the new seek position. If the returned value is equals to the
	 * passed as <code>pos</code> then no number was parsed.
	 * @throws LexerException if the number ends with .
	 */
	private int tryParseNumber(String expression, int pos, List<Token> tokens) throws LexerException {
		StringBuilder buffer = new StringBuilder();
		char digit = expression.charAt(pos);
		
		if ('0' <= digit && digit <= '9' || digit == '-') {
			buffer.append(digit);
			
			//reads the integer part
			while (++pos < expression.length()) {
				digit = expression.charAt(pos);
				
				if ('0' <= digit && digit <= '9')
					buffer.append(digit);
				else break;
			}
			
			//parse the decimal part
			if (digit == '.') {
				buffer.append('.');
				
				while (++pos < expression.length()){
					digit = expression.charAt(pos);
					
					if ('0' <= digit && digit <= '9') {
						digit = expression.charAt(pos);
						buffer.append(digit);
					}
					else break;
				}
			}
			
			String text = buffer.toString();
			if (text.charAt(text.length() - 1) == '.')
				throw new LexerException("Number can't finish with [.].");
		
			//append a new token to the list
			try {
				int number = Integer.parseInt(buffer.toString());
				tokens.add(new IntegerToken(number));
			}
			catch (Exception e) { 
				double number = Double.parseDouble(buffer.toString());
				tokens.add(new DecimalToken(number));
			}
		}
		
		return pos;
	}
	
	/**
	 * Retuns a link of interpreted tokens
	 * @return The list of interpreted tokens
	 */
	public List<Token> getList() {
		return this.tokens;
	}
		
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		//amortizing complexity! =D
		Token[] array = tokens.toArray(new Token[tokens.size()]);
		
		for (int i = 0; i < array.length - 1; i++)
			s.append(array[i].toString() + " ");
		s.append(array[array.length - 1]);
		
		return s.toString();
	}
}
