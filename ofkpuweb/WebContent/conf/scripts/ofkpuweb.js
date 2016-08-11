/**
 * 
 */

var tops;
var tops_interval;
var tops_interval_value;
var tops_active;
var tops_element;

function initHeadlines(hln_id, hln_interval) {
	var x = document.getElementById(hln_id);
	var inputs = x.getElementsByTagName("INPUT");
	var i;
	tops = new Array(inputs.length);
	for(i = 0; i < inputs.length; i ++) {
		tops[i] = convertToUtf8(new String(atob(inputs[i].value)));
	}
	tops_interval_value = hln_interval;
	tops_active = tops.length - 1;
	tops_element = x;
	tops_interval = window.setInterval(loadHeadlines, 1000);
}

function loadHeadlines() {
	tops_active = tops_active + 1;
	topButtonOnclick (tops_active >= tops.length ? 0 : tops_active);
}

function topButtonOnclick(index) {
	tops_active = index;
	window.clearInterval(tops_interval);
	tops_element.innerHTML = tops[index];
	tops_interval = window.setInterval(loadHeadlines, tops_interval_value);
}

function sabButtonOnclick(id, index, cl, cle, cld) {
	var x = document.getElementById(id)
	var xx = x.getElementsByTagName("BUTTON");
	for(i = 0; i < xx.length; i ++) {
		xx[i].disabled = (index == i);
	}
	xx = x.getElementsByClassName(cl);
	for(i = 0; i < xx.length; i ++) {
		var xxx = xx[i].getElementsByTagName("DIV");
		xxx[1].className = (cle + (index == i ? "" : (" " + cld)));
	}
}

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

function window_onload(hln_id, hln_interval) {
	initHeadlines(hln_id, hln_interval);
}

function convertToUtf8 (ascii) {
	var ret = new String();
	var i;
	for(i = 0; i < ascii.length; i ++) {
		var c = ascii.charCodeAt(i);
		if(c < 128) {
			ret = ret + String.fromCharCode(c);
		}
		else if (c < 224) {
			ret = ret + String.fromCharCode(((c & 31) * 64) + (ascii.charCodeAt(i + 1) & 63));
			i ++;
		}
		else if (c < 240) {
			c = ((c & 31) * 64 * 64) + ((ascii.charCodeAt(i + 1) & 63) * 64) + (ascii.charCodeAt(i + 2) & 63);
			i += 2;
		}
	}
	return ret;
}
