var BoardException = (function(){
	$messageWrap = $("#alertWrap");
	return {
		boardList : function(json){
			if(BoardListDOM.categoryDatas.wrap.isOpen() ) BoardListDOM.categoryDatas.wrap.hide();
			BoardListDOM.boardListDatas.alert.show( `${json.status} (${json.statuscode})` );
		},
		globalMessage : function(message,faClass){
			if(typeof faClass != "undefined"){
				$messageWrap.find(".fa").removeClass("fa-times-circle-o")
					.addClass(faClass);
			}
			$messageWrap.find(".text_message").text(message)
			$messageWrap.slideDown().delay(2000).slideUp();
		},
		globalError : function(json){
			var message;
			switch(json.status){
				case 500 :
					message = "서버 오류로 해당 작업을 수행할 수 없습니다.";
					break;
				default:
					message = json.status;
					break;
			}
			BoardException.globalMessage( message );
			
		}
	}
})();

var AjaxRequest = (function(){
	var isNull = function(value){
		return typeof value == "undefined";
	}
	
	/**
	 * 액션객체의 클래스명을 통신가능한 url로 변경
	 * @param name String 액션객체의 클래스명
	 * @return ajax로 통신할 url
	 */
	var getActionURL = function(name){
		return name + ".do";
	}
	
	/**
	 * 간단한 파라미터를 $.ajax와 호환이 가능한 파라미터로 변경
	 * @param 파라미터를 변환할 객체, type, target, data 속성 필요
	 */
	var getAjaxParam = function(param){
		param.url = getActionURL(param.target);
		if( isNull(param.dataType) ) param.dataType = "json";
		if( isNull(param.error) ) param.error = BoardException.globalError;
		param.defaultSuccess = param.success;
		param.success = function(json){
			if( json.statuscode != 0 ){
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
	var execute = function(currentParam){
		let ajaxParam = getAjaxParam(currentParam);
		$.ajax( ajaxParam );
	}
	
	
	return execute;
})();

var EventController = (function(){
	var defaultEventList = ["click","load"];
	var isDefaultEvent = function(eventName){
		return defaultEventList.includes(eventName);
	}
	
	var initOnLoad = function(data){
		data.events.load();
	}
	
	var rtn = {
		datasInit : function(datas){
			Object.keys(datas).forEach( dataName => {
				let data = datas[dataName];
				this.dataInit(data);
			});
		},
		dataInit: function(data){
			let $obj = data.$obj;
			if(typeof data.events == "undefined") return;
			Object.keys(data.events).forEach(eventName => {
				if( !isDefaultEvent(eventName) ) return;
				if(eventName == "load") initOnLoad(data);
				let listener = data.events[eventName];
				$obj.on(eventName,listener);
			})
		}
	}
	
	return rtn;
})();

var BoardData = (function(){
	var externalCtx;
	
	var setCategorysParent = function(categoryData){
		categoryData.child.forEach(childObject => {
			childObject.parent = categoryData;
			setCategorysParent(childObject);
		})
	}

	var addUserParam = function(param){
		if(userManage.isLogined()){
			let userInfo = userManage.getUser();
			param.data.user_name = userInfo.name;
		}
	}
	
	var rtn = {
			category : null,
			boardlist : null,
			board: null,
			init : function(){
				externalCtx = this;
				this.load.category(1);
				this.load.boardList(1,1);
			},
			load : {
				category : function(categoryID){
					var requestSuccess = function(json){
						rtn.category = json.data;
						setCategorysParent(json.data);
						BoardListDOM.reload.category(json.data);
					};
					var param = {
								type : "GET",
								target : "BoardCategoryView",
								data : {
									category_id : categoryID
								},
								success : requestSuccess,
								error: function(e){ console.log(e) }
					}
					AjaxRequest(param);
				},
				boardList : function(categoryID,listOffset){
					var requestSuccess = function(json){
						rtn.boardlist = json.data;
						BoardListDOM.reload.boardList(json.data);
					};
					var param = {
								type : "GET",
								target : "BoardListView",
								data : {
									category_id : categoryID,
									board_list_offset : listOffset,
								},
								success : requestSuccess,
								error : BoardException.boardList
					}
					AjaxRequest(param);
				},
				boardContent : function(boardID){
					var requestSuccess = function(json){
							BoardData.board = json.data;
							BoardContent.reload.content( json.data );
					};
					var param = {
							type : "GET",
							target : "BoardViewDetail",
							data : {
								board_id : boardID,
							},
							success : requestSuccess,
					}
					addUserParam(param);
					console.log(param)
					AjaxRequest(param);
				}
			}
	}

	return rtn;
})();

var BoardListDOM = (function(){

	var boardListDatas = {
			wrap : {
				$obj : $("#navContainer"),
				events : {
					click: function(){
						if( categoryDatas.wrap.isOpen() ) categoryDatas.wrap.hide();
						
					}
				}
			},
			ul : {
				$obj: $("#navContent"),
				addBoard : function(data){
					var $obj = BlindListDOM.listDatas.board.get(data);
					$obj.on("click",boardListDatas.li.events._click);
					this.$obj.append($obj);
				},
				reset : function(){ this.$obj.html("") }
			},
			li : {
				$obj: $(".board_item"),
				events: {
					_click : function(event){
						let boardID = $(this).data("board_id");
						BoardData.load.boardContent(boardID);
					}
				}
			},
			alert : {
				$obj : $("#navContainer > .wrap_alert"),
				isShow : function(){
					return this.$obj.css("display") !== "hide";
				},
				setMessage : function(message){
					this.$obj.find(".text_message").text(message);
				},
				show : function(message){
					this.setMessage(message)
					this.$obj.slideDown();
				},
				hide : function(){
					this.$obj.slideUp();
				}
			}
	}
	
	var headerDatas = {
			wrap : {
				$obj : $("#navHeader"),
				setBright : function(){
					headerDatas.wrap.$obj.addClass("prime");
				},
				setDark : function(){
					headerDatas.wrap.$obj.removeClass("prime");
				},
				hideChild : function(){
					$("#navHeader > .flow").slideUp();
				}
			},
			mainWrap : {
				$obj : $("#navHeaderDefault"),
				hide : function(){
					this.$obj.slideUp();
				},
				show : function(){
					headerDatas.wrap.hideChild();
					this.$obj.slideDown();
				}
			},
			searchWrap : {
				$obj : $("#navHeaderSearch"),
				hide : function(){
					this.$obj.slideUp();
				},
				show : function(){
					headerDatas.wrap.hideChild();
					this.$obj.slideDown();
				}
			},
			mainIconPageSelect: {
				$obj : $("#navHeaderDefault .area_icon_pageselect"),
				events: {
					click: function(event){
						if( categoryDatas.wrap.isOpen() ){
							categoryDatas.wrap.hide();
						} else {
							categoryDatas.wrap.show();
						}

					},
				}
			},
			mainTextTitle : {
				$obj : $("#navHeaderDefault .text_title"),
				set : function(title){
					this.$obj.text( title );
				}
			},
			mainIconWrite :{
				$obj :  $("#navHeaderDefault .area_icon_write"),
				events : {
					click : function(event){
						let writedom = BoardContent.boardWrite;
						writedom.wrap.show();
						if( !writedom.categorySelect.isInit ) writedom.categorySelect.init(BoardData.category);
					},
					load : function(event){
						$this = headerDatas.mainIconWrite.$obj;
						if( userManage.isWriteable() ) $this.show();						
					}
				}
			},
			mainIconSearch :{
				$obj :  $("#navHeaderDefault .area_icon_search"),
				events : {
					click : function(event){
						headerDatas.searchWrap.$obj.show();
					}
				}
			},
			SearchiconSearch : {
				$obj : $("#navHeaderSearch .area_icon_search"),
			},
			SearchiconSpinner : {
				$obj : $("#navHeaderSearch .area_icon_spinner"),
			},
			SearchinputSearch : {
				$obj : $("#navHeaderSearch .inp_search"),
			},
			SearchiconClose : {
				$obj : $("#navHeaderSearch .area_icon_close"),
				events : {
					click : function(event){
						headerDatas.mainWrap.show();
					}	
				}
			}
	}

	var categoryDatas = {
			ul : {
				$obj : $("#categoryContent"),
				/**
				 * 카테고리 노드 하나를 리스트에 추가함
				 * 카테고리 클릭이벤트를 설정함
				 */
				addCategory : function(data,depth){
					let $obj = BlindListDOM.listDatas.category.get(data.info,depth);
					$obj.on("click", categoryDatas.li.events._click );
					categoryDatas.ul.$obj.append( $obj );
					data.child.forEach(child => {
						this.addCategory(child,depth + 1)
					})
				},
				reset : function(){ this.$obj.html(""); }
			},
			wrap : {
				$obj : $("#categoryWrap"),
				isOpen : function(){
					return this.$obj.css("display") !== "none";
				},
				show : function(){
					headerDatas.wrap.setBright();
					this.$obj.slideDown();
				},
				hide : function(){
					headerDatas.wrap.setDark();
					this.$obj.slideUp();
				}
			},
			li : {
				$obj : $("#categoryContent .item_category"),
				events : {
					_click : function(event){
						BoardData.load.boardList($(this).data("category_id") ,1);
					}
				}
			}
	}
	
	var rtn =  {
			init : function(){
				EventController.datasInit( headerDatas );
				EventController.datasInit( boardListDatas );
			},
			reload : {
				category : function(data){
					if(categoryDatas.wrap.isOpen() ) categoryDatas.wrap.hide();
					headerDatas.mainTextTitle.set( data.info.category_name );
					categoryDatas.ul.addCategory(data,0);
				},
				boardList : function(data){
					if(categoryDatas.wrap.isOpen() ) categoryDatas.wrap.hide();
					if(boardListDatas.alert.isShow()) boardListDatas.alert.hide();
					boardListDatas.ul.reset();
					data.list.forEach( function(boardData){
						boardListDatas.ul.addBoard(boardData);
					} )
				},
			},
			headerDatas : boardListDatas,
			categoryDatas : categoryDatas,
			boardListDatas : boardListDatas,
		}

	return rtn;
})();

var BoardContent = (function(){
	var boardDatas = {
		wrap : {
			$obj : $("#contentContainer"),
			set : function(data){
				let $obj = this.$obj;
				$obj.find(".text_hit").text(data.board_hit);
				$obj.find(".text_category").text(data.board_category_name);
				$obj.find("#contentTitle").text(data.board_title);
				$obj.find(".text_like").text(data.board_like);
				$obj.find(".text_date_created").text(data.board_date_create);
				$obj.find(".text_date_modified").text(data.board_date_modify);
				$obj.find("#contentBody").text(data.board_content);
			}
		}
	}
	
	var boardWrite = {
			wrap : {
				$obj : $("#boardWriteWrap"),
				show : function(){
					this.$obj.slideDown();
				}
			},
			categorySelect : {
				$obj : $("#boardWriteWrap .board_category"),
				isInit : false,
				init : function(category){
					console.log(category)
					this.isInit = true;
					let $obj = this.$obj;
					let $option = boardWrite.categoryOption.get();
					$option.data("id", category.info.category_id);
					$option.text(category.info.category_name);
					$obj.append($option);
					
					if( category.child && category.child.length != 0 ){
						category.child.forEach(child => this.init(child) );
					}
				},
			},
			categoryOption : {
				$obj : $("#boardWriteWrap .board_category option"),
				get : function(){
					return $("<option></option>")
				}
			}
	}
	var rtn = {
		init : function(){
			
		},
		reload : {
			content :function(data){
				boardDatas.wrap.set(data);
			}
		},
		boardWrite: boardWrite
	}
	return rtn;
})();

var BlindListDOM = (function(){
	var listDatas = {
			category : {
				$obj : $("#blindList .item_category.cloneable"),
				get : function(info, depth){
					let $obj = util.getClone( listDatas.category.$obj.clone() );
					
					$obj.data("category_id",  info.category_id );
					$obj.find(".text_title").text( info.category_name );
					$obj.find(".text_boardcount").text( info.category_board_count );
					$obj.find(".text_hitcount").text( info.category_hit );
					$obj.find(".text_likecount").text( info.category_like );
					$obj.find(".text_lastupdate").text( info.category_update_date );
					$obj.data("id",info.category_id);
					
					if(depth <= 0) $obj.find(".inner_depth").addClass("off");
					$obj.find(".inner_depth").css("width",this.getLeftMargin(depth) );

					return $obj;
				},
				getLeftMargin : function(depth){ 
					return depth == 0 ? 0 : (depth + 1) * 5; 
				}
			},
			comment : {
				$obj : $("#blindList .item_comment.cloneable")
			},
			board : {
				$obj : $("#blindList .item_board.cloneable"),
				get : function(data){
					console.log(data)
					let $obj = util.getClone( this.$obj.clone() );
					
					$obj.data("board_id",data.board_id);
					$obj.find(".text_title").text( data.board_title );
					$obj.find(".text_hit").text( data.board_hit );
					$obj.find(".text_like").text( data.board_like );
					$obj.find(".text_date").text( data.board_date_create );
					return $obj
				}
			}
			
	}
	
	var util = {
			getClone : function($obj){
				return $obj.clone().removeClass("cloneable");
			}
	}

	return {
		listDatas : listDatas,
		init : function(){
			EventController.datasInit(listDatas);
			
		}
	}
})();

var etcDOM = (function(){
	return {
		init : function(){
			
		},
		Datas : {
			fadein : {
				$obj : $("#fadein"),
				show:function(){
					
				},
				hide:function(){
					
				}
			}
		}
	}
})
var userManage = (function(){
	var localLogin = function(name){
		var requestSuccess = function(json){
			Object.keys(json.data.info).forEach(function(attr){
				localStorage.setItem(attr,json.data.info[attr]);
			})
		}
		var param = {
				type: "GET",
				target: "UserGetUserAction",
				data : {
					user_name :  name
				},
				success : requestSuccess
			}
		AjaxRequest(param)
	}
	
	var exception = {
		loginFail : function(){
			window.BoardException.globalMessage("로그인에 실패했습니다. 패스워드를 확인해주세요.");
		},
		
	}
	
	return {
		login : function(name,pwd){
			var requestSuccess = function(json){
				if(json.data.is_login_success){
					localLogin(name);
				} else {
					exception.loginFail();
				}
			}
			var param = {
				type: "POST",
				target: "UserAuthAction",
				data : {
					user_name :  name,
					user_pwd : pwd,
				},
				success : requestSuccess
			};
			AjaxRequest(param);
		},
		isLogined: function(){
			return localStorage.getItem("user_name") != null ;
		},
		getUser : function(){
			return {
				name : localStorage.getItem("user_name"),
				pwd : localStorage.getItem("user_pwd"),
			}
		},
		isWriteable : function(){
			return localStorage.getItem("user_permission_write") === "true";
		}
	}
})();

var system = (function(){
	var rtn = {
			init : function(){
				BoardListDOM.init();
				BlindListDOM.init();
				BoardContent.init();
				BoardData.init();
			}
		};
	return rtn
})();

system.init();

