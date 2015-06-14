# JParser

This is a math parser and evaluator written in Java. It accepts either constant expressions, for example `3+5*2`, expressions using a single scalar variable, such `3*x-4/x` and multiple variables expressed as a single vector as in `x[1]^x[2]/3-5*x[4]`. This parser also also accepts trigonometric functions and the constants `pi` and `e`.

The interpretation occurs in two phases: first the string that contains the expression is parsed and stored.  The second step consists in parsing the expression. By doing so, it is possible to re-evaluate the expression with different varaible values without parsing it again.

# Suported Features
* Trigonometric functions: sin(x), cos(x), tan(x), sec(x), csc(x), ctg(x), asin(x), acos(x), atan(x), sinh(x), cosh(x), tanh(x);
* ln(x);
* Constants: e, pi;
* Restriction on the use of variables (scalar, vector or none).

# Usage examples

# Testing
Some tests were developed using JUnit and can be found in the package `tests`.
