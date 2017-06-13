package jp.mzw.ajaxmutator.detector.jquery;

import java.util.List;
import java.util.Set;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import com.google.common.collect.ImmutableSet;

/**
 * Event detector that detect jQuery like event attachment. e.g.,
 * $("#hoge").click('hoge'); $("#hoge").on('click', hoge);
 *
 * @author Kazuki Nishiura
 */
public class JQueryEventAttachmentDetector extends EventAttacherDetector {
    private final Set<String> jQueryEvents
        = ImmutableSet.of("blur", "change",
            "click", "dblclick", "error", "focus", "keydown", "keypress",
            "keyup", "load", "mousedown", "mousemove", "mouseout", "mouseover",
            "mouseup", "resize", "scroll", "select", "submit", "unload");
    private final Set<String> jQueryEventAttachers
        = ImmutableSet.of("bind", "on", "one", "live");

    @Override
    public EventAttachment detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            String methodName = propertyGet.getProperty().getIdentifier();
            if (jQueryEvents.contains(methodName) && arguments.size() > 0) {
                // e.g., target.click(callback);
                return new EventAttachment(functionCall,
                        propertyGet.getTarget(), propertyGet.getProperty(),
                        arguments.get(0));
            } else if (jQueryEventAttachers.contains(methodName) && arguments.size() > 0) {
                // e.g., target.on('click', callback);
                return new EventAttachment(functionCall,
                        propertyGet.getTarget(), arguments.get(0),
                        arguments.get(arguments.size() - 1));
            }
        }
        return null;
    }
}
