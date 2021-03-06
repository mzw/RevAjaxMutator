package jp.mzw.ajaxmutator.detector.yui;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;

public class YUIDelegateDetector extends EventAttacherDetector {
    static private String targetString = "delegate";

    @Override
    public EventAttachment detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            if (targetString.equals(propertyGet.getProperty().getIdentifier())) {
                return new EventAttachment(functionCall,
                        arguments.get(2), arguments.get(0),
                        arguments.get(1));
            }
        }
        return null;
    }
}