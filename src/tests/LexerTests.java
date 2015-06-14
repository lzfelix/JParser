package tests;

import static org.junit.Assert.*;
import lexer.Lexer;

import org.junit.Before;
import org.junit.Test;

import exceptions.LexerException;
import exceptions.ParserException;

public class LexerTests {
	Lexer l;
	
	@Before public void setUp() {
		l = Lexer.getInstance();
	}

	@Test public void singleDigitExpression() throws ParserException, LexerException {
		l.parseToTokens("3");
		assertEquals("3", l.toString());
	}
	
	@Test public void negativeSingleDigitExpression() throws ParserException, LexerException {
		l.parseToTokens("-3");
		assertEquals("NEG 3", l.toString());
	}
	
	@Test public void positiveSingleDigitExpression() throws ParserException, LexerException {
		l.parseToTokens("+3");
		assertEquals("POS 3", l.toString());
	}

	@Test public void addition() throws ParserException, LexerException {
		l.parseToTokens("3 + 5");
		assertEquals("3 ADD 5", l.toString());
	}
	
	@Test public void additionWithSignals() throws ParserException, LexerException {
		l.parseToTokens("-3 + +5");
		assertEquals("NEG 3 ADD POS 5", l.toString());
	}
	
	@Test public void subtraction() throws ParserException, LexerException {
		l.parseToTokens("3 - 5");
		assertEquals("3 SUB 5", l.toString());
	}
	
	@Test public void multiplication() throws ParserException, LexerException {
		l.parseToTokens("3 * 5");
		assertEquals("3 MUL 5", l.toString());
	}
	
	@Test public void division() throws ParserException, LexerException {
		l.parseToTokens("3 / 5");
		assertEquals("3 DIV 5", l.toString());
	}
	
	@Test public void decimalValues() throws ParserException, LexerException {
		l.parseToTokens("3.2 / 5.7");
		assertEquals("3.2 DIV 5.7", l.toString());
	}
	
	@Test public void negativeNumber() throws ParserException, LexerException {
		l.parseToTokens("-3^2");
		assertEquals("NEG 3 POW 2", l.toString());
	}
	
	@Test public void negativeNumberBothSides() throws ParserException, LexerException {
		l.parseToTokens("-3^-2");
		assertEquals("NEG 3 POW NEG 2", l.toString());
	}
	
	@Test public void subtractingParenthised() throws ParserException, LexerException {
		l.parseToTokens("(2+3)-(4*2)");
		assertEquals("LPAR 2 ADD 3 RPAR SUB LPAR 4 MUL 2 RPAR", l.toString());
	}
	
	@Test public void negativeParenthisedExpression() throws ParserException, LexerException {
		l.parseToTokens("-(2+3)");
		assertEquals("NEG LPAR 2 ADD 3 RPAR", l.toString());
	}
	
	@Test (expected=LexerException.class) 
	public void invalidDecimalValue() throws ParserException, LexerException {
		l.parseToTokens("3.328.2");
	}
	
	@Test public void EConstant() throws ParserException, LexerException {
		l.parseToTokens("e");
		assertEquals(Double.toString(Math.E), l.toString());
	}
	
	@Test public void PiConstant() throws ParserException, LexerException {
		l.parseToTokens("pi");
		assertEquals(Double.toString(Math.PI), l.toString());
	}
	
	@Test (expected=LexerException.class)
	public void invalidConstantt() throws ParserException, LexerException {
		l.parseToTokens("k");
	}
	
	@Test public void functionCall() throws ParserException, LexerException {
		l.parseToTokens("cos(10)");
		assertEquals("cos LPAR 10 RPAR", l.toString());
	}
	
	@Test public void ManyFunctionsCall() throws ParserException, LexerException {
		l.parseToTokens("cos(sin(ln(5)))");
		assertEquals("cos LPAR sin LPAR ln LPAR 5 RPAR RPAR RPAR", l.toString());
	}
	
	@Test public void functionWithConstant() throws ParserException, LexerException {
		l.parseToTokens("cos(e)");
		assertEquals("cos LPAR " + Math.E + " RPAR", l.toString());
	}
	
	@Test (expected=LexerException.class)
	public void invalidFunctionCall() throws ParserException, LexerException {
		l.parseToTokens("cotg(10)");
	}
	
	@Test public void singleVariable() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.parseToTokens("2*x^x");
		assertEquals("2 MUL VAR_0 POW VAR_0", l.toString());
	}
	
	@Test (expected=LexerException.class)
	public void singleVariableButIndexUsed() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.parseToTokens("x[0]+3");
		assertEquals("VAR_0 ADD 3", l.toString());
	}
	
//	@Test (expected=LexerException.class) 
//	public void invalidVarName() throws ParserException, LexerException {
//		l.setAcceptVariables(true);
//		l.parseToTokens("2*x^y");
//	}
	
	@Test (expected=RuntimeException.class)
	public void setMaxDimensionAsZero() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(0);
	}
	
	@Test (expected=RuntimeException.class)
	public void setNegativeMaxDimension() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(-10);
	}
	
	@Test public void manyVariables() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x[0]+x[1]+3");
		assertEquals("VAR_0 ADD VAR_1 ADD 3", l.toString());
	}
	
	@Test (expected=LexerException.class) 
	public void manyVariablesButScalarUsed() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x+x+3");
	}
	
	@Test (expected=LexerException.class) 
	public void variableIndexOutOfBounds() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x[0]+x[5]+3");
	}
	
	@Test (expected=LexerException.class) 
	public void variableDecimalIndexer() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x[1]+x[0.333]+3");
	}
	
	@Test (expected=LexerException.class) 
	public void variableOperationIndexer() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x[0]+x[1/3]+3");
	}
	
	@Test (expected=LexerException.class) 
	public void variableIndexer() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(2);
		l.parseToTokens("x[x[1]]+x[0]+3");
	}
	
	@Test (expected=LexerException.class) 
	public void invalidScalarVariableName() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.parseToTokens("x+y+z");
	}
	
	@Test (expected=LexerException.class) 
	public void invalidVectorVariableName() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.setMaxDimension(10);
		l.parseToTokens("var[0]+rav[2]");
	}
	
	@Test public void testHardExpression() throws ParserException, LexerException {
		l.setAcceptVariables(true);
		l.parseToTokens("ln(sin(x)^2 + cos(x)^2) + 9");
		assertEquals("ln LPAR sin LPAR VAR_0 RPAR POW 2 ADD cos LPAR VAR_0 RPAR POW 2 RPAR ADD 9", l.toString());
	}
}
