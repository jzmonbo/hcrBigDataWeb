	//切换子会场信息
	function selectSubPlay(subForm,num){
		//1.换掉标头的颜色
		$(subForm).addClass("this");
		$(subForm).parent().siblings("li").each(function(){
			$(this).children("a").removeClass("this");
		});
		//2.显示相应内容
		$("#subplace"+num).css("display","block");
		$("#subplace"+num).siblings("div").css("display","none");
	}
	
	function liover(lia){
		$(lia).children("p").css("display","block");
	}
	
	function liout(lia){
		$(lia).children("p").css("display","none");
	}