import java.util.*;

public class LogicalExpression {
	public static Set<String> arraylist = new HashSet<String>();

	private String uniqueSymbol = null; // null if sentence is a more complex
										// expression
	private Vector<LogicalExpression> subexpressions = null; // a vector of
																// LogicalExpressions
																// ( basically a
																// vector of
																// unique
																// symbols and
																// subexpressions
																// )
	private String operator = null; // null if sentence is a _UNIQUE_ symbol
	private static boolean response;
	private static Stack<String> collective_stack = new Stack<String>();

	// constructor
	public LogicalExpression() {
		// these need to stay null if they're empty
		// this.uniqueSymbol = "0";
		// this.connective = "0";
		this.subexpressions = new Vector<LogicalExpression>();
	}

	// another constructor that will ( or is supposed to ) create
	// a new logical Expression, essentially making a copy
	public LogicalExpression(LogicalExpression oldExpression) {

		if (oldExpression.getUniqueSymbol() == null) {
			this.uniqueSymbol = oldExpression.getUniqueSymbol();
		} else {
			// create a new logical expression from the one passed to it
			this.operator = oldExpression.getOperator();

			// now get all of the subExpressions
			// hint, enumerate over the subexpression vector of newExpression
			for (Enumeration e = oldExpression.getSubexpressions().elements(); e.hasMoreElements();) {
				LogicalExpression nextExpression = (LogicalExpression) e.nextElement();
				this.subexpressions.add(nextExpression);
			}
		}

	}

	/*
	 * this method replaces _part_ of read_word() this method sets the symbol
	 * for the LogicalExpression it checks to make sure that it starts with one
	 * of the appropriate letters, then checks to make sure that the rest of the
	 * string is either digits or '_'
	 */
	public void setUniqueSymbol(String newSymbol) {
		boolean valid = true;
		// remove the leading whitespace
		newSymbol.trim();
		if (this.uniqueSymbol != null) {
			System.out.println("setUniqueSymbol(): - this LE already has a unique symbol!!!" + "\nswapping :->" + this.uniqueSymbol + "<- for ->" + newSymbol + "<-\n");
		} else if (valid) {
			this.uniqueSymbol = newSymbol;
			// testing
			// System.out.println(" setUniqueSymbol() - added-" + newSymbol + "-
			// to the LogicalExpression! ");
		}
	}

	/*
	 * this method replaces _part_ of read_word() from the example code it sets
	 * the connective for this LogicalExpression
	 * 
	 * and returns the rest of the string to collect the symbols for it
	 */
	public String setOperator(String inputString) {
		String connect;
		// testing
		// System.out.println("setConnective() - beginning -" + inputString +
		// "-");
		
		// trim the whitespace at the beginning of the string if there is any
		// there shouldn't be
		inputString.trim();

		// if the first character of the inputString is a '('
		// - remove the ')' and the ')' and any whitespace after it.
		if (inputString.startsWith("(")) {
			inputString = inputString.substring(inputString.indexOf('('), inputString.length());
			// trim the whitespace
			inputString.trim();
		}

		// testing
		// System.out.println("here: setConnective1- inputString:" + inputString
		// + "--");
		if (inputString.contains(" ")) {
			// remove the connective out of the string
			connect = inputString.substring(0, inputString.indexOf(" "));
			inputString = inputString.substring((connect.length() + 1), inputString.length());
			// inputString.trim();

			// testing
			// System.out.println("I have the connective -" + connect + "- and
			// i've got symbols -" + inputString + "-");
		} else {
			// just set to get checked and empty the inputString
			// huh?
			connect = inputString;
			inputString = "";
		}

		// if connect is a proper connective
		if (connect.equalsIgnoreCase("if") || connect.equalsIgnoreCase("iff") || connect.equalsIgnoreCase("and") || connect.equalsIgnoreCase("or") || connect.equalsIgnoreCase("xor") || connect.equalsIgnoreCase("not")) {
			// ok, first word in the string is a valid connective
			// set the connective
			this.operator = connect;
			// testing
			// System.out.println( "setConnective(): I have just set the
			// connective\n->" + connect + "<-\nand i'm returning\n->" +
			// inputString + "<-" );
			return inputString;
		} else {
			System.out.println("unexpected character!!! : invalid connective!! - setConnective():-" + inputString);
			this.exit_function(0);
		}

		// invalid connective - no clue who it would get here
		System.out.println(" invalid connective! : setConnective:-" + inputString);
		return inputString;
	}

	public void setSubexpression(LogicalExpression newSub) {
		this.subexpressions.add(newSub);
	}

	public void setSubexpressions(Vector<LogicalExpression> symbols) {
		this.subexpressions = symbols;
	}

	public String getUniqueSymbol() {
		return this.uniqueSymbol;
	}

	public String getOperator() {
		return this.operator;
	}

	public LogicalExpression getNextSubexpression() {
		return this.subexpressions.lastElement();
	}

	public Vector getSubexpressions() {
		return this.subexpressions;
	}

	/************************* end getters and setters *************/

	public void print_expression(String separator) {
		arraylist.add(this.uniqueSymbol);
		if (!(this.uniqueSymbol == null)) {
			System.out.print(this.uniqueSymbol.toUpperCase());
		} else {
			// else the symbol is a nested logical expression not a unique
			// symbol
			LogicalExpression nextExpression;

			// print the connective
			System.out.print("(" + this.operator.toUpperCase());

			// enumerate over the 'symbols' ( LogicalExpression objects ) and
			// print them
			for (Enumeration e = this.subexpressions.elements(); e.hasMoreElements();) {
				nextExpression = (LogicalExpression) e.nextElement();
				System.out.print(" ");
				nextExpression.print_expression("");
				System.out.print(separator);
			}

			System.out.print(")");
		}
	}

	public boolean getSolution(HashMap<String, String> model) {
		if (this.getUniqueSymbol() != null) {
			collective_stack.push(this.getUniqueSymbol());
		} else {
			LogicalExpression nextExpression;
			collective_stack.push(this.getOperator());
			for (Enumeration e = this.subexpressions.elements(); e.hasMoreElements();) {
				nextExpression = (LogicalExpression) e.nextElement();
				nextExpression.getSolution(model);
			}
			response = getRes(model);
		}
		return response;
	}

	private boolean getRes(HashMap<String, String> model) {
		ArrayList<String> diff = new ArrayList<String>();
		String symbol, operator;
		boolean final_computation = false;
		do {
			symbol = collective_stack.pop();
			diff.add(symbol);
		} while (
			!(symbol.equalsIgnoreCase("xor") 
				|| symbol.equalsIgnoreCase("if") 
				|| symbol.equalsIgnoreCase("and") 
				|| symbol.equalsIgnoreCase("not") 
				|| symbol.equalsIgnoreCase("iff") 
				|| symbol.equalsIgnoreCase("or")
			)
		);

		diff.remove(symbol);
		operator = symbol;

		if (operator.equalsIgnoreCase("and")) {
			final_computation = true;
			while (!diff.isEmpty() && final_computation) {
				final_computation = final_computation && getTrueVal(diff.remove(0), model);
			}
		} else if (operator.equalsIgnoreCase("not")) {
			final_computation = true;
			final_computation = !getTrueVal(diff.remove(0), model);
		} else if (operator.equalsIgnoreCase("or")) {
			final_computation = false;
			while (!diff.isEmpty() && !final_computation) {
				final_computation = final_computation || getTrueVal(diff.remove(0), model);
			}
		} else if (operator.equalsIgnoreCase("if")) {
			final_computation = true;
			if (diff.size() == 2) {
				if (getTrueVal(diff.get(1), model) && !getTrueVal(diff.get(0), model)) {
					final_computation = false;
				}
			}
		} else if (operator.equalsIgnoreCase("xor")) {
			final_computation = false;
			int possitive_count_val = 0;
			while (!diff.isEmpty()) {
				if (getTrueVal(diff.remove(0), model)) {
					possitive_count_val++;
				}
			}
			if (possitive_count_val == 1) {
				final_computation = true;
			}
		} else if (operator.equalsIgnoreCase("iff")) {
			final_computation = false;
			if (diff.size() == 2) {
				boolean symbol1 = getTrueVal(diff.get(1), model);
				boolean symbol2 = getTrueVal(diff.get(0), model);
				if (symbol1 == symbol2) {
					final_computation = true;
				}
			}
		} else {
			System.out.println("Invalid operator");
		}

		if (final_computation) {
			collective_stack.push("T");
		} else {
			collective_stack.push("F");
		}
		return final_computation;
	}

	private boolean getTrueVal(String sym, HashMap<String, String> model) {
		if (sym.equalsIgnoreCase("T")) {
			return true;
		} else if (sym.equalsIgnoreCase("F")) {
			return false;
		} else if (model.get(sym) == null) {
			return CheckTrueFalse.getResponseFromArr(sym);
		} else {
			return model.get(sym) != null;
		}
	}

	private void exit_function(int value) {
		System.out.println("exiting from LogicalExpression");
		System.exit(value);
	}
}