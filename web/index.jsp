

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>test</title>
    </head>
    <body>
    <p>REGISTER USER</p>
        <form name="createuserform" action="/ProjectV1/API/Users/" method="POST">
            Username: <input type="text" name="name"><br>
            Password: <input type="password" name="password"><br>
            <input type="submit" name="submit" value="Register">
        </form>
        
    <p>LOGIN</p>
        <form name="login" action="/ProjectV1/API/Users/login/" method="POST">
            Username: <input type="text" name="name"><br>
            Password: <input type="password" name="password"><br>
            <input type="submit" name="submit" value="Login">
        </form>
    
    <p>CREATE GROUP</p>
        <form name="newgroup" action="/ProjectV1/API/Groups/" method="POST">
            Groupname <input type="text" name="groupname"><br>
            group starter ID <input type="text" name="groupstarterID"><br>
            <input type="submit" name="submit" value="Create group">
        </form>  
    
     <p>ADD USER TO GROUP</p>
        <form name="newgroup" action="/ProjectV1/API/Groups/5/users" method="POST">
            Add user <input type="text" name="userID"><br><br>
            <input type="submit" name="submit" value="Add">
        </form> 
    </body>
</html>
