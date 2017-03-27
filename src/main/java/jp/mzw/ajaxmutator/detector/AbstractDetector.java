package jp.mzw.ajaxmutator.detector;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;

/**
 * Abstract detector which provide common functionality required to implement
 * Detector.
 *
 * @author Kazuki Nishiura
 * @param <T>
 */
public abstract class AbstractDetector<T extends Mutatable> implements
		MutationPointDetector<T> {
	/**
	 * Assuming node is FunctionCall and detect if node is desired element.
	 *
	 * @return T if node is FunctionCall instance and what we want to focus,
	 *         otherwise, return null
	 */
	protected T detectFromFunctionCall(AstNode node) {
		return detectFromFunctionCall(node, false);
	}

	protected T detectFromFunction(AstNode node) {
		return detectFromFunction(node, false);
	}

	protected T detectFromAssignment(AstNode node) {
		return detectFromAssignment(node, false);
	}

	protected T detectFromBranch(AstNode node) {
		return detectFromFunctionCall(node, false);
	}

	protected T detectFromVarAndFuncParam(AstNode node) {
		return detectFromVarAndFuncParam(node, false);
	}

	/**
	 * Assuming node is FunctionCall and detect if node is desired element.
	 *
	 * @param strict
	 *            if passed argument node do not have type FunctionCall, throw
	 *            exception.
	 * @return T if node is FunctionCall instance and what we want to focus,
	 *         otherwise, return null
	 * @throws IllegalStatementException
	 *             if strict is true and node is not an instance of
	 *             FunctionalCall
	 */
	protected T detectFromFunctionCall(AstNode node, boolean strict) {
		if (node instanceof FunctionCall) {
			FunctionCall funcCall = (FunctionCall) node;
			return detectFromFunctionCall(funcCall, funcCall.getTarget(),
					funcCall.getArguments());
		} else if (strict) {
			throw new IllegalArgumentException(this.getClass() + ".detect()"
					+ " must receive any function call, but called for "
					+ node.getClass());
		}
		return null;
	}

	protected T detectFromFunction(AstNode node, boolean strict) {
		if (node instanceof FunctionNode) {
			FunctionNode funcNode = (FunctionNode) node;
			return detectFromFunction(funcNode, funcNode.getParams(),
					funcNode.getBody(), funcNode.getName(),
					funcNode.getMemberExprNode());
		} else if (strict) {
			throw new IllegalArgumentException(this.getClass() + ".detect()"
					+ " must receive any function call, but called for "
					+ node.getClass());
		}
		return null;
	}

	/**
	 * detect Mutatable from passed function call (e.g.,
	 * hoge.attachEvent('onclick', callback) )
	 *
	 * @param functionCall
	 *            whole function call (e.g. hoge.attachEvent('onclick',
	 *            callback))
	 * @param target
	 *            target for function call (e.g. hoge.attachEvent)
	 * @param arguments
	 *            argument for function call (e.g. ['onclick', callback])
	 * @return T instance or null
	 */
	protected T detectFromFunctionCall(FunctionCall functionCall,
			AstNode target, List<AstNode> arguments) {
		return null;
	}

	protected T detectFromFunction(FunctionNode functionNode,
			List<AstNode> params, AstNode body, String name, AstNode member) {
		return null;
	}

	/**
	 * Assuming node is Assignment and detect if node is desired element.
	 *
	 * @return T if node is Assignment instance and what we want to focus,
	 *         otherwise, return null
	 * @throws IllegalStatementException
	 *             if strict is true and node is not an instance of Assignment
	 */
	protected T detectFromAssignment(AstNode node, boolean strict) {
		if (node instanceof Assignment) {
			Assignment assignment = (Assignment) node;
			return detectFromAssignment(assignment, assignment.getLeft(),
					assignment.getRight());
		} else if (strict) {
			throw new IllegalArgumentException(this.getClass() + ".detect()"
					+ " must receive any assignment, but called for "
					+ node.getClass());
		}
		return null;
	}

	/**
	 * detect Mutatable from passed assignment (e.g., obj.mem = 100)
	 *
	 * @param Assignment
	 *            whole function call (e.g., obj.mem = 100)
	 * @param left
	 *            left node of assignment
	 * @param right
	 *            right node of assignment
	 * @return T instance or null
	 */
	protected T detectFromAssignment(Assignment assignment, AstNode left,
			AstNode right) {
		return null;
	}

	protected T detectFromBranch(AstNode node, boolean strict) {
		if (node instanceof IfStatement) {
			IfStatement ifStatement = (IfStatement) node;
			return detectFromBranch(ifStatement, ifStatement.getCondition(),
					ifStatement.getElsePart());
		} else if (node instanceof ForLoop) {
			ForLoop forLoop = (ForLoop) node;
			return detectFromBranch(forLoop, forLoop.getCondition(),
					forLoop.getBody(), forLoop.getInitializer(),
					forLoop.getIncrement());
		} else if (node instanceof ReturnStatement) {
			ReturnStatement returnStatement = (ReturnStatement) node;
			return detectFromBranch(returnStatement,
					returnStatement.getReturnValue());
		} else if (node instanceof WhileLoop) {
			WhileLoop whileLoop = (WhileLoop) node;
			return detectFromBranch(whileLoop, whileLoop.getCondition(),
					whileLoop.getBody());
		} else if (node instanceof SwitchStatement) {
			SwitchStatement switchStatement = (SwitchStatement) node;
			return detectFromBranch(switchStatement);
		} else if (node instanceof BreakStatement) {
			BreakStatement breakStatement = (BreakStatement) node;
			return detectFromBranch(breakStatement);
		} else if (node instanceof ContinueStatement) {
			ContinueStatement continueStatement = (ContinueStatement) node;
			return detectFromBranch(continueStatement);
		}
		return null;
	}

	protected T detectFromVarAndFuncParam(AstNode node, boolean strict) {
		if (node instanceof VariableDeclaration) {
			VariableDeclaration variableDeclaration = (VariableDeclaration) node;
			return detectFromVarAndFuncParam(variableDeclaration);
		} else if (node instanceof Assignment) {
			Assignment assignment = (Assignment) node;
			return detectFromVarAndFuncParam(assignment);
		}
		return null;
	}

	protected T detectFromVarAndFuncParam(
			VariableDeclaration variabledeclaration) {
		return null;
	}

	protected T detectFromVarAndFuncParam(Assignment assignment) {
		return null;
	}

	protected T detectFromBranch(IfStatement ifstatement, AstNode condition,
			AstNode elsepart) {
		return null;
	}

	protected T detectFromBranch(ForLoop forloop, AstNode condition,
			AstNode body, AstNode initializer, AstNode increment) {
		return null;
	}

	protected T detectFromBranch(ReturnStatement returnstatement, AstNode value) {
		return null;
	}

	protected T detectFromBranch(WhileLoop whileloop, AstNode condition,
			AstNode body) {
		return null;
	}

	protected T detectFromBranch(SwitchStatement switchstatement) {
		return null;
	}

	protected T detectFromBranch(BreakStatement breakstatement) {
		return null;
	}

	protected T detectFromBranch(ContinueStatement continuestatement) {
		return null;
	}
}
