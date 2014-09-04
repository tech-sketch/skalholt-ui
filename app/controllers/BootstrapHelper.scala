package controllers

import views.html.helper.FieldConstructor
import views.html.helper.twitterBootstrapFieldConstructor
import views.html.helper.twitterBootstrapFieldConstructorsm

object BootstrapHelper {

  implicit val fields = FieldConstructor(twitterBootstrapFieldConstructor.f)

}

object BootstrapHelperSm {

  implicit val fields = FieldConstructor(twitterBootstrapFieldConstructorsm.f)

}