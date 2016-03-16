package jp.mzw.ajaxmutator.detector.event;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;

/**
 * Detect event attachment in form of target.attachEvent('on'+event, callback).
 * IE may use this kind of event attachment.
 * 
 * @author Kazuki Nishiura
 */
public class AttachEventDetector extends EventAttacherDetector {
    static private String targetString = "attachEvent";

    @Override
    public EventAttachment detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            if (targetString.equals(propertyGet.getProperty().getIdentifier())) {
                return new EventAttachment(functionCall,
                        propertyGet.getTarget(), arguments.get(0),
                        arguments.get(1));
            }
        }
        return null;
    }
}
