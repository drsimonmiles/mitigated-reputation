import scala.collection.mutable

// Helpful utility functions
object Utilities {
  // Creates an immutable map from a list, with the original list as keys, and values created from the keys
  def toMap[V, W] (list: Iterable[V]) (createValue: V => W): Map[V, W] =
    list.map (item => (item, createValue (item))).toMap

  // Creates a mutable map from a list, with the original list as keys, and values created from the keys
  def toMutableMap[V, W] (list: Iterable[V]) (createValue: V => W): mutable.Map[V, W] =
    mutable.Map[V, W] (toMap (list)(createValue).toSeq: _*)
}
