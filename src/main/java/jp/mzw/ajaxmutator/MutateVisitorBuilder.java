package jp.mzw.ajaxmutator;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.mutatable.*;
import jp.mzw.ajaxmutator.mutatable.generic.*;
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
	private Set<? extends AbstractDetector<Statement>> statementDetectors
			= Collections.emptySet();
	private Set<? extends AbstractDetector<AssignmentExpression>> assignmentDetectors
			= Collections.emptySet();
	private Set<? extends AbstractDetector<Break>> breakDetectors
			= Collections.emptySet();
	private Set<? extends AbstractDetector<Continue>> continueDetectors
			= Collections.emptySet();
	private Set<? extends AbstractDetector<For>> forDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<FuncNode>> funcNodeDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<If>> ifDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<Return>> returnDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<Switch>> switchDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<VariableDec>> variableDeclarationDetectors 
			= Collections.emptySet();
	private Set<? extends AbstractDetector<While>> whileDetectors 
			= Collections.emptySet();

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

	public void setStatementDetectors(
			Set<? extends AbstractDetector<Statement>> statementDetectors) {
		this.statementDetectors = statementDetectors;
	}

	public void setAssignmentDetectors(Set<? extends AbstractDetector<AssignmentExpression>> assignmentDetectors){
		this.assignmentDetectors = assignmentDetectors;
	}

	public void setBreakDetectors(Set<? extends AbstractDetector<Break>> breakDetectors){
		this.breakDetectors = breakDetectors;
	}

	public void setContinueDetectors(Set<? extends AbstractDetector<Continue>> continueDetectors){
		this.continueDetectors  = continueDetectors;
	}

	public void setForDetectors(Set<? extends AbstractDetector<For>> forDetectors){
		this.forDetectors  = forDetectors;
	}

	public void setFuncNodeDetectors(Set<? extends AbstractDetector<FuncNode>> funcNodeDetectors){
		this.funcNodeDetectors  = funcNodeDetectors;
	}

	public void setIfDetectors(Set<? extends AbstractDetector<If>> ifDetectors){
		this.ifDetectors  = ifDetectors;
	}

	public void setReturnDetectors(Set<? extends AbstractDetector<Return>> returnDetectors){
		this.returnDetectors  = returnDetectors;
	}

	public void setSwitchDetectors(Set<? extends AbstractDetector<Switch>> switchDetectors){
		this.switchDetectors  = switchDetectors;
	}

	public void setVariableDeclarationDetectors(Set<? extends AbstractDetector<VariableDec>> variableDeclarationDetectors){
		this.variableDeclarationDetectors  = variableDeclarationDetectors;
	}

	public void setWhileDetectors(Set<? extends AbstractDetector<While>> whileDetectors){
		this.whileDetectors  = whileDetectors;
	}

}
