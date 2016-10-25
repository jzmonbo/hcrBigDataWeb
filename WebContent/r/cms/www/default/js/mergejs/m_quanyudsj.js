	//切换子会场嘉宾信息
	function lioverJiabin(lia,num){
		$(lia).parent().parent().children("li").each(function(k,v){
			if (k == num){
				$(this).children("a").addClass("this");
				$(this).children("a").append("<span></span>");
			}else{
				$(this).children("a").removeClass("this");
				$(this).children("a").children("span").remove();
			}
		});
		var bd = $(lia).parent().parent().parent().siblings("div[id=jiabin]");
		bd.children("div").each(function(k,v){
			if (k == num){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
	}
	//切换子会场信息
	function liover(lia,num){
		$(lia).parent().parent().children("li").each(function(k,v){
			if (k == num){
				$(this).children("a").addClass("this");
				$(this).children("a").append("<span></span>");
			}else{
				$(this).children("a").removeClass("this");
				$(this).children("a").children("span").remove();
			}
		});
		var bd = $(lia).parent().parent().siblings("div").children("div");
		bd.children("table").each(function(k,v){
			if (k == num){
				$(this).css("display","block");
			}else{
				$(this).css("display","none");
			}
		});
	}
