package by.ts.hmxy.mixins;

public class MixinHelper {
	enum AT {
		HEAD("HEAD"), RETURN("RETURN"), TAIL("TAIL"), INVOKE("INVOKE"), INVOKE_ASSIGN("INVOKE_ASSIGN"), FIELD("FIELD"),
		NEW("NEW"),INVOKE_STRING("INVOKE_STRING"),JUMP("JUMP"),CONSTANT("CONSTANT");
		public final String AT;

		AT(String at) {
			this.AT = at;
		}

	}
}
