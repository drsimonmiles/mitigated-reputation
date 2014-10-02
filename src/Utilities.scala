import scala.collection.mutable

// Helpful utility functions
object Utilities {
  // Averages a set of numbers
  def average (values: Iterable[Double]) =
    values.sum / values.size

  // Create a map where a value is generated independently for each key
  def createMap[V, W] (keys: Iterable[V]) (createValue: => W): Map[V, W] =
    toMap (keys) (_ => createValue)

  // Returns the cartesian product (all combinations of one element from the first and one from the second) of two lists
  def product[V, W] (listA: Iterable[V], listB: Iterable[W]): Iterable[(V, W)] =
    for (a <- listA; b <- listB) yield (a, b)

  // Creates an immutable map from a list, with the original list as keys, and values created from the keys
  def toMap[V, W] (list: Iterable[V]) (createValue: V => W): Map[V, W] =
    list.map (item => (item, createValue (item))).toMap

  // Creates a mutable map from a list, with the original list as keys, and values created from the keys
  def toMutableMap[V, W] (list: Iterable[V]) (createValue: V => W): mutable.Map[V, W] =
    mutable.Map[V, W] (toMap (list)(createValue).toSeq: _*)
}
