$(function(){
	$('#page_sub_head_industry_list').find('img').mouseout(function(){
		var imgname_path='/Public/WWW/pc/images/'+$(this).attr('out')+'.png';
		$(this).attr('src',imgname_path);
	}).mouseover(function(){
		var imgname_path='/Public/WWW/pc/images/'+$(this).attr('out')+'_hover.png';
		$(this).attr('src',imgname_path);
	})
	
	$('.header_nav').find('li').mouseover(function(){
		$(this).find('.icon_arro').attr('src', '/Public/WWW/pc/images/icon_arro_hover.png');
		$(this).find('.topnav_sub_menu').show();
	}).mouseleave(function(){
		$(this).find('.icon_arro').attr('src', '/Public/WWW/pc/images/icon_arro.png');
		$(this).find('.topnav_sub_menu').hide();
	})
	
	// 登录之后个人中心下拉菜单
	$('.header_nav_personag').find('#header_nav_sub_menu').mouseover(function(){
		var essay_pulldown_personag = $('.essay_pulldown_personag').height();
		$(this).height(essay_pulldown_personag);
		$(this).find('.essay_pulldown_personag').show();
	}).mouseleave(function(){
		$(this).height('30px');
		$(this).find('.essay_pulldown_personag').hide();
	})
	
	// 提交表单（登录、注册）
	$(document).on('submit', ".form_post", function () {
		var self = $(this);
		$.post(self.attr("action"), self.serialize(), success, "json");
		return false;
		function success(data) {
			if (data.status) {
				message=data.info?data.info:'欢迎您登录亿欧网，正跳转至会员中心';
				toast.success(message, '温馨提示');
				setTimeout(function () {
					window.location.href = data.url;
				}, 1500);
			} else {
				toast.error(data.info, '温馨提示');
				setTimeout(function () {
					$(".toast-close-button").click();
				}, 1500);
				//self.find(".Validform_checktip").text(data.info);
				//刷新验证码
				$(".reloadverify").click();
			}
		}
    });
    
    // 刷新验证码
    var verifyimg = $(".verifyimg").attr("src");
    $(document).on('click', ".reloadverify", function(){
        if( verifyimg.indexOf('?')>0){
            $(".verifyimg").attr("src", verifyimg+'&random='+Math.random());
        }else{
            $(".verifyimg").attr("src", verifyimg.replace(/\?.*$/,'')+'?'+Math.random());
        }
    });
    
    // 微信登录
    $(document).on('click', '.weixinlogin', function(){
    	$('#alert_box').remove();
		setloginbox('/index.php?s=/user/wxlogin/type/weixin.html');
		return false;
	})
    
    // 热点咨询切换
    $('#top_post').find('.top_post_item').hide();
	$('#top_post').find('.' + $('#top_post_filter').find('li').eq(0).attr('id')).show();
    $('#top_post_filter').on('mouseover', 'li', function(){
		$('#top_post_filter').find('li').removeClass('top_post_filter_active');
		$(this).addClass('top_post_filter_active');
		$('#top_post').find('a').hide();
		$('#top_post').find('.' + $(this).attr('id')).show();
    })
	
	// 弹出层（登录、注册、查询）
	$(document).on('click', '.alertbox', function(){
		$('#alert_box').remove();
		alertbox($(this));
	})
	
	// 侧边栏显示、隐藏微信
	$('#display_weixin').click(function(){
		$(this).next().next().toggle();
	})
	
	// 作者列表页面显示、隐藏微信
	$('.business_card_weixin').mouseover(function(){
		$(this).parents('li').next().find('.author_wechat').show();
	}).mouseout(function(){
		$(this).parents('li').next().find('.author_wechat').hide();
	})
	
	$('.exercise_option > .exercise_option_indent').mouseover(function() {
		$(this).find('em').addClass('my_action').end().siblings().find('em').removeClass('my_action');
		$('.table-responsive:eq('+$(this).index()+')').show().siblings('.table-responsive').hide();
	});
	var urls=window.location.href.split('#');
	var url_true = ['register','binding','login','wxlogin','search'];
	if(urls.length>1){
		for(var i=0; i < url_true.length; i++){
			if(urls[1] == url_true[i]){
				alertbox('',urls[1]);
			}
		}
	}
	
	// 活动筛选选择
	$('.activity_list,.activity_newest').mouseover(function(){
		$('.option_list').hide();
		$('#activity_seek').find('li').removeClass('active');
		$(this).addClass('active');
		$('#'+$(this).attr('data-id')).show();
	})
	if ($('#activity_seek')) {
		var activity_seek = $('#activity_seek').find('.active').attr('data-id');
	    $('#' + activity_seek).show();
	}
		
    // 活动搜索框
    $('.activity_seek_seek').focus(function(){
    	//alert();
    	$(this).css('width', '194px');
    }).blur(function(){
    	if(!$(this).val()){
    		$(this).css('width', '90px');
    	}
    })
    $('#search_frame').submit(function(){
    	location.href = '/a/s/title/' + $(this).find('input[name=title]').val();
    	return false;
    })
    $('#search_frame_topic').submit(function(){
    	location.href = '/topic/s/title/' + $(this).find('input[name=title]').val();
    	return false;
    })
    
    // 快讯显示、隐藏
    // $('#news_flash').on('click', 'li', function(){
    // 	var obj = $(this);
    // 	if(obj.find('.news_flash_title_selected').length){
    // 		$('#news_flash').find('.news_flash_title').removeClass('news_flash_title_selected');
	   //  	$('#news_flash').find('.news_flash_content').hide();
    // 	}else{
	   //  	$('#news_flash').find('.news_flash_title').removeClass('news_flash_title_selected');
	   //  	obj.find('.news_flash_title').addClass('news_flash_title_selected');
	   //  	$('#news_flash').find('.news_flash_content').hide();
	   //  	obj.find('.news_flash_content').show();
	   //  }
    // })
})
// 弹出层
function alertbox(e,obj){
	//var urls=window.location.href.split('#');
	try{
		urls=e.attr('href');
		urls=urls.split('#');
		if(urls.length==1){ return false}
		objid=urls[1];
	}catch(e){
		objid=obj
	}
	if(!objid) return false;
	if($('#'+objid).length<=0) return false;
	var content=$('#'+objid).html();
	$('body').css('overflow','hidden');
	//$('body').append("<div id='alert_box'></div>");
	var alert_box_div=$("<div>");
	alert_box_div.attr('id','alert_box');
	var alert_box_wrap_div=$('<div>');
	alert_box_wrap_div.addClass('alert_box_wrap');
	var close_alert_box_div=$('<div>');
	close_alert_box_div.addClass('close_alert_box alert_box_cursor');
	var alert_box_content_div=$('<div>');
	alert_box_content_div.addClass('alert_box_content_div');
	alert_box_content_div.append(content);
	alert_box_wrap_div.append(alert_box_content_div);
	alert_box_content_div.append(close_alert_box_div);
	alert_box_div.append(alert_box_wrap_div);
	$('body').append(alert_box_div);
	//$('#alert_box').html('<div class="alert_box_wrap"><div class="close_alert_box alert_box_cursor"></div><div class="alert_box_content"></div></div>');
	$('#alert_box').fadeIn(500,function(){
		if(objid=='search'){
			$('.input_txt_search').focus();
		}
	});
	
	$('.alert_box_cursor').click(function(){
		alertclose();
	})
	function alertclose(){
		$('#alert_box').fadeOut(500,function(){
			$(this).remove();
			var urls=window.location.href.split('#');
			if(urls.length==1){ return false}
			window.location.href=urls[0]+'#';
		});
		$('body').css('overflow','');
	}
}

// 显示密码（登录和注册）
function change_show(obj){
	var type = $(obj).prev().attr('type');
	if (type == undefined) {
		type = $(obj).prev().prev().attr('type');
		if (type == 'password') {
			$(obj).prev().prev().attr('type','text');
		}else{
			$(obj).prev().prev().attr('type','password');
		}
	}else{
		if (type == 'password') {
			$(obj).prev().attr('type','text');
		}else{
			$(obj).prev().attr('type','password');
		}
	}
}

// 微信登录
function setloginbox(url){
	var html='<div id="loginbox" style=" position:fixed; z-index:99; display:none; background:#FFF;border-radius: 0.4286rem;background:#FFF">\
				<div style="border: medium none;border-top-left-radius:0.4286rem;border-top-right-radius: 0.4286rem;background:#FFF"></div>\
				<div>\
					<iframe width="300px" height="400px" frameborder="0" scrolling="no" src="'+url+'"></iframe>\
				</div>\
				<div style="border-bottom-left-radius: 0.4286rem;   border-bottom-right-radius: 0.4286rem;background:#FFF"></div>\
			</div>\
			<div id="bigbox" style=" position:absolute; z-index:98; background:#000; top:0; left:0; display:none"></div>';
	$('body').append(html);
	var windowWidth=$(window).width();
	var windowHeight=$(window).height();
	var width=$('#loginbox').width();
	var height=$('#loginbox').height();
	var scollTop=$('body').scrollTop();
	var left=(windowWidth-width)/2;
	var top=(windowHeight-scollTop-height)/2
	var w=Math.max($('body').width(),windowWidth);
	var h=Math.max($('body').height(),windowHeight);
	$('#loginbox').css({'left':left,'top':top}).show();
	$('#bigbox').css({"width":w+'px','height':h+'px','filter':'alpha(opacity=50)','-moz-opacity':0.5,'-khtml-opacity': 0.5,'opacity': 0.5}).show();
	$('#bigbox').click(function(){
		$('#loginbox,#bigbox').remove();
	})
}

$(function(){
	var nowtop=$(window).scrollTop();
	var followustop=0;
	if($('#followus').size()>0){
		followustop=$('#followus').offset().top;
	}
	$(window).scroll(function() {
	
		var top=$(this).scrollTop();
		if($('#followus').size()>0){
			if(!$('#followus').hasClass('position')){
				followustop=$('#followus').offset().top;
			}
			if(top+55>=followustop){
				$('#followus').addClass('position');
			}else{
				$('#followus').removeClass('position');
			}
		}
		if(top>nowtop){
			$('#page_head').removeClass('down').addClass('up');
			$('#page_sub_head').removeClass('down').addClass('up');
		}else{
			$('#page_head').removeClass('up').addClass('down');
			$('#page_sub_head').removeClass('up').addClass('down');
		}
		nowtop=top;
	});
})
