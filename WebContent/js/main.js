/**
 * jquery 확장
 * @returns
 */
(function(jQuery){
	jQuery.fn.extend({
		responsiveHide: function(direction){

		},
		responsiveShow: function(direction){

		}
	});
}($));

var BoardListDOM = (function () {

	var wrapDatas = {
		wrap: {
			$obj : $("#navWrap"),
			hide : function(){ 
				this.$obj.addClass("hide_responsive");
			},
			show : function(){ 
				this.$obj.removeClass("hide_responsive");
				BoardContentDOM.wrapDatas.wrap.hide();
				BoardContentDOM.wrapDatas.prev.hide();
			},
		},
	}
	
	var boardListDatas = {
		wrap: {
			$obj: $("#navContainer"),
			events: {
				click: function () {
					if (categoryDatas.wrap.isOpen()) categoryDatas.wrap.hide();

				}
			}
		},
		ul: {
			$obj: $("#navContent"),
			add: function (boardBean) {
				let boardList = new listTemplet.BoardList();
				boardList.setByBean(boardBean);


				this.$obj.append(boardList.domElement);
			},
			addAll: function(beanList){
				beanList.forEach(function (boardBean) {
					boardListDatas.ul.add(boardBean);
				})
			},
			reset: function () { this.$obj.html("") }
		},
		alert: {
			$obj: $("#navContainer > .wrap_alert"),
			isShow: function () {
				return this.$obj.css("display") !== "hide";
			},
			setMessage: function (message) {
				this.$obj.find(".text_message").text(message);
			},
			show: function (message) {
				this.setMessage(message)
				this.$obj.slideDown();
			},
			hide: function () {
				this.$obj.slideUp();
			}
		}
	}

	var headerDatas = {
		wrap: {
			$obj: $("#navHeader"),
			setBright: function () {
				headerDatas.wrap.$obj.addClass("prime");
			},
			setDark: function () {
				headerDatas.wrap.$obj.removeClass("prime");
			},
			hideChild: function () {
				$("#navHeader > .flow").slideUp();
			}
		},
		mainWrap: {
			$obj: $("#navHeaderDefault"),
			hide: function () {
				this.$obj.slideUp();
			},
			show: function () {
				headerDatas.wrap.hideChild();
				this.$obj.slideDown();
			}
		},
		searchWrap: {
			$obj: $("#navHeaderSearch"),
			hide: function () {
				this.$obj.slideUp();
			},
			show: function () {
				headerDatas.wrap.hideChild();
				this.$obj.slideDown();
			}
		},
		mainIconPageSelect: {
			$obj: $("#navHeaderDefault .area_icon_pageselect"),
			events: {
				click: function (event) {
					if (categoryDatas.wrap.isOpen()) {
						categoryDatas.wrap.hide();
					} else {
						categoryDatas.wrap.show();
					}

				},
			}
		},
		mainTextTitle: {
			$obj: $("#navHeaderDefault .text_title"),
			set: function (title) {
				this.$obj.text(title);
			}
		},
		mainIconWrite: {
			$obj: $("#navHeaderDefault .area_icon_write"),
			events: {
				click: function (event) {
					let writedom = etcDOM.boardWriteDatas;
					writedom.wrap.show();
					writedom.submit.setClickEvent("clickWrite");
					if (!writedom.categorySelect.isInit) {
						writedom.categorySelect.init(BoardData.category);
						writedom.categoryOption.domreset();
					}

				},
				load: function (event) {
					let $this = headerDatas.mainIconWrite.$obj;
					if (userManage.isWriteable()) $this.show();
				}
			}
		},
		mainIconSearch: {
			$obj: $("#navHeaderDefault .area_icon_search"),
			events: {
				click: function (event) {
					headerDatas.searchWrap.show();
				}
			}
		},
		SearchiconSearch: {
			$obj: $("#navHeaderSearch .area_icon_search"),
			events:{
				click:function(){
					let categoryID = BoardData.category.info.category_id;
					let searchWord = headerDatas.SearchinputSearch.get();
					BoardData.load.boardList( categoryID,0, searchWord);
				}
			},
			show: function(){ this.$obj.fadeIn(); },
			hide: function(){ this.$obj.fadeOut(); },
		},
		SearchiconSpinner: {
			$obj: $("#navHeaderSearch .area_icon_spinner"),
			show: function(){
				headerDatas.SearchiconSearch.hide();
				this.$obj.fadeIn();
			},
			hide: function(){
				headerDatas.SearchiconSearch.hide();
				this.$obj.fadeOut();
			}
		},
		SearchinputSearch: {
			$obj: $("#navHeaderSearch .inp_search"),
			events : {
				keypress:function(event){
					if(event.key == "Enter"){
						headerDatas.SearchiconSearch.events.click()	
					};
					if(event.key == "Escape"){
						headerDatas.mainWrap.show();
					}
				},
			},
			get: function(){ return $obj.val(); },
		},
		SearchiconClose: {
			$obj: $("#navHeaderSearch .area_icon_close"),
			events: {
				click: function (event) {
					headerDatas.mainWrap.show();
				}
			}
		}
	}

	var categoryDatas = {
		ul: {
			$obj: $("#categoryContent"),
			/**
			 * 카테고리 노드 하나를 리스트에 추가함
			 * 카테고리 클릭이벤트를 설정함
			 */
			addCategory: function (data, depth) {
				let $ul = categoryDatas.ul.$obj;
				let category = new listTemplet.CategoryList();

				category.setByBean( data.info ).setDepth(data.info, depth);
				$ul.append(category.domElement);
				
				data.child.forEach(child => {
					this.addCategory(child, depth + 1)
				})
			},
			reset: function () { this.$obj.html(""); }
		},
		wrap: {
			$obj: $("#categoryWrap"),
			isOpen: function () {
				return this.$obj.css("display") !== "none";
			},
			show: function () {
				headerDatas.wrap.setBright();
				this.$obj.slideDown();
			},
			hide: function () {
				headerDatas.wrap.setDark();
				this.$obj.slideUp();
			}
		}
	}

	var rtn = {
		init: function () {
			EventController.defineAll(headerDatas);
			EventController.defineAll(boardListDatas);
		},
		reload: {
			category: function (data) {
				if (categoryDatas.wrap.isOpen()) categoryDatas.wrap.hide();
				headerDatas.mainTextTitle.set(data.info.category_name);
				categoryDatas.ul.addCategory(data, 0);
			},
			boardList: function (data) {
				if (categoryDatas.wrap.isOpen()) categoryDatas.wrap.hide();
				if (boardListDatas.alert.isShow()) boardListDatas.alert.hide();
				boardListDatas.ul.reset();
				boardListDatas.ul.addAll(data.list);

			},
		},
		headerDatas,
		categoryDatas,
		boardListDatas,
		wrapDatas,
	}

	return rtn;
})();
 
var BoardContentDOM = (function () {
	var wrapDatas = {
			wrap: {
				$obj : $("#contentWrap"),
				hide : function(){ 
					this.$obj.addClass("hide_responsive");

				},
				show : function(){ 
					this.$obj.removeClass("hide_responsive");
					wrapDatas.prev.show();
					BoardListDOM.wrapDatas.wrap.hide();
				},
			},
			prev: {
				$obj : $("#showListBtn"),
				events: {
					click: function(){
						BoardListDOM.wrapDatas.wrap.show();
					}
				},
				show: function(){ 
					this.$obj.removeClass("hide");
					},
				hide: function(){ 
					this.$obj.addClass("hide");
				},
			}
	}

	var boardDatas = {
		wrap: {
			$obj: $("#contentContainer"),
			hide: function(){ this.$obj },
			show: function(){ this.$obj },
			set: function (data) {
				let $obj = this.$obj;
				$obj.find(".text_hit").text(data.board_hit);
				$obj.find(".text_category").text(data.board_category_name);
				$obj.find("#contentTitle").html(data.board_title);
				$obj.find(".text_like").text(data.board_like);
				$obj.find(".text_date_created").text(data.board_date_create);
				$obj.find(".text_date_modified").text(data.board_date_modify);
				$obj.find("#contentBody").html(data.board_content);
			},
			isModifiable: function () {
				return userManage.getUser().name == BoardData.board.board_user_name;
			},
			isRemovable: function () {
				return userManage.isRemoveable();
			}
		},
		removeIcon: {
			$obj: $("#contentHeader .area_icon_remove"),
			events: {
				click: function (event) {
					let message = "이 글을 삭제합니다. <br /> 계속 진행하시겠어요?"
					let successCallback = function(){
						BoardData.load.remove( BoardData.board.board_id );
					}

					etcDOM.confirmDatas.wrap.show();
					etcDOM.confirmDatas.wrap.set(message, "삭제", successCallback);

				}
			}
		},
		modifyIcon: {
			$obj: $("#contentHeader .area_icon_modify"),
			events: {
				click: function (event) {
					let writedom = etcDOM.boardWriteDatas;
					if(!writedom.categorySelect.isInit) writedom.categorySelect.init( BoardData.category );
					writedom.wrap.show();					
					writedom.wrap.set( BoardData.board );
					writedom.submit.setClickEvent("clickModify");
				}
			}
		}

	}
	var boardCmtDatas = {
		wrap: {
			$obj: $("#commentListWrap"),
		},
		count: {
			$obj: $("#commentListWrap .text_count"),
			set: function (count) {
				this.$obj.text(count);
			},
			plus: function (count) {
				this.$obj.text((i, value) => parseInt(value || 0) + 1);
			},
			minus: function(count){
				this.$obj.text((i, value) => parseInt(value) - 1);
			}
		},
		ul: {
			$obj: $("#commentListContainer"),
			unshift: function(dataParam) {
				boardCmtDatas.count.plus();
				
				let comment = new listTemplet.CommentList();
				comment.setByBean( dataParam.get() );
	
				comment.addAndShow( this.$obj );
			},
			shift: function(){
				boardCmtDatas.count.minus();
				this.$obj.find("li").eq(0).instance.remove();
			},
			add: function(bean) {
				let comment = new listTemplet.CommentList();
				comment.setByBean(bean);
				comment.addAndShow( this.$obj );
			},
			reset: function() {
				this.$obj.html("");
			},
			addAll: function(beanList) {
				beanList.forEach(bean => this.add(bean));
			}
		},
	}
	var boardCmtWriteDatas = {
		wrap: {
			$obj: $("#commentWriteWrap"),
			getReplyDataSend: function () {
				let dom = boardCmtWriteDatas;
				return {
					reply_content: dom.content.get(),
					board_id: BoardData.board.board_id,
					user_name: dom.name.get(),
					user_pwd: dom.pwd.get(),
				}
			},
			getReplyDataPrepend: function () {
				let dom = boardCmtWriteDatas;
				let dataParam = new ParameterData();
				dataParam.add("reply_content", dom.content.get() )
					.add("reply_date", new Date().toLocaleString() )
					.add("reply_user_name", dom.name.get() );
				
				return dataParam;
			},
			isFilledInput: function () {
				let dom = boardCmtWriteDatas;
				let valueList = [dom.name.get(), dom.pwd.get(), dom.content.get()];
				return valueList.every(v => v.length != 0);
			},
			events: {
				load: function (e) {
					let dom = boardCmtWriteDatas;
					// 로그인 되어있을 시 이름과 비밀번호 입력
					if (userManage.isLogined()) {
						let user = userManage.getUser();
						dom.name.set(user.name);
						dom.pwd.set(user.pwd);
					}
				}
			}
		},
		name: {
			$obj: $("#commentWriteWrap .inp_name"),
			get: function () { return this.$obj.val() },
			set: function (value) { return this.$obj.val(value) },
		},
		pwd: {
			$obj: $("#commentWriteWrap .inp_password"),
			get: function () { return this.$obj.val() },
			set: function (value) { return this.$obj.val(value) },
		},
		content: {
			$obj: $("#commentWriteWrap .inp_descript"),
			get: function () { return this.$obj.val() },
			reset: function () { this.$obj.val("") },
			events : {
				keydown : function(event){
					let submitClickEvent = boardCmtWriteDatas.submit.events.click;
					if(event.key == "Enter") submitClickEvent();
				}
			}
		},
		submit: {
			$obj: $("#commentWriteWrap .btn_submit"),
			events: {
				click: function () {
					let dom = boardCmtWriteDatas;
					let sendData = dom.wrap.getReplyDataSend();
					let unshiftData = boardCmtWriteDatas.wrap.getReplyDataPrepend();

					if (!dom.wrap.isFilledInput()) {
						BoardException.print("덧글을 작성하기 위해 빈칸을 모두 써주세요.", "fa-pencil");
						return false;
					}

					boardCmtDatas.ul.unshift(unshiftData);
					BoardData.load.commentwrite(sendData);
				},
			},

		},
	}

	return {
		init: function () {
			EventController.defineAll(wrapDatas);
			EventController.defineAll(boardDatas);
			EventController.defineAll(boardCmtDatas);
			EventController.defineAll(boardCmtWriteDatas);

		},
		reload: {
			content: function (data) {
				boardDatas.wrap.set(data);
				BoardContentDOM.wrapDatas.wrap.show();
			},
			comment: function (data) {
				boardCmtDatas.ul.reset();
				boardCmtDatas.ul.addAll(data.list);
				boardCmtDatas.count.set(data.list.length);
			}
		},
		boardCmtWriteDatas,
		boardCmtDatas,
		boardDatas,
		wrapDatas,
	}
	
})();

var etcDOM = (function () {
	var boardWriteDatas = {
		wrap: {
			$obj: $("#boardWriteWrap"),
			show: function () {
				etcDatas.fadeIn.show();
				this.keydown.bind();
				this.$obj.slideDown();
			},
			hide: function () {
				etcDatas.fadeIn.hide();
				this.keydown.unbind();
				this.$obj.slideUp();
			},
			set: function(data){
				let dom = boardWriteDatas;
				dom.title.set(data.board_title);
				dom.content.set(data.board_content);
				dom.categorySelect.select( data.board_category_name );
			},
			reset: function (){
				let dom = boardWriteDatas;
				dom.title.reset();
				dom.content.reset();
			},
			destroy: function(){
				this.reset();
				this.hide();
			},
			getWriteData: function () {
				var user = userManage.getUser();
				var dom = boardWriteDatas;
				return {
					board_title: dom.title.$obj.val(),
					board_content: dom.content.$obj.val(),
					category_id: dom.categoryOption.$obj.filter(":selected").data("id"),
					user_pwd: user.pwd,
					user_name: user.name,
				};
			},
			getModifyData: function(){
				var user = userManage.getUser();
				var dom = boardWriteDatas;
				return {
					board_title: dom.title.$obj.val(),
					board_content: dom.content.$obj.val(),
					board_id: BoardData.board.board_id,
					user_pwd: user.pwd,
					user_name: user.name,
					category_id: dom.categorySelect.getID(),
				};
			},
			keydown:{
				listener: function(event){
					if(event.key == "Escape") boardWriteDatas.wrap.hide();
					
				},
				bind: function(){ 
					$("body").on("keydown",this.listener);
				},
				unbind: function(){
					$("body").off("keydown",this.listener);
				}
			},
		},
		title: {
			$obj: $("#boardWriteWrap .inp_title"),
			reset: function () {
				this.$obj.text();
			},
			set: function(value){ this.$obj.val(value)},
		},
		close: {
			$obj: $("#boardWriteWrap .area_close"),
			events: {
				click: function () {
					boardWriteDatas.wrap.hide();
				}
			},
		},
		content: {
			$obj: $("#boardWriteWrap .inp_descript"),
			reset: function () {
				this.$obj.text();
			},
			set: function(value){ this.$obj.val(value)},
		},
		categorySelect: {
			$obj: $("#boardWriteWrap .board_category"),
			isInit: false,
			init: function (category) {
				this.isInit = true;
				const dom = boardWriteDatas;
				const $option = dom.categoryOption.get(category.info);
					this.addOption($option);
	
					// 자식요소 재귀로 추가
					if (category.child && category.child.length != 0) {
						category.child.forEach(child => this.init(child));
					}
	
	
				},
			addOption: function ($obj) {
				this.$obj.append($obj);
			},
			setThisCategory: function () {
				let categoryName = BoardData.category.info.category_name;
				this.select( categoryName );
			},
			select : function( categoryName ){
				this.$obj.val(categoryName).attr("selected","selected");
			},
			getID : function(){
				this.$obj.find(":selected").data("id");
			}
		},
		categoryOption: {
			$obj: $("#boardWriteWrap .board_category option"),
			get: function (info) {
				let $option = $("<option></option>");
				$option.data("id", info.category_id);
				$option.text(info.category_name);
				return $option;
			},
			domreset: function () {
				this.$obj = $("#boardWriteWrap .board_category option");
			}
		},
		submit: {
			$obj: $("#boardWriteWrap .btn_boardwrite"),
			events: {
				click: function (event) {
					boardWriteDatas.submit.events.clickDefault(event);
				},
				clickDefault: function (event) {
					BoardException.print("서버의 응답을 기다리는 중입니다. <br />잠시만 기다려주세요..", "fa-send");
				},
				clickWrite: function (event) {
					let dom = boardWriteDatas;
					if (!userManage.isLogined()) {
						BoardException.print("로그인이 필요합니다.");
						return;
					} else if (!userManage.isWriteable()) {
						BoardException.print("글을 작성할 권한이 없습니다.");
						return;
					}

					let data = boardWriteDatas.wrap.getWriteData();
					dom.submit.setClickEvent("clickDefault");
					BoardData.load.write(data);

				},
				clickModify: function (event) {
					let dom = boardWriteDatas;
					let isModifiable = BoardData.board.board_user_name == userManage.getUser().name;
					if (!userManage.isLogined()) {
						BoardException.print("로그인이 필요합니다.");
						return;
					} else if (!isModifiable) {
						BoardException.print("글을 수정할 권한이 없습니다.");
						return;
					}

					let data = boardWriteDatas.wrap.getModifyData();
					dom.submit.setClickEvent("clickDefault");
					BoardData.load.modify(data);

				}
			},
			setClickEvent: function (eventName) {
				let listener = this.events[eventName];
				this.clearClickEvent();
				this.$obj.on("click", this.events[eventName] );
			},
			clearClickEvent: function(){
				this.$obj.off("click");
			}
		},
	};

	let confirmDatas = {
		wrap: {
			$obj: $("#confirmWrap"),
			show: function () {
				etcDatas.fadeIn.show();
				this.$obj.slideDown();
			},
			hide: function () {
				etcDatas.fadeIn.hide();
				this.$obj.slideUp();
			},
			set: function (message, successString, successCallback) {
				let dom = confirmDatas;
				dom.message.set(message);
				dom.ok.set(successString);
				dom.ok.setEvent(function (event) {
					dom.wrap.hide();
					successCallback(event);
				});
			},
		},
		message: {
			$obj: $("#confirmWrap .text_message"),
			set: function (message) {
				this.$obj.html(message);
			},
		},
		ok: {
			$obj: $("#confirmWrap .btn_ok"),
			set: function (text) {
				this.$obj.html(text);
			},
			setEvent: function (callback) {
				this.clearEvent();
				this.$obj.on("click",callback);
			},
			clearEvent: function () {
				this.$obj.off("click");
			},
		},
		cancel: {
			$obj: $("#confirmWrap .btn_cancel"),
			events: {
				click: function (event) {
					confirmDatas.ok.clearEvent();
					confirmDatas.wrap.hide();
				}
			}
		}

	};

	let etcDatas= {
		fadeIn: {
			$obj: $("#fadeIn"),
			show: function () {
				this.$obj.fadeIn();
			},
			hide: function () {
				this.$obj.fadeOut();
			},
		},
	}

	let ImageWindowDatas = {
			wrap: {
				$obj: $("#imageSearchWrap"),
				hide:function(){
					this.$obj.fadeOut();
				},
				show:function(){
					this.$obj.fadeIn();
				},
				events:{
					load: function(){
						const doms = ImageWindowDatas;
						const $obj = doms.wrap.$obj;
						
						new WriteObserver($obj).addWord(/\<img:(.+)\>/, function(matched){
							doms.searchInput.set(matched[1]);
							doms.searchIcon.$obj.trigger("click");
						})
						ImageWindowDatas.wrap.show();
					}
				}
			},
			searchInput:{
				$obj: $("#imageSearchWrap .inp_search"),
				get: function( ){
					return this.$obj.val()
				},
				set: function(content){
					return this.$obj.val(content)
				},
			},
			searchIcon:{
				$obj: $("#imageSearchWrap .icon_search"),
				events:{
					click:function(){
						const doms = ImageWindowDatas;
						searchWord = doms.searchInput.get();
						BoardData.load.imageSearch( searchWord );
					}
				}
			},
			tags:{
				$obj:$(".area_tags"),
				reset:function(){
					this.$obj.html();
				},
				add: function(){
					//not implement
				}
			},
			images:{
				$obj:$(".area_thumbs"),
				reset:function(){
					this.$obj.html();
				},
				add: function(){
					//not implement
				}
			},
	}
		
		
	return {
		init: function () {
			EventController.defineAll(etcDatas);
			EventController.defineAll(confirmDatas);
			EventController.defineAll(boardWriteDatas);
			EventController.defineAll(ImageWindowDatas);
		},
		etcDatas: etcDatas,
		boardWriteDatas: boardWriteDatas,
		confirmDatas: confirmDatas,
	}
})();

