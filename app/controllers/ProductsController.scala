package controllers

import models.Product
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._

import javax.inject._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ProductsController @Inject()(cc: ControllerComponents, config: Configuration) extends AbstractController(cc) with I18nSupport {

  def listOfProducts() = Action { implicit request: Request[AnyContent] =>
    val products = Product.findAll
    val messages = messagesApi.preferred(request)
    Ok(views.html.products.list(products, config)(request, messages))
  }


  def show(ean: Long) = Action { implicit request =>
    val messages = messagesApi.preferred(request)

    Product.findByEan(ean).map { product =>
      Ok(views.html.products.details(product, config)(request, messages))
    }.getOrElse(NotFound)
  }

  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying("validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )


  def save() = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()
    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.ProductsController.newProduct()).
          flashing(Flash(form.data) +
            ("error" -> Messages("validation.errors")))
      },
      success = { newProduct =>
        Product.add(newProduct)
        Redirect(routes.ProductsController.show(newProduct.ean))
      }
    )
  }

  def newProduct() = Action { implicit request =>

    val messages = messagesApi.preferred(request)

    val form = if (request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm

    println(form.data)
    Ok(views.html.products.editProduct(form, config)(request, messages))
  }

}
