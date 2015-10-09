package io.macgyver.core.web;

import rx.functions.Action1;

public interface UIContextDecorator extends Action1<UIContext>{

	public void call(UIContext ctx);
	
}
