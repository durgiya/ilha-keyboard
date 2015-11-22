package org.manjusa.ilha.keyboard;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.List;
import android.util.Log;

import org.manjusa.ilha.R;

/**
 * Shows a soft keyboard, rendering keys and detecting key presses.
 */
public class SoftKeyboardView extends KeyboardView {

    public Context ctx;
	
    public SoftKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
    }

    public SoftKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
    }
    
  @Override
  protected boolean onLongPress(Key key) {
    // 0xFF01~0xFF5E map to the full-width forms of the characters from
    // 0x21~0x7E. Make the long press as producing corresponding full-width
    // forms for these characters by adding the offset (0xff01 - 0x21).

    if (key.codes[0] == SoftKeyboard.KEYCODE_MODE_CHANGE_LETTER) {
      getOnKeyboardActionListener().onKey(SoftKeyboard.KEYCODE_OPTIONS, null);
      return true;
    } else if (key.codes[0] == SoftKeyboard.KEYCODE_MODE_CHANGE) {
      getOnKeyboardActionListener().onKey(SoftKeyboard.KEYCODE_SCHEMA_OPTIONS, null);
      return true;
    } else if (key.codes[0] == "，".charAt(0)) {
      getOnKeyboardActionListener().onKey(SoftKeyboard.KEYCODE_REVERSE, null);
      return true;
    } else {
      return super.onLongPress(key);
    }
  }

}
