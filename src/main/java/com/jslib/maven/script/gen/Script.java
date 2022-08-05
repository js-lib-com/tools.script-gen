package com.jslib.maven.script.gen;

import java.io.IOException;
import java.util.List;

import com.jslib.util.Classes;
import com.jslib.util.Strings;

/**
 * Script generator.
 * <p>
 * Known limitations: generated script uses <em>__callback__</em> and <em>__scope__</em> - with two leading and trailing
 * underscores - internally and given parameters are not allowed to uses them; mentioned names should be treated as reserved
 * keywords.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class Script {
	static String generate(JsClassImpl jsClassImpl) {
		Builder b = new Builder();

		b.add("%s = {\r\n", jsClassImpl.getClassName());

		for (JsMethod jsMethod : jsClassImpl.getMethods()) {
			JsMethodImpl jsMethodImpl = (JsMethodImpl) jsMethod;

			b.add("\t %s: function(", jsMethodImpl.getMethodName());
			boolean firstParameter = true;
			for (Parameter parameter : jsMethodImpl.getParameters()) {
				if (!firstParameter) {
					b.add(", ");
				}
				b.add(parameter.getName());
				firstParameter = false;
			}
			b.add(") {\r\n");

			b.add("\t\tvar __callback__ = arguments[%d];\r\n", jsMethodImpl.getParameters().size());
			b.add("\t\tvar __scope__ = arguments[%d] || window;\r\n", jsMethodImpl.getParameters().size() + 1);
			b.add("\t\tvar url = \"%s/%s.rmi\";\r\n", jsClassImpl.getQualifiedClassName().replace('.', '/'), jsMethodImpl.getMethodName());
			b.add("\t\tvar parameters = [");
			List<Parameter> parameters = jsMethodImpl.getParameters();
			if (!parameters.isEmpty()) {
				b.add(parameters.get(0).getName());
				for (int i = 1; i < parameters.size(); ++i) {
					b.add(", ");
					b.add(parameters.get(i).getName());
				}
			}
			b.add("];\r\n");
			b.add("\r\n");
			b.add("\t\tthis.fetch(url, parameters, __callback__, __scope__);");
			b.add("\r\n");

			b.add("\t}");
			b.add(",\r\n");
			b.add("\r\n");
		}

		try {
			b.add(Strings.load(Classes.getResourceAsReader("/fetch-function")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		b.add("};\r\n");

		return b.toString();
	}

	private static class Builder {
		private StringBuilder sb = new StringBuilder();

		void add(String format, Object... args) {
			this.sb.append(String.format(format, args));
		}

		@Override
		public String toString() {
			return this.sb.toString();
		}
	}
}
