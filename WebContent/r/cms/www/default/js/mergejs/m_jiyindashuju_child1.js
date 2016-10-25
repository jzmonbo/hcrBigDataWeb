	var local = 0;					//箭头位置初使化
	var max_local = 3;
	//切换子会场嘉宾信息
	function lioverJiabin(lia,num,page){
		$(lia).addClass("this").append("<span></span>");
		$(lia).parent().siblings("li").children("a").removeClass("this").children("span").remove();
		$(".zt-b-con").each(function(k,v){
			if (num == k){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
		if (page.length > 0){
			$(".zt-b-conul").css("display","block");
		}else{
			$(".zt-b-conul").css("display","none");
		}
	}
	//切换嘉宾页数
	function lioverJiabinPage(lia,num){
		$(".zt-b-conul").children("ul").children("li").each(function(k,v){
			if (num == k){
				$(this).children("a").css("color","#fff").css("background-color","#f39300").css("border-radius","10px");
			}else{
				$(this).children("a").css("color","#9d9d9d").css("background-color","#fff").css("border-radius","10px");
			}
		});
		$(".zt-b-con").each(function(k,v){
			if (num == k){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
		local = num;
	}
	//切换子会场信息
	function liover(lia,num){
		$(lia).addClass("this").append("<span></span>");
		$(lia).parent().siblings("li").children("a").removeClass("this").children("span").remove();
		$(".top-ul-con-a").children("table").each(function(k,v){
			if (num == k){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
	}
	//左右箭头翻页
	function jiatou(toward){
		var num = -1;
		if ($(".zt-b-con").last().css("display") == "block"){
			return false;
		}
		if (toward == 'left'){
			if (local == 0){
				local = max_local;
			}else{
				local = local - 1;				
			}
		}else if (toward == 'right'){
			if (local == max_local){
				local = 0;
			}else{
				local = local + 1;
			}
		}
		num = local;
		$(".zt-b-conul").children("ul").children("li").each(function(k,v){
			if (num == k){
				$(this).children("a").css("color","#fff").css("background-color","#f39300").css("border-radius","10px");
			}else{
				$(this).children("a").css("color","#9d9d9d").css("background-color","#fff").css("border-radius","10px");
			}
		});
		$(".zt-b-con").each(function(k,v){
			if (num == k){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
	}