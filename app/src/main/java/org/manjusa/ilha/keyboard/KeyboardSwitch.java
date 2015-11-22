package org.manjusa.ilha.keyboard;

import org.manjusa.ilha.R.xml;
import org.manjusa.ilha.keyboard.SoftKeyboard;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.text.InputType;

public class KeyboardSwitch {

	private final Context context;

	private final int[] manjuKeyboardIds = { xml.manju, xml.manju_symbol };
	private int manjuKeyboardId;

	private SoftKeyboard[] manjuKeyboards;
	private SoftKeyboard currentKeyboard;

	private int currentDisplayWidth;

	public KeyboardSwitch(Context context) {
		this.context = context;
	}

	public void initializeKeyboard() {
		manjuKeyboards = new SoftKeyboard[manjuKeyboardIds.length];
		for (int i = 0; i < manjuKeyboardIds.length; i++) {
			manjuKeyboards[i] = new SoftKeyboard(context, manjuKeyboardIds[i]);
		}

		manjuKeyboardId = 0;

		currentKeyboard = manjuKeyboards[manjuKeyboardId];
	}

	/**
	 * Recreates the keyboards if the display-width has been changed.
	 * 
	 * @param displayWidth
	 *            the display-width for keyboards.
	 */
	public void initializeKeyboard(int displayWidth) {
		if ((currentKeyboard != null) && (displayWidth == currentDisplayWidth)) {
			return;
		}

		currentDisplayWidth = displayWidth;
		initializeKeyboard();
	}

	public Keyboard getCurrentKeyboard() {
		return currentKeyboard;
	}

	/**
	 * Switches to the appropriate keyboard based on the type of text being
	 * edited, for example, the symbol keyboard for numbers.
	 * 
	 * @param inputType
	 *            one of the {@code InputType.TYPE_CLASS_*} values listed in
	 *            {@link android.text.InputType}.
	 */
	public void onStartInput(int inputType) {
		if ((inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT) {
			int variation = inputType & InputType.TYPE_MASK_VARIATION;
			if ((variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
					|| (variation == InputType.TYPE_TEXT_VARIATION_URI)
					|| (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD)
					|| (variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
				// toEnglish(true);
				currentKeyboard.setShifted(currentKeyboard.isShifted());
			} else
				// toEnglish(false);
				currentKeyboard = manjuKeyboards[manjuKeyboardId];
		}
	}

	/**
	 * Consumes the pressed key-code and switch keyboard if applicable.
	 * 
	 * @return {@code true} if the keyboard is switched; otherwise {@code false}
	 *         .
	 */
	public boolean onKey(int keyCode) {
		switch (keyCode) {
		case SoftKeyboard.KEYCODE_MODE_CHANGE_LETTER:
			manjuKeyboardId++;
			if (manjuKeyboardId >= manjuKeyboardIds.length) {
				manjuKeyboardId = 0;
			}
			currentKeyboard = manjuKeyboards[manjuKeyboardId];
			return true;

		case SoftKeyboard.KEYCODE_MODE_CHANGE:
			//
			return true;
		}

		// Return false if the key isn't consumed to switch a keyboard.
		return false;
	}

}
