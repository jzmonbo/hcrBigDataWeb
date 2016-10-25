$(function(){
    $('a').bind('focus', function(){
        this.blur();
    });
});

	var totalPage = 0;			//总页数
	var perPage = 10;			//每页条数
	var currentPage = 0;		//当前条数
	var linshiPage = 1;			//初使化临时当前条数
	
	function loadMore(){
		totalPage = $(".main-div-l").children(":last").children("div").size();
		currentPage = linshiPage * 10 - 1;
		for (var i=0;i<perPage+1;i++){
			currentPage++;
			if (currentPage<=totalPage){
				$('#contentdiv'+currentPage).css("display","block");
				var img = $('#contentdiv'+currentPage).children(".wz-div-img").children("a").children("img");
				var imgPath = img.attr("data-original");
				img.removeClass("lazy").removeAttr("data-original").attr("src",imgPath);
			}else if (currentPage>totalPage){
				break;
			}
		}
		linshiPage++;
		if (currentPage>totalPage){
			$('.morediv').remove();
		}
	}

function liover(lia){
		$(lia).children(":eq(1)").css('display','block');
	}
	
	function liout(lia){
		$(lia).children(":eq(1)").css('display','none');
	}
	function limyover(lia){
		$(lia).css("display","block");
	}
</script>
<script type="text/javascript">
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

	$(function(){
		$(".zjxq_nav1111").children("ul").children("li").each(function(k,v){
			$(v).hover(function(){
				$(this).addClass("zjxq_navhd");
				$(this).siblings().removeClass("zjxq_navhd");
			},function(){
				$(this).parent().children("li:eq(2)").addClass("zjxq_navhd");
				$(this).parent().children("li:not(:eq(2))").removeClass("zjxq_navhd");
			});
		});
	});