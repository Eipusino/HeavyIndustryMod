package heavyindustry.annotations;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import heavyindustry.annotations.Annotations.TypeIOHandler;
import mindustry.io.TypeIO;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

public final class TypeIOResolver {
	private TypeIOResolver() {}

	public static ClassSerializer resolve(BaseProcessor proc) {
		ClassSerializer out = new ClassSerializer(new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>(), new ObjectMap<>());

		Seq<ClassSymbol> handlers = Seq.with(proc.<ClassSymbol>with(TypeIOHandler.class)).add(proc.conv(TypeIO.class));
		for (ClassSymbol handler : handlers) {
			for (Symbol e : handler.getEnclosedElements()) {
				if (!(e instanceof MethodSymbol m)) continue;

				if (BaseProcessor.is(m, Modifier.PUBLIC, Modifier.STATIC)) {
					List<VarSymbol> params = m.params;
					int size = params.size();

					if (size == 0) continue;
					String sig = BaseProcessor.fName(handler) + "." + BaseProcessor.name(m), ret = BaseProcessor.fixName(m.getReturnType().toString());

					boolean isVoid = m.getReturnType().getKind() == TypeKind.VOID;
					Type f = params.get(0).type;
					ClassSymbol w = proc.conv(Writes.class), r = proc.conv(Reads.class);

					if (size == 2 && proc.same(f, w)) {
						(sig.endsWith("Net") ? out.netWriters : out.writers).put(BaseProcessor.fixName(params.get(1).type.toString()), sig);
					} else if (size == 1 && proc.same(f, r) && !isVoid) {
						out.readers.put(ret, sig);
					} else if (size == 2 && proc.same(f, r) && !isVoid && proc.same(m.getReturnType(), params.get(1).type)) {
						out.mutatorReaders.put(ret, sig);
					}
				}
			}
		}

		return out;
	}

	public static class ClassSerializer {
		public ObjectMap<String, String> writers, readers, mutatorReaders, netWriters;

		public ClassSerializer(ObjectMap<String, String> writers, ObjectMap<String, String> readers, ObjectMap<String, String> mutatorReaders, ObjectMap<String, String> netWriters) {
			this.writers = writers;
			this.readers = readers;
			this.mutatorReaders = mutatorReaders;
			this.netWriters = netWriters;
		}

		public String getNetWriter(String type, String fallback) {
			return netWriters.get(type, writers.get(type, fallback));
		}
	}
}
