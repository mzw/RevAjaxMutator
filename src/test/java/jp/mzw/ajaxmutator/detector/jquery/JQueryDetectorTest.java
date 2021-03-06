package jp.mzw.ajaxmutator.detector.jquery;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.*;
import jp.mzw.ajaxmutator.mutatable.Request.ResponseType;

import org.junit.Test;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAsFunctionCall;
import static org.junit.Assert.*;

public class JQueryDetectorTest {
    @Test
    public void testJQueryAppendDetector() {
        JQueryAppendDetector detector = new JQueryAppendDetector();

        DOMAppending domAppending = detector.detect(parseAsFunctionCall(
                "$('#elm').append(child);"));
        assertNotNull(domAppending);
        assertEquals("$('#elm')", domAppending.getAppendTarget().toSource());
        assertEquals("child", domAppending.getAppendedDom().toSource());

        domAppending = detector.detect(parseAsFunctionCall(
                "item.appendTo('body');"));
        assertNotNull(domAppending);
        assertEquals("'body'", domAppending.getAppendTarget().toSource());
        assertEquals("item", domAppending.getAppendedDom().toSource());
    }

    @Test
    public void testJQueryEventDetector() {
        EventAttacherDetector detector = new JQueryEventAttachmentDetector();

        EventAttachment eventAttachment = detector.detect(parseAsFunctionCall(
                "$('#elm').on('click', func);"));
        assertNotNull(eventAttachment);
        assertEquals("$('#elm')", eventAttachment.getTarget().toSource());
        assertEquals("'click'", eventAttachment.getEvent().toSource());
        assertEquals("func", eventAttachment.getCallback().toSource());

        eventAttachment = detector.detect(parseAsFunctionCall(
                "target.click(func);"));
        assertNotNull(eventAttachment);
        assertEquals("target", eventAttachment.getTarget().toSource());
        assertEquals("click", eventAttachment.getEvent().toSource());
        assertEquals("func", eventAttachment.getCallback().toSource());
    }

    @Test
    public void jQueryDomSelectionDetectorTest() {
        JQueryDOMSelectionDetector detector = new JQueryDOMSelectionDetector();
        DOMSelection result = detector.detect(parseAsFunctionCall("$('.aaa')"));
        assertTrue(result != null);
        assertEquals(DOMSelection.SelectionMethod.JQUERY,
                result.getSelectionMethod());
        assertEquals("'.aaa'", result.getSelector().toSource());
    }

    @Test
    public void testJQueryRequestDetector() {
        JQueryRequestDetector detector = new JQueryRequestDetector();
        String data = "{index: 1, value: 'fuga'}";
        Request result = detector.detect(parseAsFunctionCall(
                "$.get('hogehoge.php', " + data
                        + ", function(data){var d = data.member; func(d);});"));
        assertTrue(result != null);
        assertEquals("'hogehoge.php'", result.getUrl().toSource());
        assertEquals(data.replace("\n", "").replace(" ", ""), result.getParameters().toSource().replace("\n", "").replace(" ", ""));
        assertEquals(Request.RequestMethod.GET, result.getRequestMethod());
        assertTrue(result.getSuccessHanlder() != null);
        assertTrue(result.getFailureHandler() == null);

        data = "{type: 'POST', dataType: 'html', success: func1, error: func2}";
        result = detector.detect(parseAsFunctionCall("$.ajax('fugafuga.php', "
                + data + ");"));
        assertTrue(result != null);
        assertEquals("'fugafuga.php'", result.getUrl().toSource());
        assertEquals(ResponseType.HTML, result.getResponseType());
        assertEquals("func1", result.getSuccessHanlder().toSource());
        assertEquals(Request.RequestMethod.POST, result.getRequestMethod());
        assertEquals("func2", result.getFailureHandler().toSource());
    }

    @Test
    public void testJQueryAttributeModificationDetector() {
        JQueryAttributeModificationDetector detector = new JQueryAttributeModificationDetector();
        AttributeModification result = detector.detect(parseAsFunctionCall(
                "$(this).attr('disabled', true)"));
        assertTrue(result != null);
        assertEquals("$(this)", result.getTargetDom().toSource());
        assertEquals("'disabled'", result.getTargetAttribute().toSource());
        assertEquals("true", result.getAttributeValue().toSource());
    }

    @Test
    public void testJQueryCloningDetector() {
        JQueryCloneDetector detector = new JQueryCloneDetector();
        DOMCloning result = detector.detect(parseAsFunctionCall(
                "$('.hello').clone()"));
        assertTrue(result != null);
        assertEquals("$('.hello')", result.getTargetNode().toSource());
    }

    @Test
    public void testJQueryReplaceWithDetector() {
        JQueryReplaceWithDetector detector = new JQueryReplaceWithDetector();
        DOMReplacement result = detector.detect(
                parseAsFunctionCall("$(this).replaceWith(anotherElement)"));
        assertTrue(result != null);
        assertEquals("$(this)", result.getReplacedNode().toSource());
        assertEquals("anotherElement", result.getReplacingNode().toSource());
    }
}
