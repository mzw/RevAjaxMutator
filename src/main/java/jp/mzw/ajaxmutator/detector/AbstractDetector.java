package jp.mzw.ajaxmutator.detector;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;

/**
 * Abstract detector which provide common functionality required to implement
 * Detector.
 *
 * @author Kazuki Nishiura
 * @param <T>
 */
public abstract class AbstractDetector<T extends Mutatable>
        implements MutationPointDetector<T> {
    /**
     * Assuming node is FunctionCall and detect if node is desired element.
     *
     * @return T if node is FunctionCall instance and what we want to focus,
     *         otherwise, return null
     */
    protected T detectFromFunctionCall(AstNode node) {
        return detectFromFunctionCall(node, false);
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
    protected T detectFromAssignment(
            Assignment assignment, AstNode left, AstNode right) {
        return null;
    }
}
