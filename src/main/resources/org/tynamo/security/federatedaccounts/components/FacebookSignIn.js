function openFacebookAuthorizationWindow(url) {
	var width  = 800;
	var height = 400;
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
	var parentDiv = oauthIFrame.up('div');
	
	if (typeof(parentdiv) != undefined) parentDiv.setStyle({width: '800px', height : '400px', display : 'block'});
	oauthIFrame.src = url;
}
