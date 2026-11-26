package com.smartnewsai.domain.model

data class Category(
    val id: String,
    val name: String,
    val slug: String
) {
    companion object {
        val predefinedCategories = listOf(
            Category("all", "All", "all"),
            Category("tech", "Technology", "technology"),
            Category("biz", "Business", "business"),
            Category("sports", "Sports", "sports"),
            Category("politics", "Politics", "politics"),
            Category("edu", "Education", "education"),
            Category("ent", "Entertainment", "entertainment"),
            Category("science", "Science", "science"),
            Category("health", "Health", "health")
        )
    }
}
