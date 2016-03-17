package jp.mzw.ajaxmutator.detector.yui;

import java.util.List;
import java.util.Set;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import com.google.common.collect.ImmutableSet;

public class YUIEventAttachementDetector extends EventAttacherDetector {
	@Override
    public EventAttachment detectFromFunctionCall(FunctionCall functionCall, AstNode target, List<AstNode> arguments) {
        if (target instanceof PropertyGet) {
            PropertyGet propertyGet = (PropertyGet) target;
            String methodName = propertyGet.getProperty().getIdentifier();

            /// To be updated
        	if (YUIEventAttachers.contains(methodName)) {
        		// e.g., target.on('click', callback);
        		return new EventAttachment(
        				functionCall,
        				propertyGet.getTarget(),
        				arguments.get(0),
        				arguments.get(arguments.size() - 1));
        	}
        }
        return null;
    }

	/**
	 * All event types come from Whitelisted DOM events at http://yuilibrary.com/yui/docs/event/
	 */
    @SuppressWarnings("unused")
	private final Set<String> YUIDOMEvents = ImmutableSet.of(
    		"abort",
    		"beforeunload",
    		"blur",
    		"change",
    		"click",
    		"close",
    		"command",
    		"contextmenu",
    		"dblclick",
    		"DOMMouseScroll",
    		"drag",
    		"dragstart",
    		"dragenter",
    		"dragover",
    		"dragleave",
    		"dragend",
    		"drop",
    		"error",
    		"focus",
    		"key",
    		"keydown",
    		"keypress",
    		"keyup",
    		"load",
    		"message",
    		"mousedown",
    		"mouseenter",
    		"mouseleave",
    		"mousemove",
    		"mousemultiwheel",
    		"mouseout",
    		"mouseover",
    		"mouseup",
    		"mousewheel",
    		"orientationchange",
    		"reset",
    		"resize",
    		"select",
    		"selectstart",
    		"submit",
    		"scroll",
    		"tap",
    		"textInput",
    		"unload",
    		"DOMActivate",
    		"DOMContentLoaded",
    		"afterprint",
    		"beforeprint",
    		"canplay",
    		"canplaythrough",
    		"durationchange",
    		"emptied",
    		"ended",
    		"formchange",
    		"forminput",
    		"hashchange",
    		"input",
    		"invalid",
    		"loadedmetadata",
    		"loadeddata",
    		"loadstart",
    		"offline",
    		"online",
    		"pagehide",
    		"pageshow",
    		"pause",
    		"play",
    		"playing",
    		"popstate",
    		"progress",
    		"ratechange",
    		"readystatechange",
    		"redo",
    		"seeking",
    		"seeked",
    		"show",
    		"stalled",
    		"suspend",
    		"timeupdate",
    		"undo",
    		"volumechange",
    		"waiting",
    		"touchstart",
    		"touchmove",
    		"touchend",
    		"touchcancel",
    		"gesturestart",
    		"gesturechange",
    		"gestureend",
    		"transitionend",
    		"webkitTransitionEnd"
            );
    /**
     * To be updated
     */
    private final Set<String> YUIEventAttachers = ImmutableSet.of(
    		"on"
    		);
    
    
}
