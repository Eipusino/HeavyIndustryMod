package heavyindustry.annotations;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.OS;
import arc.util.Strings;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.AnnoConstruct;
import com.sun.tools.javac.code.Attribute.Compound;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacFiler;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import heavyindustry.annotations.util.AnnotationProxyMaker;
import mindustry.Vars;
import sun.misc.Unsafe;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseProcessor extends AbstractProcessor {
	public static final String packageName = "heavyindustry.gen";

	private static final Unsafe unsafe;

	private static final long fieldFilterOffset = 112l;
	private static final Field opensField;
	private static final Field exportField;
	private static final Method exportNative;
	private static final String[] opensModule = {
			"com.sun.tools.javac.api",
			"com.sun.tools.javac.code",
			"com.sun.tools.javac.parser",
			"com.sun.tools.javac.processing",
			"com.sun.tools.javac.tree",
			"com.sun.tools.javac.util",
			"com.sun.tools.javac.comp"
	};
	public static Fi rootDir;

	static {
		Vars.loadLogger();

		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);

			ensureFieldOpen();

			opensField = Module.class.getDeclaredField("openPackages");
			exportField = Module.class.getDeclaredField("exportedPackages");

			makeModuleOpen(Module.class.getModule(), "java.lang", BaseProcessor.class.getModule());

			exportNative = Module.class.getDeclaredMethod("addExportsToAll0", Module.class, String.class);
			exportNative.setAccessible(true);
			exportNative.invoke(null, Module.class.getModule(), "java.lang");

			for (String pack : opensModule) {
				makeModuleOpen(Tree.class.getModule(), pack, BaseProcessor.class.getModule());
			}
		} catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
		         IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public JavacElements elements;
	public JavacTrees trees;
	public JavacTypes types;
	public JavacFiler filer;

	protected int round;
	protected int rounds = 1;

	@SuppressWarnings("unchecked")
	public static void ensureFieldOpen() throws ClassNotFoundException {
		Class<?> clazz = Class.forName("jdk.internal.reflect.Reflection");
		Map<Class<?>, Set<String>> map = (Map<Class<?>, Set<String>>) unsafe.getObject(clazz, fieldFilterOffset);
		map.clear();
	}

	@SuppressWarnings("unchecked")
	private static void makeModuleOpen(Module from, String pac, Module to) {
		try {
			if (exportNative != null) exportNative.invoke(null, from, pac);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		Map<String, Set<Module>> opensMap = (Map<String, Set<Module>>) unsafe.getObjectVolatile(from, unsafe.objectFieldOffset(opensField));
		if (opensMap == null) {
			opensMap = new HashMap<>();
			unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(opensField), opensMap);
		}

		Map<String, Set<Module>> exportsMap = (Map<String, Set<Module>>) unsafe.getObjectVolatile(from, unsafe.objectFieldOffset(exportField));
		if (exportsMap == null) {
			exportsMap = new HashMap<>();
			unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(exportField), exportsMap);
		}

		Set<Module> opens = opensMap.computeIfAbsent(pac, e -> new HashSet<>());
		Set<Module> exports = exportsMap.computeIfAbsent(pac, e -> new HashSet<>());

		try {
			opens.add(to);
		} catch (UnsupportedOperationException e) {
			ArrayList<Module> lis = new ArrayList<>(opens);
			lis.add(to);
			opensMap.put(pac, new HashSet<>(lis));
		}

		try {
			exports.add(to);
		} catch (UnsupportedOperationException e) {
			ArrayList<Module> lis = new ArrayList<>(exports);
			lis.add(to);
			exportsMap.put(pac, new HashSet<>(lis));
		}
	}

	public static TypeName typeName(Class<?> type) {
		return ClassName.get(type).box();
	}

	public static TypeName typeName(Element e) {
		return e == null ? TypeName.VOID : TypeName.get(e.asType());
	}

	public static ClassName className(Class<?> type) {
		return ClassName.get(type);
	}

	public static ClassName className(String canonical) {
		canonical = canonical.replace("<any?>", "lonetrail.gen");

		Matcher matcher = Pattern.compile("\\.[A-Z]").matcher(canonical);
		boolean find = matcher.find();
		int offset = find ? matcher.start() : 0;

		String pkgName = canonical.substring(0, offset);
		Seq<String> simpleNames = Seq.with(canonical.substring(offset + 1).split("\\."));
		simpleNames.reverse();
		String simpleName = simpleNames.pop();
		simpleNames.reverse();

		return ClassName.get(pkgName.isEmpty() ? packageName : pkgName, simpleName, simpleNames.toArray());
	}

	public static ClassName className(Element e) {
		return className(stripTypeVar(e.asType().toString()));
	}

	public static TypeVariableName typeVarName(String name, TypeName... bounds) {
		return TypeVariableName.get(name, bounds);
	}

	public static String stripTypeVar(String canonical) {
		return canonical.replaceAll("<[A-Z]+>", "");
	}

	public static String lnew() {
		return Character.toString('\n');
	}

	public static boolean isConstructor(ExecutableElement e) {
		return
				simpleName(e).equals("<init>") ||
						simpleName(e).equals("<clinit>");
	}

	public static String getDefault(String value) {
		return switch (value) {
			case "float", "double", "int", "long", "short", "char", "byte" -> "0";
			case "boolean" -> "false";
			default -> "null";
		};
	}

	public static boolean isPrimitive(String type) {
		return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
				|| type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
	}

	public static <A extends Annotation> A annotation(Element e, Class<A> annotation) {
		try {
			Method m = AnnoConstruct.class.getDeclaredMethod("getAttribute", Class.class);
			m.setAccessible(true);
			Compound compound = (Compound) m.invoke(e, annotation);
			return compound == null ? null : AnnotationProxyMaker.generateAnnotation(compound, annotation);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String fullName(Element e) {
		return e.asType().toString();
	}

	public static String simpleName(Element e) {
		return simpleName(e.getSimpleName().toString());
	}

	public static String simpleName(String canonical) {
		if (canonical.contains(".")) {
			canonical = canonical.substring(canonical.lastIndexOf(".") + 1);
		}
		return canonical;
	}

	public static String simpleString(ExecutableElement e) {
		return simpleName(e) + "(" + Seq.with(e.getParameters()).toString(", ", p -> simpleName(p.asType().toString())) + ")";
	}

	public static String procBlock(String methodBlock) {
		StringBuilder builder = new StringBuilder();
		String[] lines = methodBlock.split("\n");

		for (String line : lines) {
			if (line.startsWith("    ")) line = line.substring(4);

			line = line
					.replaceAll("this\\.<(.*)>self\\(\\)", "this")
					.replaceAll("self\\(\\)(?!\\s+instanceof)", "this")
					.replaceAll(" yield ", "")
					.replaceAll("/\\*missing\\*/", "var");

			builder.append(line).append('\n');
		}

		String result = builder.toString();
		return result.substring(result.indexOf("{") + 1, result.lastIndexOf("}")).trim() + "\n";
	}

	public static TypeMirror compOf(TypeMirror type) {
		while (type instanceof ArrayType) {
			type = ((ArrayType) type).getComponentType();
		}

		return type;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) processingEnv;

		elements = javacProcessingEnv.getElementUtils();
		trees = JavacTrees.instance(javacProcessingEnv);
		types = javacProcessingEnv.getTypeUtils();
		filer = javacProcessingEnv.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (round++ >= rounds) return false;
		if (rootDir == null) {
			try {
				String path = Fi.get(filer.getResource(StandardLocation.CLASS_OUTPUT, "no", "no")
								.toUri().toURL().toString().substring(OS.isWindows ? 6 : "file:".length()))
						.parent().parent().parent().parent().parent().parent().toString().replace("%20", " ");

				rootDir = Fi.get(path);
			} catch (IOException e) {
				Throwable finalCause = Strings.getFinalCause(e);

				Log.err(finalCause);
				throw new RuntimeException(finalCause);
			}
		}

		try {
			process(roundEnv);
		} catch (Exception e) {
			Throwable finalCause = Strings.getFinalCause(e);

			Log.err(finalCause);
			throw new RuntimeException(finalCause);
		}

		return true;
	}

	public abstract void process(RoundEnvironment roundEnv) throws Exception;

	public void write(TypeSpec spec) throws Exception {
		write(spec, null);
	}

	public void write(TypeSpec spec, Seq<String> imports) throws Exception {
		try {
			JavaFile file = JavaFile.builder(packageName, spec)
					.indent("    ")
					.skipJavaLangImports(true)
					.build();

			if (imports == null || imports.isEmpty()) {
				file.writeTo(filer);
			} else {
				imports.distinct();

				Seq<String> statics = imports.select(i -> i.contains("import static ")).sort();
				imports = imports.select(i -> !statics.contains(s -> s.equals(i))).sort();
				if (!statics.isEmpty()) {
					imports = statics.addAll("\n").add(imports);
				}

				String rawSource = file.toString();
				Seq<String> source = Seq.with(rawSource.split("\n", -1));
				Seq<String> result = new Seq<>();
				for (int i = 0; i < source.size; i++) {
					String s = source.get(i);

					result.add(s);
					if (s.startsWith("package ")) {
						source.remove(i + 1);
						result.add("");
						for (String im : imports) {
							result.add(im.replace("\n", ""));
						}
					}
				}

				String out = result.toString("\n");
				JavaFileObject object = filer.createSourceFile(file.packageName + "." + file.typeSpec.name, file.typeSpec.originatingElements.toArray(new Element[0]));
				OutputStream stream = object.openOutputStream();
				stream.write(out.getBytes());
				stream.close();
			}
		} catch (FilerException e) {
			throw new Exception("Misbehaving files prevent annotation processing from being done. Try running `gradlew clean`");
		}
	}

	public TypeElement toElement(TypeMirror t) {
		return (TypeElement) types.asElement(t);
	}

	public Seq<TypeElement> elements(Runnable run) {
		try {
			run.run();
		} catch (MirroredTypesException ex) {
			return Seq.with(ex.getTypeMirrors()).map(this::toElement);
		}

		return Seq.with();
	}

	public Seq<VariableElement> vars(TypeElement t) {
		return Seq.with(t.getEnclosedElements()).select(e -> e instanceof VariableElement).map(e -> (VariableElement) e);
	}

	public Seq<ExecutableElement> methods(TypeElement t) {
		return Seq.with(t.getEnclosedElements()).select(e -> e instanceof ExecutableElement).map(e -> (ExecutableElement) e);
	}

	public Seq<TypeElement> types(TypeElement t) {
		return Seq.with(t.getEnclosedElements()).select(e -> e instanceof TypeElement).map(e -> (TypeElement) e);
	}

	public String descString(VariableElement v) {
		return v.getEnclosingElement().toString() + "#" + v.getSimpleName().toString();
	}

	public String descString(ExecutableElement m) {
		String params = Seq.with(m.getParameters()).toString(", ", e -> e.getEnclosingElement().asType() + " " + e.getSimpleName());

		return m.getEnclosingElement().toString() + "#" + simpleName(m) + "(" + params + ")";
	}

	public boolean is(Element e, Modifier... modifiers) {
		for (Modifier m : modifiers) {
			if (e.getModifiers().contains(m)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMethod(TypeElement type, ExecutableElement method) {
		for (; !(type.getSuperclass() instanceof NoType); type = toElement(type.getSuperclass())) {
			if (method(type, simpleName(method), method.getReturnType(), method.getParameters()) != null) {
				return true;
			}
		}
		return false;
	}

	public ExecutableElement method(TypeElement type, String name, TypeMirror retType, List<? extends VariableElement> params) {
		return methods(type).find(m -> {
			List<? extends VariableElement> realParams = m.getParameters();

			return
					simpleName(m).equals(name) &&
							(retType == null || types.isSameType(m.getReturnType(), retType)) &&
							paramEquals(realParams, params);
		});
	}

	public boolean paramEquals(List<? extends VariableElement> first, List<? extends VariableElement> second) {
		if (first.size() != second.size()) return false;

		boolean same = true;
		for (int i = 0; same && i < first.size(); i++) {
			VariableElement a = first.get(i);
			VariableElement b = second.get(i);

			if (!types.isSameType(a.asType(), b.asType())) same = false;
		}

		return same;
	}

	public Seq<String> getImports(Element e) {
		return Seq.with(trees.getPath(e).getCompilationUnit().getImports()).map(Object::toString);
	}

	public boolean instanceOf(String type, String other) {
		TypeElement a = elements.getTypeElement(type);
		TypeElement b = elements.getTypeElement(other);
		return a != null && b != null && types.isSubtype(a.asType(), b.asType());
	}

	public boolean isNumeric(TypeMirror type) {
		try {
			return switch (types.unboxedType(type).getKind()) {
				case BYTE, SHORT, INT, FLOAT, LONG, DOUBLE -> true;
				default -> false;
			};
		} catch (IllegalArgumentException t) {
			return false;
		}
	}

	public boolean isNumeric(String type) {
		return type.equals("byte") || type.equals("short") || type.equals("int") || type.equals("float")
				|| type.equals("long") || type.equals("double") || type.equals("Byte") || type.equals("Short")
				|| type.equals("Integer") || type.equals("Float") || type.equals("Long") || type.equals("Double");
	}

	public boolean isBool(TypeMirror type) {
		try {
			return types.unboxedType(type).getKind() == TypeKind.BOOLEAN;
		} catch (IllegalArgumentException t) {
			return false;
		}
	}

	public boolean isBool(String type) {
		return type.equals("boolean") || type.equals("Boolean");
	}

	public TypeElement toType(Class<?> type) {
		return elements.getTypeElement(type.getCanonicalName());
	}

	public boolean isVoid(TypeElement e) {
		return types.isSameType(e.asType(), toType(Void.class).asType());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_16;
	}
}