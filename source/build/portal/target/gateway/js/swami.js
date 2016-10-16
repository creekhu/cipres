
var newwindow

function popitupwithsizes(url, xsize, ysize, x, y) {
    if (newwindow)
	newwindow.close();
	
    newwindow=window.open(url,'help','width=' + xsize + ',height=' + ysize + ',scrollbars=yes,menubar=no,resizable=yes,toolbar=yes,status=no');
    newwindow.moveTo(x, y)
}

function onLoad()
{
}

function trimString(str)
{
    if (str == null)
        return "";
        
    var i = 0;
    
    for (i = 0; i < str.length && str.charAt(i) == ' '; i++);
    
    if (i == str.length)
        return "";
        
    str = str.substr(i);
    
    for (i = str.length - 1; i >= 0 && str.charAt(i) == ' '; i--);
    
    str = str.substr(0, i + 1);
    return str;
}

		
		