function mainController($scope,$timeout,$http) {
		$scope.showMain = true;
		$scope.showProperties = true;
		$scope.showPermissions = false;
		$scope.mainMenu = new Array();
                $scope.mainMenu.push({viewId: "properties",name:"Properties"});
                $scope.mainMenu.push({viewId: "permissions",name:"Permissions"});
                
		$scope.showView = function(view){
			if( view.hasOwnProperty("viewId") ){
				if( view.viewId === 'permissions' ){
					$scope.showProperties = false;
                                        $scope.showPermissions = true;
				}
				else {
                                        $scope.showPermissions = false;
					$scope.showProperties = true; 
				}
			}
		}
                
                $scope.properties = {};
                
                function propertiesChanged(response){
                    var data = response.data;
                        
                        if( data ){
                            console.log(data);
                            
                            if( data.items && data.items.length > 0 ){
                                $scope.properties = data.items[0];
                            }
                            else {
                                var defaultData = {solrinstallpath: "/opt/solr",
                                                    solrhostport: "localhost:8983",
                                                    solrzookeepertport: "localhost:9983",
                                                    solrdefaultcollection: "basic",
                                                    solradminuser: "solr",
                                                    solradminpwd: "SolrRocks"};
                                                    
                                $scope.properties = defaultData;
                            }
                        }
                        else {
                            console.log("error");
                        }
                }
                
                function loadProperties(){
                    
                    $http({method: 'GET',url: '/rest?contenttype=PROPERTIES'}).then(propertiesChanged);
                }
                
                $timeout(loadProperties,500);
                
                
                $scope.saveProperties = function(){
                    
                    var propertyData = $scope.properties;
                    $http({method: 'POST',url: '/rest?contenttype=PROPERTIES',data: propertyData}).then(propertiesChanged);
                    
                }
}