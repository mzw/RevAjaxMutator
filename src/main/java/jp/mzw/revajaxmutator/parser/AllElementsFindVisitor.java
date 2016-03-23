package jp.mzw.revajaxmutator.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class AllElementsFindVisitor extends ASTVisitor {

	private List<ASTNode> nodes;

	public AllElementsFindVisitor() {
		nodes = new ArrayList<>();
	}

	public List<ASTNode> getNodes() {
		return nodes;
	}
	
	private void _visit(ASTNode node) {
		nodes.add(node);
	}
	
	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ArrayAccess node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ArrayType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(AssertStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Assignment node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Block node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(BlockComment node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(BreakStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(CastExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(CatchClause node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(CompilationUnit node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		_visit(node);
		return true;
	}
	
	@Override
	public boolean visit(ConstructorInvocation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(CreationReference node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Dimension node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(DoStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(InfixExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Initializer node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(IntersectionType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Javadoc node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(LineComment node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MemberRef node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MemberValuePair node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MethodRef node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(Modifier node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(NameQualifiedType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(NullLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ParameterizedType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(PrimitiveType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(QualifiedName node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(QualifiedType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SimpleName node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SimpleType node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(StringLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SwitchCase node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TagElement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TextElement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ThisExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TryStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(TypeParameter node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(UnionType node) {
		_visit(node);
		return true;
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		_visit(node);
		return true;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		_visit(node);
		return true;
	}

	@Override
	public boolean visit(WildcardType node) {
		_visit(node);
		return true;
	}
	
}
