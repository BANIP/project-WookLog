// 게시글, 덧글 처리중에 작성 한번 더 누를 시 예외뜨는 예외 처리

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
		category: 1,
		board: 1,
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
				console.log(json)
				externalCtx.category = json.data;
				setCategorysParent(json.data);
				BoardListDOM.reload.category(json.data);
			};

			param.setType("GET")
				 .setTarget("BoardCategoryView")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("category_id",categoryID);

			param.send();

		},
		boardList: function (categoryID, listOffset) {
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
					 .add("board_list_offset",listOffset)

			param.send(param);
		},
		boardContent: function (boardID) {
			let param = new RequestParameter();

			load.boardContent.requestSuccess = function (json) {
				BoardData.board = json.data;
				BoardContentDOM.reload.content(json.data);
			};

			param.setType("GET")
				 .setTarget("BoardViewDetail")
				 .addSuccessCallback(load.boardContent.requestSuccess)
				 .getData().add("board_id",boardID);


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
				
				load.boardList(json.data.board_category_id,1);
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
		modifyComment : function(data,$li){
			let param = new RequestParameter();
			
			var requestSuccess = function (json) {
				
				$li.find(".text_descript").html( json.data.reply_content );
				$li.find(".text_name").html( json.data.reply_user_name );
				
				BoardContentDOM.boardCmtDatas.li.setViewMode($li);
				BoardException.print("수정 완료!");
			}
			
			param.setType("POST")
				.setTarget("ReplyModifyAction")
				.addSuccessCallback(requestSuccess)
				.getData().set(data).addUser();

			param.send();
		},
		remove: function (boardID) {
			let param = new RequestParameter();
			let requestSuccess = function (json) {
				// 작성해야함
				let categoryID = BoardData.category.bean.category_id;
				load.boardList(categoryID, 1);
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
				BoardContentDOM.boardCmtDatas.li.remove($li);
				BoardException.print("삭제 완료!");
			};

			param.setType("POST")
				 .setTarget("BoardDeleteAction")
				 .addSuccessCallback(requestSuccess)
				 .getData().add("reply_id",boardID).addUser();

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

var BoardListDOM = (function () {

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
				var boardList = new listTemplet.BoardList();
				boardList.setByBean(boardBean);


				this.$obj.append(boardList.domElement);
			},
			addAll: function(beanList){
				beanList.forEach(function (boardBean) {
					boardListDatas.ul.addBoard(boardBean);
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
					headerDatas.searchWrap.$obj.show();
				}
			}
		},
		SearchiconSearch: {
			$obj: $("#navHeaderSearch .area_icon_search"),
		},
		SearchiconSpinner: {
			$obj: $("#navHeaderSearch .area_icon_spinner"),
		},
		SearchinputSearch: {
			$obj: $("#navHeaderSearch .inp_search"),
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
				if(data.bean
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
		headerDatas: boardListDatas,
		categoryDatas: categoryDatas,
		boardListDatas: boardListDatas,
	}

	return rtn;
})();
 
var BoardContentDOM = (function () {
	var boardDatas = {
		wrap: {
			$obj: $("#contentContainer"),
			set: function (data) {
				let $obj = this.$obj;
				$obj.find(".text_hit").text(data.board_hit);
				$obj.find(".text_category").text(data.board_category_name);
				$obj.find("#contentTitle").text(data.board_title);
				$obj.find(".text_like").text(data.board_like);
				$obj.find(".text_date_created").text(data.board_date_create);
				$obj.find(".text_date_modified").text(data.board_date_modify);
				$obj.find("#contentBody").text(data.board_content);
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
				this.$obj.text((i, value) => parseInt(value) + 1);
			},
			minus: function(count){
				this.$obj.text((i, value) => parseInt(value) - 1);
			}
		},
		ul: {
			$obj: $("#commentListContainer"),
			unshift: function(data) {
				boardCmtDatas.count.plus();
				
				let $li = boardCmtDatas.li.get(data).hide();
				this.$obj.prepend($li);
				$li.slideDown();
			},
			shift: function(){
				boardCmtDatas.count.minus();
				this.$obj.find("li").eq(0).instance.remove();
			},
			add: function(bean) {
				let comment = new listTemplet.CommentList();
				comment.setBean(bean);
				this.$obj.append(comment.domElement);
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
				return {
					reply_content: dom.content.get(),
					reply_date: new Date().toLocaleString(),
					reply_user_name: dom.name.get(),
				}
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
			$obj: $(".btn_submit"),
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
	var rtn = {
		init: function () {
			EventController.defineAll(boardDatas);
			EventController.defineAll(boardCmtDatas);
			EventController.defineAll(boardCmtWriteDatas);

		},
		reload: {
			content: function (data) {
				boardDatas.wrap.set(data);
			},
			comment: function (data) {
				boardCmtDatas.ul.reset();
				boardCmtDatas.ul.addAll(data.list);
				boardCmtDatas.count.set(data.list.length);
			}
		},
		boardCmtWriteDatas: boardCmtWriteDatas,
		boardCmtDatas: boardCmtDatas,
		boardDatas: boardDatas,
	}
	return rtn;
})();


var listTemplet = (function(){
	let getChildClass = function(parent,constructor){
		let child;

		if(typeof constructor != "undefined"){
			child = function(){ 
				Object.apply(this,arguments); 
				
				return this;
			};
		} else {
			child = constructor;
		}

		child.prototype = Object.create(this,parent.prototype);
		child.prototype.constructor = child;

		return child;
	}

	let HTMLList = (function(){
		let main = function( $dom ){
			if(typeof dom == "undefined"){
				this.$domElement = this.getNewDomElement();
				this.domElement = this.$domElement[0];
			} else {
				this.domElement = dom;
				this.$domElement = $(dom);
			}
			
			this.initDOMElement();
			this.initEvent();

			return this;
		};
		
		//abstract main.prototype.$cloneDOMElement = 
		main.prototype.getNewDomElement = function(){
			return this.$cloneDOMElement.clone().removeClass(".cloneable");
		}
		
		main.prototype.initDOMElement = function(){
			this.domElement.instance = this;

			return this;
		};
		
		main.prototype.initEvent = function(){

			return this;
		};
		return main;
	});
	
	
	let CommentList = (function(){
		let main = getChildClass(HTMLList);
		main.prototype.$cloneDOMElement = $("#blindList .item_comment.cloneable");

		let clickRemoveListener = function(){
			let message = "이 덧글을 삭제합니다. <br /> 계속 진행하시겠어요?"
			let successCallback = function(){
				BoardData.load.remove( this.bean.reply_id );
			}

			etcDOM.confirmDatas.wrap.show();
			etcDOM.confirmDatas.wrap.set(message, "삭제", successCallback);
		};
		
		let clickModifyListener = function(){
			
		};
		
		main.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;

			$obj.find(".text_name").text(bean.reply_user_name);
			$obj.find(".text_date").text(bean.reply_date);
			$obj.find(".text_descript").text(bean.reply_content);

			this.reloadIconList();

			return this;
		}

		main.prototype.reloadIconList = function(){
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
		
		main.prototype.initEvent = function( ){
			let $obj = this.$domElement;

			$obj.find(".area_icon_remove").click(clickRemoveListener);
			$obj.find(".area_icon_modify").click(clickModifyListener);

			return this;
		}

		main.prototype.remove = function(){
			this.domElement.slideUp(function(){
				this.remove();
			});

			return this;
		}
		
		return main;
	});
	
	let CategoryList = (function(){
		let main = getChildClass(HTMLList);
		main.prototype.$cloneDOMElement = $("#blindList .item_category.cloneable");

		main.prototype.setByBean = function( bean ){
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

		main.prototype.setDepth = function(depth){
			// if(typeof this.bean == "undefined") throw new Error("categoryList.setByBean()로 빈을 먼저 정의해주세요.");
			let $obj = this.$domElement;
			let getLeftMargin = function (depth) {
				return depth == 0 ? 0 : (depth + 1) * 5;
			}

			if (depth <= 0) $obj.find(".inner_depth").addClass("off");
			$obj.find(".inner_depth").css("width", getLeftMargin(depth) );

			return this;
		}

	});

	let BoardList = (function(){
		let clickListener = function (event) {
			let boardID = $(this).data("board_id");
			BoardData.load.boardContent(boardID);
			BoardData.load.boardComment(boardID);
		};

		let main = getChildClass(HTMLList);
		main.prototype.$cloneDOMElement = $("#blindList .item_board.cloneable");

		main.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;

			$obj.data("board_id", bean.board_id);
			$obj.find(".text_title").text(bean.board_title);
			$obj.find(".text_hit").text(bean.board_hit);
			$obj.find(".text_like").text(bean.board_like);
			$obj.find(".text_date").text(bean.board_date_create);

			return this;
		}

		main.prototype.initEvent = function(){
			this.$domElement.click(clickListener);

			return this;
		};

	});


	return {
		CommentList : CommentList,
		CategoryList : CategoryList,
		BoardList : BoardList,
	}
})();

var etcDOM = (function () {
	var boardWriteDatas = {
		wrap: {
			$obj: $("#boardWriteWrap"),
			show: function () {
				etcDatas.fadeIn.show();
				this.$obj.slideDown();
			},
			hide: function () {
				etcDatas.fadeIn.hide();
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
				const $option = dom.categoryOption.get(category.bean
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

	return {
		init: function () {
			EventController.defineAll(etcDatas);
			EventController.defineAll(confirmDatas);
			EventController.defineAll(boardWriteDatas);
		},
		etcDatas: etcDatas,
		boardWriteDatas: boardWriteDatas,
		confirmDatas: confirmDatas,
	}
})();
