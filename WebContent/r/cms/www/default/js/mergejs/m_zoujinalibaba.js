	var count = 1;
	function clickImg(opt){
		if ($("#imagecount").length > 0){
			var imagecount = $("#imagecount").val();
			if (opt == 'left'){
				if (count == 1){
					count = imagecount;
					imgDisplay(count);
				}else{
					count = count - 1;
					imgDisplay(count);
				}
			}
			if (opt == 'right'){
				if (count == imagecount){
					count = 1;
					imgDisplay(count);
				}else{
					count = count + 1;
					imgDisplay(count);
				}
			}
		}
	}
	
	function imgDisplay(num){
		var imagef = $("#Big_Slide");
		if (imagef.length > 0){
			imagef.children("ul").children("li").each(function(k,v){
				if (num == k+1){
					v.children("img").attr("display","block");
				}else{
					v.children("img").attr("display","none");
				}
			});
		}
	}