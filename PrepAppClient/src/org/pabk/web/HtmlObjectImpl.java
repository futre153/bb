package org.pabk.web;

import java.util.Map;

abstract class HtmlObjectImpl implements HtmlObject {
	
	private static final Object ACTION_KEY = "action";

	public HtmlObject action(Map <String, String[]> params) {
		String[] action = params.get(ACTION_KEY);
		HtmlObject obj = this;
		if(action != null) {
			try {
				for(int i = 0; i < action.length; i++) {
					Class<?>[] cls = new Class[] {obj.getClass(), params.getClass()};
					Object[] args = new Object[]{obj, params};
					obj = (HtmlObject) HtmlObjectImpl.class.getDeclaredMethod(action[i], cls).invoke(null, args);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				obj = new MainHtmlObject();
			}
		}
		return obj;
	}
}
