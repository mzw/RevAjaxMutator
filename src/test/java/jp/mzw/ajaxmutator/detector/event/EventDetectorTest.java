package jp.mzw.ajaxmutator.detector.event;

import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;

import org.junit.Test;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAsFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventDetectorTest {
    @Test
    public void testAddEventListnerDetector() {
        EventAttacherDetector detector = new AddEventListenerDetector();

        EventAttachment result
            = detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);"));
        assertTrue(result != null);
        assertEquals("target", ((Name) result.getTarget()).getIdentifier());

        result = detector.detect(parseAsFunctionCall(
                "target.addEventListenr('click', func);"));
        assertTrue(result == null);
    }

    @Test
    public void testAttachEventDetector() {
        EventAttacherDetector detector = new AttachEventDetector();

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.attachEvent('onclick', func);")) != null);

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);")) == null);
    }

    @Test
    public void testTimerEventDetector() {
        TimerEventDetector detector = new TimerEventDetector();
        assertTrue(detector.detect(
                parseAsFunctionCall("setInterval(func, 1000)")) != null);
        assertTrue(detector.detect(
                parseAsFunctionCall("window.setTimeout(func, callAfter)")) != null);
        assertTrue(detector.detect(
                parseAsFunctionCall("window.setInterval(func, funcName)")) != null);

        TimerEventAttachment attachment = detector.detect(
                parseAsFunctionCall("setTimeout(func, 1000)"));
        NumberLiteral duration = (NumberLiteral) attachment.getDuration();
        assertEquals(1000.0, duration.getNumber(), 0.0000000000001);

        assertTrue(detector.detect(parseAsFunctionCall(
                "target.addEventListener('click', func);")) == null);
    }
}
