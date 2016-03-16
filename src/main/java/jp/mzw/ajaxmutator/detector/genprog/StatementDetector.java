package jp.mzw.ajaxmutator.detector.genprog;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.ReturnStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

/**
 * 
 * @author Yuta Maezawa
 *
 */
public class StatementDetector extends AbstractDetector<Statement> {

	@Override
	public Statement detect(AstNode node) {

		/*
		 * GenProg
		 * 
		 * @src/cilrep.ml let can_repair_statement sk = match sk with | Instr _
		 * | Return _ | If _ | Loop _ -> true
		 * 
		 * | Goto _ | Break _ | Continue _ | Switch _ | Block _ | TryFinally _ |
		 * TryExcept _ -> false | _ -> false
		 */

		/*
		 * https://www.cs.berkeley.edu/~necula/cil/api/Cil.html#TYPEinstr
		 * Instructions. An instruction Cil.instr is a statement that has no
		 * local (intraprocedural) control flow. It can be either an assignment,
		 * function call, or an inline assembly instruction.
		 */

		if (node instanceof Assignment) {
			return new jp.mzw.ajaxmutator.mutatable.genprog.Assignment(node);
		}
		if (node instanceof FunctionCall) {
			return new jp.mzw.ajaxmutator.mutatable.genprog.FunctionCall(node);
		}

		// Return
		if (node instanceof ReturnStatement) {
			return new jp.mzw.ajaxmutator.mutatable.genprog.ReturnStatement(
					node);
		}

		// If
		if (node instanceof IfStatement) {
			return new jp.mzw.ajaxmutator.mutatable.genprog.IfStatement(node);
		}

		// Loop
		if (node instanceof Loop) {
			return new jp.mzw.ajaxmutator.mutatable.genprog.Loop(node);
		}

		return null;
	}

}
