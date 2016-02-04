$(document).ready(function() {
    $("#loginButton").click(function(){
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/login/",
            data: {name: username,password: password},
            statusCode: {
                200: function (response) {
                alert('Correct login.');
               },
               401: function (response) {
                  alert('Invalid login.');
               }
            }
            ,success: function(loginResponse){
                $(".content").load( "/ProjectV1/chatscreen.html", function() {
                    $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Users/"+loginResponse+"/"
                    ,success: function(userInformation){
                            $xml = $( userInformation );
                            var userName = $xml.find("username");
                            $(".username").html(userName);
                        }
                    });
                    
                    
                    $("#placeholder").attr("id",loginResponse)
                });
            }
        });
    });
});