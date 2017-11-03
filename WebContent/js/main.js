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

	var addUserParam = function (param) {
		if (userManage.isLogined()) {
			let user = userManage.getUser();
			param.data.user_name = user.name;
			param.data.user_pwd = user.pwd;
		}
	}

	let load = {
		category: function (categoryID) {
			var requestSuccess = function (json) {
				externalCtx.category = json.data;
				setCategorysParent(json.data);
				BoardListDOM.reload.category(json.data);
			};
			var param = {
				type: "GET",
				target: "BoardCategoryView",
				data: {
					category_id: categoryID
				},
				success: requestSuccess,
				error: function (e) { console.log(e) }
			}
			AjaxRequest(param);
		},
		boardList: function (categoryID, listOffset) {
			var requestSuccess = function (json) {
				externalCtx.boardlist = json.data;
				BoardListDOM.reload.boardList(json.data);
			};
			var param = {
				type: "GET",
				target: "BoardListView",
				data: {
					category_id: categoryID,
					board_list_offset: listOffset,
				},
				success: requestSuccess,
				error: BoardException.boardList
			}
			AjaxRequest(param);
		},
		boardContent: function (boardID) {
			var requestSuccess = function (json) {
				BoardData.board = json.data;
				BoardContentDOM.reload.content(json.data);
			};
			var param = {
				type: "GET",
				target: "BoardViewDetail",
				data: {
					board_id: boardID,
				},
				success: requestSuccess,
			}
			AjaxRequest(param);
		},
		boardComment: function (boardID) {
			var requestSuccess = function (json) {
				BoardData.comment = json.data;
				BoardContentDOM.reload.comment(json.data);
			};
			var param = {
				type: "GET",
				target: "ReplyListView",
				data: {
					board_id: boardID,
				},
				success: requestSuccess,
			};
			AjaxRequest(param);
		},
		write : function(data){
			let writedom = etcDOM.boardWriteDatas;

			var requestSuccess = function (json) {
				const boardID = json.data.board_id;
				writedom.wrap.reset();
				writedom.wrap.hide();
				BoardException.print("작성 완료!","check-circle-o");
				load.boardContent(boardID);
				load.boardComment(boardID);
				load.boardList(data.category_id);
			}

			var requestError = function (json) {
				writedom.submit.setClickEvent("clickWrite");
				BoardException.printError(json);
			}

			var param = {
				type: "POST",
				target: "BoardAddAction",
				data: data,
				success: requestSuccess,
				error:requestError
			}

			AjaxRequest(param);
		},
		commentwrite: function (data) {
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
			
			let param = {
				type: "POST",
				target: "ReplyAddAction",
				data: data,
				success: requestSuccess,
				error:requestError,
			}
			AjaxRequest(param);

		},
		modify : function(data){
			let writedom = etcDOM.boardWriteDatas;

			var requestSuccess = function (json) {
				const boardID = json.data.board_id;
				writedom.wrap.reset();
				writedom.wrap.hide();
				BoardException.print("수정 완료!");
				load.boardContent(boardID);
				load.boardComment(boardID);
			}

			var requestError = function (json) {
				writedom.submit.setClickEvent("clickModify");
				BoardException.printError(json);
			}

			var param = {
				type: "POST",
				target: "BoardModifyAction",
				data: data,
				success: requestSuccess,
				error:requestError
			}

			AjaxRequest(param);
			
		},
		remove: function (boardID) {
			let requestSuccess = function () {
				// 작성해야함
			};
			var param = {
				type: "POST",
				target: "BoardDeleteAction",
				data: {
					board_id: boardID,
				},
				success: requestSuccess,
			};
			addUserParam(param);
			AjaxRequest(param);
		}

	}

	return {
		init: function () {
			externalCtx = this;
			this.load.category(defaultIndex.category);
			this.load.boardList(defaultIndex.category, 1);
			this.load.boardContent(defaultIndex.board);
			this.load.boardComment(defaultIndex.board);
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
			addBoard: function (data) {
				var $obj = BlindListDOM.listDatas.board.get(data);
				$obj.on("click", boardListDatas.li.events._click);
				this.$obj.append($obj);
			},
			reset: function () { this.$obj.html("") }
		},
		li: {
			$obj: $(".board_item"),
			events: {
				_click: function (event) {
					let boardID = $(this).data("board_id");
					BoardData.load.boardContent(boardID);
					BoardData.load.boardComment(boardID);
				}
			}
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
				let $obj = BlindListDOM.listDatas.category.get(data.info, depth);
				$obj.on("click", categoryDatas.li.events._click);
				categoryDatas.ul.$obj.append($obj);
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
		},
		li: {
			$obj: $("#categoryContent .item_category"),
			events: {
				_click: function (event) {
					BoardData.load.boardList($(this).data("category_id"), 1);
				}
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
				data.list.forEach(function (boardData) {
					boardListDatas.ul.addBoard(boardData);
				})
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
			unshift: function (data) {
				boardCmtDatas.count.plus();
				
				let $li = boardCmtDatas.li.get(data).hide();
				this.$obj.prepend($li);
				$li.slideDown();
			},
			shift: function(){
				let callback = function(){
					this.remove();
				}
				boardCmtDatas.count.minus();
				this.$obj.find("li").eq(0).slideUp(400,callback);
			},
			add: function (data) {
				let $li = boardCmtDatas.li.get(data);
				this.$obj.append($li);
			},
			reset: function () {
				this.$obj.html("");
			},
			addAll: function (list) {
				list.forEach(data => this.add(data));

			}
		},
		li: {
			$obj: $("#commentWriteWrap .item_comment"),
			get: function (data) {
				return BlindListDOM.listDatas.comment.get(data);
			},
			isModifiable: function ($li) {
				$li.find(".text_name").text == userManage.getUser().name;
			}
		}

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

var BlindListDOM = (function () {
	var listDatas = {
		category: {
			$obj: $("#blindList .item_category.cloneable"),
			get: function (info, depth) {
				let $obj = util.getClone(this.$obj);

				$obj.data("category_id", info.category_id);
				$obj.find(".text_title").text(info.category_name);
				$obj.find(".text_boardcount").text(info.category_board_count);
				$obj.find(".text_hitcount").text(info.category_hit);
				$obj.find(".text_likecount").text(info.category_like);
				$obj.find(".text_lastupdate").text(info.category_update_date);
				$obj.data("id", info.category_id);

				if (depth <= 0) $obj.find(".inner_depth").addClass("off");
				$obj.find(".inner_depth").css("width", this.getLeftMargin(depth));

				return $obj;
			},
			getLeftMargin: function (depth) {
				return depth == 0 ? 0 : (depth + 1) * 5;
			}
		},
		comment: {
			$obj: $("#blindList .item_comment.cloneable"),
			get: function (data) {
				let $obj = util.getClone(this.$obj);
				$obj.find(".text_name").text(data.reply_user_name);
				$obj.find(".text_date").text(data.reply_date);
				$obj.find(".text_descript").text(data.reply_content);

				return $obj;
			}
		},
		board: {
			$obj: $("#blindList .item_board.cloneable"),
			get: function (data) {
				let $obj = util.getClone(this.$obj);

				$obj.data("board_id", data.board_id);
				$obj.find(".text_title").text(data.board_title);
				$obj.find(".text_hit").text(data.board_hit);
				$obj.find(".text_like").text(data.board_like);
				$obj.find(".text_date").text(data.board_date_create);
				return $obj
			}
		}

	}

	var util = {
		getClone: function ($obj) {
			return $obj.clone().removeClass("cloneable");
		}
	}

	return {
		listDatas: listDatas,
		init: function () {
			EventController.defineAll(listDatas);

		}
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
			reset: function () {
				let dom = boardWriteDatas;
				dom.title.reset();
				dom.content.reset();
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
				this.$obj.find("oprtion").each(function () {
				})
			},
			select : function( categoryName ){
				this.$obj.val(categoryName).attr("selected","selected");
			},
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
