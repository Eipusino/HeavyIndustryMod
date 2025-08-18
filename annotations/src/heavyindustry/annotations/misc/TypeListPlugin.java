package heavyindustry.annotations.misc;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import heavyindustry.annotations.Annotations.ListClasses;
import heavyindustry.annotations.Annotations.ListPackages;

/**
 * Gathers all declared non-anonymous classes and packages and appends them to fields with {@code Seq.<String>with()}
 * initializer and annotated with {@link ListClasses} or {@link ListPackages}
 */
public class TypeListPlugin implements Plugin {
	Seq<JCMethodInvocation> classes = new Seq<>(), packages = new Seq<>();
	Seq<String> classDefs = new Seq<>(), packageDefs = new Seq<>();
	ObjectMap<JCMethodInvocation, List<JCExpression>> classArgs = new ObjectMap<>(), packArgs = new ObjectMap<>();

	@Override
	public void init(JavacTask task, String... args) {
		TreeMaker maker = TreeMaker.instance(((JavacTaskImpl) task).getContext());
		task.addTaskListener(new TaskListener() {
			@Override
			public void finished(TaskEvent event) {
				if (event.getKind() == Kind.PARSE) {
					event.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
						@Override
						public Void visitVariable(VariableTree node, Void unused) {
							ExpressionTree init = node.getInitializer();
							if (init != null && init.toString().startsWith("Seq.with(")) {
								if (node.getModifiers().getAnnotations().stream().anyMatch(a -> a.getAnnotationType().toString().equals(ListClasses.class.getSimpleName()))) {
									classes.add((JCMethodInvocation) init);
								} else if (node.getModifiers().getAnnotations().stream().anyMatch(a -> a.getAnnotationType().toString().equals(ListPackages.class.getSimpleName()))) {
									packages.add((JCMethodInvocation) init);
								}
							}

							return super.visitVariable(node, unused);
						}
					}, null);
				} else if (event.getKind() == Kind.ENTER) {
					event.getCompilationUnit().accept(new TreeScanner<Void, Void>() {
						@Override
						public Void visitClass(ClassTree node, Void unused) {
							ClassSymbol sym = ((JCClassDecl) node).sym;
							if (
									sym != null &&
											!sym.isAnonymous() &&
											!(
													sym.getQualifiedName().toString().startsWith("heavyindustry.entities.comp") ||
															sym.getQualifiedName().toString().startsWith("heavyindustry.entities.merge") ||
															sym.getQualifiedName().toString().startsWith("heavyindustry.fetched")
											)
							) {
								StringBuilder builder = new StringBuilder(sym.getSimpleName().toString());

								Symbol current = sym;
								while (!(current instanceof PackageSymbol)) {
									current = current.getEnclosingElement();

									if (current instanceof PackageSymbol) {
										builder.insert(0, current.getQualifiedName().toString() + ".");
									} else {
										builder.insert(0, current.getSimpleName().toString() + "$");
									}
								}

								String cname = builder.toString();
								if (!classDefs.contains(cname)) {
									classDefs.add(cname);
								}

								String pname = current.getQualifiedName().toString();
								if (!packageDefs.contains(pname)) {
									packageDefs.add(pname);
								}
							}

							return super.visitClass(node, unused);
						}
					}, null);
				}
			}

			@Override
			public void started(TaskEvent event) {
				if (event.getKind() == Kind.ANALYZE) {
					classes.each(e -> e.args = classArgs.get(e, () -> List.from(Seq.with(e.args).addAll(classDefs.map(maker::Literal)))));
					packages.each(e -> e.args = packArgs.get(e, () -> List.from(Seq.with(e.args).addAll(packageDefs.map(maker::Literal)))));
				}
			}
		});
	}

	@Override
	public boolean autoStart() {
		return true;
	}

	@Override
	public String getName() {
		return "typelist";
	}
}