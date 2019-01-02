    
    
     app.controller('brandController' ,function($scope,brandService, $controller){
    	
    	 //继承
    	 $controller("baseController",{$scope:$scope})
    	 
    	 //读取列表数据绑定到表单中 
    	$scope.findAll=function(){
    	
    		brandService.findAll().success(
    	
    				 function(response){
    	
    					 $scope.list=response;
    	    	 }   	    		 
    		 ) }
    	 
    	
    	
    	   
    	
    	 
    	 /* 分页查询 */
         $scope.findPage=function(page,size){
        	 
        	 brandService.findPage(page,size) .success(
        			 function(response){
        		 $scope.list=response.rows;
        		 $scope.paginationConf.totalItems=response.total;
        		 
        	 })
        	 
         };
         
        
    	 
      /*添加数据  */
     

      $scope.save=function(){
    	  
          var object=null;
    	  if($scope.entry.id!=null){
    	    object=brandService.update($scope.entry);
    	  }else{
    		object=brandService.save($scope.entry);
    	  }
    		  
    		  
    	  object.success(
    	    function(response){
    		if(response.success){
    			$scope.reloadList();
    		}else{
    			alert(response.message)
    		}
    		
    	}) 
    	  
    	  
      };
      
      /* 修改功能开始 */
      $scope.findOne=function(id){
    	  brandService.findOne(id).success(
    	function(response){
    		$scope.entry=response;
	})
	
      };
      
      
     
      
      //删除选中行
      $scope.del=function(){
    	  brandService.del($scope.selectIds).success(
    	 function(response){
    		
    		if(response.success){
    			
    			$scope.reloadList()
    			
    		}else{
    			
    			alert(response.message)
    		}
    		  
    	  })
      };
      
      $scope.searchEntity={};//定义搜索对象 
  	
  	//搜索
  	$scope.search=function(page,rows){			
  		brandService.findSearch(page,rows,$scope.searchEntity).success(
  			function(response){
  				$scope.list=response.rows;	
  				$scope.paginationConf.totalItems=response.total;//更新总记录数
  			}			
  		);
  	}
 
    	
     });