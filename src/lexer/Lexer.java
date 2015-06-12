package lexer;

import java.util.LinkedList;
import java.util.List;

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
	
	private List<Token> tokens;
	
	public Lexer(String expression) {
		StringBuilder buffer = new StringBuilder();
		tokens = new LinkedList<Token>();
		
		int pos = 0;
		
		//strips spaces and transform the expression into lower case
		expression = expression.trim().toLowerCase();
		
		while (pos < expression.length()) {
			char digit = expression.charAt(pos);
			boolean isText = false;
			boolean isNumber = false;
			
			//treating literals
			while (Character.isLetter(digit)) {
				isText = true;
				buffer.append(digit);
				
				if (++pos >= expression.length()) break;
				digit = expression.charAt(pos);
			}
			
			//just parsed a text, create a new token and flush the string buffer
			if (isText) {
				String text = buffer.toString();

				FunctionID fID = FunctionToken.isFunction(text);
				
				if (fID != null)
					tokens.add(new FunctionToken(Token.Type.FUN, fID));
				else
					tokens.add(new VarToken(Token.Type.VAR, text));
				
				//clears the buffer
				buffer.delete(0, text.length());
				
				//we have just read a text token and the lookahead is pointing to the next
				//character. Since this can't be another text token (otherwise it would be
				//concatenated with the current one, the only next valid option is an 
				//operator.
			}
			else {
				//try to parse a number 
				
				while ('0' <= digit && digit <= '9') {
					buffer.append(digit);
					isNumber = true;
					
					if (++pos >= expression.length()) break;
					digit = expression.charAt(pos);
				}
				
				if (isNumber) {
					String text = buffer.toString();
					double number = Double.parseDouble(text);
					
					//Clears the buffer
					buffer.delete(0, text.length());
					
					tokens.add(new NumericToken(Token.Type.NUM, number));
				}
			}
			

			switch (digit) {
				//treating operators case
				case ADD: tokens.add(new Token(Token.Type.ADD)); break;
				case SUB: tokens.add(new Token(Token.Type.SUB)); break;
				case MUL: tokens.add(new Token(Token.Type.MUL)); break;
				case DIV: tokens.add(new Token(Token.Type.DIV)); break;
				case POW: tokens.add(new Token(Token.Type.POW)); break;
				
				//parenthesis and comma
				case LPAR: tokens.add(new Token(Token.Type.LPAR)); break;
				case COM: tokens.add(new Token(Token.Type.COM)); break;
				case RPAR: tokens.add(new Token(Token.Type.RPAR)); break;
			}
			
			pos++;
		}
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
