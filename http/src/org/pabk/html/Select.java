package org.pabk.html;

public class Select extends HtmlTag{
	
	private Select(String name, String[] options, String[] optionIDs,int selected) {
		if(name!=null) {
			this.setAttribute("name", name);
			this.setAttribute("id", name);
		}
		for(int i=0;i<options.length;i++) {
			this.appendChild(Option.getInstance(options[i], optionIDs==null?null:optionIDs[i], selected==i));
		}
		
	}

	public static Select getInstance(String name, String[] options, String[] optionIDs, int selected) {
		if(options==null) options = new String[]{};
		if(selected > options.length) selected = -1;
		if(optionIDs != null) if(optionIDs.length<options.length) optionIDs=null;
		return new Select(name, options, optionIDs, selected);
	}
}
