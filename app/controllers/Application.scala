//play requestor
package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data._
import play.api.data.Forms._
import models.User
import models.UserReturn



object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  
  //The user form
  val userForm = Form(
  mapping(
    "name" -> text,
    "id" -> text)(User.apply)(User.unapply))
  
  
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
	  
  implicit val personReads: Reads[User] = (
  (__ \ "name").read[String]
  and (__ \ "id").read[String])(User)
	   
  implicit val userReturnReads: Reads[UserReturn] = (
  (__ \ "name").read[String]
  and (__ \ "id").read[String] 
  and (__ \ "age").read[Int] 
  and (__ \ "gender").read[String] 
  and (__ \ "address").read[String])(UserReturn)
  
	   
  def jsonForm = Action {   
    Ok(views.html.jsonForm(userForm))  
  }   
  
  def sendRequest = Action.async {
	  
	  implicit request =>
	  val userID : String = userForm.bindFromRequest.get.id
	  val userName : String = userForm.bindFromRequest.get.name
      
	  val data = Json.obj("name" -> userName,"age" -> userID)
	  
	  val url = "http://localhost:9001/jsonRespond"
   	  	  
   	  val futureResult = WS.url(url).post(data).map {response => (response.json).validate[UserReturn] match {
        case JsSuccess(user, _) =>  
          Ok(views.html.formSubmitted(user))
        case JsError(errors) => 
          Ok("There are errors in JSON")
      } 
    }
	  
	futureResult 

  }
  


}