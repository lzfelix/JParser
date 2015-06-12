package lexer;

/**
 * Holds functions's names
 * 
 * @author Luiz Felix
 */
public class FunctionToken extends Token {
	
	//TODO: Get rid of this dependency!
	public static enum FunctionID {sin, cos, tan, sec, csc, ctg, asin, acos, atan, sinh, cosh, tanh, ln}
	
	FunctionID functionID;
	
	public FunctionToken(Type type, FunctionID fID) {
		super(type);
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
