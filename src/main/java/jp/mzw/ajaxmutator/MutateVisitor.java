package jp.mzw.ajaxmutator;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
import jp.mzw.ajaxmutator.detector.MutationPointDetector;
import jp.mzw.ajaxmutator.detector.dom.*;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.detector.event.AttachEventDetector;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.detector.jquery.*;
import jp.mzw.ajaxmutator.mutatable.*;
import jp.mzw.ajaxmutator.mutatable.generic.*;
import jp.mzw.ajaxmutator.mutatable.genprog.*;

import org.bouncycastle.crypto.engines.ISAACEngine;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.Loop;

import java.util.Set;
import java.util.TreeSet;

/**
 * Visitor for JavaScript's AST to get information needed to apply mutation
 * operations.
 *
 * @author Kazuki Nishiura
 */
public class MutateVisitor implements NodeVisitor {
    private final ImmutableSet<EventAttacherDetector> eventAttacherDetectors;
    private final ImmutableSet<TimerEventDetector> timerEventDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMCreation>> domCreationDetectors;
    private final ImmutableSet<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMAppending>> domAppendingDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMCloning>> domCloningDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMNormalization>> domNormalizationDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMReplacement>> domReplacementDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors;
    private final ImmutableSet<? extends AbstractDetector<DOMSelection>> domSelectionDetectors;
    private final ImmutableSet<? extends AbstractDetector<Request>> requestDetectors;
    private final ImmutableSet<? extends AbstractDetector<Statement>> statementDetectors;
    private final ImmutableSet<? extends AbstractDetector<AssignmentExpression>> assignmentDetectors;
    private final ImmutableSet<? extends AbstractDetector<Break>> breakDetectors;
    private final ImmutableSet<? extends AbstractDetector<Continue>> continueDetectors;
    private final ImmutableSet<? extends AbstractDetector<For>> forDetectors;
    private final ImmutableSet<? extends AbstractDetector<FuncNode>> funcNodeDetectors;
    private final ImmutableSet<? extends AbstractDetector<If>> ifDetectors;
    private final ImmutableSet<? extends AbstractDetector<Return>> returnDetectors;
    private final ImmutableSet<? extends AbstractDetector<Switch>> switchDetectors;
    private final ImmutableSet<? extends AbstractDetector<VariableDec>> variableDeclarationDetectors;
    private final ImmutableSet<? extends AbstractDetector<While>> whileDetectors;

    private final Set<EventAttachment> eventAttachments
        = new TreeSet<EventAttachment>();
    private final Set<TimerEventAttachment> timerEventAttachmentExpressions
        = new TreeSet<TimerEventAttachment>();
    private final Set<DOMCreation> domCreations = new TreeSet<DOMCreation>();
    private final Set<AttributeModification> attributeModifications
        = new TreeSet<AttributeModification>();
    private final Set<DOMAppending> domAppendings = new TreeSet<DOMAppending>();
    private final Set<DOMCloning> domClonings = new TreeSet<DOMCloning>();
    private final Set<DOMNormalization> domNormalizations = new TreeSet<DOMNormalization>();
    private final Set<DOMReplacement> domReplacements = new TreeSet<DOMReplacement>();
    private final Set<DOMRemoval> domRemovals = new TreeSet<DOMRemoval>();
    private final Set<DOMSelection> domSelections = new TreeSet<DOMSelection>();
    private final Set<Request> requests = new TreeSet<Request>();
    private final Set<Statement> statements = new TreeSet<Statement>();
    private final Set<AssignmentExpression> assignmentExpressions = new TreeSet<AssignmentExpression>();
    private final Set<Break> breaks = new TreeSet<Break>();
    private final Set<Continue> continues = new TreeSet<Continue>();
    private final Set<For> fors = new TreeSet<For>();
    private final Set<FuncNode> funcNodes = new TreeSet<FuncNode>();
    private final Set<If> ifs = new TreeSet<If>();
    private final Set<Return> returns = new TreeSet<Return>();
    private final Set<Switch> switches = new TreeSet<Switch>();
    private final Set<VariableDec> variableDecs = new TreeSet<VariableDec>();
    private final Set<While> whiles = new TreeSet<While>();
    

    public MutateVisitor(
            Set<EventAttacherDetector> eventAttacherDetectors,
            Set<TimerEventDetector> timerEventDetectors,
            Set<? extends AbstractDetector<DOMCreation>> domCreationDetectors,
            Set<? extends AbstractDetector<AttributeModification>> attributeModificationDetectors,
            Set<? extends AbstractDetector<DOMAppending>> domAppendingDetectors,
            Set<? extends AbstractDetector<DOMCloning>> domCloningDetectors,
            Set<? extends AbstractDetector<DOMNormalization>> domNormalizationDetectors,
            Set<? extends AbstractDetector<DOMReplacement>> domReplacementDetectors,
            Set<? extends AbstractDetector<DOMRemoval>> domRemovalDetectors,
            Set<? extends AbstractDetector<DOMSelection>> domSelectionDetectors,
            Set<? extends AbstractDetector<Request>> requestDetectors,
            Set<? extends AbstractDetector<Statement>> statementDetectors,
            Set<? extends AbstractDetector<AssignmentExpression>> assignmentDetectors,
            Set<? extends AbstractDetector<Break>> breakDetectors,
            Set<? extends AbstractDetector<Continue>> continueDetectors,
            Set<? extends AbstractDetector<For>> forDetectors,
            Set<? extends AbstractDetector<FuncNode>> funcNodeDetectors,
            Set<? extends AbstractDetector<If>> ifDetectors,
            Set<? extends AbstractDetector<Return>> returnDetectors,
            Set<? extends AbstractDetector<Switch>> switchDetectors,
            Set<? extends AbstractDetector<VariableDec>> variableDeclarationDetectors,
            Set<? extends AbstractDetector<While>> whileDetectors
            		) {
        this.eventAttacherDetectors = immutableCopyOf(eventAttacherDetectors);
        this.timerEventDetectors = immutableCopyOf(timerEventDetectors);
        this.domCreationDetectors = immutableCopyOf(domCreationDetectors);
        this.attributeModificationDetectors = immutableCopyOf(attributeModificationDetectors);
        this.domAppendingDetectors = immutableCopyOf(domAppendingDetectors);
        this.domCloningDetectors = immutableCopyOf(domCloningDetectors);
        this.domNormalizationDetectors = immutableCopyOf(domNormalizationDetectors);
        this.domReplacementDetectors = immutableCopyOf(domReplacementDetectors);
        this.domRemovalDetectors = immutableCopyOf(domRemovalDetectors);
        this.domSelectionDetectors = immutableCopyOf(domSelectionDetectors);
        this.requestDetectors = immutableCopyOf(requestDetectors);
        this.statementDetectors = immutableCopyOf(statementDetectors);
        this.assignmentDetectors = immutableCopyOf(assignmentDetectors);
        this.breakDetectors = immutableCopyOf(breakDetectors);
        this.continueDetectors = immutableCopyOf(continueDetectors);
        this.forDetectors = immutableCopyOf(forDetectors);
        this.funcNodeDetectors = immutableCopyOf(funcNodeDetectors);
        this.ifDetectors = immutableCopyOf(ifDetectors);
        this.returnDetectors = immutableCopyOf(returnDetectors);
        this.switchDetectors = immutableCopyOf(switchDetectors);
        this.variableDeclarationDetectors = immutableCopyOf(variableDeclarationDetectors);
        this.whileDetectors = immutableCopyOf(whileDetectors);
        		
    }

    private <T> ImmutableSet<T> immutableCopyOf(Set<T> original) {
        if (original == null)
            return ImmutableSet.of();
        else
            return ImmutableSet.copyOf(original);
    }

    private <T extends Mutatable> void detectAndAdd(
            MutationPointDetector<T> detector, AstNode node, Set<T> mutatable) {
        T result = detector.detect(node);
        if (result != null)
            mutatable.add(result);
    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof FunctionCall) {
            return visit((FunctionCall) node);
        } else if (node instanceof Assignment) {
            for (AbstractDetector<AttributeModification> detector : attributeModificationDetectors)
                detectAndAdd(detector, node, attributeModifications);
            for (AbstractDetector<AssignmentExpression> detector : assignmentDetectors)
            	detectAndAdd(detector, node, assignmentExpressions);
        } else if (node instanceof Loop){
        	for (AbstractDetector<Statement> detector : statementDetectors)
        		detectAndAdd(detector, node, statements);
        } else if(node instanceof BreakStatement){
        	for (AbstractDetector<Break> detector : breakDetectors)
            	detectAndAdd(detector, node, breaks);
        } else if(node instanceof ContinueStatement){
        	for (AbstractDetector<Continue> detector : continueDetectors)
            	detectAndAdd(detector, node, continues);
        } else if(node instanceof ForLoop){
        	for (AbstractDetector<For> detector : forDetectors)
            	detectAndAdd(detector, node, fors);
        } else if(node instanceof FunctionNode){
            for (AbstractDetector<FuncNode> detector : funcNodeDetectors)
            	detectAndAdd(detector, node, funcNodes);
        } else if (node instanceof IfStatement){
        	for (AbstractDetector<Statement> detector : statementDetectors)
        		detectAndAdd(detector, node, statements);
        	for (AbstractDetector<If> detector : ifDetectors)
            	detectAndAdd(detector, node, ifs);
        } else if (node instanceof ReturnStatement){
        	for (AbstractDetector<Statement> detector : statementDetectors)
        		detectAndAdd(detector, node, statements);
        	for (AbstractDetector<Return> detector : returnDetectors)
            	detectAndAdd(detector, node, returns);
        } else if (node instanceof SwitchStatement){
        	for (AbstractDetector<Switch> detector : switchDetectors)
            	detectAndAdd(detector, node, switches);
        } else if(node instanceof VariableDeclaration){
        	for (AbstractDetector<VariableDec> detector : variableDeclarationDetectors)
            	detectAndAdd(detector, node, variableDecs);
        } else if(node instanceof WhileLoop){
        	for (AbstractDetector<While> detector : whileDetectors)
            	detectAndAdd(detector, node, whiles);
        } 
       
        return true;
    }

    public boolean visit(FunctionCall call) {
        for (EventAttacherDetector detector : eventAttacherDetectors)
            detectAndAdd(detector, call, eventAttachments);
        for (TimerEventDetector detector : timerEventDetectors)
            detectAndAdd(detector, call, timerEventAttachmentExpressions);
        for (AbstractDetector<DOMCreation> detector : domCreationDetectors)
            detectAndAdd(detector, call, domCreations);
        for (AbstractDetector<DOMAppending> detector: domAppendingDetectors)
            detectAndAdd(detector, call, domAppendings);
        for (AbstractDetector<DOMCloning> detector: domCloningDetectors)
            detectAndAdd(detector, call, domClonings);
        for (AbstractDetector<DOMNormalization> detector: domNormalizationDetectors)
            detectAndAdd(detector, call, domNormalizations);
        for (AbstractDetector<DOMReplacement> detector: domReplacementDetectors)
            detectAndAdd(detector, call, domReplacements);
        for (AbstractDetector<DOMRemoval> detector : domRemovalDetectors)
            detectAndAdd(detector, call, domRemovals);
        for (AbstractDetector<DOMSelection> detector : domSelectionDetectors)
            detectAndAdd(detector, call, domSelections);
        for (AbstractDetector<Request> detector : requestDetectors)
            detectAndAdd(detector, call, requests);
        for (AbstractDetector<AttributeModification> detector : attributeModificationDetectors)
            detectAndAdd(detector, call, attributeModifications);
        for (AbstractDetector<Statement> detector : statementDetectors)
        	detectAndAdd(detector, call, statements);
        for (AbstractDetector<Break> detector : breakDetectors)
        	detectAndAdd(detector, call, breaks);
        for (AbstractDetector<Continue> detector : continueDetectors)
        	detectAndAdd(detector, call, continues);
        for (AbstractDetector<For> detector : forDetectors)
        	detectAndAdd(detector, call, fors);
        for (AbstractDetector<If> detector : ifDetectors)
        	detectAndAdd(detector, call, ifs);
        for (AbstractDetector<Return> detector : returnDetectors)
        	detectAndAdd(detector, call, returns);
        for (AbstractDetector<Switch> detector : switchDetectors)
        	detectAndAdd(detector, call, switches);
        for (AbstractDetector<VariableDec> detector : variableDeclarationDetectors)
        	detectAndAdd(detector, call, variableDecs);
        for (AbstractDetector<While> detector : whileDetectors)
        	detectAndAdd(detector, call, whiles);
        
        return true;
    }

    public Set<EventAttachment> getEventAttachments() {
        return eventAttachments;
    }

    public Set<TimerEventAttachment> getTimerEventAttachmentExpressions() {
        return timerEventAttachmentExpressions;
    }

    public Set<DOMCreation> getDomCreations() {
        return domCreations;
    }

    public Set<AttributeModification> getAttributeModifications() {
        return attributeModifications;
    }

    public Set<DOMAppending> getDomAppendings() {
        return domAppendings;
    }

    public Set<DOMCloning> getDomClonings() {
        return domClonings;
    }

    public Set<DOMNormalization> getDomNormalizations() {
        return domNormalizations;
    }

    public Set<DOMReplacement> getDomReplacements() {
        return domReplacements;
    }

    public Set<DOMRemoval> getDomRemovals() {
        return domRemovals;
    }

    public Set<DOMSelection> getDomSelections() {
        return domSelections;
    }

    public Set<Request> getRequests() {
        return requests;
    }
    
    public Set<Statement> getStatements() {
    	return statements;
    }
    
    public Set<AssignmentExpression> getAssignmentExpressions() {
    	return assignmentExpressions;
    }
    
    public Set<Break> getBreaks() {
    	return breaks;
    }
    
    public Set<Continue> getContinues() {
    	return continues;
    }
    
    public Set<For> getFors() {
    	return fors;
    }
    
    public Set<FuncNode> getFuncnodes() {
    	return funcNodes;
    }
    
    public Set<If> getIfs() {
    	return ifs;
    }
    public Set<Return> getReturns() {
    	return returns;
    }
    
    public Set<Switch> getSwitches() {
    	return switches;
    }
    
    public Set<VariableDec> getVariableDecss() {
    	return variableDecs;
    }
    
    public Set<While> getWhiles() {
    	return whiles;
    }
    

    /**
     * @return Information about mutatables found by visiting AST.
     */
    public String getMutatablesInfo() {
        return getMutatablesInfo(true);
    }

    /**
     * @param detailed if true output detailed information about each mutant,
     *                 otherwise output value only contain number of each mutants
     * @return Information about mutatables found by visiting AST.
     */
    public String getMutatablesInfo(boolean detailed) {
        StringBuilder builder = new StringBuilder();
        builder.append("=== Event ===").append(System.lineSeparator());
        appendMutatablesInfo(
                "Event attachments", builder, eventAttachments, detailed);
        appendMutatablesInfo("Timer event attachment", builder,
                timerEventAttachmentExpressions, detailed);
        builder.append("=== DOM ===").append(System.lineSeparator());
        appendMutatablesInfo("DOM creation", builder, domCreations, detailed);
        appendMutatablesInfo("Attribute modification", builder,
                attributeModifications, detailed);
        appendMutatablesInfo("DOM removal", builder, domRemovals, detailed);
        appendMutatablesInfo("DOM Selection", builder, domSelections, detailed);
        builder.append("=== Asynchrous communications ===")
                .append(System.lineSeparator());
        appendMutatablesInfo("Requests", builder, requests, detailed);

        builder.append("=== Statement ===").append(System.lineSeparator());
        appendMutatablesInfo("Statements", builder, statements, detailed);
        
        builder.append("=== General ===").append(System.lineSeparator());
        appendMutatablesInfo("AssignmentExpressions", builder, assignmentExpressions, detailed);
        appendMutatablesInfo("Breaks", builder, breaks, detailed);
        appendMutatablesInfo("Continues", builder, continues, detailed);
        appendMutatablesInfo("Fors", builder, fors, detailed);
        appendMutatablesInfo("FuncNodes", builder, funcNodes, detailed);
        appendMutatablesInfo("Ifs", builder, ifs, detailed);
        appendMutatablesInfo("Returns", builder, returns, detailed);
        appendMutatablesInfo("Switches", builder, switches, detailed);
        appendMutatablesInfo("VariableDecs", builder, variableDecs, detailed);
        appendMutatablesInfo("Whiles", builder, whiles, detailed);
        
        return builder.toString();
    }

    private <T> void appendMutatablesInfo(
            String title, StringBuilder builder, Set<T> set, boolean detailed) {
        builder.append("  --- ").append(title);
        builder.append(" (").append(set.size()).append(") ---")
                .append(System.lineSeparator());
        if (!detailed) {
            return;
        }
        for (T element : set) {
            String str = element.toString();
            String spaceBeforeContent = "    ";
            builder.append(spaceBeforeContent)
                    .append(str.replaceAll("\n", "\n" + spaceBeforeContent))
                    .append(System.lineSeparator());
        }
    }

    /**
     * @return Builder instance with no configuration.
     */
    public static MutateVisitorBuilder emptyBuilder() {
        return new MutateVisitorBuilder();
    }

    /**
     * @return Builder instance with typical detector configurations.
     */
    public static MutateVisitorBuilder defaultBuilder() {
        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setAttributeModificationDetectors(
                ImmutableSet.of(new AttributeAssignmentDetector(), new SetAttributeDetector()));
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new AppendChildDetector()));
        builder.setDomCreationDetectors(
                ImmutableSet.of(new CreateElementDetector()));
        builder.setDomCloningDetectors(ImmutableSet.of(new CloneNodeDetector()));
        builder.setDomNormalizationDetectors(ImmutableSet.of(new DOMNormalizationDetector()));
        builder.setDomReplacementDetectors(ImmutableSet.of(new ReplaceChildDetector()));
        builder.setDomRemovalDetectors(
                ImmutableSet.of(new RemoveChildDetector()));
        builder.setDomSelectionDetectors(
                ImmutableSet.of(new DOMSelectionDetector()));
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(
                        new AddEventListenerDetector(), new AttachEventDetector()));
        builder.setTimerEventDetectors(
                ImmutableSet.of(new TimerEventDetector()));
        return builder;
    }

    public static MutateVisitorBuilder defaultJqueryBuilder() {
        MutateVisitorBuilder builder = new MutateVisitorBuilder();
        builder.setAttributeModificationDetectors(
                ImmutableSet.of(
                        new AttributeAssignmentDetector(), new SetAttributeDetector(),
                        new JQueryAttributeModificationDetector()));
        builder.setDomAppendingDetectors(
                ImmutableSet.of(new JQueryAppendDetector()));
        builder.setDomCreationDetectors(
                ImmutableSet.of(new CreateElementDetector()));
        builder.setDomCloningDetectors(ImmutableSet.of(new JQueryCloneDetector()));
        builder.setDomNormalizationDetectors(ImmutableSet.of(new DOMNormalizationDetector()));
        builder.setDomReplacementDetectors(ImmutableSet.of(new JQueryReplaceWithDetector()));
        builder.setDomRemovalDetectors(
                ImmutableSet.of(new RemoveChildDetector(), new JQueryRemoveDetector()));
        builder.setDomSelectionDetectors(
                ImmutableSet.of(new JQueryDOMSelectionDetector()));
        builder.setEventAttacherDetectors(
                ImmutableSet.<EventAttacherDetector>of(
                        new JQueryEventAttachmentDetector()));
        builder.setTimerEventDetectors(
                ImmutableSet.of(new TimerEventDetector()));
        builder.setRequestDetectors(
                ImmutableSet.of( new JQueryRequestDetector()));
        return builder;
    }
}