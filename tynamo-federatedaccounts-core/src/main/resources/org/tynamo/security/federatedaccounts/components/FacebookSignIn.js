function openFacebookAuthorizationWindow(url, width, height) {

	var left   = (screen.width  - width)/2;
	var top    = (screen.height - height)/2;
	var params = 'width='+width+', height='+height;
	params += ', top='+top+', left='+left;
	params += ', directories=no';
	params += ', location=no';
	params += ', menubar=no';
	params += ', resizable=no';
	params += ', scrollbars=no';
	params += ', status=no';
	params += ', toolbar=no';
	fbAuthWindow=window.open(url,'FBauthentication', params);
	if (window.focus) fbAuthWindow.focus();
	return false;
}

function openFacebookAuthorizationInline(url) {
	var oauthIFrame = $('oauthInlineFrame');
	oauthIFrame.src = url;
}
