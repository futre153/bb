package org.pabk.html;

abstract class TableCell extends HtmlTag {
	protected void setContent(Object cell) {
		if(cell == null) {
			appendChild(TextTag.NBSP);
		}
		else {
			if(cell instanceof Tag[]) {
				Tag[] tags = (Tag[]) cell;
				for (int i = 0; i < tags.length; i ++) {
					appendChild(tags[i]);
				}
			}
			else if (cell instanceof Tag) {
				appendChild((Tag) cell);
			}
			else if (cell instanceof String) {
				appendChild(TextTag.getInstance((String) cell));
			}
			else {
				appendChild(TextTag.NBSP);
			}
		}
	}
}
