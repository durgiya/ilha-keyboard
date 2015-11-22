package org.manjusa.ilha;

import org.manjusa.ilha.keyboard.KeyboardSwitch;
import org.manjusa.ilha.keyboard.SoftKeyboard;
import org.manjusa.ilha.keyboard.SoftKeyboardView;

import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class Ilha
		extends InputMethodService
		implements
		KeyboardView.OnKeyboardActionListener {

	protected SoftKeyboardView inputView;
	private KeyboardSwitch keyboardSwitch;

	private boolean enterAsLineBreak;
	private boolean isLeftApo = true;
	private boolean isLeftQuote = true;

	// private ManjuEditor editor = new ManjuEditor();

	protected int[] keyboardIds;

	// private AlertDialog mOptionsDialog;

	@Override
	public void onCreate() {
		super.onCreate();
		
		initKeyboard();
		
		// Use the following line to debug IME service.
		// android.os.Debug.waitForDebugger();
	}

	/**
	 * @title Configuration
	 * @see android.inputmethodservice.InputMethodService#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
				candidatesStart, candidatesEnd);
		if ((candidatesEnd != -1)
				&& ((newSelStart != candidatesEnd) || (newSelEnd != candidatesEnd))) {
		}
	}

	@Override
	public void onComputeInsets(InputMethodService.Insets outInsets) {
		super.onComputeInsets(outInsets);
		outInsets.contentTopInsets = outInsets.visibleTopInsets;
	}

	@Override
	public View onCreateInputView() {
		inputView = (SoftKeyboardView) getLayoutInflater().inflate(
				R.layout.input, null);
		inputView.setOnKeyboardActionListener(this);
		return inputView;
	}

	@Override
	public View onCreateCandidatesView() {
		return null;
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		editorstart(attribute.inputType);
	}

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
		bindKeyboardToInputView();
	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		super.onFinishInputView(finishingInput);
		// Dismiss any pop-ups when the input-view is being finished and hidden.
		inputView.closing();
	}

	@Override
	public void onFinishCandidatesView(boolean finishingInput) {
		super.onFinishCandidatesView(finishingInput);
	}

	@Override
	public void onUnbindInput() {
		super.onUnbindInput();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
			// Handle the back-key to close the pop-up keyboards.
			if ((inputView != null) && inputView.handleBack()) {
				return true;
			}
		}

		if (processKey(event))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * methods implements KeyboardView.OnKeyboardActionListener onKey: int
	 * primaryCode, int[] keyCodes onPress , onRelease: int primaryCode onText:
	 * CharSequence swipeDown , swipeLeft , swipeRight , swipeUp
	 */

	// Order: onPress -> onKey -> onRelease
	@Override
	public void onPress(int primaryCode) {
		// sound effect etc.
		/*
		 * if
		 * (primaryCode==32||primaryCode==-5||primaryCode==10||primaryCode==-2)
		 * { inputView.setPreviewEnabled(false); }
		 */
		inputView.setPreviewEnabled(false);
	}
	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		if (keyboardSwitch.onKey(primaryCode)) {
			bindKeyboardToInputView();
			return;
		}
		if (handleOption(primaryCode) || handleCapsLock(primaryCode)
				|| handleEnter(primaryCode) || handleSpace(primaryCode)
				|| handleDelete(primaryCode)) {
			return;
		}
		handleKey(primaryCode);
	}

	@Override
	public void onRelease(int primaryCode) {
		// no-op
		// inputView.setPreviewEnabled(true);
	}

	@Override
	public void onText(CharSequence text) {
		commitText(text);
	}

	public void swipeLeft() {
		// no-op
	}

	public void swipeRight() {
		// no-op
	}

	public void swipeUp() {
		// no-op
		commitText("\u180A\u1873");
	}

	public void swipeDown() {
		requestHideSelf(0);
	}

	
	/**
	 * Other methods (user defined)
	 */

	/**
	 * Commits the given text to the editing field.
	 */

	private boolean processKey(KeyEvent event) {
		int keyCode = event.getKeyCode();
		int keyChar = 0;
		CharSequence keyText = "";
		if (KeyEvent.KEYCODE_SPACE == keyCode && event.isShiftPressed()) {
			keyChar = SoftKeyboard.KEYCODE_MODE_CHANGE_LETTER;
			onKey(keyChar, null);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DEL) {
			return false;
		}
		// if (!isManju()) return false;
		Log.e("kyle", event.toString());
		if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			keyChar = keyCode - KeyEvent.KEYCODE_A + 'a';
		} else if (!event.isShiftPressed() && keyCode >= KeyEvent.KEYCODE_0
				&& keyCode <= KeyEvent.KEYCODE_9) {
			keyChar = keyCode - KeyEvent.KEYCODE_0 + '0';
		} else if (keyCode == KeyEvent.KEYCODE_COMMA) {
			keyText = event.isShiftPressed() ? "《" : "，";
		} else if (keyCode == KeyEvent.KEYCODE_PERIOD) {
			keyText = event.isShiftPressed() ? "》" : "。";
		} else if (keyCode == KeyEvent.KEYCODE_SEMICOLON) {
			keyText = event.isShiftPressed() ? "：" : "；";
		} else if (keyCode == KeyEvent.KEYCODE_SLASH) {
			keyText = event.isShiftPressed() ? "？" : "、";
		} else if (keyCode == KeyEvent.KEYCODE_BACKSLASH) {
			keyText = "、";
		} else if (keyCode == KeyEvent.KEYCODE_MINUS && event.isShiftPressed()) {
			keyText = "——";
		} else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE
				&& !event.isShiftPressed()) {
			keyText = isLeftApo ? "‘" : "’";
			isLeftApo = !isLeftApo;
		} else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE
				&& event.isShiftPressed()) {
			keyText = isLeftQuote ? "“" : "”";
			isLeftQuote = !isLeftQuote;
		} else if (keyCode == KeyEvent.KEYCODE_1 && event.isShiftPressed()) {
			keyText = "！";
		} else if (keyCode == KeyEvent.KEYCODE_4 && event.isShiftPressed()) {
			keyText = "￥";
		} else if (keyCode == KeyEvent.KEYCODE_6 && event.isShiftPressed()) {
			keyText = "……";
		} else if (keyCode == KeyEvent.KEYCODE_9 && event.isShiftPressed()) {
			keyText = "（";
		} else if (keyCode == KeyEvent.KEYCODE_0 && event.isShiftPressed()) {
			keyText = "）";
		} else if (keyCode == KeyEvent.KEYCODE_SPACE) {
			keyChar = ' ';
		} else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
			keyChar = '\'';
		} else if (keyCode == KeyEvent.KEYCODE_DEL) {
			keyChar = Keyboard.KEYCODE_DELETE;
		} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
			keyChar = '\n';
		} else if (keyCode == KeyEvent.KEYCODE_GRAVE) {
			keyChar = '`';
		}
		if (keyText.length() > 0) {
			commitText(keyText);
			return true;
		} else if (0 != keyChar) {
			onKey(keyChar, null);
			return true;
		}
		return false;
	}

	private void commitText(CharSequence text) {
		InputConnection ic = getCurrentInputConnection();
		if (ic != null) {
			if (text.length() > 1) {
				// Batch edit a sequence of characters.
				ic.beginBatchEdit();
				ic.commitText(text, 1);
				ic.endBatchEdit();
			} else {
				ic.commitText(text, 1);
			}
		}
	}

	private boolean handleOption(int keyCode) {
		return false;
	}

	private boolean handleCapsLock(int keyCode) {
		return (keyCode == Keyboard.KEYCODE_SHIFT)
				&& inputView.setShifted(!inputView.isShifted());
	}

	private boolean handleEnter(int keyCode) {
		if (keyCode == '\n') {
			if (enterAsLineBreak) {
				commitText("\n");
			} else {
				sendKeyChar('\n');
			}
			return true;
		}
		return false;
	}

	private boolean handleSpace(int keyCode) {
		if (keyCode == ' ') {
			commitText(" ");
			return true;
		}
		return false;
	}

	private boolean handleDelete(int keyCode) {
		// Handle delete-key only when no composing text.
		if ((keyCode == Keyboard.KEYCODE_DELETE)) {
			sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
			return true;
		}
		return false;
	}

	private void handleKey(int keyCode) {
		if (isInputViewShown() && inputView.isShifted()) {
			keyCode = Character.toUpperCase(keyCode);
		}
		commitText(String.valueOf((char) keyCode));
	}
	

	private void initKeyboard() {
		keyboardSwitch = new KeyboardSwitch(this);
		keyboardSwitch.initializeKeyboard();
	}

	/**
	 * Resets the internal state of this editor, typically called when a new
	 * input session commences.
	 */
	private void editorstart(int inputType) {
		enterAsLineBreak = false;

		switch (inputType & InputType.TYPE_MASK_CLASS) {
		case InputType.TYPE_CLASS_TEXT:
			int variation = inputType & InputType.TYPE_MASK_VARIATION;
			if (variation == InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
				// Make enter-key as line-breaks for messaging.
				enterAsLineBreak = true;
			}
			break;
		}
		// Select a keyboard based on the input type of the editing field.
		keyboardSwitch.initializeKeyboard(getMaxWidth());
		keyboardSwitch.onStartInput(inputType);
		setCandidatesViewShown(true);
		setCandidatesViewShown(false);
	}
	
	private void bindKeyboardToInputView() {
		if (inputView != null) {
			// Bind the selected keyboard to the input view.
			SoftKeyboard sk = (SoftKeyboard) keyboardSwitch.getCurrentKeyboard();
			inputView.setKeyboard(sk);
			inputView.setPreviewEnabled(true);
		}
	}


}
