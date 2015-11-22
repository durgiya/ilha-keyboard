package org.manjusa.ilha.keyboard;

import org.manjusa.ilha.R.xml;

import android.content.Context;
import android.inputmethodservice.Keyboard;

/**
 * A soft keyboard definition.
 */
public class SoftKeyboard extends Keyboard {

	public static final int KEYCODE_MODE_CHANGE_LETTER = -200;// change
	public static final int KEYCODE_OPTIONS = -100;
	public static final int KEYCODE_SCHEMA_OPTIONS = -99;
	public static final int KEYCODE_REVERSE = 96;

	private final int id;

	public boolean isManjuSymbol() {
		return id == xml.manju_symbol;
	}

	public SoftKeyboard(Context context, int xmlLayoutResId) {
		super(context, xmlLayoutResId);
		id = xmlLayoutResId;
	}

}
