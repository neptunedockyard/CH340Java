package com.neptunedockyard;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class fieldFilter extends DocumentFilter {
	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offs, int length,
			String str, AttributeSet attrs) throws BadLocationException {
		if ((fb.getDocument().getLength() + str.length() - length) <= 10)
			super.replace(fb, offs, length, str.replaceAll("[^0-9a-fA-F]", ""),
					attrs);
		else
			Toolkit.getDefaultToolkit().beep();
	}
}
