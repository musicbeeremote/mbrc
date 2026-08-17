package com.kelsos.mbrc.core.ui.compose

import androidx.paging.compose.LazyPagingItems

/**
 * Builds the `key` lambda for a paged lazy list, namespaced by [listId].
 *
 * Replaces the `items.peek(index)?.let(key) ?: index` shape, which has two problems:
 *
 * - A placeholder slot keys on the raw index while a loaded row keys on its id, so the two share
 *   one key space. They only avoid collision today because a `Long` id is never equal to an `Int`
 *   index in Kotlin, which stops holding the moment a key function returns an `Int`.
 * - `IllegalArgumentException: Key "534019" was already used` quotes the offending key and nothing
 *   else, and every frame above it is framework code. A crash report cannot say which list failed.
 *
 * Prefixing with [listId] gives placeholders their own key space and puts the list's name into the
 * exception message, so the next report reads `Key "albumtracks-534019" was already used`.
 *
 * [listId] must be unique per list and stable across releases, since it is the identifier crash
 * reports are read by.
 */
fun <T : Any> pagedItemKey(
  listId: String,
  items: LazyPagingItems<T>,
  key: (T) -> Any
): (Int) -> Any = { index ->
  items.peek(index)?.let { item -> namespacedKey(listId, key(item)) }
    ?: placeholderKey(listId, index)
}

/**
 * The key a loaded row takes in the [listId] namespace. See [pagedItemKey].
 */
fun namespacedKey(listId: String, key: Any): String = "$listId-$key"

/**
 * The key an unloaded placeholder slot takes in the [listId] namespace. Separate from the loaded
 * row space so an id can never collide with an index. See [pagedItemKey].
 */
fun placeholderKey(listId: String, index: Int): String = "$listId-placeholder-$index"
