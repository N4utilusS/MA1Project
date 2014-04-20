package be.n4utiluss.wysiwyd.fonts;

import android.content.Context;
import android.graphics.Typeface;

public class Fonts {

	final private static Object o = new Object();
	private static Fonts fonts = null;
	
	final public Typeface chopinScript;
	
	public static Fonts getFonts(Context context) {
		synchronized(o) {
			if (fonts == null) {
				fonts = new Fonts(context);
			}
		}
		
		return fonts;
	}
	
	private Fonts(Context context) {
		super();
		
		this.chopinScript = Typeface.createFromAsset(context.getAssets(), "fonts/ChopinScript.otf");
	}
}
