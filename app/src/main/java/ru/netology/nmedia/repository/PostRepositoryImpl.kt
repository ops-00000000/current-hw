package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()


        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeById(post: Post,callback: PostRepository.OnePostCallback) {
        val id = post.id

        if(!post.likedByMe) {
            val request: Request = Request.Builder()
                .post("".toRequestBody(jsonType))
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()

            client.newCall(request)
                .enqueue(object : Callback{override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }
                })
        }
        else {
            val request: Request = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()

            client.newCall(request)
                .enqueue(object : Callback{override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }
                })


        }

    }


    override fun save(post: Post,callback: PostRepository.OnePostCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: throw RuntimeException("body is null")
                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeById(id: Long,callback: PostRepository.OnePostCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: throw RuntimeException("body is null")
                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
}
