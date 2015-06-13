package lexer;

/**
 * Holds functions's names
 * 
 * @author Luiz Felix
 */
public class FunctionToken extends Token {
	
	public static enum FunctionID {sin, cos, tan, sec, csc, ctg, asin, acos, atan, sinh, cosh, tanh, ln}
	
	FunctionID functionID;
	
	/**
	 * Creates a token that represents a function. The suported functions are: sin, cos, tan, asin, acos,
	 * atan, sec, csc, ctg, sinh, cosh, tanh, ln
	 * @param fID the stored function's ID.
	 */
	public FunctionToken(FunctionID fID) {
		super(Token.Type.FUN);
		this.functionID = fID;
	}
	
	public FunctionID getFunctionID() {
		return this.functionID;
	}
	
	public String toString() {
		return this.functionID.toString();
	}

	/**
	 * Return the corresponding function token, if <code>text</code> is a valid function name.
	 * @param text The text to be tests if it is a function
	 * @return The function's token if this is a valid function. <code>null</code> otherwise.
	 */
	public static FunctionID isFunction(String text) {
		FunctionID toReturn = null;
		
		//if there is no such function token, bypasses the exception and returns null
		try {
			toReturn = FunctionID.valueOf(text);
		} catch (Exception e) { }
		
		return toReturn;
	}
}
