package models

case class Product(ean: Long, name: String, description: String)

object Product {

  var products = Set(
    Product(21111L, "Paperclips Large", "Large paper clips"),
    Product(62222L, "Paperclips Giant", "Large paper clips Giant"),
    Product(43333L, "Paperclips Giant Plain", "Large paper clips Giant Plain"),
    Product(43444L, "No Paperclips", "No Large paper clips"),
    Product(5010255079763L, "Zebra Paperclips", "Zebra Large paper clips")
  )

  def findAll = products.toList.sortBy(_.ean)

  def findByEan(ean: Long) = products.toList.find(_.ean == ean)

  def add(product: Product) = {
    products = products + product
  }
}
