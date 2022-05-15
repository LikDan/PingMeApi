# Api for PingMe
***

### JSON

/api/user -> current user | GET

/api/user/login -> login user | POST | login -> String, password -> String

/api/user/singUp -> create new user | POST | login -> String, password -> String, name -> String

/api/user/delete -> delete current user | DELETE | authToken -> cookies

/api/user/edit -> edit current user | PUT | authToken -> cookies, login -> String, password -> String, name -> String

/api/user/revoke -> revoke token for current user | DELETE | authToken -> cookies

/api/user/{id} -> info about user with id | GET  