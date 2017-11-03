let request = (function(){
	let load = {
			uploadIamge : function(){
				
			}
	}
	
	return {
		load: load
	} 
})();

let uploadDOM = (function(){

	let domDatas = {
			loadwrap : {
				$obj : $(".article_load"),
				
			},
			loadupload : {
				$obj : $(".article_upload"),				
				hide: methodManage.get(this,"hide"),
				show: methodManage.get(this,"show"),				
			},
			table : {
				$obj: $(".trable_result"),				
				addAll: function(datas){
					datas.forEach(v => this.add(v) );
				},
				add: function(data){
					let $tr = domDatas.tr.get(data);
					this.$obj.append($tr);
				}
			},
			tr : {
				$clonable: $(".trable_result .blind.cloneable"),
				get : function(){
					let $tr = this.getClone();
				},
				getClone: function(){
					return this.$cloneable.removeClass("blind").removeClass("cloneable");
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
		domDatas:domDatas
	}
})();

let uploadClientDOM = (function(){
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