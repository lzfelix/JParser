# JParser

This is a math parser and evaluator written in Java. It accepts either constant expressions, for example `3+5*2`, expressions using a single scalar variable, such as `3*x-4/x` and multiple variables expressions in the form `x[0]^x[1]/3-5*x[2]`. This parser also also accepts trigonometric functions and the constants `pi` and `e`. The parser also support signs, so the expression `+2-(--5)` is valid.

This API inteprets a function in two phases: initially it is compilled into RPN notation using Dijkstra's Shunting-Yard algorithm and then it is evaluated based on the variables's value. This can be useful when the same expression must be evaluated multiple times with different values, as happens when implementing Numerical Methods. In this situation, the expression doesn't need to be parsed again, since it is already cached.

# Supported Features
* The standard operations `+`, `-`, `*`, `/`, `^`, respecting precedence order;
* Correctly parenthised expressions and signs;
* Trigonometric functions: sin(x), cos(x), tan(x), sec(x), csc(x), ctg(x), asin(x), acos(x), atan(x), sinh(x), cosh(x), tanh(x);
* ln(x);
* Constants: e, pi;
* Restriction on the use of variables (scalar, vector or none).

# Usage examples

For constant expressions:
```java
  JParser jp = JParser.getInstance();
  
  jp.setConstantExpression();
  jp.compileExpression("5+3*cos(ln(e) - 1)");
  double result = jp.evaluate();  //result = 8
```

For single variable expressions:
```java
  JParser jp = JParser.getInstance();
  
  jp.setVariable(10);
  jp.compileExpression("x^2");
  double result = jp.evaluate();  //result = 100
  
  jp.setVariable(2);
  result = jp.evaluate();  //result = 4
  
  jp.setVariable(e);
  jp.compileExpression("ln(x)/2");
  result = jp.evaluate();  //result = 0.5
```

For multiple variables:
```java
  JParser jp = JParser.getInstance();
  
  jp.setVariable(new double[]{5, 10, 2});
  jp.compileExpression("x[0] + x[1] / x[2]");
  double result = jp.evaluate();  //result = 15
  
  jp.setVariable(new double[]{1, 1, 5});
  double result = jp.evaluate();  //result = 1.2
```

Notice that the way that the parser operates can be changed during runtime.

__ATTENTION__: `jp.setVariable(new double[]{5});` will allow parsing an expression with `x[0]` only, while `jp.setVariable(10);` will allow the use of `x` only.

# Zero tunning
The parser keeps track of the operands values to prevent illegal mathematical operations, such as division by zero, `0^0` and find the logarithm of numbers smaller or equals to 0, since in these cases Java will generate an error, which can't be handled. This is bypassed by throwing a `ParserException`instead, so the parser can be used to directly handle expressions input by the user.

This control is done through comparing the operand's absolute value to `epsilon`. The default value of this parameter is `1e-10` and it is stored on the constant `EPSILON`, but it can be changed according to the user's needs by calling `getEpsilon()` method.

# Testing
I developed some testing using JUnit, these can be found in the `tests` package, but for usage this package is not needed. To run theses tests you'll need both JUnit and Hamcrest (which are included on the project).

#License
This software is provided under the MIT license.
