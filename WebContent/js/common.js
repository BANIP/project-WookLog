let debug = {
		isdebug : true,
		printSendParam : function(param){
			if(this.isdebug) console.log("send : ",param);
		},
		printReceiveParam: function(param){
			if(this.isdebug) console.log("receive : ",param);			
		}
}
 
let ParameterData = (function(){
	let ParameterData = function(data){
		if(typeof data == "undefined") this.data = {};
		  else this.data = data;
	}

	ParameterData.prototype.get = function(){ return this.data };
	
	ParameterData.prototype.set = function(data){
		this.data = data;
		return this;
	}

	ParameterData.prototype.add = function(name, value){
		this.data[name] = value;
		return this;
	}
	
	ParameterData.prototype.addByValue = function(name, $dom){
		this.data[name] = $dom.val();
		return this;
	}
	
	ParameterData.prototype.addByhtml = function(name, $dom){
		this.data[name] = $dom.html();
		return this;
	}

	ParameterData.prototype.addBytext = function(name, $dom){
		this.data[name] = $dom.text();
		return this;
	}
	
	ParameterData.prototype.addAll = function(dataList){
		Object.assign( this.data, dataList );
		return this;
	}

	ParameterData.prototype.addUser = function(){
		if (userManage.isLogined()) {
			let user = userManage.getUser();
			this.data.user_name = user.name;
			this.data.user_pwd = user.pwd;
		}
	}


	return ParameterData;
})();

let RequestParameter = (function(){

	let main = function(){
		this.core = {};
		this.core.data = new ParameterData();
		return this;
	}

	/**
	 * ajax와 호환 가능한 파라미터를 얻음
	 * @return {Object} $.ajax로 전송할 수 있는 객체
	 */
	main.prototype.getSendableParam = function () {
		
		let thisParam = this.getCore();

		let ctx = this;
		let requestSuccessListener = function (json) {
			debug.printReceiveParam(json);
			if (json.statuscode != 0) {
				ctx.getCore().error(json);
			} else {
				ctx.getCore().success(json);
			}
		}
		
		checkNull( thisParam.type, "HTTP 프로토콜 타입이 입력되지 않았습니다.");
		checkNull( thisParam.url, "통신 성공 콜백 함수가 정의되지 않았습니다.");

		thisParam.error = thisParam.error || BoardException.printError;
		
		return {
			type : thisParam.type,
			url : thisParam.url,
			success : requestSuccessListener,
			error : thisParam.error,
			dataType : "json",
			data : this.getData().get(),
		}
	}

	/** 
	 * 값이 undefined인지 아닌지 확인
	 * @param  {*}  value 확인할 값
	 * @return {Boolean}       undefined일시 true 반환
	 */
	let isNull = function (value) {
		return typeof value == "undefined";
	}

	/**
	 * 값이 Null이면 오류 출력
	 * @param {*} target null의 여부를 확인할 값
	 * @param {String} message null일시 출력되는 오류 메세지
	 */
	let checkNull = function(target,message){
		if(isNull(target)) throw Error(message);
	}

	main.prototype.getCore = function(){
		return this.core;
	}

	main.prototype.setTarget = function(target){
		this.core.url = _getActionURL(target);
		return this;
	}

	main.prototype.getData = function(){
		return this.core.data;
	}

	/**
	 * 액션객체의 클래스명을 통신 가능한 url로 변경
	 * @param  {String} name 액션 객체의 클래스명
	 * @return {String}      통신 할 수 있는 url
	 */
	var _getActionURL = function (name) {
		return name + ".do";
	}

	main.prototype.setType = function(type){
		this.core.type = type;
		return this;
	}
	main.prototype.addSuccessCallback = function(listener){
		this.core.success = listener
		return this;
	}
	main.prototype.addErrorCallback = function(listener){
		this.core.error = listener
		return this;
	}
	main.prototype.send = function(){
		let param = this.getSendableParam();
		debug.printSendParam( param );
		$.ajax(param);
		return this;
	}

	return main;
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

	let saveLocalUserInfo = function(info){
		info.user_name = tempUser.name
		info.user_pwd = tempUser.pwd
		localStorage.setItem("user",JSON.stringify(info));
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
				saveLocalUserInfo(json.data);
			}
			
			let param = new RequestParameter();
			
			param.setType("POST")
			.setTarget("UserAuthAction")
			.addSuccessCallback(requestSuccess)
			.addErrorCallback(exception.loginFail)
			.getData()
			  .add("user_name",name)
			  .add("user_pwd",pwd);
	
			param.send();

		},
		simpleLogin: function (name, pwd) {
			if (!this.isLogined() || this.getUser().name != name || this.getUser().pwd != pwd) {
				this.login(name, pwd);
				return true;
			}
			return false;
		},
		isLogined: function () {
			return this.getUser() != false;
		},
		getUser: function () {
			let bean = this.getBean();

			let info = {
				name: bean.user_name,
				pwd: bean.user_pwd,
			};

			return (info.name && info.pwd) && info;
		},
		isWriteable: function () {
			let bean = this.getBean();
			return bean && bean.user_permission_write;
		},
		isRemoveable: function () {
			let bean = this.getBean();
			return bean && bean.user_permission_remove;
		},
		getBean: function(){
			return JSON.parse(localStorage.getItem("user"));
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