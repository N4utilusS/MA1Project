package be.n4utiluss.wysiwyd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurBuilder {
	private static final float BITMAP_SCALE = 0.2f;
	private static final float BLUR_RADIUS = 7.5f;

	@SuppressLint("NewApi")
	public static Bitmap blur(Context ctx, Bitmap image) {
		
		Bitmap outputBitmap;
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
			int width = Math.round(image.getWidth() * BITMAP_SCALE);
			int height = Math.round(image.getHeight() * BITMAP_SCALE);

			Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
			outputBitmap = Bitmap.createBitmap(inputBitmap);

			RenderScript rs = RenderScript.create(ctx);
			ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
			Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
			theIntrinsic.setRadius(BLUR_RADIUS);
			theIntrinsic.setInput(tmpIn);
			theIntrinsic.forEach(tmpOut);
			tmpOut.copyTo(outputBitmap);
		} else {
			int width = Math.round(image.getWidth() * BITMAP_SCALE);
			int height = Math.round(image.getHeight() * BITMAP_SCALE);

			Bitmap temp = Bitmap.createScaledBitmap(image, width, height, true);
			outputBitmap = Bitmap.createScaledBitmap(temp, image.getWidth(), image.getHeight(), true);
		}

		return outputBitmap;
	}

}