package jp.mzw.revajaxmutator.detector.mootools;

import java.util.List;
import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.detector.AbstractDetector;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request.ResponseType;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.ImmutableSet;

public class MootoolsRequestDetector extends AbstractDetector<Request> {
    private static final String CONSTRUCTOR = "Request";
    public static final Set<String> METHODS = ImmutableSet.of("get", "send");

    private AstNode successHandler;
    private AstNode failureHandler;
    private AstNode requestMethodNode;
    private Request.ResponseType responseType;
    private AstNode data;
    private AstNode url;

    @Override
    public Request detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }

    @Override
    public Request detectFromFunctionCall(FunctionCall functionCall,
            AstNode target, List<AstNode> arguments) {
        resetParams();
        if (!(target instanceof PropertyGet)) return null;
        PropertyGet properyGet = (PropertyGet) target;
        AstNode newExpr = properyGet.getTarget();
        if (!(newExpr instanceof NewExpression)) return null;
        AstNode constructor = ((NewExpression) newExpr).getTarget();
        if (!(constructor instanceof Name)) return null;
        if (!(CONSTRUCTOR.equals(((Name) constructor).getIdentifier()))) return null;
        Name methodName = properyGet.getProperty();
        if (!(METHODS.contains(methodName.getIdentifier()))) return null;
        List<AstNode> newArguments = ((NewExpression) newExpr).getArguments();
        if (!(newArguments.size() == 1)) return null;
        AstNode obj = newArguments.get(0);
        if (!(obj instanceof ObjectLiteral)) return null;
        parseParams((ObjectLiteral) obj);
        System.err.println(obj.toSource());
        return new Request(functionCall, url,
                successHandler, failureHandler, methodName,
                ResponseType.TEXT, data, Request.Type.UNKNOWN);
    }

    private void resetParams() {
        successHandler = null;
        failureHandler = null;
        responseType = ResponseType.TEXT;
        data = null;
        url = null;
    }

    private void parseParams(ObjectLiteral params) {
        data = params;
        for (ObjectProperty property : params.getElements()) {
            AstNode left = property.getLeft();
            String leftInStr = null;
            if (left instanceof StringLiteral) {
                leftInStr = ((StringLiteral) left).getValue();
            } else if (left instanceof Name) {
                leftInStr = ((Name) left).getIdentifier();
            } else {
                continue;
            }
            AstNode right = property.getRight();

            if ("onSuccess".equals(leftInStr)) {
                successHandler = right;
            } else if ("url".equals(leftInStr)) {
                url = right;
            }
        }
    }
}
