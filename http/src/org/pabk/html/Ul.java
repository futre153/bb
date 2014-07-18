package org.pabk.html;

public class Ul extends HtmlTag implements HtmlList {
	
	private Ul(){}
		
	public static Ul getInstance(){return new Ul();}
	public static Ul getInstance(String[] list) {
		Ul ul=new Ul();
		ul.setList(list);
		return ul;
	}
	
	@Override
	public void setList(String[] list) {for(int i=0;i<list.length;i++) {this.appendChild(Li.getInstance(list[i]));}}
}
