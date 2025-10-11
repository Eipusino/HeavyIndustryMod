package heavyindustry.annotations;

import arc.func.Prov;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope.WriteableScope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Pair;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.ExceptionProxy;

import javax.lang.model.element.ElementKind;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Reliably generates a proxy handling an annotation type from model elements.
 *
 * @author Anuke
 */
public record AnnoProxyMaker(Attribute.Compound anno, Class<? extends Annotation> type) {
	public static <A extends Annotation> A generate(Attribute.Compound anno, Class<A> type) {
		if (anno == null) return null;
		return type.cast(new AnnoProxyMaker(anno, type).generate());
	}

	private Annotation generate() {
		return AnnotationParser.annotationForMap(type, getAllReflectedValues());
	}

	private Map<String, Object> getAllReflectedValues() {
		Map<String, Object> res = new LinkedHashMap<>();
		for (Entry<MethodSymbol, Attribute> entry : getAllValues().entrySet()) {
			MethodSymbol meth = entry.getKey();
			Object value = generateValue(meth, entry.getValue());
			if (value != null) {
				res.put(meth.name.toString(), value);
			}
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	private Map<MethodSymbol, Attribute> getAllValues() {
		Map<MethodSymbol, Attribute> map = new LinkedHashMap<>();
		ClassSymbol cl = (ClassSymbol) anno.type.tsym;

		try {
			Class<?> entryClass = Class.forName("com.sun.tools.javac.code.Scope$Entry");
			Field siblingField = entryClass.getField("sibling");
			Field symField = entryClass.getField("sym");

			WriteableScope members = cl.members();
			Field field = members.getClass().getField("elems");
			Object elems = field.get(members);

			for (Object currEntry = elems; currEntry != null; currEntry = siblingField.get(currEntry)) {
				handleSymbol((Symbol) symField.get(currEntry), map);
			}
		} catch (Throwable e) {
			try {
				Class<?> lookupClass = Class.forName("com.sun.tools.javac.code.Scope$LookupKind");
				Field nonRecField = lookupClass.getField("NON_RECURSIVE");
				Object nonRec = nonRecField.get(null);

				WriteableScope scope = cl.members();
				Method getSyms = scope.getClass().getMethod("getSymbols", lookupClass);
				Iterable<Symbol> it = (Iterable<Symbol>) getSyms.invoke(scope, nonRec);
				for (Symbol symbol : it) handleSymbol(symbol, map);
			} catch (Throwable death) {
				throw new RuntimeException(death);
			}
		}

		for (Pair<MethodSymbol, Attribute> pair : anno.values) map.put(pair.fst, pair.snd);
		return map;
	}

	@SuppressWarnings("unchecked")
	private <T extends Symbol> void handleSymbol(Symbol sym, Map<T, Attribute> map) {
		if (sym.getKind() == ElementKind.METHOD) {
			Attribute def = ((MethodSymbol) sym).getDefaultValue();
			if (def != null) map.put((T) sym, def);
		}
	}

	private Object generateValue(MethodSymbol meth, Attribute attrib) {
		return new ValueVisitor(meth).getValue(attrib);
	}

	private class ValueVisitor implements Attribute.Visitor {
		private final MethodSymbol meth;
		private Class<?> returnClass;
		private Object value;

		ValueVisitor(MethodSymbol meth) {
			this.meth = meth;
		}

		Object getValue(Attribute attrib) {
			Method meth;
			try {
				meth = type.getMethod(this.meth.name.toString());
			} catch (NoSuchMethodException e) {
				return null;
			}

			returnClass = meth.getReturnType();
			attrib.accept(this);

			if (!(value instanceof ExceptionProxy) && !AnnotationType.invocationHandlerReturnType(returnClass).isInstance(value))
				typeMismatch(meth, attrib);
			return value;
		}

		@Override
		public void visitConstant(Attribute.Constant constant) {
			value = constant.getValue();
		}

		@Override
		public void visitClass(Attribute.Class type) {
			value = mirrorProxy(type.classType);
		}

		@Override
		public void visitArray(Attribute.Array arr) {
			var name = ((Type.ArrayType) arr.type).elemtype.tsym.getQualifiedName();
			if (name.equals(name.table.names.java_lang_Class)) {
				ListBuffer<Type> list = new ListBuffer<>();
				for (var attrib : arr.values) {
					var type = attrib instanceof Attribute.UnresolvedClass u
							? u.classType
							: ((Attribute.Class) attrib).classType;

					list.append(type);
				}

				value = mirrorProxy(list.toList());
			} else {
				Class<?> arrType = returnClass;
				returnClass = returnClass.getComponentType();

				try {
					Object inst = Array.newInstance(returnClass, arr.values.length);
					for (int i = 0; i < arr.values.length; i++) {
						arr.values[i].accept(this);
						if (value == null || value instanceof ExceptionProxy) return;

						try {
							Array.set(inst, i, value);
						} catch (IllegalArgumentException e) {
							value = null;
							return;
						}
					}

					value = inst;
				} finally {
					returnClass = arrType;
				}
			}
		}

		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public void visitEnum(Attribute.Enum enumType) {
			if (returnClass.isEnum()) {
				String name = enumType.value.toString();
				try {
					value = Enum.valueOf((Class) returnClass, name);
				} catch (IllegalArgumentException e) {
					value = proxify(() -> new EnumConstantNotPresentException((Class) returnClass, name));
				}
			} else {
				value = null;
			}
		}

		@Override
		public void visitCompound(Attribute.Compound anno) {
			try {
				Class<? extends Annotation> type = returnClass.asSubclass(Annotation.class);
				value = generate(anno, type);
			} catch (ClassCastException e) {
				value = null;
			}
		}

		@Override
		public void visitError(Attribute.Error err) {
			if (err instanceof Attribute.UnresolvedClass u) {
				value = mirrorProxy(u.classType);
			} else {
				value = null;
			}
		}

		private void typeMismatch(Method meth, Attribute attrib) {
			value = proxify(() -> new AnnotationTypeMismatchException(meth, attrib.type.toString()));
		}
	}

	private static Object mirrorProxy(Type t) {
		return proxify(() -> new MirroredTypeException(t));
	}

	private static Object mirrorProxy(List<Type> t) {
		return proxify(() -> new MirroredTypesException(t));
	}

	private static <T extends RuntimeException> Object proxify(Prov<T> prov) {
		try {
			return new ExceptionProxy() {
				@Serial
				private static final long serialVersionUID = 1L;

				@Override
				protected RuntimeException generateException() {
					return prov.get();
				}
			};
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
