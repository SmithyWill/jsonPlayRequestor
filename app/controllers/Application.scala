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


//The index page
object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  
  //The user form
  val userForm = Form(
  mapping(
    "name" -> text,
    "id" -> text)(User.apply)(User.unapply))
  
  
  //Required for future Result call. 
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  
  //How to read a user object. 
  implicit val userReads: Reads[User] = (
  (__ \ "name").read[String]
  and (__ \ "id").read[String])(User)

  //How to read the user returned object. 
  implicit val userReturnReads: Reads[UserReturn] = (
  (__ \ "name").read[String]
  and (__ \ "id").read[String] 
  and (__ \ "age").read[Int] 
  and (__ \ "gender").read[String] 
  and (__ \ "address").read[String])(UserReturn)
  
  /*The json form to display, the empty form object is passed to the view in order to bind the fields
   * on the form to the field variables. 
   */
  def jsonForm = Action {   
	  Ok(views.html.jsonForm(userForm))  
  }   
  
  //The method to send a request to the play service expecting a json request. 
  def sendRequest = Action.async { implicit request => //gets the request object from the browser. 
	  
      val userID : String = userForm.bindFromRequest.get.id //get the id property 
	  val userName : String = userForm.bindFromRequest.get.name//get the name property
      
	  val data = Json.obj("name" -> userName,"age" -> userID)// construct a json object 
	  
	  val url = "http://localhost:9001/jsonRespond" //the service url
   	  
	  
	  /* The post method will return an object of type response  
	   * response => .... states take the response and apply it to the following anonymous function
	   * .validate will attempt to convert the response and map it into a UserReturn object. 
	   * The validate method will return a JsResult object that can either be JsSuccess (conversion succeeded) or JsError (conversion failed).
	   * Depending on the case either the Ok Result with the form submitted html is returned or an OK result with a simple error message. 
	   */
   	  val futureResult = WS.url(url).post(data).map {response => (response.json).validate[UserReturn] match {
        case JsSuccess(user, _) =>  
          Ok(views.html.formSubmitted(user))
        case JsError(errors) => 
          Ok("There are errors in the JSON")
      } 
    }
	  	//The result is returned. 
		futureResult 

  }
  


}