
			$(function(){
				$(".btn").click(function(){$(".fm").submit()});
				$(".fm").validate({
					rules:{
						'email':{
							required:true,
							email:true
						},
						'password':{
							required:true,
							rangelength:[6,18]
						}
					},
					messages:{
						email:{
							required:'请输入电子邮件地址',
							email:'电子邮件格式错误'
						},
						password:{
							required:"请输入密码",
							rangelength:"密码长度为6~18位"
						}

					}
    		});
    	});
		
		function change(){
			$("#fir").fadeOut(1000);
			$("#denglu_page").addClass("hide");
			$("#send").fadeIn(1000);
			$("#zuce_page").removeClass("hide");	

		}
		function change_page(){
			$("#fir").fadeIn();
			$("#denglu_page").removeClass("hide");
			$("#send").fadeOut();	
			$("#zuce_page").addClass("hide");
		}
	