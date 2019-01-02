 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entry=response;
				$scope.entry.brandIds=JSON.parse($scope.entry.brandIds) ;	
				$scope.entry.specIds=JSON.parse($scope.entry.specIds) ;
				$scope.entry.customAttributeItems=JSON.parse($scope.entry.customAttributeItems) ;
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entry.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entry ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entry  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询所有的品牌
	$scope.brandList={data:[]};
	$scope.findBrandList=function(){
		brandService.selectOptionList().success(
				function(response){
			    $scope.brandList={data:response};
				})
	}
	
	$scope.specList={data:[]};
	$scope.findOptionList=function(){
		specificationService.selectOptionList().success(
				function(response){
			        $scope.specList={data:response};	
				})
	}
    
	$scope.entry={'customAttributeItems':[]}
	//删除选项
	$scope.deleTableRow=function(index){
		
		$scope.entry.customAttributeItems.splice(index,1);
	}
	
	//新增选项行
	$scope.addTableRow=function(){
		$scope.entry.customAttributeItems.push({});
	}
	
	////提取 json 字符串数据中某个属性，返回拼接字符串 逗号分隔
	$scope.jsonToString=function(jsonString,key){
		var json=JSON.parse(jsonString)//将 json 字符串转换为 json 对象
		
		var value="";
		
		for(var i=0;i<json.length;i++){
			if(i>0){
				value+=","
			}
			value+=json[i][key];
		}
		return value;
	}
});	
