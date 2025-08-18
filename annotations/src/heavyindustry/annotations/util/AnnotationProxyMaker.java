package heavyindustry.annotations.util;

import arc.func.Prov;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Attribute.Array;
import com.sun.tools.javac.code.Attribute.Compound;
import com.sun.tools.javac.code.Attribute.Constant;
import com.sun.tools.javac.code.Attribute.Enum;
import com.sun.tools.javac.code.Attribute.Error;
import com.sun.tools.javac.code.Attribute.UnresolvedClass;
import com.sun.tools.javac.code.Attribute.Visitor;
import com.sun.tools.javac.code.Scope.WriteableScope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

//anuke's implementation of annotation proxy maker, to replace the broken one from oracle
//thanks, anuke
//damn you, oracle
public record AnnotationProxyMaker(Compound anno, Class<? extends Annotation> annoType) {
	public static <A extends Annotation> A generateAnnotation(Compound anno, Class<A> annoType) {
		AnnotationProxyMaker var2 = new AnnotationProxyMaker(anno, annoType);
		return annoType.cast(var2.generateAnnotation());
	}

	private static Object mirrorProxy(Type t) {
		return proxify(() -> new MirroredTypeException(t));
	}

	private static Object mirrorProxy(List<Type> t) {
		return proxify(() -> new MirroredTypesException(t));
	}

	private static <T extends Throwable> Object proxify(Prov<T> prov) {
		try {
			return new ExceptionProxy() {
				@Serial
				private static final long serialVersionUID = 1L;

				@Override
				protected RuntimeException generateException() {
					return (RuntimeException) prov.get();
				}
			};
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private Annotation generateAnnotation() {
		return AnnotationParser.annotationForMap(annoType, getAllReflectedValues());
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
				for (Symbol symbol : it) {
					handleSymbol(symbol, map);
				}
			} catch (Throwable death) {
				throw new RuntimeException(death);
			}
		}

		for (Pair<MethodSymbol, Attribute> var7 : anno.values) {
			map.put(var7.fst, var7.snd);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private <T extends Symbol> void handleSymbol(Symbol sym, Map<T, Attribute> map) {
		if (sym.getKind() == ElementKind.METHOD) {
			MethodSymbol symbol = (MethodSymbol) sym;
			Attribute attribute = symbol.getDefaultValue();
			if (attribute != null) {
				map.put((T) symbol, attribute);
			}
		}
	}

	private Object generateValue(MethodSymbol var1, Attribute var2) {
		ValueVisitor visitor = new ValueVisitor(var1);
		return visitor.getValue(var2);
	}

	private class ValueVisitor implements Visitor {
		private final MethodSymbol meth;
		private Class<?> returnClass;
		private Object value;

		ValueVisitor(MethodSymbol met) {
			meth = met;
		}

		Object getValue(Attribute attribute) {
			Method var2;
			try {
				var2 = annoType.getMethod(meth.name.toString());
			} catch (NoSuchMethodException var4) {
				return null;
			}

			returnClass = var2.getReturnType();
			attribute.accept(this);
			if (!(value instanceof ExceptionProxy) && !AnnotationType.invocationHandlerReturnType(returnClass).isInstance(value)) {
				typeMismatch(var2, attribute);
			}

			return value;
		}

		@Override
		public void visitConstant(Constant val) {
			value = val.getValue();
		}

		@Override
		public void visitClass(Attribute.Class clazz) {
			value = mirrorProxy(clazz.classType);
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void visitArray(Array array) {
			Name name = ((Type.ArrayType) array.type).elemtype.tsym.getQualifiedName();
			int i;
			if (name.equals(name.table.names.java_lang_Class)) {
				ListBuffer buffer = new ListBuffer();
				Attribute[] attributes = array.values;
				int var16 = attributes.length;

				for (i = 0; i < var16; ++i) {
					Attribute attribute = attributes[i];
					Type var8 = attribute instanceof UnresolvedClass ? ((UnresolvedClass) attribute).classType : ((Attribute.Class) attribute).classType;
					buffer.append(var8);
				}

				value = mirrorProxy(buffer.toList());
			} else {
				int var3 = array.values.length;
				Class var4 = returnClass;
				returnClass = returnClass.getComponentType();

				try {
					Object arr = java.lang.reflect.Array.newInstance(returnClass, var3);

					for (i = 0; i < var3; ++i) {
						array.values[i].accept(this);
						if (value == null || value instanceof ExceptionProxy) {
							return;
						}

						try {
							java.lang.reflect.Array.set(arr, i, value);
						} catch (IllegalArgumentException var12) {
							value = null;
							return;
						}
					}

					value = arr;
				} finally {
					returnClass = var4;
				}
			}
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public void visitEnum(Enum e) {
			if (returnClass.isEnum()) {
				String n = e.value.toString();

				try {
					value = java.lang.Enum.valueOf((Class) returnClass, n);
				} catch (IllegalArgumentException var4) {
					value = proxify(() -> new EnumConstantNotPresentException((Class) returnClass, n));
				}
			} else {
				value = null;
			}
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void visitCompound(Compound compound) {
			try {
				Class var2 = returnClass.asSubclass(Annotation.class);
				value = AnnotationProxyMaker.generateAnnotation(compound, var2);
			} catch (ClassCastException var3) {
				value = null;
			}

		}

		@Override
		public void visitError(Error e) {
			if (e instanceof UnresolvedClass) {
				value = mirrorProxy(((UnresolvedClass) e).classType);
			} else {
				value = null;
			}
		}

		private void typeMismatch(Method method, final Attribute attribute) {
			value = proxify(() -> new AnnotationTypeMismatchException(method, attribute.type.toString()));
		}
	}
}
