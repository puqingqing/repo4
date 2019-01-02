app.service('loginService',function($http){
	this.name=function(){
		 return $http.get('../login/name.do');
	}
})