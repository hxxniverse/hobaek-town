package io.github.hxxniverse.hobeaktown.util.extension

fun <T> List<T>.retrieveValues(startIndex: Int, count: Int): List<T> {
    // Check if the list is not empty and count is positive
    if (this.isNotEmpty() && count > 0) {
        // Adjust 'startIndex' to be a positive index within the list size
        var adjustedStartIndex = (startIndex - 1) % this.size
        if (adjustedStartIndex < 0) {
            adjustedStartIndex += this.size  // Adjust for negative index
        }

        val resultList = mutableListOf<T>()
        var currentIndex = adjustedStartIndex
        var retrievedCount = 0

        while (retrievedCount < count) {
            resultList.add(this[currentIndex])
            currentIndex = (currentIndex + 1) % this.size
            retrievedCount++
        }

        return resultList
    } else {
        // Handle invalid input, such as empty list or non-positive count
        return emptyList()
    }
}
