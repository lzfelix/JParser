package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import parser.JParser;
import exceptions.LexerException;
import exceptions.ParserException;

public class ParserTests {
	private static double DELTA = 1e-5;
	
	JParser parser;
	
	@Before
	public void setUp() throws Exception {
		parser = JParser.getInstance();
	}

	@Test public void simpleAddition() throws LexerException, ParserException {
		parser.compileExpression("3+5");
		assertEquals(8, parser.evaluate(), DELTA);
	}
	
	@Test public void simpleSubtraction() throws LexerException, ParserException {
		parser.compileExpression("5-3");
		assertEquals(2, parser.evaluate(), DELTA);
	}
	
	@Test public void simpleSubtractionNegativeResult() throws LexerException, ParserException {
		parser.compileExpression("3-5");
		assertEquals(-2, parser.evaluate(), DELTA);
	}
	
	@Test public void simpleMultiplication() throws LexerException, ParserException {
		parser.compileExpression("3*5");
		assertEquals(15, parser.evaluate(), DELTA);
	}
	
	@Test public void simpleDivision() throws LexerException, ParserException {
		parser.compileExpression("3/5");
		assertEquals(0.6, parser.evaluate(), DELTA);
	}
	
	@Test(expected=ParserException.class) 
	public void divisionByZero() throws LexerException, ParserException {
		parser.compileExpression("3/0");
		parser.evaluate();
	}
	
	@Test public void simplePower() throws LexerException, ParserException {
		parser.compileExpression("3^5");
		assertEquals(243, parser.evaluate(), DELTA);
	}

	@Test(expected=ParserException.class) 
	public void zeroPowerZero() throws LexerException, ParserException {
		parser.compileExpression("0^0");
		parser.evaluate();
	}
	
	@Test public void reckonPi() throws LexerException, ParserException {
		parser.compileExpression("2*pi");
		assertEquals(2*Math.PI, parser.evaluate(), DELTA);
	}
	
	@Test public void reckonE() throws LexerException, ParserException {
		parser.compileExpression("e");
		assertEquals(Math.E, parser.evaluate(), DELTA);
	}
	
	@Test public void sinAndCos() throws LexerException, ParserException {
		parser.compileExpression("sin(0)^2 + cos(0)^2");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test public void tan() throws LexerException, ParserException {
		parser.compileExpression("tan(pi/4)");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test public void voodooTan() throws LexerException, ParserException {
		parser.compileExpression("tan(pi/2)");
		
		//this is pure Voodoo, because 
		assertEquals(Math.tan(Math.PI/2), parser.evaluate(), DELTA);
	}
	
	@Test public void sec() throws LexerException, ParserException {
		parser.compileExpression("sec(0)");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test public void csc() throws LexerException, ParserException {
		parser.compileExpression("csc(pi/2)");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test public void ctg() throws LexerException, ParserException {
		parser.compileExpression("tan(pi/4)");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test (expected=ParserException.class) 
	public void invalidSec() throws LexerException, ParserException {
		parser.compileExpression("sec(pi/2)");
		parser.evaluate();
	}
	
	@Test (expected=ParserException.class) 
	public void invalidCsc() throws LexerException, ParserException {
		//20pi is congruent to 0
		parser.compileExpression("csc(20*pi)");
		parser.evaluate();
	}
	
	@Test (expected=ParserException.class) 
	public void invalidCtg() throws LexerException, ParserException {
		//tan(pi) = 0 -> ctg(pi) = 1/0 = undetermined
		parser.compileExpression("ctg(pi)");
		parser.evaluate();
	}
	
	@Test public void ln() throws LexerException, ParserException {
		parser.compileExpression("ln(e)");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test (expected=ParserException.class)  
	public void invalidLn() throws LexerException, ParserException {
		parser.compileExpression("ln(0)");
		parser.evaluate();
	}

	@Test public void usingManyFunctions() throws LexerException, ParserException {
		parser.compileExpression("ln(cos(sin(pi)))");
		assertEquals(0, parser.evaluate(), DELTA);
	}

	@Test public void singleVariable() throws LexerException, ParserException {
		parser.setVariable(2);
		parser.compileExpression("x^2");
		assertEquals(4, parser.evaluate(), DELTA);
	}
	
	@Test public void multipleSingleVariableEvaluations() throws LexerException, ParserException {
		parser.setVariable(2);
		parser.compileExpression("x^2");
		assertEquals(4, parser.evaluate(), DELTA);
		
		parser.setVariable(4);
		assertEquals(16, parser.evaluate(), DELTA);
	}
	
	@Test public void multiVariables() throws LexerException, ParserException {
		parser.setVariable(new double[]{1, 1});
		parser.compileExpression("sin(x[0])^2 + cos(x[1])^2");
		assertEquals(1, parser.evaluate(), DELTA);
	}
	
	@Test public void multiVariablesMultipleEvaluates() throws LexerException, ParserException {
		parser.setVariable(new double[]{1, 2});
		parser.compileExpression("x[0]/x[1]");
		assertEquals(0.5, parser.evaluate(), DELTA);
		
		parser.setVariable(new double[]{10, -20});
		assertEquals(-0.5, parser.evaluate(), DELTA);
	}

	@Test public void constantExpression() throws LexerException, ParserException {
		parser.setConstantExpression();
		parser.compileExpression("5^2");
		assertEquals(25, parser.evaluate(), DELTA);
	}

	@Test public void switchingOperationMode() throws LexerException, ParserException {
		parser.setConstantExpression();
		parser.compileExpression("2*3");
		assertEquals(6, parser.evaluate(), DELTA);
		
		parser.setVariable(3);
		parser.compileExpression("x*3");
		assertEquals(9, parser.evaluate(), DELTA);
		
		parser.setVariable(12);
		assertEquals(36, parser.evaluate(), DELTA);
		
		parser.setVariable(new double[] {10, 2, 5});
		parser.compileExpression("x[0]/x[1]+x[2]-3*x[2]");
		assertEquals(-5, parser.evaluate(), DELTA);
		
		parser.setVariable(new double[] {1, 1, 1});
		assertEquals(-1, parser.evaluate(), DELTA);
	}
}
