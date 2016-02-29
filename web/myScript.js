$(document).ready(function() {
    redirectToChat();
    
    $('#username, #password').keypress(function (e) {
        if(e.which == 13)  // the enter key code
         {
           $('#loginButton').click();
           return false;  
         }
    });
    
    function redirectToChat(){
        if (sessionStorage.credentials){
            console.log("Credentials found redirecting to chat");
            window.location.href = "/ProjectV1/chatscreen.html";
        }
    }
    $("#loginButton").click(function(){
        var username = $("#username").val();
        var password = $("#password").val();
        
        var headerString;
        if (sessionStorage.credentials){
            headerString = sessionStorage.credentials;
            console.log("sessionStorage found.");
        }else{
            headerString = "Basic " + btoa(username + ":" + password);
            console.log("sessionStorage not found.");
        }
         
         $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/login/",
            dataType: 'xml',
            headers:{
                "Authorization": headerString
            },statusCode: {
                200: function (response) {
                    
               },
               401: function (response) {
                  alert('Invalid login.');
               }
            }, success: function (loginResponse) {
                $('returninformation', loginResponse).each(function () {   
                    var userID = $(this).find("userID").text();
                    var credentials = $(this).find("authCred").text();
                    sessionStorage.setItem("credentials", credentials);
                    sessionStorage.setItem("userID", userID);
                    console.log(sessionStorage.credentials);
                    console.log(sessionStorage.userID);
                });
                window.location.href = "/ProjectV1/chatscreen.html";
            }        
        });
        
    });
    $('#newUsername, #newPassword').keypress(function (e) {
        if(e.which == 13)  // the enter key code
         {
           $('#createButton').click();
           return false;  
         }
    });
    $("#createButton").click(function(){
        var newName = $("#newUsername").val();
        var newPass = $("#newPassword").val();
        if(newName && newPass){
            $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/",
            data: {name: newName,password: newPass}
            ,success: function (createUserResponse) {
                $("#errorDiv").html(createUserResponse);
                if(createUserResponse.indexOf("User created") > -1){
                    $("#username").val(newName);
                    $("#password").val(newPass);
                    $("#loginButton").click();
                }
                console.log(createUserResponse);
            }        
         });
        }
    });
    
    $(".index_buttons").click(function(){
        $(".index_buttons").css("background-color","#6394cf");
        $(this).css("background-color","#366db0");
    });
    
     $("#login_form").click(function(){
        
        $("#inputToggle2").hide();
        $("#inputToggle").show(); 
    });
    
    $("#create_form").click(function(){
        
        $("#inputToggle").hide();
        $("#inputToggle2").show(); 
    });
});


