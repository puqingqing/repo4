app.controller('searchController',function($scope,$location,searchService){
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''}//搜索对象
	
	$scope.pageLable=[];//新建分页属性
	
	
	//加载查询字符串
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}

	$scope.keywordsIsBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
				return true;
				}
		}
		return false;
	}
	
	
	//排序
	$scope.sortSearch=function(sortField,sort){
		$scope.searchMap.sortField=sortField;
		$scope.searchMap.sort=sort;
		$scope.search();
	}
	
	//判断当前页是否是第一个
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}
	//判断是不是最后一页
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	
	//根据页码进行查询
	$scope.queryPage=function(pageNo){
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return ;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	
	
	//构建分页标签
	buildPageLable=function(){
	
		var maxPageNo=$scope.resultMap.totalPages;//得到最后页码
		
		var firstPage=1;//开始页码
		var lastPage=maxPageNo;//截至页码
		
		$scope.firstDot=true;//前面有点
		$scope.lastDot=true;//后面有点
		
		if($scope.resultMap.totalPages>5){
			
			if($scope.searchMap.pageNo<=3){
				lastPage=5;
		      $scope.firstDot=false;//前面的点没有了
			
			}else if($scope.searchMap.pageNo>=lastPage-2){
				firstPage=maxPageNo-4;
				$scope.lastDot=false;//后面的点没有了
				
			
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2
			}
		}else{
			
			$scope.firstDot=false;//前面无点
			$scope.lastDot=false;//后面无点
		}
		
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLable.push(i);
		}
		
	}
	
	
	
	
	//删除搜索的条件
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand' ||price=='price'){
			$scope.searchMap[key]="";
		}else{//规格
			delete $scope.searchMap.spec[key];//移除此属性
		}
		     $scope.search();//执行搜索
	}
	
	
	
	//添加搜索的条件
	$scope.addSearch=function(key,value){
		if(key=='category' || key=='brand'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();//执行搜索
	}
	//搜索
	$scope.search=function(){
	
		$scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo) 
		
		searchService.search($scope.searchMap).success(
			function(response){
				
				$scope.resultMap=response;	
			    buildPageLable()//调用
			}
		);		
	}
	
	
});