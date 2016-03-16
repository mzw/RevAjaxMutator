package jp.mzw.ajaxmutator.detector.jquery;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.DOMCloning;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.List;

/**
 * detector for element.clone()
 */
public class JQueryCloneDetector extends AbstractDetector<DOMCloning> {
    private static final String CLONE_KEYWORD = "clone";

    @Override
    public DOMCloning detect(AstNode node) {
        return detectFromFunctionCall(node, false);
    }

    @Override
    public DOMCloning detectFromFunctionCall(
            FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            String methodName = propertyGet.getProperty().getIdentifier();
            if (CLONE_KEYWORD.equals(methodName)) {
                return new DOMCloning(functionCall, propertyGet.getTarget());
            }
        }
        return null;
    }
}
