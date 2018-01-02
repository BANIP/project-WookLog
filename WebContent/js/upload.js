let BoardException = (function(){
	
	return {
		printError: function(json){
			console.log("에러발생",json)
		}
	}
})();

let requestSend = (function(){
	let load = {
			uploadIamge : function(dataParameter){
				let requestSuccess = function(json){
					alert("석섹스!!");
					$("#uploadSection").append(JSON.stringify(json))
				}
				
				let requestError = function(json){
					$("#uploadSection").append(JSON.stringify(json));
				}
				
				const param = new RequestParameter();
				param.setType("POST")
				 .setTarget("ImageAddAction")
				 .addSuccessCallback(requestSuccess)
				 .setData(dataParameter);

				param.send(param);
			}
	}
	
	return {
		load: load
	} 
})();

let listTemplet = (function(){
	let UploadTR = (function(){
		
		let parent = HTMLList;
		let UploadTR = function(){ 
			parent.apply(this,arguments); 
			return this;
		};
		
		const getImage = function(url){
			const image = new Image();
			image.src = url;
			return image;
		}
		
		UploadTR.prototype = Object.create(parent.prototype);
		UploadTR.prototype.constructor = UploadTR;
		UploadTR.prototype.$cloneDOMElement = $(".table_result tr.cloneable.item_image");
		
		UploadTR.prototype.setByBean = function( bean ){
			let $obj = this.$domElement;
			this.bean = bean;
			console.log(bean);
			
			
			$obj.removeClass("hide");
			$obj.find(".area_image").html( getImage(bean.thumbnail_url) );
			
			return this;
		}
		
		UploadTR.prototype.getSendData = function(){
			const $obj = this.$domElement;
			return {
				image_tags :  $obj.find(".inp_tag").val().split(/\s*\,\s*/),
				image_title :  $obj.find(".inp_title").val(),
				image_url :  this.bean.url,
				image_thumb_url :  this.bean.thumbnail_url,
			}
		}

		UploadTR.prototype.initEvent = function( ){

		}
		
		return UploadTR;
	})();
	
	return {
		UploadTR : UploadTR
	}
	
})();

var uploadDOM = (function(){

	let domDatas = {
			loadwrap : {
				$obj : $(".article_load"),
				hide: function(){ this.$obj.hide(); },
				show: function(){ this.$obj.show(); },
			},
			loadupload : {
				$obj : $(".article_upload"),				
				hide: function(){ this.$obj.hide(); },
				show: function(){ this.$obj.show(); },		
			},
			table : {
				$obj: $(".table_result"),				
				addAll: function(datas){
					datas.forEach(v => this.add(v) );
				},
				add: function(data){
					let tr = new listTemplet.UploadTR();
					tr.setByBean(data);
					this.$obj.append( tr.domElement );
				},
				getAllData: function(){
					const parameter = new ParameterData();
					parameter.addUser();
					
					const image_list = [];
					this.$obj.find("item_image").not(".hide").each(function(){
						let instance = this.instance;
						image_list.push( instance.getSendData() );
					})
					parameter.add("image_list",JSON.stringify(image_list) );
					return parameter;
				}
			},
			submit:{
				$obj:$(".btn_upload"),
				events:{
					click:function(event){
						let datas = domDatas.table.getAllData();
						requestSend.load.uploadIamge(datas);
					}
				}
			}

				
			
	}
	
	return {
		init : function(){
			EventController.defineAll( domDatas );			
		},
		reload : {
			uploadList : function(datas){
				domDatas.loadwrap.hide();
				domDatas.loadupload.show();
				domDatas.table.addAll(datas);
			},
			uploadInfoResult : function(){
				
			}
		},
		domDatas
	}
})();

var uploadClientDOM = (function(){
	let requestSuccess = function(error, result){
	      console.log(error, result) 
	      if(result.length > 0) uploadDOM.reload.uploadList(result);
	}
	
	let widgetConfig = { cloud_name: 'banipublic', upload_preset: 'gbzgarft'};
	
	let setDefaultConfig = function(){
		let configset = { cloud_name: 'banipublic', secure: false};
		$.cloudinary.config( configset );
	}
	
	let openClient = function(){
	    cloudinary.openUploadWidget(widgetConfig, requestSuccess);
	    $('.cloudinary-fileupload').cloudinary_fileupload({
			disableImageResize: true,
		});
	}
	
	return {
		init : function(){

			setDefaultConfig();
			openClient();
		}
	}
})();