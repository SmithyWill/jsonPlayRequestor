package models


case class User(
  name: String,
  id: String
)

case class UserReturn(
    name:String,
    id:String,
    age:Int,
    gender:String,
    address:String
)
