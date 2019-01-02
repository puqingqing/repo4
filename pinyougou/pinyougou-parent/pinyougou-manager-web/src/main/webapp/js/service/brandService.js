app.service('brandService',function($http){
    	
    	this.findAll=function(){
    		return  $http.get('../brand/findAll.do');
       	}
       	
    	
       	this.findPage=function(page,size){
    		return $http.get("../brand/findPage.do?page="+page+"&size="+size);
    	}
       	
       this.findSearch=function(page,size,searchEntity){
    	   return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity);
       }
       
      this.findOne=function(id){
    	 
    	 return $http.post("../brand/findOne.do?id="+id);
      }
      
     this.del=function(selectIds){
    	 return $http.get("../brand/delete.do?ids="+selectIds);
     }
     
     this.save=function(entry){
    	 return $http.post("../brand/save.do",entry);
     }
     
     this.update=function(entry){
    	 return $http.post("../brand/update.do",entry);
     }
     
     this.selectOptionList=function(){
    	 return $http.post("../brand/selectOptionList.do"); 
     }
       	
    } );
    
    