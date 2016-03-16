package jp.mzw.ajaxmutator.detector.dom;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;

public class DOMSelectionDetector extends AbstractDetector<DOMSelection> {
    @Override
    public DOMSelection detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }

    @Override
    protected DOMSelection detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        AstNode range = null;
        String selectionMethod = null;

        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            range = propertyGet.getTarget();
            selectionMethod = propertyGet.getProperty().getIdentifier();

            DOMSelection.SelectionMethod method = null;
            if ("getElementById".equals(selectionMethod)) {
                method = DOMSelection.SelectionMethod.ID;
            } else if ("getElementsByTagName".equals(selectionMethod)) {
                method = DOMSelection.SelectionMethod.TAG_NAME;
            } else if ("getElementsByClassName".equals(selectionMethod)) {
                method = DOMSelection.SelectionMethod.CLASS;
            } else if ("getElementsByName".equals(selectionMethod)) {
                method = DOMSelection.SelectionMethod.NAME;
            }

            if (method != null)
                return new DOMSelection(functionCall, range, method,
                        arguments.get(0));
        }

        return null;
    }
}
