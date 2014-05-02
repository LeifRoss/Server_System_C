execute "gruppec/database.js"
execute "gruppec/request.js"
execute "user_interface.js"
remote ssh start
remote sftp start
server start
execute "gruppec/autoupload.js"



