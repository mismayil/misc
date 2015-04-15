 
$(document).ready(function() {
  $("#start").click(function() {
  var n=10;
  var t=10;
  $(".ball").css({"position" : "relative","top" : "0px", "left" : "0px"});
  $(".ball").show();
  var timer=setInterval(function(){myTimer()},1000);
  function myTimer() {
  $("#time").text(t--);
  if (t==-1) {
  clearInterval(timer);
  $(".ball").stop(true,true);
  $(".ball").hide();
  }
  $("#reset").click(function(){
  $(".ball").stop(true);
  $(".ball").css({"position" : "relative","top" : "0px", "left" : "0px"});
  $(".ball").show();
  clearInterval(timer);
  $("#time").text(10);
  });
  }
  while (n>0) {
     $("#b1").animate({left: '550px'},2000).animate({left: '0px'},2500);
	 $("#b2").animate({left: '550px'},3000).animate({left: '0px'},3500);
	 $("#b3").animate({left: '550px'},4000).animate({left: '0px'},4500);
	 $("#b4").animate({left: '550px'},5000).animate({left: '0px'},5500);
     n--;
  }
});
});

$(function() {
  $("*").keydown(function(event) {
         if (event.which=="32") {
            $("#shot").animate({bottom: '390px'},500,function() {
                              $("#shot").hide("fast",function() {
                                        $("#bullet").html('<div id="shot"></div>');
										});
					  var score=0;
  var shotPosTop=parseInt($("#shot").position().top);
  var shotPosLeft=parseInt($("#shot").position().left);
  
  var b1PosTop=parseInt($("#b1").position().top);
  var b1PosLeft=parseInt($("#b1").position().left);
  
  var b2PosTop=parseInt($("#b2").position().top);
  var b2PosLeft=parseInt($("#b2").position().left);
  
  var b3PosTop=parseInt($("#b3").position().top);
  var b3PosLeft=parseInt($("#b3").position().left);
  
  var b4PosTop=parseInt($("#b4").position().top);
  var b4PosLeft=parseInt($("#b4").position().left);
  
  if (((shotPosLeft>b1PosLeft)&&(shotPosLeft<(b1PosLeft+30))&&(shotPosTop>b1PosTop)&&(shotPosTop<(b1PosTop+30))) ||
     ((shotPosLeft>b2PosLeft)&&(shotPosLeft<(b2PosLeft+30))&&(shotPosTop>b2PosTop)&&(shotPosTop<(b2PosTop+30))) ||
	 ((shotPosLeft>b3PosLeft)&&(shotPosLeft<(b3PosLeft+30))&&(shotPosTop>b3PosTop)&&(shotPosTop<(b3PosTop+30))) ||
	 ((shotPosLeft>b4PosLeft)&&(shotPosLeft<(b4PosLeft+30))&&(shotPosTop>b4PosTop)&&(shotPosTop<(b4PosTop+30))))
	 {
	 score+=5;
	 $("#sco").text(score);
	 }
					});
	 }
  var x=$("#tank").position().left;
  if (event.which=="37") {
    if (x>15) {
      $("#tank").animate({left: '-=15px'},"fast");
    }
  }  
  if (event.which=="39") {
    if (x<520) {
      $("#tank").animate({left: '+=15px'},"fast");
    }
  }
  if (event.which=="40") {
    $("#tank").stop();
  }
});
}
);

/*$(function() {
  var score=0;
  var shotPosTop=parseInt($("#shot").position().top);
  var shotPosLeft=parseInt($("#shot").position().left);
  
  var b1PosTop=parseInt($("#b1").position().top);
  var b1PosLeft=parseInt($("#b1").position().left);
  
  var b2PosTop=parseInt($("#b2").position().top);
  var b2PosLeft=parseInt($("#b2").position().left);
  
  var b3PosTop=parseInt($("#b3").position().top);
  var b3PosLeft=parseInt($("#b3").position().left);
  
  var b4PosTop=parseInt($("#b4").position().top);
  var b4PosLeft=parseInt($("#b4").position().left);
  
  if (((shotPosLeft>b1PosLeft)&&(shotPosLeft<(b1PosLeft+30))&&(shotPosTop>b1PosTop)&&(shotPosTop<(b1PosTop+30))) ||
     ((shotPosLeft>b2PosLeft)&&(shotPosLeft<(b2PosLeft+30))&&(shotPosTop>b2PosTop)&&(shotPosTop<(b2PosTop+30))) ||
	 ((shotPosLeft>b3PosLeft)&&(shotPosLeft<(b3PosLeft+30))&&(shotPosTop>b3PosTop)&&(shotPosTop<(b3PosTop+30))) ||
	 ((shotPosLeft>b4PosLeft)&&(shotPosLeft<(b4PosLeft+30))&&(shotPosTop>b4PosTop)&&(shotPosTop<(b4PosTop+30))))
	 {
	 score+=5;
	 $("#sco").text(score);
	 }
});*/

