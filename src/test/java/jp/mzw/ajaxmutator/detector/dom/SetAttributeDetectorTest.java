package jp.mzw.ajaxmutator.detector.dom;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;

import org.junit.Test;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.StringLiteral;

import static jp.mzw.ajaxmutator.util.StringToAst.parseAsFunctionCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SetAttributeDetectorTest {
    @Test
    public void testAppendChildDetector() {
        SetAttributeDetector detector = new SetAttributeDetector();

        AttributeModification result
                = detector.detect(parseAsFunctionCall("document.getElementsByTagName('tr')[3].setAttribute('id', value);"));
        assertTrue(result != null);
        assertEquals("id", ((StringLiteral) result.getTargetAttribute()).getValue());
        assertEquals("value", ((Name) result.getAttributeValue()).getIdentifier());
        assertEquals("document.getElementsByTagName('tr')[3]", result.getTargetDom().toSource());
    }
}
