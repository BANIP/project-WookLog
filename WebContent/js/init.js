
var system = (function () {
	
	let customInitDatas ={
		index : function(){
			BoardData.init(); 
		},
		upload : function(){
			uploadClientDOM.init();
		}
	}
	
	let gethtmlName = function(){
		let fileexr = /\/([a-zA-Z0-9]+)\.html/;
		let execResult = fileexr.exec(location.pathname);
		if(execResult == null ) return "index";
		return execResult[1];
	}
	
	let getcustomInit = function(){
		let fileName = gethtmlName();
		return customInitDatas[fileName];
	}

	
	var getDOMData = function () {
		return Object.keys(window).filter(key => key.split("DOM")[1] == "");
	}

	var allDOMInit = function () {
		getDOMData().forEach(key => window[key].init && window[key].init());
	}


	
	var rtn = {
		init: function () {
			let customFunction = getcustomInit();
			
			allDOMInit();
			customFunction();
		}
	}; 
	return rtn
})();

system.init();

   