package heavyindustry.annotations.util;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import com.squareup.javapoet.TypeName;
import heavyindustry.annotations.BaseProcessor;
import mindustry.io.TypeIO;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import static heavyindustry.annotations.BaseProcessor.fullName;
import static heavyindustry.annotations.BaseProcessor.simpleName;
import static heavyindustry.annotations.BaseProcessor.typeName;

public final class TypeIOResolver {
	private TypeIOResolver() {}

	public static ClassSerializer resolve(BaseProcessor proc) {
		ClassSerializer out = new ClassSerializer(new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>());

		TypeElement type = proc.elements.getTypeElement(TypeIO.class.getCanonicalName());
		Seq<ExecutableElement> methods = proc.methods(type);
		for (ExecutableElement meth : methods) {
			if (proc.is(meth, Modifier.PUBLIC) && proc.is(meth, Modifier.STATIC)) {
				Seq<VariableElement> params = Seq.with(meth.getParameters()).as();

				if (params.size == 2 && typeName(params.first()).toString().equals("arc.util.io.Writes")) {
					out.writers.put(fix(typeName(params.get(1)).toString()), fullName(type) + "." + simpleName(meth));
				} else if (params.size == 1 && typeName(params.first()).toString().equals("arc.util.io.Reads") && meth.getReturnType().getKind() != TypeKind.VOID) {
					out.readers.put(fix(TypeName.get(meth.getReturnType()).toString()), fullName(type) + "." + simpleName(meth));
				} else if (params.size == 2 && typeName(params.first()).toString().equals("arc.util.io.Reads") && meth.getReturnType().getKind() != TypeKind.VOID && proc.types.isSameType(meth.getReturnType(), meth.getParameters().get(1).asType())) {
					out.mutatorReaders.put(fix(TypeName.get(meth.getReturnType()).toString()), fullName(type) + "." + simpleName(meth));
				}
			}
		}

		return out;
	}

	private static String fix(String str) {
		return str.replace("mindustry.gen", "").replace("lonetrail.gen", "");
	}

	/**
	 * Information about read/write methods for class types.
	 */
	public static class ClassSerializer {
		public final ObjectMap<String, String> writers, readers, mutatorReaders;

		public ClassSerializer(ObjectMap<String, String> writer, ObjectMap<String, String> reader, ObjectMap<String, String> mutatorReader) {
			writers = writer;
			readers = reader;
			mutatorReaders = mutatorReader;
		}
	}
}
