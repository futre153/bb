/**
 * 
 */

function open_file (fil, mim) {
	var f = document.getElementsByName(fil)[0].files[0].type;
	var m = document.getElementsByName(mim)[0].value = f;
}