function buildApp(){
    var app = angular.module("mainApp", []);
    app.controller("mainController", mainController);
    
    app.directive('headerPage', function(){
            return {
                    restrict: 'E',
                    replace: true, 
                    templateUrl: 'pages/header-page.html'
            };
    });
    
    app.directive('footerPage', function(){
            return {
                    restrict: 'E',
                    replace: true, 
                    templateUrl: 'pages/footer-page.html'
            };
    });
    
    app.directive('mainPage', function(){
            return {
                    restrict: 'E',
                    replace: true, 
                    templateUrl: 'pages/main-page.html'
            };
    });
    
    app.directive('propertiesPage', function(){
            return {
                    restrict: 'E',
                    replace: true, 
                    templateUrl: 'pages/properties-page.html'
            };
    });
    
    app.directive('permissionsPage', function(){
            return {
                    restrict: 'E',
                    replace: true, 
                    templateUrl: 'pages/permissions-page.html'
            };
    });
}
