package lexer;

import java.util.LinkedList;
import java.util.Queue;

import lexer.FunctionToken.FunctionID;

public class Lexer {
	private static final char ADD = '+';
	private static final char SUB = '-';
	private static final char MUL = '*';
	private static final char DIV = '/';
	private static final char POW = '^';
	private static final char LPAR = '(';
	private static final char COM = ',';
	private static final char RPAR = ')';
	
	private Queue<Token> tokens;
	
	public Lexer(String expression) throws ParserException {
		tokens = new LinkedList<Token>();
		
		int pos = -1;
		
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
			
			//Same for a number
			newPos = tryParseNumber(expression, pos, tokens);
			if (newPos != pos) {
				pos = newPos - 1;
				continue;
			}
			

			switch (digit) {
				//treating operators case
				case ADD: tokens.add(new Token(Token.Type.ADD)); continue;
				case SUB: tokens.add(new Token(Token.Type.SUB)); continue;
				case MUL: tokens.add(new Token(Token.Type.MUL)); continue;
				case DIV: tokens.add(new Token(Token.Type.DIV)); continue;
				case POW: tokens.add(new Token(Token.Type.POW)); continue;
				
				//parenthesis and comma
				case LPAR: tokens.add(new Token(Token.Type.LPAR)); continue;
				case COM: tokens.add(new Token(Token.Type.COM)); continue;
				case RPAR: tokens.add(new Token(Token.Type.RPAR)); continue;
			}
			
			//if we got here, then there is something wrong with this expression
			throw new ParserException("Expression malformed arround position " + pos + ".");
		}
	}
	
	/**
	 * Tries to parse a function or variable name from <code>expression</code> starting on position <code>pos</code> 
	 * @param expression The expression being parsed
	 * @param pos The current seek position on the expression
	 * @param tokens The list of already parsed tokens
	 * @return the new seek position. If the returned value is equals to the
	 * passed as <code>pos</code> then no text was parsed.
	 */
	private int tryParseText(String expression, int pos, Queue<Token> tokens) {
		StringBuilder buffer = new StringBuilder();
		boolean isText = false;
		char digit = expression.charAt(pos);
		
		while (Character.isLetter(digit)) {
			isText = true;
			buffer.append(digit);
			
			if (++pos >= expression.length()) break;
			digit = expression.charAt(pos);
		}
		
		if (isText) {
			String text = buffer.toString();
			FunctionID fID = FunctionToken.isFunction(text);
			
			if (fID != null)
				tokens.add(new FunctionToken(Token.Type.FUN, fID));
			else
				tokens.add(new VarToken(Token.Type.VAR, text));
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
	 * @throws ParserException if the number ends with .
	 */
	private int tryParseNumber(String expression, int pos, Queue<Token> tokens) throws ParserException {
		StringBuilder buffer = new StringBuilder();
		char digit = expression.charAt(pos);
		
		if ('0' <= digit && digit <= '9') {
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
				throw new ParserException("Number can't finish with [.].");
		
			//append a new token to the list
			double number = Double.parseDouble(buffer.toString());
			tokens.add(new NumericToken(Token.Type.NUM, number));
		}
		
		return pos;
	}
	
	/**
	 * Retuns a link of interpreted tokens
	 * @return The list of interpreted tokens
	 */
	public Queue<Token> getList() {
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
