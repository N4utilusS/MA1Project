package be.n4utiluss.wysiwyd.fonts;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Manages the fonts used in the app with a singleton.
 * @author anthonydebruyn
 *
 */
public class Fonts {

	final private static Object o = new Object();
	private static Fonts fonts = null;
	
	/**
	 * Handwritten font used for the bottle names.
	 */
	final public Typeface chopinScript;
	
	/**
	 * Returns the only instance of the class.
	 * @param context
	 * @return
	 */
	public static Fonts getFonts(Context context) {
		synchronized(o) {
			if (fonts == null) {
				fonts = new Fonts(context);
			}
		}
		
		return fonts;
	}
	
	/**
	 * Private constructor.
	 * @param context The call context.
	 */
	private Fonts(Context context) {
		super();
		
		this.chopinScript = Typeface.createFromAsset(context.getAssets(), "fonts/ChopinScript.otf");
	}
}
