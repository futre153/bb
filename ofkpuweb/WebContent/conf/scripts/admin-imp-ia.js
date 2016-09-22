/**
 * 
 */

var tael;
var dfoe;
var dfie;
var dfel;
var dfcp;
var inin;
var arcp;
var ardc;
var arau;
var artx;
var arfl;
var arln;
var iurl;
var incp;
var inau;
var indc;
var inlm;
var inpu;
var inid;
var inpi;
var inte;
var inac;
var inti;
var insi;
var form;
var dfte;
var tate;
var pide;
var pitb;
var pigs;
var piag;
var ping;
var gaid;
var mgid;
var pdtt;
var emel;
var emte;
var save;
var coel;
var cote;
var cobu;
var coac;
var arte;
var artt;
var artn;
var artf;
var arpe;
var arpt;
var arpn;
var arpf;
var arxc;
var arxi;
var adte;
var adtt;
var adtn;
var adtf;
var adpe;
var adpt;
var adpn;
var adpf;
var adxi;
var acna;
var aclt;
var aclp;
var acdt;
var acdp;
var acsa;
var hpag;


function initDialogFrame(dialogFrameClass, dialogFrameCaptionId) {
	dfel = document.getElementsByClassName(dialogFrameClass)[0];
	dfcp = document.getElementById (dialogFrameCaptionId);
}
function init(dialogFrameClass, dialogFrameCaptionId, textareaId, textDialogFrameClass, textTextareaId, articleCaptionClass, articleCreationClass, articleAuthorClass, articleTextClass, articleFirstLineClass, articleLineClass, articleImageUrl, inputCaptionName, inputAuthotName, inputCreationName, inputModifiedName, inputPublishedName, inputIdName, inputPicturesName, inputTextName, inputActionName, inputArticleTmpId, inputArticlePublishedId, pictureDialogClass, pictureTBodyClass, pictureGalleriesSliderClass, pictureActiveGalleryClass, pictureNotActiveGalleryClass, pictureDeleteText, maxGalleries, errorMessageClass, errorMessageTextClass, errorMessage, confirmMessageClass, confirmMessageTextClass, confirmButtonClass, tmpArticlesClass, tmpArticlesName, tmpArticlesFunction, publishedArticlesClass, publishedArticlesName, publishedArticlesFunction, tmpDelArticlesClass, tmpDelArticlesName, tmpDelArticlesFunction, publishedDelArticlesClass, publishedDelArticlesName, publishedDelArticlesFunction, articlesSplitChar, articlesImageSrc, articlesDelImageSrc, actionNewArticle, actionLoadTmpArticle, actionLoadPublishedArticle, actionDelTmpArticle, actionDelPublishedArticle, actionSaveArticle, homePage) {
	initDialogFrame(dialogFrameClass, dialogFrameCaptionId);
	initTextarea (textareaId);
	initTextDialog (textDialogFrameClass, textTextareaId);
	initArticle(articleCaptionClass, articleCreationClass, articleAuthorClass, articleTextClass, articleFirstLineClass, articleLineClass, articleImageUrl);
	initInputs(inputCaptionName, inputAuthotName, inputCreationName, inputModifiedName, inputPublishedName, inputIdName, inputPicturesName, inputTextName, inputActionName, inputArticleTmpId, inputArticlePublishedId);
	initPictureGallery(pictureDialogClass, pictureTBodyClass, pictureGalleriesSliderClass, pictureActiveGalleryClass, pictureNotActiveGalleryClass, maxGalleries);
	initConfirmMessage(confirmMessageClass, confirmMessageTextClass, confirmButtonClass);
	initErrorMessage(errorMessageClass, errorMessageTextClass, errorMessage);
	initActions(actionNewArticle, actionLoadTmpArticle, actionLoadPublishedArticle, actionDelTmpArticle, actionDelPublishedArticle, actionSaveArticle);
	initTmpArticles (tmpArticlesClass, tmpArticlesName, tmpArticlesFunction, publishedArticlesClass, publishedArticlesName, publishedArticlesFunction, articlesSplitChar, articlesImageSrc);
	initDelTmpArticles (tmpDelArticlesClass, tmpDelArticlesName, tmpDelArticlesFunction, publishedDelArticlesClass, publishedDelArticlesName, publishedDelArticlesFunction, articlesDelImageSrc);
	reloadArticle();
	returnHTML(inte.value);
	hpag = homePage;
	save = 1;
}

function initActions(actionNewArticle, actionLoadTmpArticle, actionLoadPublishedArticle, actionDelTmpArticle, actionDelPublishedArticle, actionSaveArticle) {
	acna = actionNewArticle;
	aclt = actionLoadTmpArticle;
	aclp = actionLoadPublishedArticle;
	acdt = actionDelTmpArticle;
	acdp = actionDelPublishedArticle;
	acsa =actionSaveArticle;
}

function initConfirmMessage(confirmMessageClass, confirmMessageTextClass, confirmButtonClass) {
	coel = document.getElementsByClassName(confirmMessageClass)[0];
	cote = document.getElementsByClassName(confirmMessageTextClass)[0].getElementsByTagName("DIV")[0];
	cobu = document.getElementsByClassName(confirmButtonClass)[0].getElementsByTagName("BUTTON")[0];
}

function initErrorMessage(errorMessageClass, errorMessageTextClass, errorMessage) {
	emel = document.getElementsByClassName(errorMessageClass)[0];
	emte = document.getElementsByClassName(errorMessageTextClass)[0].getElementsByTagName("DIV")[0];
	if(errorMessage != null && errorMessage.length > 0) {
		dislayErrorMessage(errorMessage);
	}
}

function dislayErrorMessage(errorMessage) {
	emte.textContent = errorMessage;
	emel.style.display = "block";
}

function returnError() {
	emel.style.display = "";
}

function initPictureGallery(pictureDialogClass, pictureTBodyClass, pictureGalleriesSliderClass, pictureActiveGalleryClass, pictureNotActiveGalleryClass, pictureDeleteText, maxGalleries) {
	pide = document.getElementsByClassName(pictureDialogClass)[0];
	pitb = document.getElementsByClassName(pictureTBodyClass)[0];
	pigs = document.getElementsByClassName(pictureGalleriesSliderClass)[0];
	piag = pictureActiveGalleryClass;
	ping = pictureNotActiveGalleryClass;
	gaid = 0;
	mgid = maxGalleries;
	pdtt = pictureDeleteText;
}
function initTextDialog (textDialogFrameClass, textTextareaId) {
	dfte = document.getElementsByClassName(textDialogFrameClass)[0];
	tate = document.getElementById (textTextareaId);
}
function initInputs(inputCaptionName, inputAuthotName, inputCreationName, inputModifiedName, inputPublishedName, inputIdName, inputPicturesName, inputTextName, inputActionName, inputArticleTmpId, inputArticlePublishedId) {
	incp = document.getElementsByName(inputCaptionName)[0];
	inau = document.getElementsByName(inputAuthotName)[0];
	indc = document.getElementsByName(inputCreationName)[0];
	inlm = document.getElementsByName(inputModifiedName)[0];
	inpu = document.getElementsByName(inputPublishedName)[0];
	inid = document.getElementsByName(inputIdName)[0];
	inpi = document.getElementsByName(inputPicturesName)[0];
	inte = document.getElementsByName(inputTextName)[0];
	inac = document.getElementsByName(inputActionName)[0];
	inti = document.getElementsByName(inputArticleTmpId)[0];
	insi = document.getElementsByName(inputArticlePublishedId)[0];
	form = document.getElementsByTagName("FORM")[0];
}
function initArticle(articleCaptionClass, articleCreationClass, articleAuthorClass, articleTextClass, articleFirstLineClass, articleLineClass, articleImageUrl) {
	arcp = document.getElementsByClassName(articleCaptionClass)[0];
	ardc = document.getElementsByClassName(articleCreationClass)[0];
	arau = document.getElementsByClassName(articleAuthorClass)[0];
	artx = document.getElementsByClassName(articleTextClass)[0];
	arfl = articleFirstLineClass;
	arln = articleLineClass;
	iurl = articleImageUrl;
}
function reloadArticle() {
	arcp.textContent = incp.value;
	ardc.textContent = indc.value;
	arau.textContent = inau.value;
}

function initTextarea(textareaId) {
	tael = document.getElementById(textareaId);
}

function editText() {
	dfte.style.display = "block";
	tate.textContent = getText();
	save = 0;
}

function getText () {
	var text = "";
	var elems = artx.getElementsByTagName("DIV");
	var i = 0;
	for(;i < elems.length; i ++) {
		if(i > 0) {
			text += "\r\n";
		}
		text += elems[i].innerHTML;
	}
	return text;
}

function returnTextDialogValue() {
	return returnHTML(tate.textContent.trim());
}

function returnHTML (text) {
	text = text == null ? "" : text;
	var pics = inpi.value != null && inpi.value.length > 0 ? inpi.value.split(",") : [];	
	var line;
	var out = "";
	var i = 0;
	var j = 0;
	while (text.length != 0 || i < pics.length) {
		line = "";
		if(text.length != 0) {
			var index = text.indexOf("\n");
			if(index >= 0) {
				line = text.substring(0, index).trim();
				text = text.substring(index + 1, text.length).trim();
			}
			else {
				line = text;
				text = "";
			}
		}
		if(line.length == 0 && text.length > 0) {
			continue;
		}
		if(line.length > 0) {
			out+="<div class=\"" + (j == 0 ? arfl : arln)  + "\">" + (j == 0 ? "&#160;&#160;&#160;&#160;" : "") + line + "</div>";
			j ++;
		}
		if(i < pics.length) {
			out += "<img src=\"" + iurl + pics[i] + "\" title=\"" + pdtt + "\" onclick=\"removePicture(" + pics[i] + ")\">";
			i ++;
		}
	}
	//window.alert(out);
	artx.innerHTML = out;
	dfte.style.display = "";
}

function dialogTextarea(len) {
	dfel.style.display = "block";
	tael.textContent = dfie.textContent;
	tael.maxLength = len;
	dfoe = dfie;
	dfie = tael;
}
function editAuthor (editedId, inputName, x, caption) {
	editCaption (editedId, inputName, x, caption);
	save = 0;
}
function editCaption (editedId, inputName, x, caption) {
	dfcp.textContent = caption;
	dfie = document.getElementById (editedId);
	inin = document.getElementsByName(inputName)[0];
	dialogTextarea(x);
	save = 0;
}
function returnDialogValue() {
	dfoe.textContent = dfie.textContent;
	inin.value = dfie.textContent;
	dfel.style.display = "";
	reloadArticle();
}
function removePicture(id) {
	var pics = inpi.value != null && inpi.value.length > 0 ? inpi.value.split(",") : [];
	var val = "";
	var i = 0;
	var j = 0;
	for (; i < pics.length; i ++) {
		if(pics[i] != id) {
			val += (id == 0 ? "" : ((j > 0 ? "," : "") + pics[i]));
			j ++;
		}
	}
	inpi.value = val;
	returnHTML(getText());
	save = 0;
}

function addPicture(id) {
	if(id == 0) {
		pigs.innerHTML = getPictureSliderHTML(mgid, gaid, "setExactGallery");
		pitb.innerHTML = getPictureTBodyHTML(gaid, 5, 10, "addPicture");
		pide.style.display = "block";
	}
	else {
		var pics = inpi.value != null && inpi.value.length > 0 ? inpi.value.split(",") : [];
		var i = 0;
		for(; i < pics.length; i ++) {
			if(pics[i] == id) {
				break;
			}
		}
		if(i == pics.length) {
			inpi.value = inpi.value == null || inpi.value.length == 0 ? id : (inpi.value + "," + id);
			returnHTML(getText());
			save = 0;
		}
		pide.style.display = "";
	}
}

function setExactGallery(id) {
	gaid = id - 1;
	pigs.innerHTML = getPictureSliderHTML(mgid, gaid, "setExactGallery");
	pitb.innerHTML = getPictureTBodyHTML(gaid, 5, 10, "addPicture");
}

function getPictureTBodyHTML(id, li, lj, fn) {
	var html="";
	var i = 0;
	for(; i < li; i ++) {
		html += "<tr>";
		var j = 0;
		for(; j < lj; j++) {
			html += ("<td><div onclick=\"" + fn + "(" + (id * li * lj + ((i * lj) + (j + 1))) + ");\"><img src=\"" + iurl + (id * li * lj + (((i * lj) + (j + 1)))) + "\"></div></td>");
		}
		html += "</tr>";
	}
	//window.alert (html);
	return html;
}

function setLowGallery() {
	if(gaid > 0) {
		gaid --;
		pigs.innerHTML = getPictureSliderHTML(mgid, gaid, "setExactGallery");
		pitb.innerHTML = getPictureTBodyHTML(gaid, 5, 10, "addPicture");
	} 
}

function setHighGallery() {
	if(gaid < mgid) {
		gaid ++;
		pigs.innerHTML = getPictureSliderHTML(mgid, gaid, "setExactGallery");
		pitb.innerHTML = getPictureTBodyHTML(gaid, 5, 10, "addPicture");
	} 
}

function getPictureSliderHTML(l, id, fn) {
	var html = "";
	var i = 0;
	for(; i < l; i ++) {
		if(i > 0) {
			html += "&#160;";
		}
		html += ("<span onclick =\"" + fn + "(" + (i + 1) + ");\"  class=\"" + (i == id ? (piag) : (ping)) + "\">" + (i + 1) + "</span>");
	}
	//window.alert (html);
	return html;
}
function setConfirm(fn, buttonText, msg) {
	//alert (save);
	if(save == 1) {
		fn();
	}
	else {
		coac = fn;
		cobu.textContent = buttonText;
		cote.textContent = msg;
		coel.style.display = "block";
	}
}
function returnNotConfirm() {
	coel.style.display = "";
}
function returnConfirm() {
	coel.style.display = "";
	coac();
}
function newArticle() {
	//window.alert("new Article is running");
	inac.value = acna;
	form.submit();
}

function loadArticle() {
	arte.style.display = "block";
}

function loadPubArticle() {
	arpe.style.display = "block";
}

function delArticle() {
	adte.style.display = "block";
}

function delPubArticle() {
	adpe.style.display = "block";
}

function loadPublishedArticle(id) {
	inac.value = aclp;
	insi.value = id;
	form.submit();
}

function saveArticle() {
	inac.value = acsa;
	inte = getText();
	form.submit();
}

function delTempArticle(id) {
	inac.value = acdt;
	inti.value = id;
	form.submit();
}

function delPublishedArticle(id) {
	inac.value = acdp;
	insi.value = id;
	form.submit();
}



function loadTempArticle(id) {
	inac.value = aclt;
	inti.value = id;
	form.submit();
}

function cancelLoadPublished() {
	arpe.style.display = "";
}

function cancelLoadTemp() {
	arte.style.display = "";
}

function cancelDelPublished() {
	adpe.style.display = "";
}

function cancelDelTemp() {
	adte.style.display = "";
}


function initTmpArticles (tmpArticlesClass, tmpArticlesName, tmpArticlesFunction, publishedArticlesClass, publishedArticlesName, publishedArticlesFunction, articlesSplitChar, articlesImageSrc) {
	arte = document.getElementsByClassName(tmpArticlesClass)[0];
	artt = arte.getElementsByTagName("TABLE")[0];
	artn = tmpArticlesName;
	artf = tmpArticlesFunction;
	arpe = document.getElementsByClassName(publishedArticlesClass)[0];
	arpt = arpe.getElementsByTagName("TABLE")[0];
	arpn = publishedArticlesName;
	arpf = publishedArticlesFunction;
	arxc = articlesSplitChar;
	arxi = articlesImageSrc;
	loadArticlesTable (artt, artn, artf, arxc, arxi);
	loadArticlesTable (arpt, arpn, arpf, arxc, arxi);
}

function initDelTmpArticles (tmpDelArticlesClass, tmpDelArticlesName, tmpDelArticlesFunction, publishedDelArticlesClass, publishedDelArticlesName, publishedDelArticlesFunction, articlesDelImageSrc) {
	adte = document.getElementsByClassName(tmpDelArticlesClass)[0];
	adtt = adte.getElementsByTagName("TABLE")[0];
	adtn = tmpDelArticlesName;
	adtf = tmpDelArticlesFunction;
	adpe = document.getElementsByClassName(publishedDelArticlesClass)[0];
	adpt = adpe.getElementsByTagName("TABLE")[0];
	adpn = publishedDelArticlesName;
	adpf = publishedDelArticlesFunction;
	adxi = articlesDelImageSrc;
	loadArticlesTable (adtt, adtn, adtf, arxc, adxi);
	loadArticlesTable (adpt, adpn, adpf, arxc, adxi);
}

function loadArticlesTable (table, name, fn, chr, imageSrc) {
	var tbody = table.getElementsByTagName("TBODY")[0];
	var inputs = document.getElementsByName(name);
	var i;
	var html = "";
	for(i = 0; i < inputs.length; i ++) {
		var b64 = inputs[i].value.split(chr);
		var id = convertToUtf8 (atob(b64[0]));
		var caption = convertToUtf8 (atob(b64[1]));
		var author = convertToUtf8 (atob(b64[2]));
		var date = convertToUtf8 (atob(b64[3]));
		html += ("<tr><td>" + id + "</td><td>" + caption + "</td><td>" + author + "</td><td>" + date + "</td><td><img src=\"" + imageSrc + "\" onclick=\"" + fn.replace(chr, id) + ";\"></img></td></tr>");
	}
	tbody.innerHTML = html;
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
function closeApp() {
	window.location.replace(hpag);
}