package jp.mzw.ajaxmutator;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.mutatable.*;
import jp.mzw.ajaxmutator.mutatable.genprog.*;

import java.util.Collections;
import java.util.Set;

public class MutateVisitorBuilder {
    private Set<EventAttacherDetector> eventAttacherDetectors
        = Collections.emptySet();
    private Set<TimerEventDetector> timerEventDetectors
        = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors
        = Collections.emptySet();
    private Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMNormalization>> domNormalizationDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMReplacement>> domReplacementDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<Request>> requestDetectors
            = Collections.emptySet();
    private Set<? extends AbstractDetector<Statement>> statementDetectors = Collections.emptySet();

    protected MutateVisitorBuilder() {

    }

    public MutateVisitor build() {
        return new MutateVisitor(eventAttacherDetectors, timerEventDetectors,
                domCreationDetectors, attributeModificationDetectors, domAppendingDetectors,
                domCloningDetectors, domNormalizationDetectors, domReplacementDetectors,
                domRemovalDetectors, domSelectionDetectors, requestDetectors,
                statementDetectors);
    }

    public void setEventAttacherDetectors(
            Set<EventAttacherDetector> eventAttacherDetectors) {
        this.eventAttacherDetectors = eventAttacherDetectors;
    }

    public void setTimerEventDetectors(
            Set<TimerEventDetector> timerEventDetectors) {
        this.timerEventDetectors = timerEventDetectors;
    }

    public void setDomCreationDetectors(
            Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors) {
        this.domCreationDetectors = domCreationDetectors;
    }

    public void setAttributeModificationDetectors(
            Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors) {
        this.attributeModificationDetectors = attributeModificationDetectors;
    }

    public void setDomAppendingDetectors(
            Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors) {
        this.domAppendingDetectors = domAppendingDetectors;
    }

    public void setDomCloningDetectors(
            Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors) {
        this.domCloningDetectors = domCloningDetectors;
    }

    public void setDomNormalizationDetectors(
            Set<? extends AbstractDetector<DOMNormalization>> domNormalizationDetectors) {
        this.domNormalizationDetectors = domNormalizationDetectors;
    }

    public void setDomReplacementDetectors(
            Set<? extends AbstractDetector<DOMReplacement>> domReplacementDetectors) {
        this.domReplacementDetectors = domReplacementDetectors;
    }

    public void setDomRemovalDetectors(
            Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors) {
        this.domRemovalDetectors = domRemovalDetectors;
    }

    public void setDomSelectionDetectors(
            Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors) {
        this.domSelectionDetectors = domSelectionDetectors;
    }

    public void setRequestDetectors(
            Set<? extends AbstractDetector<Request>> requestDetectors) {
        this.requestDetectors = requestDetectors;
    }
    
    public void setStatementDetectors(Set<? extends AbstractDetector<Statement>> statementDetectors) {
    	this.statementDetectors = statementDetectors;
    }
}
