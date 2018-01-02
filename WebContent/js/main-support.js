var urlManager = (function(){
	const SITENAME = "욱로그";
	const FILENAME = "index.html"
	let getURL = function(){
		let fileName =location.pathname.replace(/^.*\//,"");
		let queryString = [];
		if(BoardData.category || false) queryString.push(`category=${ BoardData.category.info.category_id }`);
		if(BoardData.board || false) queryString.push(`id=${ BoardData.board.board_id }`);
		return `${ fileName }?${ queryString.join("&") }`;
	}
	
	let getTitle = function(){
		let boardTitle = BoardData.board.board_title;
		return [SITENAME, boardTitle].join(" - ");
	}
	
	return {
		reloadURL : function(){
			document.title = getTitle();
			history.pushState(null,null,getURL());
			
		},
		getCategoryID: function(){
			const regex = /category\=([0-9]+)/;
			const result = regex.exec(location.search);
			return result && result[1];
		},
		getBoardID: function(){
			const regex = /id\=([0-9]+)/;
			const result = regex.exec(location.search);
			return result && result[1];
		}
	}
})();

var listTemplet = (function(){
	function _extend(child,parent){
		child.prototype = Object.create(parent.prototype);
		child.prototype.constructor = child;
	}
	
	let CommentList = (function(_parent){
		_extend(CommentList,_parent);
		function CommentList(){ 
			_parent.apply(this,arguments); 
			return this;
		};

		
		CommentList.prototype.$cloneDOMElement = $("#blindList .item_comment.cloneable");

		let clickRemoveListener = function(event){
			let instance = $(event.currentTarget).closest(".item_comment")[0].instance;
			let bean = instance.bean;

			
			let message = "이 덧글을 삭제합니다. <br /> 계속 진행하시겠어요?"
			let successCallback = function(){
				console.log(bean,bean.reply_id)
				instance.remove();
				BoardData.load.removeComment( bean.reply_id );
			}

			etcDOM.confirmDatas.wrap.show();
			etcDOM.confirmDatas.wrap.set(message, "삭제", successCallback);
		};
		
		let clickModifyListener = function(){
			let instance = $(event.currentTarget).closest(".item_comment")[0].instance;
			instance.modeToggle();
		};
		
		let clickModifySubmitListener = function(){
			let $obj = $(event.currentTarget).closest(".item_comment");
			let instance = $obj[0].instance;

			let parameterData = new ParameterData();
			
			BoardData.load.modifyComment(instance.getModifyData(), $obj);
		};
		
		let keyDownListener = function(event){			
			let $input = event.currentTarget;
			let $obj = $(event.currentTarget).closest(".item_comment");
			let instance = $obj[0].instance;
			let $submit = $obj.find(".btn_submit");
			

			if(event.key == "Enter") $submit.trigger("click");
			if(event.key == "Escape") instance.modeToggle();
		}
		
		CommentList.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;

			$obj.find(".text_name").text(bean.reply_user_name);
			$obj.find(".text_date").text(bean.reply_date);
			$obj.find(".text_descript").text(bean.reply_content);

			this.reloadIconList();

			return this;
		}

		CommentList.prototype.reloadIconList = function(){
			let $removeIcon = this.$domElement.find(".area_icon_remove");
			let $modifyIcon = this.$domElement.find(".area_icon_modify");

			let commentUserName = this.bean.reply_user_name;
			let userBean = userManage.getBean();
			let hasRemovePermission = userBean && userBean.user_permission_remove;

			let isRemoveable = hasRemovePermission || commentUserName == userBean.user_name;
			let isModifiable = commentUserName == userBean.user_name;

			if( isRemoveable ) $removeIcon.show();
			 else $removeIcon.hide();
			
			if( isModifiable ) $modifyIcon.show();
				else $modifyIcon.hide();

			return this;
		}
		
		CommentList.prototype.initEvent = function( ){
			let $obj = this.$domElement;

			$obj.find(".area_icon_remove").click(clickRemoveListener);
			$obj.find(".area_icon_modify").click(clickModifyListener);
			$obj.find(".btn_submit").click(clickModifySubmitListener);
			$obj.find(".inp_descript").keydown(keyDownListener);
			return this;
		}

		CommentList.prototype.remove = function(){
			this.$domElement.slideUp(function(){
				this.remove();
			});

			return this;
		}
		
		CommentList.prototype.addAndShow = function($ul){
			this.$domElement.hide();
			$ul.append( this.$domElement );
			this.$domElement.slideDown();

			return this;
		}
		
		CommentList.prototype.modeToggle = function(){
			const $obj = this.$domElement;
			const $descript = $obj.find(".area_descript");
			const $write = $obj.find(".area_write");
			const descript = $descript.find(".text_descript").html();
			
			$write.find(".inp_descript")
			$descript.slideToggle();
			$write.slideToggle();
		}
		
		CommentList.prototype.getModifyData = function(){
			const $obj = this.$domElement;
			const paramData = new ParameterData();
			paramData.add( "reply_content", $obj.find(".inp_descript").val() )
							 .add( "reply_id", this.bean.reply_id )
							 .addUser();
							 
			return paramData;	 
		}
		
		return CommentList;
	})(HTMLList);
	
	let CategoryList = (function(_parent){
		_extend(CategoryList,_parent);
		
		function CategoryList(){ 
			_parent.apply(this,arguments); 
			return this;
		};
		CategoryList.prototype.$cloneDOMElement = $("#blindList .item_category.cloneable");

		
		
		let clickEventListener = function(event){
			let bean = event.currentTarget.instance.bean;
			let categoryID = bean.category_id;
			BoardData.load.boardList( categoryID , 0);
		};

		CategoryList.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;

			$obj.data("category_id", bean.category_id);
			$obj.find(".text_title").text(bean.category_name);
			$obj.find(".text_boardcount").text(bean.category_board_count);
			$obj.find(".text_hitcount").text(bean.category_hit);
			$obj.find(".text_likecount").text(bean.category_like);
			$obj.find(".text_lastupdate").text(bean.category_update_date);
			$obj.data("id", bean.category_id);

			return this;
		}

		CategoryList.prototype.initEvent = function(){
			this.$domElement.click( clickEventListener );
		}

		CategoryList.prototype.setDepth = function(depth){
			// if(typeof this.bean == "undefined") throw new Error("categoryList.setByBean()로 빈을 먼저 정의해주세요.");
			let $obj = this.$domElement;
			let getLeftMargin = function (depth) {
				return depth == 0 ? 0 : (depth + 1) * 5;
			}

			if (depth <= 0) $obj.find(".inner_depth").addClass("off");
			$obj.find(".inner_depth").css("width", getLeftMargin(depth) );

			return this;
		}

		return CategoryList;
	})(HTMLList);

	let BoardList = (function(_parent){
		_extend(BoardList,_parent);
		function BoardList(){ 
			_parent.apply(this,arguments); 
			return this;
		};
		BoardList.prototype.$cloneDOMElement = $("#blindList .item_board.cloneable");
		
		
		let clickListener = function (event) {
			let boardID = $(this).data("board_id");
			BoardData.load.boardContent(boardID);
			BoardData.load.boardComment(boardID);
		};



		BoardList.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;

			$obj.data("board_id", bean.board_id);
			$obj.find(".text_title").text(bean.board_title);
			$obj.find(".text_comment").text(`[${bean.board_reply_count}]`);
			$obj.find(".text_hit").text(bean.board_hit);
			$obj.find(".text_like").text(bean.board_like);
			$obj.find(".text_date").text(bean.board_date_create);

			return this;
		}

		BoardList.prototype.initEvent = function(){
			this.$domElement.click(clickListener);

			return this;
		};

		return BoardList;
	})(HTMLList);

	let imageThumb = (function(){
		
	})
	return {
		CommentList : CommentList,
		CategoryList : CategoryList,
		BoardList : BoardList,
	}
})();

var BoardException = (function () {

	let messageDatas = {
		wrap: {
			$obj: $("#alertWrap"),
			showStart: function () {
				this.$obj.slideDown().delay(2000).slideUp();
			}
		},
		icon: {
			$obj: $("#alertWrap .area_icon_normal > i"),
			set: function (faClass) {
				this.reset();
				this.$obj.addClass(faClass);
			},
			reset: function () {
				this.$obj.removeClass().addClass("fa");
			},
 
		},
		text: {
			$obj: $("#alertWrap .area_text"),
			set: function (message) {
				this.$obj.html(message);
			}
		},
	}


	return {
		boardList: function (json) {
			if (BoardListDOM.categoryDatas.wrap.isOpen()) BoardListDOM.categoryDatas.wrap.hide();
			BoardListDOM.boardListDatas.ul.reset();
			BoardListDOM.boardListDatas.alert.show(`${json.status} (${json.statuscode})`);
		},
		print: function (message, faClass = "fa-times-circle-o") {
			let dom = messageDatas;
			dom.icon.set(faClass);
			dom.text.set(message);
			dom.wrap.showStart();
		},
		messageDatas: messageDatas,

		printError: function (json) {
			var message;
			switch (json.status) {
				case 500:
					message = "서버 오류로 해당 작업을 수행할 수 없습니다.";
					break;
				default:
					message = json.status
					break;
			}
			BoardException.print(message);

		}
	}
})();
 
var BoardData = (function () {
	var defaultIndex = {
		category: urlManager.getCategoryID() || 1,
		board:  urlManager.getBoardID() || 1,
	}

	var externalCtx;
	/**
	 * 카테고리들의 부모 객체를 설정함
	 * @param {Object} categoryData 카테고리 트리 데이터
	 */
	var setCategorysParent = function (categoryData) {
		categoryData.child.forEach(childObject => {
			childObject.parent = categoryData;
			setCategorysParent(childObject);
		})
	}

	let load = {
		category: function (categoryID) {
			let param = new RequestParameter();
			
			let requestSuccess = function (json) {
				externalCtx.category = json.data;
				setCategorysParent(json.data);
				BoardListDOM.reload.category(json.data);
				urlManager.reloadURL();
			};

			param.setType("GET")
				 .setTarget("BoardCategoryView")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("category_id",categoryID);

			param.send();

		},
		boardList: function (categoryID, listOffset,searchWord) {
			let param = new RequestParameter();

			var requestSuccess = function (json) {
				externalCtx.boardlist = json.data;
				BoardListDOM.reload.boardList(json.data);
			};

			param.setType("GET")
				 .setTarget("BoardListView")
				 .addSuccessCallback(requestSuccess)
				 .addErrorCallback(BoardException.boardList)
				 .getData()
					 .add("category_id",categoryID)
					 .add("board_list_offset",listOffset);
			if(typeof searchWord != "undefined") param.getData().add("board_search_word",searchWord);
			param.send(param);
		},
		boardContent: function (boardID) {
			let param = new RequestParameter();

			load.boardContent.requestSuccess = function (json) {
				BoardData.board = json.data;
				urlManager.reloadURL();
				BoardContentDOM.reload.content(json.data);

			};

			param.setType("GET")
				 .setTarget("BoardViewDetail")
				 .addSuccessCallback(load.boardContent.requestSuccess)
				 .getData().add("board_id",boardID);

			BoardContentDOM.boardDatas.wrap.hide();
			param.send(param);
		},
		boardComment: function (boardID) {
			let param = new RequestParameter();

			var requestSuccess = function (json) {
				BoardData.comment = json.data;
				BoardContentDOM.reload.comment(json.data);
			};

			param.setType("GET")
				 .setTarget("ReplyListView")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("board_id",boardID);


			param.send(param);
		},
		write : function(data){
			let param = new RequestParameter();
			let writedom = etcDOM.boardWriteDatas;

			var requestSuccess = function (json) {
				const boardID = json.data.board_id;
				let commentUL = BoardContentDOM.boardCmtDatas.ul
				
				writedom.wrap.destroy();
				BoardException.print("작성 완료!","fa-check-circle-o");
				commentUL.reset();
				
				//획득한 BoardBean 돔에 반영
				BoardData.board = json.data;
				BoardContentDOM.reload.content(json.data);
				
				load.boardList(json.data.board_category_id,0);
			}

			var requestError = function (json) {
				writedom.submit.setClickEvent("clickWrite");
				BoardException.printError(json);
			}

 
			param.setType("POST")
				 .setTarget("BoardAddAction")
				 .addSuccessCallback(requestSuccess)
				 .addErrorCallback(requestError)
				 .getData().set(data);

			param.send();
		},
		commentwrite: function (data) {
			let param = new RequestParameter();
			let writedom = BoardContentDOM.boardCmtWriteDatas;
			let listdom = BoardContentDOM.boardCmtDatas;
			
			userManage.simpleLogin(data.user_name, data.user_pwd);
			let requestSuccess = function (json) {
				writedom.content.reset();
			}

			let requestError = function(json){
				listdom.ul.shift();
				BoardException.printError(json);
			}
			
			param.setType("POST")
				.setTarget("ReplyAddAction")
				.addSuccessCallback(requestSuccess)
				.addErrorCallback(requestError)
				.getData().set(data);

			param.send();
		},
		modify : function(data){
			let param = new RequestParameter();
			let writedom = etcDOM.boardWriteDatas;

			var requestSuccess = function (json) {
				const boardID = json.data.board_id;
				writedom.wrap.reset();
				writedom.wrap.hide();
				BoardException.print("수정 완료!");
				load.boardContent.requestSuccess(json);
				load.boardComment(boardID);
			}
			var requestError = function (json) {
				writedom.submit.setClickEvent("clickModify");
				BoardException.printError(json);
			}

			param.setType("POST")
				.setTarget("BoardModifyAction")
				.addSuccessCallback(requestSuccess)
				.addErrorCallback(requestError)
				.getData().set(data);

			param.send();

		},
		modifyComment : function(paramData,$li){
			console.log($li);
			let param = new RequestParameter();
			
			var requestSuccess = function (json) {
				
				$li.find(".text_descript").html( json.data.reply_content );
				
				$li[0].instance.modeToggle();
				BoardException.print("수정 완료!");
			}
			
			param.setType("POST")
				.setTarget("ReplyModifyAction")
				.addSuccessCallback(requestSuccess)
				.setData(paramData);


			param.send();
		},
		remove: function (boardID) {
			let param = new RequestParameter();
			let requestSuccess = function (json) {
				// 작성해야함
				let categoryID = BoardData.category.info.category_id;
				load.boardList(categoryID, 0);
				load.boardContent(defaultIndex.board);
			};

			param.setType("POST")
				 .setTarget("BoardDeleteAction")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("board_id",boardID)
				.addUser();

			param.send();

		},
		removeComment : function(reply_id,$li){
			let param = new RequestParameter();
			let requestSuccess = function (json) {
				// 작성해야함
				BoardException.print("삭제 완료!");
			};

			param.setType("POST")
				 .setTarget("ReplyDeleteAction")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("reply_id",reply_id).addUser();

			param.send();

		},
		imageSearch: function(searchWord){
			let param = new RequestParameter();
			let requestSuccess = function(json){
				// not implement
				console.log(json)
			}
			
			param.setType("GET")
				.setTarget("ImageLoadAction")
				.addSuccessCallback(requestSuccess)
				.getData().add("search_word",searchWord);
			
			param.send();
		}

	}

	return {
		init: function () {
			externalCtx = this;
			this.load.boardList(defaultIndex.category, 0);
			this.load.boardContent(defaultIndex.board);
			this.load.boardComment(defaultIndex.board);
			this.load.category(defaultIndex.category);
		},
		load: load
	}
})();

var WriteObserver = (function(){
	
	
	function observeEvent(self,content){
		self.wordList.forEach(v => {
			const searchResult = v.word.exec(content);
			if(searchResult != null) v.callback(searchResult);
		})
	}

	
	function WriteObserver($eventTarget){
		
		this.wordList = [];
		let self = this;
		
		$eventTarget.keyup(function(e){
			const content = $(this).val();
			
			observeEvent(self,content);
		})
		
	}
	/**
	 * @param 
	 */
	WriteObserver.prototype.addWord = function(word,callback){
		if( !(word instanceof RegExp) ) throw Error("word가 올바른 타입이 아니므로 정규식으로 바꿔주십쇼");
		this.wordList.push({word,callback});
		return this;
	}
	
	return WriteObserver;
	
})();
