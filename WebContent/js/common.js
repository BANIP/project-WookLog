var AjaxRequest = (function () {
	/**
	 * 값이 undefined인지 아닌지 확인
	 * @param  {Object}  value 확인할 값
	 * @return {Boolean}       undefined일시 true 반환
	 */
	var isNull = function (value) {
		return typeof value == "undefined";
	}

	/**
	 * 액션객체의 클래스명을 통신 가능한 url로 변경
	 * @param  {String} name 액션 객체의 클래스명
	 * @return {String}      ajax로 통신하기 위한 url
	 */
	var getActionURL = function (name) {
		return name + ".do";
	}

	/**
	 * 간단한 파라미터를 $.ajax와 호환이 가능한 파라미터로 변경
	 * @param 파라미터를 변환할 객체, type, target, data 속성 필요
	 */
	var getAjaxParam = function (param) {
		param.url = getActionURL(param.target);
		if (isNull(param.dataType)) param.dataType = "json";
		if (isNull(param.error)) param.error = BoardException.printError;
		param.defaultSuccess = param.success;
		param.success = function (json) {
			if (json.statuscode != 0) {
				param.error(json);
			} else {
				param.defaultSuccess(json);
			}
		}

		return param;
	}

	/**
	 * ajax통신 실행
	 */
	var execute = function (currentParam) {
		let ajaxParam = getAjaxParam(currentParam);
		$.ajax(ajaxParam);
	}


	return execute;
})();

var EventController = (function () {

	var isDefaultEvent = function (eventName) {
		let defaultEventList = ["click", "load", "keydown"];
		return defaultEventList.includes(eventName);
	}

	var startLoadEvent = function (listener) {
		listener();
	}

	let eventBinding = {
		bindAll: function(data){
			let $obj = data.$obj;
			Object.keys(data.events).forEach(eventName => {
				let listener = data.events[eventName];
				this.bind( $obj,eventName,listener );
			})
		},
		bind: function( $obj,eventName,listener ){
			if (!isDefaultEvent(eventName)) return;
			if (eventName == "load") startLoadEvent(listener);
			$obj.on(eventName, listener);
		}
	}
	
	let mergeMethod = {
		merge : function(data, package ){
			Object.assign(data, package.getAll(data) )
		},
		mergeAll : function(data, packageList ){
			packageList.forEach(function(package){
				this.merge(data,package)
			})
		}
	}
	
	var rtn = {
		/**
		 * 돔 데이터들의 이벤트를 정의함
		 */
		defineAll : function (datas) {
			Object.keys(datas).forEach(dataName => {
				let data = datas[dataName];
				this.define(data);
			});
		},
		define : function (data) {
			let $obj = data.$obj;
			if (typeof data.events != "undefined"){
				eventBinding.bindAll(data);
			}
			if(typeof data.defaultMethod != "undefined"){
				mergeMethod.mergeAll(data, data.defaultMethod);
			}
		}
	}

	return rtn;
})();


var userManage = (function () {
	let tempUser = {

	}

	var localLogin = function (name) {
		var requestSuccess = function (json) {
			Object.keys(json.data.info).forEach(function (attr) {
				localStorage.setItem(attr, json.data.info[attr]);
			})
			localStorage.setItem("user_pwd", tempUser.pwd);
		}
		var param = {
			type: "GET",
			target: "UserGetUserAction",
			data: {
				user_name: name
			},
			success: requestSuccess
		}
		AjaxRequest(param)
	}

	var exception = {
		loginFail: function () {
			window.BoardException.print("로그인에 실패했습니다. 패스워드를 확인해주세요.");
		},

	}

	return {
		login: function (name, pwd) {
			tempUser = { name: name, pwd: pwd };
			var requestSuccess = function (json) {
				if (json.data.is_login_success) {
					localLogin(name);
				} else {
					exception.loginFail();
				}
			}
			var param = {
				type: "POST",
				target: "UserAuthAction",
				data: {
					user_name: name,
					user_pwd: pwd,
				},
				success: requestSuccess
			};
			AjaxRequest(param);
		},
		simpleLogin: function (name, pwd) {
			if (!this.isLogined() || this.getUser().name != name || this.getUser().pwd != pwd) {
				this.login(name, pwd);
				return true;
			}
			return false;
		},
		isLogined: function () {
			return localStorage.getItem("user_name") != null;
		},
		getUser: function () {
			let info = {
				name: localStorage.getItem("user_name"),
				pwd: localStorage.getItem("user_pwd"),
			};
			return (info.name && info.pwd) ? info : false;
		},
		isWriteable: function () {
			return localStorage.getItem("user_permission_write") === "true";
		},
		isRemoveable: function () {
			return localStorage.getItem("user_permission_remove") === "true";
		}
	}
})();

var methodManage = (function(){
	let methodList = {
		hide : function(){ this.$obj.hide() }, 
		show : function(){ this.$obj.show() },
		slideDown : function(){ this.$obj.slideDown() },
		slideUp : function(){ this.$obj.slideUp() },
	}
	return {
		get : function(ctx,methodName){
			return methodList[methodName].bind(ctx);
		}
	}
})();

let methodTemplet = (function(){
	let Package = (function(){
		
		let main = function(){
			this.methods = {};
		}
		
		let bindContext = {
			bindAll : function(methods,context){
				let bindedMethod = {};
				Object.keys(methods).forEach(function(key){
					bindedMethod[key] = bindContext.bind(methods[key], context )
				})
				return bindedMethod;
			},
			bind : function(method,context){
				return method.bind(context);
			},
		};
		
		main.prototype.add = function(name,method){
			this.methods[name] = method;
		};
		
		main.prototype.addAll = function(methods){
			Object.assign(this.methods, methods )
		}

		main.prototype.getAll = function(context){
			let methods = this.methods;
			return bindContext.bindAll(methods,context);
		}
		return main;
		
	})();
	return {
		show:function(){
			let package = new Package();
			package.addAll({
				show: function(){ 
					console.log(this); 
					this.$obj.show(); 
				},
				hide: function(){ this.$obj.hide(); },				
			});
			return package;
		},
		fade:function(){
			let package = new Package();
			package.addAll({
				show: function(){ this.$obj.fadeIn(); },
				hide: function(){ this.$obj.fadeOut(); },				
			});
			return package;
		},
		slide:function(){
			let package = new Package();
			package.addAll({
				show: function(){ this.$obj.slideDown(); },
				hide: function(){ this.$obj.slideUp(); },				
			});
			return package;
		}
	}
})();