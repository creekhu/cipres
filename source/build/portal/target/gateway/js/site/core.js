/**
 * Foundation JavaScript for my english website.
 * utilities for the website
 * author: sheimi
 * version: 1
 */

/*
// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length)
  this.length = from < 0 ? this.length + from : from
  return this.push.apply(this, rest)
}
*/

if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
  return this.indexOf(str) == 0;
  };
}

//author kavinyao
!function($){
  $.fn.center = function() {
    return this.each(function(){
      var $this = $(this);
      $this.css("position", "absolute");
      $this.css("top", ($(window).height() - $this.height())/2 + $(window).scrollTop() + "px");
      $this.css("left", ($(window).width() - $this.width())/2 + $(window).scrollLeft() + "px");
    });
  };
  $.fn.topcenter = function() {
    return this.each(function(){
      var $this = $(this);
      $this.css("position", "fixed");;
      $this.css("top", "0px");;
      $this.css("left", "50%");;
    });
  };
  $.fn.set_highlight = function() {
    $(this).hover(function() {
      $(this).css({background: 'rgba( 255, 255, 190, 1)'});
    }, function() {
      $(this).css({background: 'transparent'});
    });
  };
}(jQuery);


!function(window){
  var notif = $('<div class="loading"></div>');
  notif.topcenter();
  notif.css({
    'padding': '7px 20px 7px 20px'
    , 'background': 'rgb(115, 171, 255)'
    , 'color': '#fff'
    , 'font-size': '18px'
    , 'border-bottom-left-radius': '2px'
    , 'border-bottom-right-radius': '2px'
    , 'z-index': '100000'
    , 'box-shadow': '2px 2px 5px rgba(0, 0, 0, 0.5)'
    , '-moz-box-shadow': '2px 2px 5px rgba(0, 0, 0, 0.5)'
    , '-webkit-box-shadow': '2px 2px 5px rgba(0, 0, 0, 0.5)'
    , '-ms-box-shadow': '2px 2px 5px rgba(0, 0, 0, 0.5)'
    , '-o-box-shadow': '2px 2px 5px rgba(0, 0, 0, 0.5)'
  });
  window.loading_msg = {
    show: function(message) {
      notif.text(message);
      notif.appendTo("body").hide();
      notif.css('margin-left', -notif.width()/2);
      notif.fadeIn();
    },
    hideWithMessage: function(message) {
      notif.text(message).hide();
      notif.css('margin-left', -notif.width()/2);
      notif.fadeIn().delay(2000).fadeOut();
    },
    hide: function() {
      notif.fadeOut(function() {
        notif.remove();
      });
    }
  };
}(window);

$(document).ready(function() {

  $(document).on('click', 'a.do-nothing', function(e) {
    e.preventDefault();
  });

  if (history.pushState === undefined) {
    $(document).on('click', 'a.mc-replace', function(e) {
      var href = $(this).attr('href');
      location.href = href;
    });
    return;
  }

  var originalUrl = location.pathname;
  var currentPath = location.pathname;

  function ajaxMainContent(url, cb) {
    if (currentPath === url) {
      return;
    }
    loading_msg.show("Loading page ... ");
    var appName = String(/^\/.+\//.exec(document.location.pathname));
    appName = appName.substr(0, appName.length - 1);
    $.ajax({
      url: url
      , data: { decorator:'raw' }
    }).success(function(data) {
      loading_msg.hide();
      if (data.startsWith('<!DOCTYPE')) {
        location.href=appName;
        return;
      }
      $('.main-content').html(data);
      currentPath = url;
      if (cb) cb();
      // MONA: this is a hack to reload the update profile page so the country field will show up
      if ( url == appName + "/updateProfile!input.action" )
      {
    	  /* Another way to do reload if blow doesn't work
    	  if(!window.location.hash) {
    	        window.location = window.location + '#loaded';
    	        window.location.reload();
    	    }
    	   */
    	  window.location.reload();
      }
    }).error(function() {
      location.href=appName;
    });
  }

  // if history.pushState != undefined use ajax to load main content
  $(document).on('click', 'a.mc-replace', function(e) {
    e.preventDefault();
    var href = $(this).attr('href');
    ajaxMainContent(href, function() {
      history.pushState({
        url: href
      }, '', href);
    });
  });

  $(window).on('popstate', function(e) {
    var state = e.originalEvent.state;
    if (state === null && originalUrl) {
      ajaxMainContent(originalUrl);
    }
    if (state !== null) {
      ajaxMainContent(state.url);
    }
  });

  $(document).on('click', '#show-content-toggle', function(e) {
    e.preventDefault();
    if ($('#contents').data('not-shown')) {
      $('#contents').fadeIn(); 
      $('#contents').data('not-shown', false);
    } else {
      $('#contents').fadeOut();
      $('#contents').data('not-shown', true);
    }
  });
});
