 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService
		                    ,upLoadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entry={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
	$scope.itemCatList=[];//商品分类列表
	$scope.searchEntity={};//定义搜索对象 
	//更改状态
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(
				function(response){
					if(response.success){
						$scope.reloadList();//刷新列表
						$scope.selectIds=[];//清空 ID 集合
					}else{
						alert(response.message);
					}
				})
	}
	
	//目录级别的实现
	$scope.findItemCatList=function(){
		
		itemCatService.findAll().success(
				function(response){
					for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
					}
				})
	}
	
	
	
	
	
	
	
	//搜索
	$scope.search=function(page,rows){	
		
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//规格功能

	$scope.updateSpecAttribute=function($event,name,value){

		var object= $scope.searchObjectByKey($scope.entry.tbGoodsDesc.specificationItems,'attributeName',name);
		
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{//取消勾选
				object.attributeValue.splice( object.attributeValue.indexOf(value ),1);
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
				$scope.entry.tbGoodsDesc.specificationItems.splice(
				 $scope.entry.tbGoodsDesc.specificationItems.indexOf(object),1);

				}
				}
		}else{
			$scope.entry.tbGoodsDesc.specificationItems.push(
				{"attributeName":name,"attributeValue":[value]});
		}
			
			
	}
	
	
	$scope.createItemList=function(){
		$scope.entry.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ]	;//初始
		
		var items= $scope.entry.tbGoodsDesc.specificationItems;
		for(var i=0;i< items.length;i++){
		
			$scope.entry.itemList =addColumn( $scope.entry.itemList,items[i].attributeName,items[i].attributeValue ); 
		}
		
		
	}
	
	addColumn=function(list,columnName,columnValues){
		
		var newList=[];//新的集合
		for(var i=0;i<list.length;i++){
			var oldRow= list[i];
			for(var j=0;j<columnValues.length;j++){
			var newRow= JSON.parse( JSON.stringify( oldRow ) );//深克隆
			newRow.spec[columnName]=columnValues[j];
			newList.push(newRow);
			} 
			} 
			return newList;
	}
	
	
	
	//读取一级分类
	$scope.selectItemCat1List=function(){
		itemCatService.findParentById(0).success(
				function(response){
			        $scope.itemCat1List=response;
				})
	}
	//读取二级分类
	$scope.$watch('entry.tbGoods.category1Id',function(newValue, oldValue){
		itemCatService.findParentById(newValue).success(
				function(response){
			       $scope.itemCat2List=response;
				})
		
	})
	
	//读取三级分类
	$scope.$watch('entry.tbGoods.category2Id',function(newValue, oldValue){
		itemCatService.findParentById(newValue).success(
				function(response){
					
			   $scope.itemCat3List=response;
				})
		
	})
	
	//读取模版的ID
	$scope.$watch('entry.tbGoods.category3Id',function(newValue, oldValue){
		itemCatService.findOne(newValue).success(
				function(response){
					
			    $scope.entry.tbGoods.typeTemplateId=response.typeId;
				})
		
	})
	
	//品牌列表的选择
	$scope.$watch('entry.tbGoods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
				function(response){
				
					$scope.typeTemplate=response; //获取模版对象
					//将模版对象中的brandIds的json字符串转化为json格式的对象
					
					$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
					//扩展属性
					if($location.search()['id']==null){
				     $scope.entry.tbGoodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)
					}
					
				});
		
		    typeTemplateService.findSpecList(newValue).success(
				function(response){
					$scope.specList=response;
				})
	})
	
	
	//文件的上传
	$scope.uploadFile=function(){
		upLoadService.upload().success(function(response){
			if(response.success){
				alert(response.message);
				$scope.image_entity.url=response.message;
			}else{
				alert(response.message);
			}
		}).error(function(){
			alert("上传失败");
		})
	}
	
	
	
	//图片的添加
	$scope.add_image_entity=function(){
	$scope.entry.tbGoodsDesc.itemImages.push($scope.image_entity)
	}
	
	$scope.remove_image_entity=function(index){
		 $scope.entry.tbGoodsDesc.itemImages.splice(index,1);
		 }
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		var id=$location.search()['id'];
		if(id==null){
			return ;
		}
		
		goodsService.findOne(id).success(
			function(response){
				$scope.entry= response;	
				//显示富文本编辑器
				editor.html($scope.entry.tbGoodsDesc.introduction)
				//显示图片
				$scope.entry.tbGoodsDesc.itemImages
				           =JSON.parse($scope.entry.tbGoodsDesc.itemImages);
				//显示扩展属性
				$scope.entry.tbGoodsDesc.customAttributeItems
				          =JSON.parse($scope.entry.tbGoodsDesc.customAttributeItems);
				
				//规格
				$scope.entry.tbGoodsDesc.specificationItems
				         =JSON.parse($scope.entry.tbGoodsDesc.specificationItems);
				
				for(var i=0;i<$scope.entry.itemList.length;i++){
					
					$scope.entry.itemList[i].spec
					     =JSON.parse($scope.entry.itemList[i].spec);
						
				}
				
			}
		);				
	}
	
	$scope.checkAttributeValue=function(specName,optionName){
		
	 var item=$scope.entry.tbGoodsDesc.specificationItems;
	var object= $scope.searchObjectByKey(item,'attributeName',specName);
	if(object==null){
		return false;
	}else{
		if(object.attributeValue.indexOf(optionName)>0){
			return true;
		}else{
			return false;
		}
	}
	
	
	}
	
	
	//保存 
	$scope.add=function(){				
		
		$scope.entry.tbGoodsDesc.introduction=editor.html()
		
		var serviceObject;//服务层对象 
		if($scope.entry.tbGoods.id!=null){
			serviceObject=goodsService.update( $scope.entry ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entry)//增加
		}
		
	
		serviceObject.success(
			function(response){
				if(response.success){
					/*alert("保存成功")
					$scope.entry={};
					editor.html('');*/
					location.href="goods.html";////跳转到商品列表页
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	

    
});	
