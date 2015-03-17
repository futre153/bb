var INPUT = "input", HEAD = "head", SCRIPT = "script", TR = "tr", TD = "td", P = "p", H1 = "h1", BODY = "body", TABLE = "table", CAPTION = "caption", THEAD = "thead", TBODY = "tbody", TFOOT = "tfoot", TH = "th", DIV = "div";
var MAIN_TABLE_CLASS = "main-table", LOGIN_TABLE_CAPTION = "Prihlásenie&nbsp;do&nbsp;aplikácie&nbsp;SMS&nbsp;Norification&nbsp;Viewer";
var EMPTY = "", NUMBER = "#", LESS = "<", GREATHER = ">", SLASH = "/", DOT = ".";


var CSS_VA = "vertical-align", CSS_TA = "text-align", CSS_D = "display", CSS_P = "padding", CSS_MB = "margin-bottom", CSS_BGC = "background-color", CSS_W = "width", CSS_H = "height", CSS_M = "margin";
var ATT_ONL = "onload", ATT_ORS = "onresize", ATT_ID = "id", ATT_VAL = "value";

var URL_ID = "page_id";
var BODY_ONLOAD = "win_onload();", BODY_ONRESIZE = "resize();", BODY_WIDTH = "100%", BODY_HEIGHT = "100%", BODY_COLOR = "whitesmoke", BODY_MARGIN = "0px", BODY_PADDING = "0px";
var CONTAINER_ID = "container-id", CONTAINER_WIDTH = "100%", CONTAINER_HEIGHT = "auto", CONTAINER_COLOR = "lightblue";
var CONTENT_ID = "content-id", CONTENT_WIDTH = "100%", CONTENT_HEIGHT = "auto", CONTENT_COLOR = "blue";
var L_CONTENT_WIDTH = "20%", LEFT_HEIGHT = "auto", L_CONTENT_COLOR = "red", L_CONTENT_DISPLAY = "inline-block";
var C_CONTENT_VERTICAL_ALIGN = "middle", C_CONTENT_TEXT_ALIGN = "center", C_CONTENT_ID = "center-content-id", C_CONTENT_WIDTH = "60%", CENTER_HEIGHT = "auto", C_CONTENT_COLOR = "orange", C_CONTENT_DISPLAY = "inline-block";
var R_CONTENT_WIDTH = "20%", RIGHT_HEIGHT = "auto", R_CONTENT_COLOR = "purple", R_CONTENT_DISPLAY = "inline-block";

var HEADER_ID = "header-id", HEADER_COLOR = "blue", HEADER_TEXT_MARGIN_BOTTOM = "10px", HEADER_TEXT = "Welcome to PREPAID Card SMS Notification Viewer application";
var C_HEADER_DISPLAY = "inline-block";
var C_FOOTER_DISPLAY = "inline-block";

var FOOTER_ID = "footer-id", FOOTER_COLOR = "navy";

var LOGIN_URL = "", URL = "data/login.txt";
var USERNAME_KEY = "username", PASSWORD_KEY = "password", ACTION_KEY = "action";
var ACTION_LOGIN = "login";
var ELEMENT = null;


$(document).ready(setDocument(URL,"",BODY));
/*
$(document).ready(function () {
	$.post(URL, function (data) {
		window.status = setElement($(BODY), 0, JSON.parse(data));
		resize();
		$(DIV).animate({opacity: 1}, "slow");
		});
	});

*/
function setDocument(url, rData, elem) {
	ELEMENT = elem;
	$.post(url, rData, function (data) {
		//alert(data);
		var parsedData = JSON.parse(data);
		var index = setElement($(ELEMENT), 0, parsedData);
		index = setScripts(index, parsedData);
		resize();
		$(DIV).animate({opacity: 1}, "slow");
	});
};

setScripts = function(index, data) {
	var script, i, j = data[index];
	index ++;
	for(i = 0; i < j; i ++) {
		$.post(data[index], function(text) {
			script = $(LESS + SCRIPT + GREATHER + LESS + SLASH + SCRIPT + GREATHER).text(text);
			$(HEAD).append(script);
		});
		index ++;
	}
};

setElement = function(e, index, data) {
	var i, j = data[index], child;
	index ++;
	for(i = 0; i < j; i ++) {
		child = $(LESS + data[index] + GREATHER + LESS + SLASH + data[index] + GREATHER);
		index ++;
		e.append(child);
		index = setElement(child, index, data);
	}
	j = data[index];
	index ++;
	for(i = 0; i < j; i++) {
		e.attr(data[index], data[index + 1]);
		index += 2;
	}
	j = data[index];
	index ++;
	for(i = 0; i < j; i++) {
		e.css(data[index], data[index + 1]);
		index += 2;
	}
	if(data[index] != null) {
		e.append(data[index]);
	}
	index ++;
	return index;
};

size = function (w, h) {
	this.width = w;
	this.height = h;
};

detectSize = function(e) {
	return new size(e.width(), e.height());
};

c_size =  null;

resize = function() {
	var c = $(NUMBER + CONTAINER_ID);
	var w = $(window);
	var w_size = detectSize(w);
	window.onresize = null;
	if(c_size == null) {		
		c_size = detectSize(c);
	}
	if(c_size.height < w_size.height) {
		var x = w_size.height - $(NUMBER + HEADER_ID).height() - $(NUMBER + FOOTER_ID).height() - 1;
		$(NUMBER + CONTENT_ID).height(x);
		$(NUMBER + C_CONTENT_ID).height(x);
		c = $(NUMBER + "center-object");
		x = (x - c.height()) / 2;
		if(x > 0) {
			c.css("margin-top", x - x%2 + "px");
		}
	}
	window.onresize = resize;
};

login = function() {
	var user = null, pass = null;
	$(INPUT).each(function(index) {
		switch(index) {
		case 0:
			user = CryptoJS.SHA256($(this).val());
			break;
		case 1:
			pass = CryptoJS.SHA256($(this).val());
				
		}
	});
	/*setDocument(LOGIN_URL + "?" + 
			ACTION_KEY + "=" + ACTION_LOGIN + ";" + 
			USERNAME_KEY + "=" + user + ";" + 
			PASSWORD_KEY + "=" + pass, "", BODY);*/
	setDocument(LOGIN_URL,  
	ACTION_KEY + "=" + ACTION_LOGIN + "&" + 
	USERNAME_KEY + "=" + user + "&" + 
	PASSWORD_KEY + "=" + pass, "", BODY);
};