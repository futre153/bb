/**
 * 
 */

function setClassById (id, value) {
	var x = document.getElementById (id);
	if(x != null) {
		x.className = value;
	}
}

function getElementByClassName (x, className) {
	while(true) {
		if(x == null || x.className == className) {
			break;
		}
		x = x.parentElement;
	}
	return x; 
}

function menuOnMouseOverOut (id, value, id2, image) {
	var x = document.getElementById(id2);
	if(x != null) {
		x.style.backgroundImage = image;
	}
	setClassById (id, value);
}