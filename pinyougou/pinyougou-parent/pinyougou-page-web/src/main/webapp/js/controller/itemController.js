//控制层 
app.controller('itemController' ,function($scope){	
	
	$scope.specificationItems={};//记录用户选择的规格
   
   //加载默认 SKU
   $scope.loadSku=function(){
	   $scope.sku=skuList[0];
	   $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
   }
   
   
   //用户选择规格
   $scope.selectSpecification=function(key,value){
	   $scope.specificationItems[key]=value;
   }
   
  //判断某规格选项是否被用户选中
  $scope.isSelected=function(name,value){
	  if($scope.specificationItems[name]==value){
		  return true;
	  }else{
		  return false;
	  }
  }

	
	//在方法中控制数量不能小于 1
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		
		if($scope.num<1){
			$scope.num=1;
		}
	}
});	
