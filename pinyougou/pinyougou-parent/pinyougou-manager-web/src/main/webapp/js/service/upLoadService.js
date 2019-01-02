app.service("upLoadService",function($http){
	
	this.upload=function(){
		var formData=new FormData();
		 formData.append("file",file.files[0]); 
	      
	       return $http({
	    	   method:'POST',
	    	   url:"../upLoad.do",
	    	   data: formData,
	    	   headers: {'Content-Type':undefined},
	    	   transformRequest: angular.identity
	    	   });


	}
})