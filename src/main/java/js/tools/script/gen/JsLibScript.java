package js.tools.script.gen;

import java.util.List;

/**
 * Script generator. This class generates HTTP-RMI stubs compatible with j(s)-lib JavaScript library. It is deprecated, being
 * replaced by vanilla JavaScript alternative.
 * <p>
 * Known limitations: generated script uses <em>__callback__</em> and <em>__scope__</em> - with two leading and trailing
 * underscores - internally and given parameters are not allowed to uses them; mentioned names should be treated as reserved
 * keywords.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class JsLibScript {
	static String generate(JsClassImpl jsClassImpl) {
		Builder b = new Builder();
		b.add("$package(\"%s\");\r\n", jsClassImpl.getPackageName());
		b.add("\r\n");

		b.add("/**\r\n");
		b.add(" * %s.\r\n", titleCase(jsClassImpl.getClassName()));
		b.add(" */\r\n");

		b.add("%s = {\r\n", jsClassImpl.getQualifiedClassName());

		boolean firstMethod = true;
		for (JsMethod jsMethod : jsClassImpl.getMethods()) {
			if (!firstMethod) {
				b.add(",\r\n");
				b.add("\r\n");
			}
			JsMethodImpl jsMethodImpl = (JsMethodImpl) jsMethod;
			boolean isVoid = "void".equalsIgnoreCase(jsMethodImpl.getReturnType());

			b.add("\t/**\r\n");
			b.add("\t * %s.\r\n", titleCase(jsMethodImpl.getMethodName()));
			b.add("\t *\r\n");
			for (Parameter parameter : jsMethodImpl.getParameters()) {
				b.add("\t * @param %s %s,\r\n", parameter.getType(), parameter.getName());
			}
			b.add("\t * @param Function callback function to invoke on RMI completion,\r\n");
			b.add("\t * @param Object scope optional callback run-time scope, default to global scope.\r\n");
			b.add("\t * @return %s\r\n", jsMethodImpl.getReturnType());
			for (String exceptionType : jsMethodImpl.getExceptionTypes()) {
				b.add("\t * @throws %s\r\n", exceptionType);
			}
			b.add("\t * @assert callback is a {@link Function} and scope is an {@link Object}");
			if (isVoid) {
				b.add(", if they are defined.\r\n");
				b.add("\t * @note since method return type is void, callback, and hence scope too, is optional.\r\n");
			} else {
				b.add(".\r\n");
			}
			b.add("\t */\r\n");

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

			for (Parameter parameter : jsMethodImpl.getParameters()) {
				b.add("\t\t$assert(typeof %s !== \"undefined\", \"%s#%s\", \"%s argument is undefined.\");\r\n", parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				// array should be tested first because we can have an array of strings, for example,
				// and Parameter class can mistake array for string; the same is true for the other types
				if (parameter.isArray()) {
					b.add("\t\t$assert(%s === null || js.lang.Types.isArray(%s), \"%s#%s\", \"%s argument is not an array.\");\r\n", parameter.getName(), parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				}
				if (parameter.isString()) {
					b.add("\t\t$assert(%s === null || js.lang.Types.isString(%s), \"%s#%s\", \"%s argument is not a string.\");\r\n", parameter.getName(), parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				}
				if (parameter.isNumber()) {
					b.add("\t\t$assert(js.lang.Types.isNumber(%s), \"%s#%s\", \"%s argument is not a number.\");\r\n", parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				}
				if (parameter.isBoolean()) {
					b.add("\t\t$assert(js.lang.Types.isBoolean(%s), \"%s#%s\", \"%s argument is not a boolean.\");\r\n", parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				}
				if (parameter.isDate()) {
					b.add("\t\t$assert(%s === null || js.lang.Types.isDate(%s), \"%s#%s\", \"%s argument is not a date.\");\r\n", parameter.getName(), parameter.getName(), jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName(), titleCase(parameter.getName()));
				}
			}
			if (jsMethodImpl.hasParameters()) {
				b.add("\r\n");
			}

			b.add("\t\tvar __callback__ = arguments[%d];\r\n", jsMethodImpl.getParameters().size());
			if (isVoid) {
				b.add("\t\t$assert(typeof __callback__ === \"undefined\" || js.lang.Types.isFunction(__callback__), \"%s#%s\", \"Callback is not a function.\");\r\n", jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName());
			} else {
				b.add("\t\t$assert(js.lang.Types.isFunction(__callback__), \"%s#%s\", \"Callback is not a function.\");\r\n", jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName());
			}
			b.add("\t\tvar __scope__ = arguments[%d];\r\n", jsMethodImpl.getParameters().size() + 1);
			b.add("\t\t$assert(typeof __scope__ === \"undefined\" || js.lang.Types.isObject(__scope__), \"%s#%s\", \"Scope is not an object.\");\r\n", jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName());
			b.add("\t\tif(!js.lang.Types.isObject(__scope__)) {\r\n");
			b.add("\t\t\t__scope__ = window;\r\n");
			b.add("\t\t}\r\n");
			b.add("\r\n");

			b.add("\t\tvar rmi = new js.net.RMI();\r\n");
			b.add("\t\trmi.setMethod(\"%s\", \"%s\");\r\n", jsClassImpl.getQualifiedClassName(), jsMethodImpl.getMethodName());

			if (jsMethodImpl.hasParameters()) {
				b.add("\t\trmi.setParameters(");
				List<Parameter> parameters = jsMethodImpl.getParameters();
				b.add(parameters.get(0).getName());
				for (int i = 1; i < parameters.size(); ++i) {
					b.add(", ");
					b.add(parameters.get(i).getName());
				}
				b.add(");\r\n");
			}

			b.add("\t\trmi.exec(__callback__, __scope__);\r\n");

			b.add("\t}");
			firstMethod = false;
		}

		b.add("\r\n");
		b.add("};\r\n");

		// add simple class name alias for qualified class name

		// if(typeof ObjectController === "undefined") {
		// ObjectController = sixqs.site.controller.ObjectController;
		// }

		b.add("\r\nif(typeof ");
		b.add(jsClassImpl.getClassName());
		b.add(" === 'undefined') {\r\n\t");
		b.add(jsClassImpl.getClassName());
		b.add(" = ");
		b.add(jsClassImpl.getQualifiedClassName());
		b.add(";\r\n}\r\n");

		return b.toString();
	}

	private static String titleCase(String camelCase) {
		// TODO replace with regex
		int length = camelCase.length();
		StringBuilder sb = new StringBuilder(length);
		sb.append(Character.toUpperCase(camelCase.charAt(0)));
		for (int i = 1; i < length - 1; i++) {
			char ch = camelCase.charAt(i);
			if (Character.isLowerCase(camelCase.charAt(i - 1)) && Character.isUpperCase(ch)) {
				sb.append(' ');
				if (Character.isLowerCase(camelCase.charAt(i + 1))) {
					ch = Character.toLowerCase(ch);
				}
			}
			sb.append(ch);
		}
		sb.append(camelCase.charAt(length - 1));
		return sb.toString();
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
