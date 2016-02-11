$(document).ready(function() {
    $('#username, #password').keypress(function (e) {
    if(e.which == 13)  // the enter key code
     {
       $('#loginButton').click();
       return false;  
     }
    });   
    
    $("#loginButton").click(function(){
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/login/",
            data: {name: username,password: password},
            statusCode: {
                200: function (response) {
                
               },
               401: function (response) {
                  alert('Invalid login.');
               }
            }
            ,success: function(loginResponse){
                document.cookie="userID="+loginResponse;
                
                window.location.href = "/ProjectV1/chatscreen.html";

            }
        });
 
    });

});