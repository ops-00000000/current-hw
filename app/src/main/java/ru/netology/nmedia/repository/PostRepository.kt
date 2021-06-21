package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post,callback: OnePostCallback)
    fun save(post: Post,callback: OnePostCallback)
    fun removeById(id: Long,callback: OnePostCallback)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
    interface OnePostCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}
