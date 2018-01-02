/*
* File: jquery.flexisel.js
* Version: 1.0.1
* Description: Responsive carousel jQuery plugin
* Author: 9bit Studios
* Copyright 2012, 9bit Studios
* http://www.9bitstudios.com
* Free to use and abuse under the MIT license.
* http://www.opensource.org/licenses/mit-license.php
*/

(function ($) {

    $.fn.flexisel = function (options) {

        var defaults = $.extend({
    		visibleItems: 2,   //一行显数的个数
            animationSpeed: 3000, //动画时间
            autoPlay: true,     //自动播放
            autoPlaySpeed: 5000, //播放间隔时间
            pauseOnHover: true,  //鼠标悬浮是否停止播放
            clone:false,         //是否使用克隆
            enableResponsiveBreakpoints: true, //是否开启响应式
            responsiveBreakpoints: {
              portrait: {    //项目名，可随意命名
                changePoint:480,  //屏幕最大像素，意思为当屏幕最大像素为480时，只显示一个Item，即例子只显示一张图片。
                visibleItems: 1   //只显示一个Item，下面的雷同
              }, 
              landscape: {
                changePoint:640,
                visibleItems: 2
              },
              tablet: {
                changePoint:768,
                visibleItems: 2
              }
            }
        }, options);
        
		/******************************
		Private Variables
		*******************************/         
        
        var object = $(this);
		var settings = $.extend(defaults, options);        
		var itemsWidth; // Declare the global width of each item in carousel
		var canNavigate = true; 
        var itemsVisible = settings.visibleItems; 
        var responsivePoints = [];
		
		/******************************
		Public Methods
		*******************************/        
        
        var methods = {
        		
			init: function() {
				
        		return this.each(function () {
        			methods.appendHTML();
        			methods.setEventHandlers();      			
        			methods.initializeItems();
					
				});
			},

			/******************************
			Initialize Items
			Set up carousel
			*******************************/			
			
			initializeItems: function() {
				
				var listParent = object.parent();
				var innerHeight = listParent.height(); 
				var childSet = object.children();
				methods.sortResponsiveObject(settings.responsiveBreakpoints);
				
    			var innerWidth = listParent.width(); // Set widths
    			itemsWidth = (innerWidth)/itemsVisible;
    			childSet.width(itemsWidth);
    			childSet.last().insertBefore(childSet.first());
    			childSet.last().insertBefore(childSet.first());
    			object.css({'left' : -itemsWidth}); 

    			object.fadeIn();
				$(window).trigger("resize"); // needed to position arrows correctly
				
			},
			
			
			/******************************
			Append HTML
			Wrap list in markup with classes needed for carousel to function
			*******************************/			
			
			appendHTML: function() {
				
   			 	object.addClass("nbs-flexisel-ul");
   			 	object.wrap("<div class='nbs-flexisel-container'><div class='nbs-flexisel-inner'></div></div>");
   			 	object.find("li").addClass("nbs-flexisel-item");
 
   			 	if(settings.setMaxWidthAndHeight) {
	   			 	var baseWidth = $(".nbs-flexisel-item").width();
	   			 	var baseHeight = $(".nbs-flexisel-item").height();
	   			 	$(".nbs-flexisel-item").css("max-width", baseWidth);
	   			 	$(".nbs-flexisel-item").css("max-height", baseHeight);
   			 	}
 
   			 	$("<div class='nbs-flexisel-nav-left'></div><div class='nbs-flexisel-nav-right'></div>").insertAfter(object);
   			 	var cloneContent = object.children().clone();
   			 	object.append(cloneContent);
			},
					
			
			/******************************
			Set Event Handlers
			Set events for carousel
			*******************************/
			setEventHandlers: function() {
				
				var listParent = object.parent();
				var childSet = object.children();
				var leftArrow = listParent.find($(".nbs-flexisel-nav-left"));
				var rightArrow = listParent.find($(".nbs-flexisel-nav-right"));
				
				$(window).on("resize", function(event){
					
					methods.setResponsiveEvents();
					
					var innerWidth = $(listParent).width();
					var innerHeight = $(listParent).height(); 
					
					itemsWidth = (innerWidth)/itemsVisible;
					
					childSet.width(itemsWidth);
					object.css({'left' : -itemsWidth});
					
					var halfArrowHeight = (leftArrow.height())/2;
					var arrowMargin = (innerHeight/2) - halfArrowHeight;
					leftArrow.css("top", arrowMargin + "px");
					rightArrow.css("top", arrowMargin + "px");
					
				});					
				
				$(leftArrow).on("click", function (event) {
					methods.scrollLeft();
				});
				
				$(rightArrow).on("click", function (event) {
					methods.scrollRight();
				});
				
				if(settings.pauseOnHover) {
					$(".nbs-flexisel-item").on({
						mouseenter: function () {
							canNavigate = false;
						}, 
						mouseleave: function () {
							canNavigate = true;
						}
					 });
				}

				if(settings.autoPlay) {
					
					setInterval(function () {
						if(canNavigate)
							methods.scrollRight();
					}, settings.autoPlaySpeed);
				}
				
			},
			
			/******************************
			Set Responsive Events
			Set breakpoints depending on responsiveBreakpoints
			*******************************/			
			
			setResponsiveEvents: function() {
				var contentWidth = $('html').width();
				
				if(settings.enableResponsiveBreakpoints) {
					
					var largestCustom = responsivePoints[responsivePoints.length-1].changePoint; // sorted array 
					
					for(var i in responsivePoints) {
						
						if(contentWidth >= largestCustom) { // set to default if width greater than largest custom responsiveBreakpoint 
							itemsVisible = settings.visibleItems;
							break;
						}
						else { // determine custom responsiveBreakpoint to use
						
							if(contentWidth < responsivePoints[i].changePoint) {
								itemsVisible = responsivePoints[i].visibleItems;
								break;
							}
							else
								continue;
						}
					}
				}
			},

			/******************************
			Sort Responsive Object
			Gets all the settings in resposiveBreakpoints and sorts them into an array
			*******************************/			
			
			sortResponsiveObject: function(obj) {
				
				var responsiveObjects = [];
				
				for(var i in obj) {
					responsiveObjects.push(obj[i]);
				}
				
				responsiveObjects.sort(function(a, b) {
					return a.changePoint - b.changePoint;
				});
			
				responsivePoints = responsiveObjects;
			},				
			
			/******************************
			Scroll Left
			Scrolls the carousel to the left
			*******************************/				
			
			scrollLeft:function() {

				if(canNavigate) {
					canNavigate = false;
					
					var listParent = object.parent();
					var innerWidth = listParent.width();
					
					itemsWidth = (innerWidth)/itemsVisible;
					
					var childSet = object.children();
					
					object.animate({
							'left' : "+=" + itemsWidth
						},
						{
							queue:false, 
							duration:settings.animationSpeed,
							easing: "linear",
							complete: function() {  
								childSet.last().insertBefore(childSet.first()); // Get the first list item and put it after the last list item (that's how the infinite effects is made)   								
								methods.adjustScroll();
								canNavigate = true; 
							}
						}
					);
				}
			},
			
			/******************************
			Scroll Right
			Scrolls the carousel to the right
			*******************************/				
			
			scrollRight:function() {
				
				if(canNavigate) {
					canNavigate = false;
					
					var listParent = object.parent();
					var innerWidth = listParent.width();
					
					itemsWidth = (innerWidth)/itemsVisible;
					
					var childSet = object.children();
					
					object.animate({
							'left' : "-=" + itemsWidth
						},
						{
							queue:false, 
							duration:settings.animationSpeed,
							easing: "linear",
							complete: function() {  
								childSet.first().insertAfter(childSet.last()); // Get the first list item and put it after the last list item (that's how the infinite effects is made)   
								methods.adjustScroll();
								canNavigate = true; 
							}
						}
					);
				}
			},
			
			/******************************
			Adjust Scroll 
			Needed to position arrows correctly on init and resize
			*******************************/
			
			adjustScroll: function() {
				
				var listParent = object.parent();
				var childSet = object.children();				
				
				var innerWidth = listParent.width(); 
				itemsWidth = (innerWidth)/itemsVisible;
				childSet.width(itemsWidth);
				object.css({'left' : -itemsWidth});		
			}			
        
        };
        
        if (methods[options]) { 	// $("#element").pluginName('methodName', 'arg1', 'arg2');
            return methods[options].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof options === 'object' || !options) { 	// $("#element").pluginName({ option: 1, option:2 });
            return methods.init.apply(this);  
        } else {
            $.error( 'Method "' +  method + '" does not exist in flexisel plugin!');
        }        
};

})(jQuery);

