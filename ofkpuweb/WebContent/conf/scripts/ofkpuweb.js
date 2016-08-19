/**
 * 
 */

var tops;
var tops_interval;
var tops_interval_value;
var tops_active;
var tops_element;
var partners_interval;
var partners;
var partner = 0;
var partner_id;
var partner_interval_value;
var next_partners;
var partners_class;
var load_next_partners;

(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/sk_SK/sdk.js#xfbml=1&version=v2.7";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


function activateBookmark(index, mid, tid, mc, mca, tc, tci) {
	for(var i = 1; true; i ++) {
		var x = document.getElementById (mid + i);
		if(x == null) {
			break;
		}
		else {
			x.className = i == index ? (mc + " " + mca) : mc;
			document.getElementById (tid + i).className = i == index ? tc : (tc + " " + tci);
		}
	}
}

function initPartners(p_cl, p_id, interval) {
	var x = document.getElementsByClassName(p_cl);
	partner_id = p_id;
	partner_interval_value = interval;
	x = x[0].getElementsByTagName ("INPUT");
	partners = new Array (x.length);
	var i;
	var s;
	for (i = 0; i < x.length; i ++) {
		s = convertToUtf8(new String(atob(x[i].value)));
		s = s.split("|");
		partners[i] = {name:s[0], caption:s[1], url:s[2], id:s[3], web:s[4]};
	}
	for(i = 1; i < 6; i ++) {
		var index = getEmptyPartners();
		if(index >= 0) {
			x = document.getElementById(partner_id + i);
			var xx = x.getElementsByTagName("DIV");
			xx[1].textContent = partners[index].caption;
			xx[2].style.backgroundImage = "url(image?idi=" + partners[index].url + ")";
			xx[3].textContent = partners[index].name;
			x.getElementsByTagName ("A")[0].href = partners[index].web;
		}
	}
	load_next_partners = window.setInterval (loadNextPartners, 500);
}

function loadNextPartners () {
	window.clearInterval (load_next_partners);
	next_partners = new Array(5);
	var i = 0;
	partners_interval = new Array(5);
	for (; i < next_partners.length; i ++) {
		var index = getEmptyPartners();
		if(index < 0) {
			break;
		}
		else {
			next_partners[i] = {_new:partners[index], opa:100, control:200, neg:1};
			switch (i) {
			case 0:
				partners_interval[i] = window.setInterval(loadPartner1, partner_interval_value + i * 1000);
				break;
			case 1:
				partners_interval[i] = window.setInterval(loadPartner2, partner_interval_value + i * 1000);
				break;
			case 2:
				partners_interval[i] = window.setInterval(loadPartner3, partner_interval_value + i * 1000);
				break;
			case 3:
				partners_interval[i] = window.setInterval(loadPartner4, partner_interval_value + i * 1000);
				break;
			case 4:
				partners_interval[i] = window.setInterval(loadPartner5, partner_interval_value + i * 1000);
				break;
			}
			 
		}
	}
}

function loadPartner1 () {loadPartner(1);}
function loadPartner2 () {loadPartner(2);}
function loadPartner3 () {loadPartner(3);}
function loadPartner4 () {loadPartner(4);}
function loadPartner5 () {loadPartner(5);}

function loadPartner(index) {
	window.clearInterval(partners_interval[index - 1]);
	if (next_partners[index - 1].control != 0) {
		var x = document.getElementById(partner_id + index);
		var xx = x.getElementsByTagName("DIV");
		if(next_partners[index - 1].control == 200) {
			xx[2].innerHTML = "<img src=\"image?idi=" + next_partners[index - 1]._new.url + "\"></img>";
		}
		xx[1].style.opacity = (next_partners[index - 1].opa - next_partners[index - 1].neg) / 100;
		xx[2].style.opacity = (next_partners[index - 1].opa - next_partners[index - 1].neg) / 100;
		xx[3].style.opacity = (next_partners[index - 1].opa - next_partners[index - 1].neg) / 100;
		xx[2].getElementsByTagName ("IMG")[0].style.opacity = (100 - next_partners[index - 1].opa - next_partners[index - 1].neg) / 100;
		next_partners[index - 1].opa -= (1 * next_partners[index - 1].neg);
		next_partners[index - 1].control -= 1;
		if(next_partners[index - 1].control == 125) {
			var s = xx[2].style.backgroundImage;
			var o = xx[2].getElementsByTagName ("IMG")[0].style.opacity;
			xx[1].textContent = next_partners[index - 1]._new.caption;
			xx[2].style.backgroundImage = "url(image?idi=" + next_partners[index - 1]._new.url + ")";
			xx[3].textContent = next_partners[index - 1]._new.name;
			xx[2].innerHTML = "<img src=" + s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")) + "></img>";
			xx[2].getElementsByTagName ("IMG")[0].style.opacity = o;
			x.getElementsByTagName ("A")[0].href = next_partners[index - 1]._new.web;
			next_partners[index - 1].neg = -1;
		}
		if(next_partners[index - 1].control == 50) {
			next_partners[index - 1].control = 0;
			xx[2].innerHTML = "";
			xx[1].style.opacity = 1;
			xx[2].style.opacity = 1;
			xx[3].style.opacity = 1;
			if(next_partners[0].control + next_partners[1].control + next_partners[2].control + next_partners[2].control + next_partners[4].control == 0) {
				load_next_partners = window.setInterval (loadNextPartners, 25);
			}
		}
		else {
			switch (index) {
			case 1:
				partners_interval[index - 1] = window.setInterval (loadPartner1, 25);
				break;
			case 2:
				partners_interval[index - 1] = window.setInterval (loadPartner2, 25);
				break;
			case 3:
				partners_interval[index - 1] = window.setInterval (loadPartner3, 25);
				break;
			case 4:
				partners_interval[index - 1] = window.setInterval (loadPartner4, 25);
				break;
			case 5:
				partners_interval[index - 1] = window.setInterval (loadPartner5, 25);
				break;
			default:
				load_next_partners = window.setInterval (loadNextPartners, 25);
			}
		}
	}
}

function getEmptyPartners () {
	if(partners.length > 0) {
		var index = partner;
		partner = ((partner + 1) == partners.length) ? 0 : (partner + 1);
		return index;
	}
	else {
		return -1;
	}
}


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

function window_onload(hln_id, hln_interval, p_cl, p_id, p_interval) {
	initHeadlines(hln_id, hln_interval);
	initPartners(p_cl, p_id, p_interval);
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
