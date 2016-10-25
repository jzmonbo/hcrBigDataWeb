	/*$(function(){
		var thisURL = document.URL;    
		var  getval =thisURL.split('?')[1];  
		var showval= getval.split("=")[1];  
		$("#selectId").val(showval);
		selectContact(showval);
	});*/
	
	function selectContact(n){
		$('#menu ul li').each(function(){
			alert($(this));
			if ($(this).index() == n){
				$(this).addCss("this");
			}else{
				$(this).removeCss("this");
			}
		});
	}

var title = $("#contact h3").text();
$(".f-nav-div ul li").each(function(){
	var ta = $(this).children("a");
	if (ta.text() == title){
		ta.css("color","orange");
	}else{
		ta.css("color","#666");
	}
});

$(function(){
$("a").each(function(){
	var val = $(this).attr("href");
	if (indexNum(val) == 1){
		var value = val.substring(0,val.indexOf("="));
		val = val.substring(val.indexOf("=")+1);
		$(this).attr("href",value+"="+encodeURIComponent(val));
	}
});
});


function indexNum(str){
var flag = true;
var count = 0;
while (flag){
	if (str == null || str == ""){
		break;
	}
	if (str.indexOf("=") > 0){
		count = count + 1;
		str = str.substring(str.indexOf("=")+1);
	}else{
		break;
	}
}
return count;
}