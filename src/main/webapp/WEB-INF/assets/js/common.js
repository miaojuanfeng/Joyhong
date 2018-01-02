$(document).ready(function(){
  var W = $(window).width();
  // 手机导航
  //$(function(){
    var menuwidth  = 180; // 边栏宽度
    var menuspeed  = 400; // 边栏滑出耗费时间
    
    var $bdy       = $('body');
    var $container = $('#pgcontainer');
    var $burger    = $('#hamburgermenu');
    var negwidth   = "-"+menuwidth+"px";
    var poswidth   = menuwidth+"px";
    
    //$(window).resize(function() { console.log('b');
    //  if(W<768){
        // $('#navbar').css('display','block');

        $('.menubtn').on('click',function(e){
          if($bdy.hasClass('openmenu')) {
            jsAnimateMenu('close');
          } else {
            jsAnimateMenu('open');
          }
        });
        
        $('.overlay').on('click', function(e){
          if($bdy.hasClass('openmenu')) {
            jsAnimateMenu('close');
          }
        });
        
        function jsAnimateMenu(tog) {
          if(tog == 'open') {
            $bdy.addClass('openmenu');
            
            $container.animate({marginRight: negwidth, marginLeft: poswidth}, menuspeed);
            $burger.animate({width: poswidth}, menuspeed);
            $('.overlay').animate({left: poswidth}, menuspeed);
          }
          
          if(tog == 'close') {
            $bdy.removeClass('openmenu');
            
            $container.animate({marginRight: "0", marginLeft: "0"}, menuspeed);
            $burger.animate({width: "0"}, menuspeed);
            $('.overlay').animate({left: "0"}, menuspeed);
          }
        }
    //  }
    //});
  //});

  // var foot_h = $(".footer .col-sm-8").height();
  // $(".footer .col-sm-4").css("line-height",foot_h+'px');
  // $(window).resize(function() {
  //   if( W>1200 ){
  //     var foot_h = $(".footer .col-sm-8").height();
  //     $(".footer .col-sm-4").css("line-height",foot_h+'px');
  //   };

  //   if( W<768 ){
  //     var foot_h = $(".footer .col-sm-8").height();
  //     $(".footer .col-sm-4").css("line-height",1);
  //   };

  // });
  
  // if( W<768 ){
  //   var foot_h = $(".footer .col-sm-8").height();
  //   $(".footer .col-sm-4").css("line-height",1);
  // };

  // 滑动导航
  $(".nav-list li div").hover(function (e) {

    // make sure we cannot click the slider
    if ($(this).hasClass('slider')) {
      return;
    }

    /* Add the slider movement */
    var liW = $(".nav-list li div").width();
    // what tab was pressed
    var whatTab = $(this).index();

    // Work out how far the slider needs to go
    var howFar = liW * whatTab;

    $(".slider").css({
      left: howFar + "px"
    });

    /* Add the ripple */

    // Remove olds ones
    $(".ripple").remove();

    // Setup
    var posX = $(this).offset().left,
        posY = $(this).offset().top,
        buttonWidth = $(this).width(),
        buttonHeight = $(this).height();

    // Add the element
    $(this).prepend("<span class='ripple'></span>");

    // Make it round!
    if (buttonWidth >= buttonHeight) {
      buttonHeight = buttonWidth;
    } else {
      buttonWidth = buttonHeight;
    }

    // Get the center of the element
    var x = e.pageX - posX - buttonWidth / 2;
    var y = e.pageY - posY - buttonHeight / 2;

    // Add the ripples CSS and start the animation
    $(".ripple").css({
      width: buttonWidth,
      height: buttonHeight,
      top: y + 'px',
      left: x + 'px'
    }).addClass("rippleEffect");

    
  });

  $(".nav-list li div").mouseenter(function(){
    $(this).siblings(".second-menu").stop().fadeIn(500);
    $(this).siblings(".second-menu").mouseenter(function(){
        $(this).stop().fadeIn(500);
    }).mouseleave(function(){
        $(this).stop().fadeOut(500);
    });

  }).mouseleave(function(){
    $(this).siblings(".second-menu").stop().fadeOut(500);
  })

  $(".header .text-right li").mouseenter(function(){
    $(this).children(".down_menu").stop().fadeIn(500);
  }).mouseleave(function(){
    $(this).children(".down_menu").stop().fadeOut(500);
  });
  $(".header .text-right .down_menu").mouseenter(function(){
      $(this).stop().fadeIn(500);
      
  }).mouseleave(function(){
      $(this).stop().fadeOut(500);
  });

  $("#hamburgermenu i").click(function(event) {
    $(this).siblings(".down_menu").stop().slideToggle(500);
    $(this).toggleClass("toggle");
  });
});

$(window).resize(function() {
  var li_w = $(".nav-list li").width();
  console.log(li_w);
  $(".nav .second-menu").css("width",li_w);
});

;(function ($) {
  $.fn.extend({
    ImgLoading: function (options) {
      var defaults = {
        errorimg: "http://www.oyly.net/Images/default/Journey/journeydetail.png",
        loadimg: "http://www1.ytedu.cn/cnet/dynamic/presentation/net_23/images/loading.gif",
        Node: $(this).find("img"),
        Parent: $('.vlign-center').find('a'),
        timeout: 1000
      };
      var options = $.extend(defaults, options);
      var Browser = new Object();
      var minHeight = 255;
      var plus = {
        BrowserVerify:function(){
          Browser.userAgent = window.navigator.userAgent.toLowerCase();
          Browser.ie = /msie/.test(Browser.userAgent);
          Browser.Moz = /gecko/.test(Browser.userAgent);
        },
        EachImg: function () {
          defaults.Node.each(function (i) {
            var img = defaults.Node.eq(i);
            plus.LoadEnd(Browser, img.attr("imgurl"), i, plus.LoadImg);
          });
        },
        LoadState:function(){
          defaults.Node.each(function (i) {
            var img = defaults.Node.eq(i);
            var url = img.attr("src");
            img.attr("imgurl", url);
            img.attr("src",defaults.loadimg);
          })
        },
        LoadEnd: function (Browser, url, imgindex, callback) {
          var val = url;
          var img = new Image();
          if (Browser.ie) {
            img.onreadystatechange = function () {
              if (img.readyState == "complete" || img.readyState == "loaded") {
                callback(img, imgindex);
              }
            }
          } else if (Browser.Moz) {
            img.onload = function () {
              if (img.complete == true) {
                callback(img, imgindex);
              }
            }
          }
          img.onerror = function () { img.src = defaults.errorimg }
          img.src = val;
        },
        LoadImg: function (obj, imgindex) {
          setTimeout(function () {
            defaults.Node.eq(imgindex).attr("src", obj.src);
            if( minHeight < defaults.Node.eq(imgindex)[0].height ){
              minHeight = defaults.Node.eq(imgindex)[0].height;
              defaults.Parent.each(function (i) {
                var a = defaults.Parent.eq(i);
                var img = defaults.Node.eq(i);
                a[0].style.maxHeight = minHeight + 'px';
                a[0].style.lineHeight = minHeight + 'px';
              });
            }
          }, defaults.timeout);
        }
      }
      plus.LoadState();
      plus.BrowserVerify();
      plus.EachImg();
    }
  }); 
})(jQuery);