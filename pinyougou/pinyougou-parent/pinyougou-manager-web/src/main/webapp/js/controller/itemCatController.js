 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.parentId=0;//上级 ID
	
	/**
	  *根据父id进行查找 
	  */
	$scope.findByParentId=function(parentId){
	
		$scope.parentId=parentId;
		
		itemCatService.findParentById(parentId).success(
			function(response){
				$scope.list=response;
			})
	}
	
	$scope.grade=1;//默认为 1 级
	$scope.setGrade=function(value){
		$scope.grade=value;
	}
	
	$scope.selectList=function(p_entry){
		if($scope.grade==1){
			$scope.entry_1=null;
			$scope.entry_2=null;
		}
		
		if($scope.grade==2){
			$scope.entry_1=p_entry;
			$scope.entry_2=null;
		}
		 if($scope.grade==3){
		   $scope.entry_2=p_entry;
		 }
		 
		 $scope.findByParentId(p_entry.id);
	}
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entry.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entry ); //修改  
		}else{
			
			$scope.entry.parentId=$scope.parentId;
			
			serviceObject=itemCatService.add( $scope.entry);//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert('success');
					$scope.findByParentId($scope.parentId)//重新查询 
		        ;//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
