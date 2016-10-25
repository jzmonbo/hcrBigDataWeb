	function liover(lia){
		$(lia).parent().children("li").each(function(){
			$(this).children("div").css("display","none");
		});
		$(lia).children("div").css('display','block');
	}
	
	function liout(lia,ev){
		$(lia).parent().children("li").each(function(){
			$(this).children("div").css("display","none");
		});
		//$(lia).siblings("div").css('display','none');
		//思路:判断鼠标位置是否在ul层外面,如果是显示第一个li企业图片
		mouseLocaltion(lia,ev);
	}
	
	function lioverBanner(lia){
		$(lia).children(".nav_img").css('display','none');
		$(lia).children(".nav_banner").css('display','block');
	}
	
	function lioutBanner(lia){
		$(lia).children(".nav_img").css('display','block');
		$(lia).children(".nav_banner").css('display','none');
	}
	
	function lioutUl(lia){
		$(lia).children("div").children("div").children("ul").children(":eq(1)").children("div").css("display","block");
	}
	
	function mouseLocaltion(lia,ev){
		//var x = window.event.clientX;
		//var y = window.event.clientY;
		var x,y;
		ev = ev || window.event; 
		if(ev.pageX || ev.pageY){ 
			x = ev.pageX;
			y = ev.pageY;
		}else{
			x = ev.clientX + document.body.scrollLeft - document.body.clientLeft, 
			y = ev.clientY + document.body.scrollTop - document.body.clientTop 
		}
		//alert("a [" + x + "] , y [" + y + "]");
		
		//var d_left = $(lia).parent().parent().offset().left; 
	    //var d_top = $(lia).parent().parent().offset().top; 
	    //var d_width = $(lia).parent().parent().outerWidth(true);
	    //var d_height = $(lia).parent().parent().outerHeight(true);
	
        var div = lia.parentNode.parentNode;
        var divx1 = div.offsetLeft;  
        var divy1 = div.offsetTop;  
        var divx2 = div.offsetLeft + div.offsetWidth;  
        var divy2 = div.offsetTop + div.offsetHeight;
        //alert("x["+x+"],y["+y+"],divx1["+divx1+"],divy1["+divy1+"],divx2["+divx2+"],divy2["+divy2+"],left["+div.offsetLeft+"],top["+div.offsetTop+"],width["+div.offsetWidth+"],height["+div.offsetHeight+"]");
        if( x < divx1 || x > divx2 || y < divy1 || y > divy2){ 
        	$(lia).parent().children(":eq(1)").children("div").css("display","block");
        }
	
		//alert("wx="+wx+",wy="+wy+",left="+d_left+",d_top="+d_top+",d_width="+d_width+",d_height="+d_height);
		//alert("wx < d_left [" + (wx < d_left) + "],wy <(t_top - d_height) [" + (wy <(t_top - d_height)) + "],(wx > (d_left + d_width) && wy > d_top) [" + ((wx > (d_left + d_width) && wy > d_top)) + "],wx > (d_left + d_width) [" + (wx > (d_left + d_width)) + "]");
		//alert("wx < d_left [" + (wx < d_left) + "],wx [" + wx + "], d_left [" + d_left + "]");
		//if(wx < d_left || wy < d_top || wx > (d_left + d_width) || wy > (d_top + d_height)){
		//if (wx < d_left || wy < (d_top - d_height) || (wx > (d_left + d_width) && wy > d_top) || wx > (d_left + d_width)){
		//	$(lia).parent().parent().children(":eq(1)").children("div").css("display","block");
		//}
		
	}

	function openCompany(liall){
		$(liall).parent().parent().children("li").each(function(){
			$(this).css("display","block");
		});
	}