
function pickUpReset(){
	$("#pkUsername").attr("value","");
	$("#pkTelphone").attr("value","");
	$("#pkEmail").attr("value","");
	$("#pkCar").attr("value","");
	$("#pkDate").attr("value","");
	$("#pkCheat").attr("value","");
	}

function checkInfor(){
	var username = $("#pkUsername").val();
	var tel = $("#pkTelphone").val();
	var email = $("#pkEmail").val();
	var car = $("#pkCar").val();
	var date = $("#pkCheat").val();
	
	if(username=="" || username=="${flash.pkUsername}" ){
		alert("请填写正确的姓名");
		return;
	}
	
	if(tel=="" || tel=="${flash.pkTelphone}" ){
		alert("请填写正确的电话");
		return;
		
	}
	if(email=="" || email=="${flash.pkEmail}" ){
		alert("请填写正确的邮箱地址");
		 	return;
	}
	
	if(car=="" || car=="${flash.pkCar}" ){
		alert("请填写正确的车型");
		return;
	}
	
	if(date=="" || date=="${flash.pkDate}" ){
		alert("请填写正确的接机时间");
		 return;
	}
}

function checkUsername(){
	var username = $("#pkUsername").val();
	if(username=="" || username=="${flash.pkUsername}" ){
		alert("请填写你的姓名");
		return false;
	}
	
}
function checkTel(){
	var tel = $("#pkTelphone").val();
	if(tel=="" || tel=="${flash.pkTelphone}" ){
		alert("请填写正确的电话号码");
		return false;
	}
}
function checkCar(){
	var car = $("#pkCar").val();
	if(car=="" || car=="${flash.pkCar}" ){
		alert("请填写你的车型");
		return false;
	}
}
function checkDate(){
	var date = $("#pkDate").val();
	if(date=="" || date=="${flash.pkDate}" ){
		alert("请填写可接机的实践");
		return false;
	}
}
function checkCheat(){
	var cheat = $("#pkCheat").val();
	if(cheat=="" || cheat=="${flash.pkCheat}" ){
		alert("出错啦，请填写正确的电话号码");
		return false;
	}
}

 function checkEmail(){
    var temp = $("#pkEmail").val();
	var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/; 
  //验证邮箱的正则表达式 判断
  if(!reg.test(temp))
    {
        alert("邮箱格式不对");
        return false;
    } 
}

function stuInforReset(){
	$("#pkUsername").attr("value","");
	$("#pkTelphone").attr("value","");
	$("#pkEmail").attr("value","");
	$("#pkCar").attr("value","");
	$("#pkDate").attr("value","");
	$("#pkCheat").attr("value","");
	}