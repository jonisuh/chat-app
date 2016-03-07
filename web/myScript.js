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
                $('returninformation', response).each(function () {   
                    var userID = $(this).find("userID").text();
                    var credentials = $(this).find("authCred").text();
                    sessionStorage.setItem("credentials", credentials);
                    sessionStorage.setItem("userID", userID);
                    console.log(sessionStorage.credentials);
                    console.log(sessionStorage.userID);
                });
                window.location.href = "/ProjectV1/chatscreen.html";
               },
               401: function (response) {
                  alert('Invalid login.');
               }
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
        var fname = $("#firstname").val();
        var lname = $("#lastname").val();
        var department = $("#department").val();
        var title = $("#title").val();
        var email = $("#email").val();
        var validation = validateInput();
        if(validation === true){
            $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/",
            data: {name: newName,password: newPass,firstname: fname,lastname: lname,department:department,title: title,email: email},
            statusCode: {
                200: function (response) {
                    $("#username").val(newName);
                    $("#password").val(newPass);
                    $("#loginButton").click();
               },
               400: function (response) {
                  
               }
            }     
         });
        }
    });
    
    function validateInput(){
        var newName = $("#newUsername").val();
        var newPass = $("#newPassword").val();
        var passwordAgain = $("#newPasswordAgain").val();
        var fname = $("#firstname").val();
        var lname = $("#lastname").val();
        var email = $("#email").val();
        
        $(".registrationError").html("");
        $(".registrationError").hide();
        $(".registrationArrow").hide();
        
        var validationResult = true;
        
        if(!newName){
            $("#usernameError").html("You must fill this field!").show();
            $("#usernameArrow").css("display","inline-block");
            validationResult = false;
        } 
        if(newName.length > 16){
            $("#usernameError").html("Username can't be longer than 16 letters!").show();
            $("#usernameArrow").css("display","inline-block");
            validationResult = false;
        }
        if(!newPass){
            $("#passwordError").html("You must fill this field!").show();
            $("#passwordArrow").css("display","inline-block");
            validationResult = false;
        }
        if(newPass.length > 16){
            $("#passwordError").html("Password can't be longer than 16 letters!").show();
            $("#passwordArrow").css("display","inline-block");
            validationResult = false;
        }
        if(newPass != passwordAgain || !passwordAgain){
            $("#passwordAgainError").html("Passwords don't match!").show();
            $("#newpassArrow").css("display","inline-block");
            validationResult = false;
        }
        if(!fname){
            $("#firstnameError").html("You must fill this field!").show();
            $("#fnameArrow").css("display","inline-block");
            validationResult = false;
        }
        if(fname.length > 16){
            $("#firstnameError").html("First name can't be longer than 16 letters!").show();
            $("#fnameArrow").css("display","inline-block");
            validationResult = false;
        }
        if(!lname){
            $("#lastnameError").html("You must fill this field!").show();
            $("#lnameArrow").css("display","inline-block");
            validationResult = false;
        }
        if(lname.length > 16){
            $("#lastnameError").html("Last name can't be longer than 16 letters!").show();
            $("#lnameArrow").css("display","inline-block");
            validationResult = false;
        }
        if(!email){
            $("#emailError").html("You must fill this field!").show();
            $("#emailArrow").css("display","inline-block");
            validationResult = false;
        }
        if(email.length > 30){
            $("#emailError").html("Last name can't be longer than 30 letters!").show();
            $("#emailArrow").css("display","inline-block");
            validationResult = false;
        }
        return validationResult;
    }
    
    $(".index_buttons").click(function(){
        $(".index_buttons").css("background-color","#6394cf");
        $(this).css("background-color","#366db0");
    });
    
     $("#login_form").click(function(){
         $(".registrationError").html("");
        $(".registrationError").hide();
        $(".registrationArrow").hide();
        
        $("#inputToggle2").hide();
        $("#inputToggle").show(); 
    });
    
    $("#create_form").click(function(){
         $(".registrationError").html("");
        $(".registrationError").hide();
        $(".registrationArrow").hide();
        
        $("#inputToggle").hide();
        $("#inputToggle2").show(); 
    });
});


