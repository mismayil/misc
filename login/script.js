
function checkMatch() {
    if (document.getElementById("txtpswrd").value==document.getElementById("txtcnfrm").value) {
	    document.getElementById("confirmError").innerHTML="Passwords match"; 
     } else document.getElementById("confirmError").innerHTML="Passwords don't match";	   
}