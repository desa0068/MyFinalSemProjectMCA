package com.bsns.app;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class URLSpanNoUnderline extends URLSpan {

	public URLSpanNoUnderline(String url) {
		super(url);
		// TODO Auto-generated constructor stub

	}
	
	 @Override public void updateDrawState(TextPaint ds) {
	        super.updateDrawState(ds);
	        ds.setUnderlineText(true);
	        }

}
